package com.netent.application.bestgameever.dto;

import com.google.common.base.MoreObjects;
import com.netent.application.bestgameever.entity.RoundResult;
import com.netent.application.bestgameever.entity.User;

public class ResultPage {
    private final RoundResult roundResult;
    private final String username;
    private final double balance;

    public ResultPage(RoundResult roundResult, User user) {
        this.roundResult = roundResult;
        this.username = user.getUsername();
        this.balance = user.getBalance();
    }

    public RoundResult getRoundResult() {
        return roundResult;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("roundResult", roundResult)
                .add("username", username)
                .add("balance", balance)
                .toString();
    }
}
