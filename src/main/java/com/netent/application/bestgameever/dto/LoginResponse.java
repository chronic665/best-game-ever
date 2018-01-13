package com.netent.application.bestgameever.dto;

public class LoginResponse {

    private final ResponseType type;
    private final String message;

    public enum ResponseType {
        I,
        W,
        E
    }

    public LoginResponse(final ResponseType type, final String message) {
        this.type = type;
        this.message = message;
    }

    public ResponseType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
