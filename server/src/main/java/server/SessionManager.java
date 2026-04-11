package server;

import io.javalin.websocket.WsContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import websocket.messages.ServerMessage;

public class SessionManager {
    public final Map<Integer, List<SessionEntry>> gameSessions = new HashMap<>();
    private final Gson gson = new Gson();

    private static class SessionEntry {
        WsContext session;
        String authToken;

        SessionEntry(WsContext session, String authToken) {
            this.session = session;
            this.authToken = authToken;
        }
    }

    public void addSessionToGame(int gameID, String authToken, WsContext session) {
        gameSessions.computeIfAbsent(gameID, k -> new ArrayList<>()).add(new SessionEntry(session, authToken));
    }

    public void removeSession(WsContext session) {
        for (List<SessionEntry> entries : gameSessions.values()) {
            entries.removeIf(entry -> entry.session.equals(session));
        }
    }

    public void broadcast(int gameID, ServerMessage message, String excludeAuthToken) throws IOException {
        List<SessionEntry> entries = gameSessions.get(gameID);
        if (entries != null) {
            String jsonMessage = gson.toJson(message);
            for (SessionEntry entry : entries) {
                if (entry.session.session.isOpen()) {
                    if (excludeAuthToken == null || !entry.authToken.equals(excludeAuthToken)) {
                        entry.session.send(jsonMessage);
                    }
                }
            }
        }
    }
}