package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

public class ServiceTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private ClearService clearService;
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    @DisplayName("Clear Success")
    public void clearSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("u", "p", "e"));
        authDAO.createAuth(new AuthData("t", "u"));
        gameDAO.createGame("g");
        
        clearService.clear();
        
        Assertions.assertNull(userDAO.getUser("u"));
        Assertions.assertNull(authDAO.getAuth("t"));
        Assertions.assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    @DisplayName("Register Success")
    public void registerSuccess() throws Exception {
        RegisterRequest req = new RegisterRequest("user", "pass", "email");
        RegisterResult res = userService.register(req);
        
        Assertions.assertEquals("user", res.username());
        Assertions.assertNotNull(res.authToken());
    }

    @Test
    @DisplayName("Register Already Taken")
    public void registerAlreadyTaken() throws Exception {
        RegisterRequest req = new RegisterRequest("user", "pass", "email");
        userService.register(req);
        
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(req));
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess() throws Exception {
        userService.register(new RegisterRequest("user", "pass", "email"));
        
        LoginRequest req = new LoginRequest("user", "pass");
        LoginResult res = userService.login(req);
        
        Assertions.assertEquals("user", res.username());
        Assertions.assertNotNull(res.authToken());
    }

    @Test
    @DisplayName("Login Wrong Password")
    public void loginWrongPassword() throws Exception {
        userService.register(new RegisterRequest("user", "pass", "email"));
        
        LoginRequest req = new LoginRequest("user", "wrong");
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.login(req));
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() throws Exception {
        RegisterResult res = userService.register(new RegisterRequest("user", "pass", "email"));
        
        userService.logout(res.authToken());
        Assertions.assertNull(authDAO.getAuth(res.authToken()));
    }

    @Test
    @DisplayName("Logout Invalid Token")
    public void logoutInvalidToken() {
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.logout("invalid"));
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGameSuccess() throws Exception {
        RegisterResult res = userService.register(new RegisterRequest("user", "pass", "email"));
        
        CreateGameResult gameRes = gameService.createGame(res.authToken(), new CreateGameRequest("game"));
        Assertions.assertTrue(gameRes.gameID() > 0);
    }

    @Test
    @DisplayName("Create Game Unauthorized")
    public void createGameUnauthorized() {
        Assertions.assertThrows(UnauthorizedException.class, () -> 
            gameService.createGame("invalid", new CreateGameRequest("game")));
    }

    @Test
    @DisplayName("List Games Success")
    public void listGamesSuccess() throws Exception {
        RegisterResult res = userService.register(new RegisterRequest("user", "pass", "email"));
        gameService.createGame(res.authToken(), new CreateGameRequest("g1"));
        gameService.createGame(res.authToken(), new CreateGameRequest("g2"));
        
        ListGamesResult listRes = gameService.listGames(res.authToken());
        Assertions.assertEquals(2, listRes.games().size());
    }

    @Test
    @DisplayName("List Games Unauthorized")
    public void listGamesUnauthorized() {
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.listGames("invalid"));
    }

    @Test
    @DisplayName("Join Game Success")
    public void joinGameSuccess() throws Exception {
        RegisterResult res = userService.register(new RegisterRequest("user", "pass", "email"));
        CreateGameResult gameRes = gameService.createGame(res.authToken(), new CreateGameRequest("game"));
        
        gameService.joinGame(res.authToken(), new JoinGameRequest("WHITE", gameRes.gameID()));
        
        GameData game = gameDAO.getGame(gameRes.gameID());
        Assertions.assertEquals("user", game.whiteUsername());
    }

    @Test
    @DisplayName("Join Game Taken")
    public void joinGameTaken() throws Exception {
        RegisterResult res1 = userService.register(new RegisterRequest("u1", "p", "e"));
        RegisterResult res2 = userService.register(new RegisterRequest("u2", "p", "e"));
        CreateGameResult gameRes = gameService.createGame(res1.authToken(), new CreateGameRequest("game"));
        
        gameService.joinGame(res1.authToken(), new JoinGameRequest("WHITE", gameRes.gameID()));
        
        Assertions.assertThrows(AlreadyTakenException.class, () -> 
            gameService.joinGame(res2.authToken(), new JoinGameRequest("WHITE", gameRes.gameID())));
    }
}