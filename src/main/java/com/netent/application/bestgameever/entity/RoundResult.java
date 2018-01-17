package com.netent.application.bestgameever.entity;

import com.google.common.base.MoreObjects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class RoundResult {
    private final String roundId;
    private final ResultType result;
    private final String timestamp;
    private final boolean freeRound;

    public RoundResult(String roundId, ResultType result) {
        this(roundId, result, false);
    }
    public RoundResult(String roundId, ResultType result, boolean freeRound) {
        this.roundId = roundId;
        this.result = result;
        this.freeRound = freeRound;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
    }

    public String getRoundId() {
        return roundId;
    }

    public ResultType getResult() {
        return result;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isFreeRound() {
        return freeRound;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("roundId", roundId)
                .add("result", result)
                .add("timestamp", timestamp)
                .add("freeRound", freeRound)
                .toString();
    }
}
