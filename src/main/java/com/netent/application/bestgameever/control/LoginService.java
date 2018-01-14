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

    public boolean login(String username, boolean useExisting) throws UserAlreadyExistsException {
        User user = this.personRepository.find(username);
        if(null != user) {
            if(!useExisting) {
                throw new UserAlreadyExistsException();
            }
            return true;
        } else {
            personRepository.addUser(username);
            return true;
        }
    }

}
