package com.netent.application.bestgameever.dto;

import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.RoundResult;

public class PlayResponse {

    private final String roundId;
    private final ResultType result;
    private final String message;
    private final double balance;

    public PlayResponse(RoundResult roundResult, double balance) {
        this.roundId = roundResult.getRoundId();
        this.result = roundResult.getResult();
        this.message = roundResult.getResult().getMessage();
        this.balance = balance;
    }

    public String getRoundId() {
        return roundId;
    }

    public ResultType getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public double getBalance() {
        return balance;
    }
}
