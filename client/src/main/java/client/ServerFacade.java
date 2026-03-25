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
        return makeRequest("POST", "/user", null, request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        return makeRequest("POST", "/session", null, request, LoginResult.class);
    }

    private <T> T makeRequest(String method, String path, String authToken, Object requestBody, Class<T> responseClass) throws Exception {
        URL url = new URI(serverUrl + path).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        http.setDoOutput(requestBody != null);

        if (authToken != null) {
            http.addRequestProperty("Authorization", authToken);
        }

        if (requestBody != null) {
            http.addRequestProperty("Content-Type", "application/json");
            try (OutputStream os = http.getOutputStream()) {
                os.write(new Gson().toJson(requestBody).getBytes());
            }
        }

        http.connect();
        
        if (http.getResponseCode() >= 400) {
            throw new Exception("Error: " + http.getResponseCode());
        }

        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(respBody);
            if (responseClass == null) {
                return null;
            }
            return new Gson().fromJson(reader, responseClass);
        }
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
