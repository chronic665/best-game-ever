package com.netent.application.bestgameever.framework;

import com.netent.application.bestgameever.entity.ResultType;
import org.springframework.stereotype.Component;

/**
 * Game eninge / framework component that takes care of all decisions related to game steps.
 * Encapsulates the game configuration.
 * Could be extracted into a standalone service / module.
 */
@Component
public class GameFramework {
    private final GameConfig config;

    public GameFramework() {
        // TODO move into application.yml
        this.config = new GameConfig(10, 0.3, 0.1, 20, 1000);
    }

    /**
     * Calculates the win or loss of a round, depending on the result of a dice throw
     * @param roundResult Dice throw result
     * @return absolute amount that should be added or subtracted from a players balance
     */
    public double calculateAmount(final ResultType roundResult) {
        if(roundResult == ResultType.FILL_UP_BALANCE) {
            throw new IllegalArgumentException("ResultType " + ResultType.FILL_UP_BALANCE + " is not a valid input to calculate round prize");
        }
        double amount = 0 - config.getCost();
        if(roundResult == ResultType.WIN || roundResult == ResultType.WIN_AND_FREE_ROUND) {
            amount += config.getPrize();
        }
        return amount;
    }

    /**
     * Throws a dice according to the @{@link GameConfig}
     * @return @{@link ResultType} of the dice throw
     */
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

    public boolean outOfFunds(final Double currentBalance) {
        return currentBalance <= config.getCost();
    }

    public Double getFillUpAmount() {
        return config.getFillUpAmount();
    }

}
