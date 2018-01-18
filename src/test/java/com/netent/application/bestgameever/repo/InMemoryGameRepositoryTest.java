package com.netent.application.bestgameever.repo;

import com.netent.application.bestgameever.entity.ResultType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Stack;

import static java.time.temporal.ChronoUnit.MICROS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InMemoryGameRepositoryTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String MOCK_USERNAME = "testNormalGame";
    private InMemoryGameRepository cut;
    private Stack<String> ids;


    @BeforeEach
    public void setup() {
        this.cut = new IdTestableInMemoryGameRepository();
        ids = new Stack<>();
        ids.push("5");
        ids.push("4");
        ids.push("3");
        ids.push("2");
        ids.push("1");
    }

    @Test
    public void testNormalGame() {
        new Thread(() -> {
            // wait for StepVerifier to start listening
            sleep(1000);
            this.cut.storeRound(ResultType.FREE_ROUND, MOCK_USERNAME, false);
            sleep(100);
            this.cut.storeRound(ResultType.WIN, MOCK_USERNAME, true);
            sleep(100);
            this.cut.storeRound(ResultType.LOSE, MOCK_USERNAME, false);
            sleep(100);
            this.cut.storeRound(ResultType.WIN_AND_FREE_ROUND, MOCK_USERNAME, false);
            sleep(100);
            this.cut.storeRound(ResultType.LOSE, MOCK_USERNAME, true);
        }).start();
        StepVerifier.create(
                this.cut.subscribeToResults(MOCK_USERNAME, null)
                )
            .consumeNextWith(roundResult -> {
                System.out.println(roundResult);
                assertThat(roundResult.getRoundId(), is("1"));
                assertThat(roundResult.getResult(), is(ResultType.FREE_ROUND));
                assertThat(roundResult.isFreeRound(), is(false));
            })
            .consumeNextWith(roundResult -> {
                System.out.println(roundResult);
                assertThat(roundResult.getRoundId(), is("2"));
                assertThat(roundResult.getResult(), is(ResultType.WIN));
                assertThat(roundResult.isFreeRound(), is(true));
            })
            .consumeNextWith(roundResult -> {
                System.out.println(roundResult);
                assertThat(roundResult.getRoundId(), is("3"));
                assertThat(roundResult.getResult(), is(ResultType.LOSE));
                assertThat(roundResult.isFreeRound(), is(false));
            })
            .consumeNextWith(roundResult -> {
                System.out.println(roundResult);
                assertThat(roundResult.getRoundId(), is("4"));
                assertThat(roundResult.getResult(), is(ResultType.WIN_AND_FREE_ROUND));
                assertThat(roundResult.isFreeRound(), is(false));
            })
            .consumeNextWith(roundResult -> {
                System.out.println(roundResult);
                assertThat(roundResult.getRoundId(), is("5"));
                assertThat(roundResult.getResult(), is(ResultType.LOSE));
                assertThat(roundResult.isFreeRound(), is(true));
            })
            .thenCancel()
            .verify();
        //roundResult -> log.error("subscribed: {}", roundResult));
    }

    void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class IdTestableInMemoryGameRepository extends InMemoryGameRepository {
        @Override
        String generateId() {
            return ids.pop();
        }
    }
}