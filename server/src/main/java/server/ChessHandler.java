package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import service.*;
import java.util.Map;

public class ChessHandler {
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ChessHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    public void clear(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e) {
            handleException(ctx, e);
        }
    }

    private void handleException(Context ctx, Exception e) {
        ctx.status(500);
        ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
    }
}