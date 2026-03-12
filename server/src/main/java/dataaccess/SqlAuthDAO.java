package dataaccess;

import model.AuthData;
import java.sql.SQLException;

public class SqlAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
    }

    @Override
    public void clear() throws DataAccessException {
    }
}
