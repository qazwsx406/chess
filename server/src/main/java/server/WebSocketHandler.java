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
            System.out.println("WebSocket connected: " + ctx.getSessionId());
        });

        ws.onMessage(ctx -> {
            String message = ctx.message();
            System.out.println("Received message: " + message);
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            handleCommand(ctx, command);
        });

        ws.onClose(ctx -> {
            System.out.println("WebSocket closed: " + ctx.getSessionId());
            sessionManager.removeSession(ctx);
        });

        ws.onError(ctx -> {
            System.out.println("WebSocket error: " + ctx.getSessionId());
            ctx.error().printStackTrace();
        });
    }

    private void handleCommand(io.javalin.websocket.WsMessageContext ctx, UserGameCommand command) {
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

    private void connect(io.javalin.websocket.WsMessageContext ctx, UserGameCommand command) throws Exception {
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

    private void makeMove(io.javalin.websocket.WsMessageContext ctx, UserGameCommand command) throws Exception { }
    private void leave(io.javalin.websocket.WsMessageContext ctx, UserGameCommand command) throws Exception {
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
    private void resign(io.javalin.websocket.WsMessageContext ctx, UserGameCommand command) throws Exception {
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

    private void sendError(io.javalin.websocket.WsMessageContext ctx, String message) {
        try {
            ctx.send(gson.toJson(new websocket.messages.ErrorMessage("Error: " + message)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
