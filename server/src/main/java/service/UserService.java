package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest req) throws BadRequestException, AlreadyTakenException, DataAccessException {
        if (req.username() == null || req.password() == null || req.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (userDAO.getUser(req.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        userDAO.createUser(new UserData(req.username(), req.password(), req.email()));
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, req.username()));
        return new RegisterResult(req.username(), authToken);
    }
}