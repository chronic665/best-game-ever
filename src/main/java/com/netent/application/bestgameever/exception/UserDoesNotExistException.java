package com.netent.application.bestgameever.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No user with this username exists. Please login first!")
public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String username) {
        super("There is no user with username '"+ username + "'. Please login first");
    }
}
