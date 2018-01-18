package com.netent.application.bestgameever.entity;

import com.google.common.base.MoreObjects;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document(collection = RoundResult.COLLECTION)
public class RoundResult {
    public final static transient String COLLECTION = "rounds";

    @Id
    private String roundId;
    private ResultType result;
    private String timestamp;
    private boolean freeRound;
    private String username;

    public RoundResult() {
    }

    public RoundResult(String roundId, String username, ResultType result, boolean freeRound) {
        this.roundId = roundId;
        this.result = result;
        this.freeRound = freeRound;
        this.username = username;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
    }

    public RoundResult(ChangeStreamDocument<org.bson.Document> csDoc) {
        this.roundId = String.valueOf(csDoc.getFullDocument().get("_id"));
        this.username = String.valueOf(csDoc.getFullDocument().get("username"));
        this.result = ResultType.valueOf(String.valueOf(csDoc.getFullDocument().get("result")));
        this.freeRound = Boolean.valueOf(String.valueOf(csDoc.getFullDocument().get("freeRound")));
        this.timestamp = String.valueOf(csDoc.getFullDocument().get("timestamp"));
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

    public String getUsername() {
        return username;
    }

    public void setRoundId(String roundId) {
        this.roundId = roundId;
    }

    public void setResult(ResultType result) {
        this.result = result;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setFreeRound(boolean freeRound) {
        this.freeRound = freeRound;
    }

    public void setUsername(String username) {
        this.username = username;
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
