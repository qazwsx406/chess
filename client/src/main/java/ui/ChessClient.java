package ui;

import client.ServerFacade;
import java.util.Arrays;

public class ChessClient {
    private final ServerFacade facade;
    private final String serverUrl;
    private State state = State.LOGGED_OUT;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        int port = Integer.parseInt(serverUrl.split(":")[2]);
        this.facade = new ServerFacade(port);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "quit" -> "quit";
                default -> "Unknown command. Type Help to see available commands.";
            };
        } catch (Exception e) {
            return e.getMessage();
        }
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
