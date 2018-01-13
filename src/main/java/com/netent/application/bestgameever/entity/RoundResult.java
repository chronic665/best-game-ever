package com.netent.application.bestgameever.entity;

public class RoundResult {
    private final String roundId;
    private final ResultType result;

    public RoundResult(String roundId, ResultType result) {
        this.roundId = roundId;
        this.result = result;
    }

    public String getRoundId() {
        return roundId;
    }

    public ResultType getResult() {
        return result;
    }
}
