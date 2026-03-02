package dataaccess;

import model.GameData;
import chess.ChessGame;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;

    @Override
    public int createGame(String gameName) {
        int id = nextID++;
        GameData game = new GameData(id, null, null, gameName, new ChessGame());
        games.put(id, game);
        return id;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public void clear() {
        games.clear();
    }
}