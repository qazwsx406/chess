package ui;

import client.ServerFacade;
import model.GameData;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChessClient {
    private final ServerFacade facade;
    private final String serverUrl;
    private State state = State.LOGGED_OUT;
    private String authToken = null;
    private Map<Integer, Integer> gameIdMap = new HashMap<>();

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
                case "quit" -> "quit";
                default -> "Unknown command. Type Help to see available commands.";
            };
        } catch (Exception e) {
            return e.getMessage();
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
        var res = facade.createGame(authToken, new service.CreateGameRequest(params[0]));
        return "Created game " + params[0] + " with ID " + res.gameID() + ".";
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
        if (state == State.LOGGED_OUT) {
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
        
        String playerColor = params[1].toUpperCase();
        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new Exception("Player color must be WHITE or BLACK.");
        }
        
        facade.joinGame(authToken, new service.JoinGameRequest(playerColor, gameId));
        
        // Draw the board (Perspective: WHITE if color is WHITE, BLACK if color is BLACK)
        return "Joined game " + listId + " as " + playerColor + ".\n" + drawBoard(playerColor);
    }

    private String observeGame(String[] params) throws Exception {
        if (state == State.LOGGED_OUT) {
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
        
        facade.joinGame(authToken, new service.JoinGameRequest(null, gameId));
        
        // Draw the board (Perspective: WHITE for observers)
        return "Observing game " + listId + ".\n" + drawBoard("WHITE");
    }

    private String drawBoard(String perspective) {
        return "Chessboard (" + perspective + " perspective) will be drawn here in Milestone 13-15.";
    }

    public String help() {
        if (state == State.LOGGED_OUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        return """
                create <NAME> - a game
                list - games
                play <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

    public State getState() {
        return state;
    }
}
