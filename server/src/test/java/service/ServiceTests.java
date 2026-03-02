package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

public class ServiceTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private ClearService clearService;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
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
}