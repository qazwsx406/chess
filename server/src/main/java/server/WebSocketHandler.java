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
        });

        ws.onError(ctx -> {
            System.out.println("WebSocket error: " + ctx.getSessionId());
            ctx.error().printStackTrace();
        });
    }

    private void handleCommand(io.javalin.websocket.WsMessageContext ctx, UserGameCommand command) {
        // To be implemented in future milestones
        System.out.println("Handling command: " + command.getCommandType());
    }
}
