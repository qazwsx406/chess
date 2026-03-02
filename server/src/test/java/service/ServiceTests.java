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

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
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
}