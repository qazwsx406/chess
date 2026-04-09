package ui;

import client.ServerFacade;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketCommunicator;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChessClient implements NotificationHandler {
    private final ServerFacade facade;
    private final String serverUrl;
    private State state = State.LOGGED_OUT;
    private String authToken = null;
    private Map<Integer, Integer> gameIdMap = new HashMap<>();
    private WebSocketCommunicator ws = null;
    private Integer currentGameId = null;
    private String playerColor = null;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        int port = 8080;
        try {
            port = Integer.parseInt(serverUrl.split(":")[2]);
        } catch (Exception e) {
            // Default to 8080
        }
        this.facade = new ServerFacade(port);
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";      
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "redraw" -> redrawBoard();
                case "leave" -> leave();
                case "quit" -> "quit";
                default -> "Unknown command. Type Help to see available commands.";
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public void notify(ServerMessage notification) {
        switch (notification.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadMessage = (LoadGameMessage) notification;
                System.out.println("\n" + BoardDrawer.draw(playerColor != null ? playerColor : "WHITE"));
                System.out.print("[GAMEPLAY] >>> ");
            }
            case NOTIFICATION -> {
                NotificationMessage notifMessage = (NotificationMessage) notification;
                System.out.println("\n" + notifMessage.getMessage());
                System.out.print("[GAMEPLAY] >>> ");
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) notification;
                System.out.println("\n" + errorMessage.getErrorMessage());
                System.out.print("[GAMEPLAY] >>> ");
            }
        }
    }

    private String register(String[] params) throws Exception {
        if (state != State.LOGGED_OUT) {
            throw new Exception("You are already logged in.");
        }
        if (params.length != 3) {
            throw new Exception("Expected: register <USERNAME> <PASSWORD> <EMAIL>");
        }
        var res = facade.register(new service.RegisterRequest(params[0], params[1], params[2]));
        state = State.LOGGED_IN;
        authToken = res.authToken();
        return "Successfully registered and logged in as " + res.username() + "."; 
    }

    private String login(String[] params) throws Exception {
        if (state != State.LOGGED_OUT) {
            throw new Exception("You are already logged in.");
        }
        if (params.length != 2) {
            throw new Exception("Expected: login <USERNAME> <PASSWORD>");
        }
        var res = facade.login(new service.LoginRequest(params[0], params[1]));    
        state = State.LOGGED_IN;
        authToken = res.authToken();
        return "Logged in as " + res.username() + ".";
    }

    private String logout() throws Exception {
        if (state == State.LOGGED_OUT) {
            throw new Exception("You are not logged in.");
        }
        facade.logout(authToken);
        state = State.LOGGED_OUT;
        authToken = null;
        return "Logged out.";
    }

    private String createGame(String[] params) throws Exception {
        if (state == State.LOGGED_OUT) {
            throw new Exception("You must be logged in to create a game.");        
        }
        if (params.length != 1) {
            throw new Exception("Expected: create <NAME>");
        }
        facade.createGame(authToken, new service.CreateGameRequest(params[0]));    
        return "Created game " + params[0] + ".";
    }

    private String listGames() throws Exception {
        if (state == State.LOGGED_OUT) {
            throw new Exception("You must be logged in to list games.");
        }
        var res = facade.listGames(authToken);
        gameIdMap.clear();
        var sb = new StringBuilder();
        int i = 1;
        for (var game : res.games()) {
            gameIdMap.put(i, game.gameID());
            sb.append(i).append(". ").append(game.gameName()).append("\n");        
            sb.append("   White: ").append(game.whiteUsername() != null ? game.whiteUsername() : "Empty").append("\n");
            sb.append("   Black: ").append(game.blackUsername() != null ? game.blackUsername() : "Empty").append("\n");
            i++;
        }
        return sb.toString();
    }

    private String joinGame(String[] params) throws Exception {
        if (state != State.LOGGED_IN) {
            throw new Exception("You must be logged in to join a game.");
        }
        if (params.length != 2) {
            throw new Exception("Expected: play <ID> [WHITE|BLACK]");
        }

        int listId;
        try {
            listId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new Exception("ID must be a number from the list command.");     
        }

        Integer gameId = gameIdMap.get(listId);
        if (gameId == null) {
            throw new Exception("Invalid game ID. Use the list command to see available games.");
        }

        String color = params[1].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new Exception("Player color must be WHITE or BLACK.");
        }

        facade.joinGame(authToken, new service.JoinGameRequest(color, gameId));

        this.playerColor = color;
        this.currentGameId = gameId;
        this.ws = new WebSocketCommunicator(serverUrl, this);
        this.ws.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId));
        this.state = State.GAMEPLAY;

        return "Joined game " + listId + " as " + color + ".";
    }

    private String observeGame(String[] params) throws Exception {
        if (state != State.LOGGED_IN) {
            throw new Exception("You must be logged in to observe a game.");       
        }
        if (params.length != 1) {
            throw new Exception("Expected: observe <ID>");
        }

        int listId;
        try {
            listId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new Exception("ID must be a number from the list command.");     
        }

        Integer gameId = gameIdMap.get(listId);
        if (gameId == null) {
            throw new Exception("Invalid game ID. Use the list command to see available games.");
        }

        this.playerColor = null; // Observer perspective is WHITE
        this.currentGameId = gameId;
        this.ws = new WebSocketCommunicator(serverUrl, this);
        this.ws.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId));
        this.state = State.GAMEPLAY;

        return "Observing game " + listId + ".";
    }

    private String redrawBoard() throws Exception {
        if (state != State.GAMEPLAY) {
            throw new Exception("You are not in a game.");
        }
        return BoardDrawer.draw(playerColor != null ? playerColor : "WHITE");
    }

    private String leave() throws Exception {
        if (state != State.GAMEPLAY) {
            throw new Exception("You are not in a game.");
        }
        this.ws.send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, currentGameId));
        this.state = State.LOGGED_IN;
        this.currentGameId = null;
        this.playerColor = null;
        this.ws = null;
        return "Left the game.";
    }

    public String help() {
        if (state == State.LOGGED_OUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account  
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        } else if (state == State.LOGGED_IN) {
            return """
                    create <NAME> - a game
                    list - games
                    play <ID> [WHITE|BLACK] - a game
                    observe <ID> - a game
                    logout - when you are done
                    quit - playing chess
                    help - with possible commands
                    """;
        } else {
            return """
                    redraw - the board
                    leave - the game
                    make <MOVE> - a move
                    resign - the match
                    highlight <PIECE> - legal moves
                    help - with possible commands
                    """;
        }
    }

    public State getState() {
        return state;
    }
}