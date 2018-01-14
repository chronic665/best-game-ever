package com.netent.application.bestgameever.entity;

import com.google.common.base.MoreObjects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class RoundResult {
    private final String roundId;
    private final ResultType result;
    private final String timestamp;

    public RoundResult(String roundId, ResultType result) {
        this.roundId = roundId;
        this.result = result;
        timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("roundId", roundId)
                .add("result", result)
                .add("timestamp", timestamp)
                .toString();
    }
}
