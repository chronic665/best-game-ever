package com.netent.application.bestgameever.entity;

public class GameConfig {
    private double cost;
    private double winRate;
    private double freeRoundRate;
    private double prize;
    private double fillUpAmount;

    public GameConfig() {
    }

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

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public double getFreeRoundRate() {
        return freeRoundRate;
    }

    public void setFreeRoundRate(double freeRoundRate) {
        this.freeRoundRate = freeRoundRate;
    }

    public double getPrize() {
        return prize;
    }

    public void setPrize(double prize) {
        this.prize = prize;
    }

    public double getFillUpAmount() {
        return fillUpAmount;
    }

    public void setFillUpAmount(double fillUpAmount) {
        this.fillUpAmount = fillUpAmount;
    }
}
