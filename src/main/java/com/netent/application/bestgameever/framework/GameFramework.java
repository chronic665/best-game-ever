package com.netent.application.bestgameever.framework;

import com.netent.application.bestgameever.entity.ResultType;
import org.springframework.stereotype.Component;

@Component
public class GameFramework {
    private final GameConfig config;

    public GameFramework() {
        // TODO move into application.yml
        this.config = new GameConfig(10, 0.3, 0.1, 20, 1000);;
    }

    public double calculateAmount(ResultType roundResult) {
        double amount = 0 - config.getCost();
        if(roundResult == ResultType.WIN) {
            amount += config.getPrize();
        }
        return amount;
    }

    public ResultType throwDice() {
        ResultType type = ResultType.LOSE;
        // roll dice for win
        if (Math.random() <= config.getWinRate()) {
            type = ResultType.WIN;
        }
        // roll extra dice for free round
        if (Math.random() <= config.getFreeRoundRate()) {
            // add the free round to the previously rolled result type
            type = (type == ResultType.WIN)
                    ? ResultType.WIN_AND_FREE_ROUND
                    : ResultType.FREE_ROUND;
        }
        return type;
    }

    public boolean outOfFunds(Double currentBalance) {
        return currentBalance <= config.getCost();
    }

    public Double getFillUpAmount() {
        return config.getFillUpAmount();
    }

}
