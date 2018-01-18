package com.netent.application.bestgameever.framework;

public class GameConfig {
    private final double cost;
    private final double winRate;
    private final double freeRoundRate;
    private final double prize;
    private final double fillUpAmount;

    public GameConfig(double cost, double winRate, double freeRoundRate, double prize, double fillUpAmount) {
        this.cost = cost;
        this.winRate = winRate;
        this.freeRoundRate = freeRoundRate;
        this.prize = prize;
        this.fillUpAmount = fillUpAmount;
    }

    public double getCost() {
        return cost;
    }

    public double getWinRate() {
        return winRate;
    }

    public double getFreeRoundRate() {
        return freeRoundRate;
    }

    public double getPrize() {
        return prize;
    }

    public double getFillUpAmount() {
        return fillUpAmount;
    }
}

