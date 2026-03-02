package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import service.*;
import java.util.Map;

public class ChessHandler {
    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ChessHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.clearService = new ClearService(userDAO, authDAO, gameDAO);
        this.userService = new UserService(userDAO, authDAO);
        this.gameService = new GameService(gameDAO, authDAO);
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

    public void register(Context ctx) {
        try {
            RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult res = userService.register(req);
            ctx.status(200);
            ctx.result(gson.toJson(res));
        } catch (BadRequestException e) {
            handleException(ctx, e, 400);
        } catch (AlreadyTakenException e) {
            handleException(ctx, e, 403);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    public void login(Context ctx) {
        try {
            LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResult res = userService.login(req);
            ctx.status(200);
            ctx.result(gson.toJson(res));
        } catch (BadRequestException e) {
            handleException(ctx, e, 400);
        } catch (UnauthorizedException e) {
            handleException(ctx, e, 401);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            userService.logout(authToken);
            ctx.status(200);
            ctx.result("{}");
        } catch (UnauthorizedException e) {
            handleException(ctx, e, 401);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            ListGamesResult res = gameService.listGames(authToken);
            ctx.status(200);
            ctx.result(gson.toJson(res));
        } catch (UnauthorizedException e) {
            handleException(ctx, e, 401);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameResult res = gameService.createGame(authToken, req);
            ctx.status(200);
            ctx.result(gson.toJson(res));
        } catch (BadRequestException e) {
            handleException(ctx, e, 400);
        } catch (UnauthorizedException e) {
            handleException(ctx, e, 401);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);
            gameService.joinGame(authToken, req);
            ctx.status(200);
            ctx.result("{}");
        } catch (BadRequestException e) {
            handleException(ctx, e, 400);
        } catch (UnauthorizedException e) {
            handleException(ctx, e, 401);
        } catch (AlreadyTakenException e) {
            handleException(ctx, e, 403);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    private void handleException(Context ctx, Exception e, int status) {
        ctx.status(status);
        ctx.result(gson.toJson(Map.of("message", e.getMessage())));
    }

    private void handleException(Context ctx, Exception e) {
        ctx.status(500);
        ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
    }
}