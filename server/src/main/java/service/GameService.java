package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedException, DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        Collection<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest req) throws UnauthorizedException, BadRequestException, DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (req.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        int id = gameDAO.createGame(req.gameName());
        return new CreateGameResult(id);
    }

    public void joinGame(String authToken, JoinGameRequest req) throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        GameData game = gameDAO.getGame(req.gameID());
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (req.playerColor() == null) {
            throw new BadRequestException("Error: bad request");
        }

        String color = req.playerColor().toUpperCase();
        if (color.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
            gameDAO.updateGame(new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game()));
        } else if (color.equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
            gameDAO.updateGame(new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game()));
        } else {
            throw new BadRequestException("Error: bad request");
        }
    }
}