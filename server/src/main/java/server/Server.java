package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.staticfiles.Location;

public class Server {
    private final Javalin javalin;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final ChessHandler handler = new ChessHandler(userDAO, authDAO, gameDAO);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("src/main/resources/web", Location.EXTERNAL));

        javalin.delete("/db", handler::clear);
        javalin.post("/user", handler::register);
        javalin.post("/session", handler::login);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}