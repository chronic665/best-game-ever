package com.netent.application.bestgameever.dto;

public class LoginResponse {

    private final ResponseType type;
    private final String message;

    public LoginResponse(final ResponseType type) {
        this.type = type;
        this.message = type.getMessage();
    }

    public ResponseType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
