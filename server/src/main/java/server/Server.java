package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.staticfiles.Location;

public class Server {
    private final Javalin javalin;
    private final UserDAO userDAO = new SqlUserDAO();
    private final AuthDAO authDAO = new SqlAuthDAO();
    private final GameDAO gameDAO = new SqlGameDAO();
    private final ChessHandler handler = new ChessHandler(userDAO, authDAO, gameDAO);

    public Server() {
        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }

        javalin = Javalin.create(config -> config.staticFiles.add("/web", Location.CLASSPATH));

        javalin.delete("/db", handler::clear);
        javalin.post("/user", handler::register);
        javalin.post("/session", handler::login);
        javalin.delete("/session", handler::logout);
        javalin.get("/game", handler::listGames);
        javalin.post("/game", handler::createGame);
        javalin.put("/game", handler::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}