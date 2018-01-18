package com.netent.application.bestgameever.repo;

import com.google.common.collect.TreeBasedTable;
import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.RoundResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock implementation of a Stream enabled game repository. Can be used for testing without a running MongoDB.
 *<br />
 * Uses a Guava table as data store.
 * <br /><br />
 * <b>State does not survive application restarts!</b>
 */
@Repository
@Profile("default")
public class InMemoryGameRepository implements GameRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private TreeBasedTable<String, String, RoundResult> dataStore;

    public InMemoryGameRepository() {
        this.dataStore = TreeBasedTable.create();
    }

    @Override
    public String storeRound(final ResultType result, final String username, boolean freeRound) {
        final RoundResult roundResult = new RoundResult(generateId(), username, result, freeRound);
        dataStore.put(username, roundResult.getTimestamp(), roundResult);
        return roundResult.getRoundId();
    }

    String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Flux<RoundResult> subscribeToResults(final String username, final String roundId) {
        return Flux.generate(
                () -> null, // some initial state
                (state, sink) -> {
                    RoundResult result;
                    if(roundId == null) {
                        result = waitForNextResult(username, (RoundResult) state);
                        log.debug("next: {}", result);
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
            final SortedMap<String, RoundResult> row = dataStore.row(username);

            // extract last element that was not push to the sink yet
            // filter all old elements from the entryset and return the first one from the remains
            final List<RoundResult> collect = row.entrySet().stream()
                    // filter for newer entries than last round
                    .filter(entry ->
                        lastRound == null ? true :
                        0 < entry.getKey().compareTo(lastRound.getTimestamp())
                    )
                    .map(entry -> entry.getValue())
                    // make sure new list is sorted by timestamp
                    .sorted(Comparator.comparing(RoundResult::getTimestamp))
                    .collect(Collectors.toList());
            if(!collect.isEmpty()){
                result = collect.get(0);
            }
            // first round
            if(lastRound == null && result != null) {
                dirty = true;
            }
            // break out of the loop if a result was found
            if(result != null) {
                log.debug("returning round {} for user {}", result, username);
                dirty = true;
            } else {
                // cheap waiting
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
