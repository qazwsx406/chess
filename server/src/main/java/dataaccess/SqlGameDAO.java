package dataaccess;

import model.GameData;
import java.util.Collection;
import java.util.ArrayList;
import java.sql.SQLException;

public class SqlGameDAO implements GameDAO {
    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
    }

    @Override
    public void clear() throws DataAccessException {
    }
}
