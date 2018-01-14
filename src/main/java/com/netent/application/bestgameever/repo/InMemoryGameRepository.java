package com.netent.application.bestgameever.repo;

import com.google.common.collect.TreeBasedTable;
import com.netent.application.bestgameever.framework.GameConfig;
import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.RoundResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryGameRepository implements GameRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private TreeBasedTable<String, String, RoundResult> dataStore;

    public InMemoryGameRepository() {
        this.dataStore = TreeBasedTable.create();
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
                    RoundResult result;
                    if(roundId == null) {
                        result = waitForNextResult(username, (RoundResult) state);
                        sink.next(result);
                    } else { // if roundId is requested we only deliver that certain roundId
                        List<RoundResult> results = dataStore.row(username).values().stream()
                                .filter((roundResult) -> roundResult.getRoundId().equals(roundId))
                                .collect(Collectors.toList());
                        // if the round doesn't exist, throw exception that will return an HTTP 404 through the spring rest controller
                        if(results.isEmpty()){
                            throw new ResourceNotFoundException("No round with id " + roundId + " for user " + username + " was found");
                        }else {
                            result = results.get(0);
                        }
                        sink.next(result);
                        // complete sink after first value
                        sink.complete();
                    }
                    return result;
                });
    }

    /**
     * Waits and checks the tree table for new elements and returns once there is a new element for a username
     * @param username
     * @param lastRound
     * @return
     */
    private RoundResult waitForNextResult(final String username, final RoundResult lastRound) {
        RoundResult result = null;
        boolean dirty = false;
        while(!dirty) {
            // get results for a username
            SortedMap<String, RoundResult> row = dataStore.row(username);
            try {
                // extract last element, catch case that the user didn't play any games yet
                result = row.get(row.lastKey());
            } catch (NoSuchElementException e) {
                // ignore, no games yet
            }
            // break out of the loop if last round and this round have different IDs (i.e. a new round has been played)
            if(result != null && (lastRound == null || !result.getRoundId().equals(lastRound.getRoundId()))) {
                log.debug("returning round {} for user {}", result, username);
                dirty = true;
            } else {
                // grade school waiting
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    log.error("meh: {}", e.getMessage(), e);
                }
            }
        }
        return result;
    }
}
