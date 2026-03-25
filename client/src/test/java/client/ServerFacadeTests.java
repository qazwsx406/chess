package client;

import org.junit.jupiter.api.*;
import server.Server;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() throws Exception {
        facade.clear();
    }

    @Test
    public void registerSuccess() throws Exception {
        var res = facade.register(new RegisterRequest("user", "pass", "email"));
        assertNotNull(res.authToken());
        assertEquals("user", res.username());
    }

    @Test
    public void registerFailure() throws Exception {
        facade.register(new RegisterRequest("user", "pass", "email"));
        assertThrows(Exception.class, () -> facade.register(new RegisterRequest("user", "pass", "email")));
    }

    @Test
    public void loginSuccess() throws Exception {
        facade.register(new RegisterRequest("user", "pass", "email"));
        var res = facade.login(new LoginRequest("user", "pass"));
        assertNotNull(res.authToken());
        assertEquals("user", res.username());
    }

    @Test
    public void loginFailure() throws Exception {
        facade.register(new RegisterRequest("user", "pass", "email"));
        assertThrows(Exception.class, () -> facade.login(new LoginRequest("user", "wrong")));
    }

    @Test
    public void logoutSuccess() throws Exception {
        var res = facade.register(new RegisterRequest("user", "pass", "email"));
        assertDoesNotThrow(() -> facade.logout(res.authToken()));
    }

    @Test
    public void logoutFailure() throws Exception {
        assertThrows(Exception.class, () -> facade.logout("invalid_token"));
    }

    @Test
    public void createGameSuccess() throws Exception {
        var res = facade.register(new RegisterRequest("user", "pass", "email"));
        var gameRes = facade.createGame(res.authToken(), new CreateGameRequest("game1"));
        assertNotNull(gameRes.gameID());
    }

    @Test
    public void createGameFailure() throws Exception {
        assertThrows(Exception.class, () -> facade.createGame("invalid_token", new CreateGameRequest("game1")));
    }

    @Test
    public void listGamesSuccess() throws Exception {
        var res = facade.register(new RegisterRequest("user", "pass", "email"));
        facade.createGame(res.authToken(), new CreateGameRequest("game1"));
        facade.createGame(res.authToken(), new CreateGameRequest("game2"));
        var listRes = facade.listGames(res.authToken());
        assertEquals(2, listRes.games().size());
    }

    @Test
    public void listGamesFailure() throws Exception {
        assertThrows(Exception.class, () -> facade.listGames("invalid_token"));
    }

    @Test
    public void joinGameSuccess() throws Exception {
        var res = facade.register(new RegisterRequest("user", "pass", "email"));
        var gameRes = facade.createGame(res.authToken(), new CreateGameRequest("game1"));
        assertDoesNotThrow(() -> facade.joinGame(res.authToken(), new JoinGameRequest("WHITE", gameRes.gameID())));
    }

    @Test
    public void joinGameFailure() throws Exception {
        var res = facade.register(new RegisterRequest("user", "pass", "email"));
        var gameRes = facade.createGame(res.authToken(), new CreateGameRequest("game1"));
        assertThrows(Exception.class, () -> facade.joinGame("invalid_token", new JoinGameRequest("WHITE", gameRes.gameID())));
    }
}
