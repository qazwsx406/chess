package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.websocket.WsConfig;
import websocket.commands.UserGameCommand;

public class WebSocketHandler {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final SessionManager sessionManager = new SessionManager();
    private final Gson gson = new Gson();

    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void configure(WsConfig ws) {
        ws.onConnect(ctx -> {
            System.out.println("WebSocket connected: " + ctx.sessionId());
        });

        ws.onMessage(ctx -> {
            String message = ctx.message();
            System.out.println("Received message: " + message);
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                command = gson.fromJson(message, websocket.commands.MakeMoveCommand.class);
            }
            handleCommand(ctx, command);
        });

        ws.onClose(ctx -> {
            System.out.println("WebSocket closed: " + ctx.sessionId());
            sessionManager.removeSession(ctx);
        });

        ws.onError(ctx -> {
            System.out.println("WebSocket error: " + ctx.sessionId());
            ctx.error().printStackTrace();
        });
    }

    private void handleCommand(io.javalin.websocket.WsContext ctx, UserGameCommand command) {
        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx, command);
                case MAKE_MOVE -> makeMove(ctx, command);
                case LEAVE -> leave(ctx, command);
                case RESIGN -> resign(ctx, command);
            }
        } catch (Exception e) {
            sendError(ctx, e.getMessage());
        }
    }

    private void connect(io.javalin.websocket.WsContext ctx, UserGameCommand command) throws Exception {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        model.AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new Exception("Invalid auth token");
        }

        model.GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new Exception("Invalid game ID");
        }

        sessionManager.addSessionToGame(gameID, authToken, ctx);

        websocket.messages.LoadGameMessage loadMessage = new websocket.messages.LoadGameMessage(gameData.game());
        ctx.send(gson.toJson(loadMessage));

        String username = auth.username();
        String role = "";
        if (username.equals(gameData.whiteUsername())) {
            role = "white player";
        } else if (username.equals(gameData.blackUsername())) {
            role = "black player";
        } else {
            role = "observer";
        }

        String message = String.format("%s joined the game as %s", username, role);
        websocket.messages.NotificationMessage notification = new websocket.messages.NotificationMessage(message);
        sessionManager.broadcast(gameID, notification, authToken);
    }

    private void makeMove(io.javalin.websocket.WsContext ctx, UserGameCommand command) throws Exception {
        if (!(command instanceof websocket.commands.MakeMoveCommand moveCommand)) {
            throw new Exception("Invalid move command");
        }
        
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        chess.ChessMove move = moveCommand.getMove();

        model.AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new Exception("Invalid auth token");
        }

        model.GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new Exception("Invalid game ID");
        }

        chess.ChessGame game = gameData.game();
        if (game.isFinished()) {
            throw new Exception("Game is already over");
        }

        String username = auth.username();
        chess.ChessGame.TeamColor userColor = null;
        if (username.equals(gameData.whiteUsername())) {
            userColor = chess.ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            userColor = chess.ChessGame.TeamColor.BLACK;
        } else {
            throw new Exception("Observers cannot make moves");
        }

        if (game.getTeamTurn() != userColor) {
            throw new Exception("Not your turn");
        }

        try {
            game.makeMove(move);
        } catch (chess.InvalidMoveException e) {
            throw new Exception("Invalid move");
        }

        gameDAO.updateGame(gameData);

        websocket.messages.LoadGameMessage loadMessage = new websocket.messages.LoadGameMessage(game);
        sessionManager.broadcast(gameID, loadMessage, null);

        String moveStr = String.format("%s moved from %s to %s", username, move.getStartPosition(), move.getEndPosition());
        websocket.messages.NotificationMessage notification = new websocket.messages.NotificationMessage(moveStr);
        sessionManager.broadcast(gameID, notification, authToken);

        chess.ChessGame.TeamColor opponentColor = (userColor == chess.ChessGame.TeamColor.WHITE) ? chess.ChessGame.TeamColor.BLACK : chess.ChessGame.TeamColor.WHITE;
        if (game.isInCheckmate(opponentColor)) {
            game.setFinished(true);
            gameDAO.updateGame(gameData);
            sessionManager.broadcast(gameID, new websocket.messages.NotificationMessage(opponentColor + " is in checkmate. Game over."), null);
        } else if (game.isInStalemate(opponentColor)) {
            game.setFinished(true);
            gameDAO.updateGame(gameData);
            sessionManager.broadcast(gameID, new websocket.messages.NotificationMessage("Game is in stalemate. Game over."), null);
        } else if (game.isInCheck(opponentColor)) {
            sessionManager.broadcast(gameID, new websocket.messages.NotificationMessage(opponentColor + " is in check."), null);
        }
    }
    private void leave(io.javalin.websocket.WsContext ctx, UserGameCommand command) throws Exception {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        model.AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new Exception("Invalid auth token");
        }

        model.GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new Exception("Invalid game ID");
        }

        String username = auth.username();
        boolean isPlayer = false;
        if (username.equals(gameData.whiteUsername())) {
            gameData = new model.GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            gameDAO.updateGame(gameData);
            isPlayer = true;
        } else if (username.equals(gameData.blackUsername())) {
            gameData = new model.GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            gameDAO.updateGame(gameData);
            isPlayer = true;
        }

        sessionManager.removeSession(ctx);

        String message = String.format("%s left the game", username);
        websocket.messages.NotificationMessage notification = new websocket.messages.NotificationMessage(message);
        sessionManager.broadcast(gameID, notification, authToken);
    }
    private void resign(io.javalin.websocket.WsContext ctx, UserGameCommand command) throws Exception {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        model.AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new Exception("Invalid auth token");
        }

        model.GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new Exception("Invalid game ID");
        }

        String username = auth.username();
        if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
            throw new Exception("Observers cannot resign");
        }

        if (gameData.game().isFinished()) {
            throw new Exception("Game is already over");
        }

        gameData.game().setFinished(true);
        gameDAO.updateGame(gameData);

        String message = String.format("%s resigned. The game is over.", username);
        websocket.messages.NotificationMessage notification = new websocket.messages.NotificationMessage(message);
        sessionManager.broadcast(gameID, notification, null);
    }

    private void sendError(io.javalin.websocket.WsContext ctx, String message) {
        try {
            ctx.send(gson.toJson(new websocket.messages.ErrorMessage("Error: " + message)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
