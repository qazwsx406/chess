package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import java.sql.SQLException;

public class UserDAOTests {
    private static UserDAO userDAO;

    @BeforeAll
    public static void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        userDAO = new SqlUserDAO();
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    public void createUserPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        userDAO.createUser(user);
        UserData retrieved = userDAO.getUser("testUser");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("testUser", retrieved.username());
        Assertions.assertEquals("email@test.com", retrieved.email());
    }

    @Test
    public void createUserNegative() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        userDAO.createUser(user);
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(user);
        });
    }

    @Test
    public void getUserPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        userDAO.createUser(user);
        UserData retrieved = userDAO.getUser("testUser");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("testUser", retrieved.username());
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        UserData retrieved = userDAO.getUser("nonExistentUser");
        Assertions.assertNull(retrieved);
    }

    @Test
    public void clearTest() throws DataAccessException {
        userDAO.createUser(new UserData("testUser", "password", "email@test.com"));
        userDAO.clear();
        UserData retrieved = userDAO.getUser("testUser");
        Assertions.assertNull(retrieved);
    }
}
