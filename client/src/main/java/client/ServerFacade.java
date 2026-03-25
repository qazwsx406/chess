package client;

import model.*;
import service.*;
import com.google.gson.Gson;
import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        return null;
    }

    public LoginResult login(LoginRequest request) throws Exception {
        return null;
    }

    public void logout(String authToken) throws Exception {
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws Exception {
        return null;
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        return null;
    }

    public void joinGame(String authToken, JoinGameRequest request) throws Exception {
    }
}
