package com.netent.application.bestgameever.control;

import com.netent.application.bestgameever.exception.UserAlreadyExistsException;
import org.springframework.stereotype.Controller;

@Controller
public class LoginService {

    public boolean login(String username) throws UserAlreadyExistsException {
        throw new RuntimeException("Not Implemented yet");
    }
}
