package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

public class AuthDAOTests {
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        authDAO = new SqlAuthDAO();
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    public void createAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testUser");
        authDAO.createAuth(auth);
        AuthData retrieved = authDAO.getAuth("token123");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("testUser", retrieved.username());
    }

    @Test
    public void createAuthNegative() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testUser");
        authDAO.createAuth(auth);
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(auth);
        });
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testUser");
        authDAO.createAuth(auth);
        AuthData retrieved = authDAO.getAuth("token123");
        Assertions.assertNotNull(retrieved);
    }

    @Test
    public void getAuthNegative() throws DataAccessException {
        AuthData retrieved = authDAO.getAuth("nonExistentToken");
        Assertions.assertNull(retrieved);
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testUser");
        authDAO.createAuth(auth);
        authDAO.deleteAuth("token123");
        AuthData retrieved = authDAO.getAuth("token123");
        Assertions.assertNull(retrieved);
    }

    @Test
    public void deleteAuthNegative() throws DataAccessException {
        authDAO.deleteAuth("nonExistentToken");
        AuthData retrieved = authDAO.getAuth("nonExistentToken");
        Assertions.assertNull(retrieved);
    }

    @Test
    public void clearTest() throws DataAccessException {
        authDAO.createAuth(new AuthData("token123", "testUser"));
        authDAO.clear();
        AuthData retrieved = authDAO.getAuth("token123");
        Assertions.assertNull(retrieved);
    }
}
