package ui;

import client.ServerFacade;
import java.util.Arrays;

public class ChessClient {
    private final ServerFacade facade;
    private final String serverUrl;
    private State state = State.LOGGED_OUT;
    private String authToken = null;

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
        return "Not implemented";
    }

    private String listGames() throws Exception {
        return "Not implemented";
    }

    private String joinGame(String[] params) throws Exception {
        return "Not implemented";
    }

    private String observeGame(String[] params) throws Exception {
        return "Not implemented";
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
