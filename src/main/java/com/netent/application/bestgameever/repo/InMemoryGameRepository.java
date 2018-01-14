package com.netent.application.bestgameever.repo;

import com.google.common.collect.TreeBasedTable;
import com.netent.application.bestgameever.entity.GameConfig;
import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.RoundResult;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.UUID;

@Repository
public class InMemoryGameRepository implements GameRepository {

    private TreeBasedTable<String, LocalDateTime, RoundResult> dataStore;

    public InMemoryGameRepository() {
        this.dataStore = TreeBasedTable.create();
    }

    @Override
    public GameConfig getGameConfig() {
        return new GameConfig(10, 0.3, 0.1, 20);
    }

    @Override
    public String storeRound(final ResultType result, final String username) {
        final RoundResult roundResult = new RoundResult(UUID.randomUUID().toString(), result);
        dataStore.put(username, roundResult.getTimestamp(), roundResult);
        return roundResult.getRoundId();
    }

    @Override
    public Flux<RoundResult> subscribeToResults(final String username, final String roundId) {
        return Flux.generate(
                () -> null, // some initial state
                (state, sink) -> {
                    RoundResult result = waitForNextResult(username, (RoundResult) state);
                    sink.next(result);
                    if(result.getRoundId().startsWith("6"))
                        sink.complete();
                    return result;
                });
    }

    private RoundResult waitForNextResult(String username, RoundResult lastRound) {
        RoundResult result = null;
        boolean dirty = false;
        while(!dirty) {
            SortedMap<LocalDateTime, RoundResult> row = dataStore.row(username);
            try {
                result = row.get(row.lastKey());
            } catch (NoSuchElementException e) {
                // ignore, no games yet
            }
            if(result != null && (lastRound == null || !result.getRoundId().equals(lastRound.getRoundId()))) {
                System.out.println("Output: " + result);
                dirty = true;
            } else {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    System.err.println("meh: " + e);
                }
            }
        }
        return result;
    }
}
