package com.netent.application.bestgameever.framework;

import com.netent.application.bestgameever.entity.ResultType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.netent.application.bestgameever.entity.ResultType.*;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameFrameworkTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    // Magic values for tests, see GameConfig class
    private final double WIN_RATE = 0.3;
    private final double FREE_ROUND_RATE = 0.1;
    private final double COST = -10d;
    private final double PRIZE = 20d;
    private static final double FILL_UP_AMOUNT = 1000d;

    private GameFramework cut;

    @BeforeEach
    public void setup() {
        this.cut = new GameFramework();
    }

    @Test
    public void testAverageDiceOutcome() {
        int wins = 0, loss = 0, freeRound = 0;
        int rounds = 1000;
        for(int i = 0; i < rounds; i++) {
            ResultType resultType = cut.throwDice();
            if(resultType == WIN || resultType == WIN_AND_FREE_ROUND) wins++;
            if(resultType == LOSE || resultType == FREE_ROUND) loss++;
            if(resultType == WIN_AND_FREE_ROUND || resultType == FREE_ROUND) freeRound++;
        }
        log.debug("Wins ({}), Loss({}), FR ({})", wins, loss, freeRound);
        assertThat(wins, is(both(greaterThan(lowerBoundary(WIN_RATE, rounds))).and(lessThan(upperBoundary(WIN_RATE, rounds)))));
        assertThat(freeRound, is(both(greaterThan(lowerBoundary(FREE_ROUND_RATE, rounds))).and(lessThan(upperBoundary(FREE_ROUND_RATE, rounds)))));
        assertThat(wins + loss, is(rounds));
    }

    /**
     * upper boundary is +15% of average rate
     * @param rate
     * @param rounds
     * @return
     */
    private int upperBoundary(double rate, int rounds) {
        int expected = (int) (rate * rounds);
        return expected + (int) (0.15 * expected);
    }

    /**
     * lower boundary is -15% of average rate
     * @param rate
     * @param rounds
     * @return
     */
    private int lowerBoundary(double rate, int rounds) {
        int expected = (int) (rate * rounds);
        return expected - (int) (0.15 * expected);
    }

    @Test
    public void testMoneyCalculation() {
        assertThat(cut.calculateAmount(null, true), is(COST));
        assertThat(cut.calculateAmount(LOSE, true), is(COST));
        assertThat(cut.calculateAmount(WIN, true), is(COST + PRIZE));
        assertThat(cut.calculateAmount(WIN_AND_FREE_ROUND, true), is(COST + PRIZE));
        assertThat(cut.calculateAmount(FREE_ROUND, true), is(COST));
        assertThrows(IllegalArgumentException.class, () -> cut.calculateAmount(FILL_UP_BALANCE, true));
    }

    @Test
    public void testOutOfBalance() {
        assertThat(cut.outOfFunds(COST), is(true));
        assertThat(cut.outOfFunds(-1 * COST), is(true));
        assertThat(cut.outOfFunds(-1 * COST + 1 ), is(false));
        assertThat(cut.outOfFunds(-1 * COST + 0.1 ), is(false));
        assertThat(cut.outOfFunds(1000d), is(false));
        assertThat(cut.outOfFunds(Double.MIN_VALUE), is(true));
        assertThat(cut.outOfFunds(Double.MAX_VALUE), is(false));
    }

    @Test
    public void testFillUpAmount() {
        assertThat(cut.getFillUpAmount(), is(FILL_UP_AMOUNT));
    }
}