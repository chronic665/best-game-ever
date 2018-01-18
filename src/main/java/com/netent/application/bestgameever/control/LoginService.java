package com.netent.application.bestgameever.control;

import com.netent.application.bestgameever.entity.User;
import com.netent.application.bestgameever.exception.UserAlreadyExistsException;
import com.netent.application.bestgameever.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoginService {

    private final PersonRepository personRepository;

    @Autowired
    public LoginService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Simple mock implementation of a user service.
     * Combines registration and login. If "useExisting" is false, an exception will be thrown if there
     * already exists a user with the given username.
     * Adds the user to a repository if she doesn't exist
     * @param username
     * @param useExisting
     * @return
     * @throws UserAlreadyExistsException
     */
    public User login(String username, boolean useExisting) throws UserAlreadyExistsException {
        User user = this.personRepository.find(username);
        if(null != user) {
            if(!useExisting) {
                throw new UserAlreadyExistsException();
            }
            return user;
        } else {
            return personRepository.addUser(username);
        }
    }

}
