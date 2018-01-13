package com.netent.application.bestgameever.entity;

public enum ResultType {

    WIN("You won this round!"),
    FREE_ROUND("You won a free round!"),
    WIN_AND_FREE_ROUND("You won this round and got a free round!"),
    LOSE("You lost this round, try again!");

    private String message;

    ResultType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
