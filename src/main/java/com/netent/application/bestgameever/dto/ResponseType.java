package com.netent.application.bestgameever.dto;

import org.springframework.http.HttpStatus;

public enum ResponseType {
    I("You logged in successfully", HttpStatus.OK),
    W("This username is already taken, do you want to use it?", HttpStatus.FORBIDDEN),
    E("Error occurred while logging in, please try again later", HttpStatus.SERVICE_UNAVAILABLE);
    private final String message;
    private final HttpStatus code;

    ResponseType(final String message, final HttpStatus code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getCode() {
        return code;
    }
}
