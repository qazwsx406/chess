package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import java.util.Collection;

public class GameDAOTests {
    private static GameDAO gameDAO;

    @BeforeAll
    public static void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        gameDAO = new SqlGameDAO();
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    public void createGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("Test Game");
        Assertions.assertTrue(gameID > 0);
        GameData retrieved = gameDAO.getGame(gameID);
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("Test Game", retrieved.gameName());
    }

    @Test
    public void createGameNegative() {
        // gameName is NOT NULL in schema, so null should throw exception
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(null);
        });
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("Test Game");
        GameData retrieved = gameDAO.getGame(gameID);
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals(gameID, retrieved.gameID());
    }

    @Test
    public void getGameNegative() throws DataAccessException {
        GameData retrieved = gameDAO.getGame(9999);
        Assertions.assertNull(retrieved);
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        gameDAO.createGame("Game 1");
        gameDAO.createGame("Game 2");
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertEquals(2, games.size());
    }

    @Test
    public void listGamesEmpty() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void updateGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("Original Name");
        GameData original = gameDAO.getGame(gameID);
        GameData updated = new GameData(gameID, "whiteUser", "blackUser", "New Name", original.game());
        gameDAO.updateGame(updated);
        
        GameData retrieved = gameDAO.getGame(gameID);
        Assertions.assertEquals("New Name", retrieved.gameName());
        Assertions.assertEquals("whiteUser", retrieved.whiteUsername());
        Assertions.assertEquals("blackUser", retrieved.blackUsername());
    }

    @Test
    public void updateGameNegative() throws DataAccessException {
        // Try to update a game that doesn't exist
        GameData nonExistent = new GameData(9999, "white", "black", "Name", new ChessGame());
        gameDAO.updateGame(nonExistent);
        Assertions.assertNull(gameDAO.getGame(9999));
    }

    @Test
    public void clearTest() throws DataAccessException {
        gameDAO.createGame("Game 1");
        gameDAO.clear();
        Assertions.assertTrue(gameDAO.listGames().isEmpty());
    }
}
