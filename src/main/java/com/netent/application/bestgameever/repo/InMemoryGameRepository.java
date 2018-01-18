package com.netent.application.bestgameever.repo;

import com.google.common.collect.FluxEnableTreeBasedTable;
import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.RoundResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    private FluxEnableTreeBasedTable<String, String, RoundResult> dataStore;

    public InMemoryGameRepository() {
        this.dataStore = FluxEnableTreeBasedTable.create();
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

        if(roundId != null) {
             //if roundId is requested we only deliver that certain roundId
            List<RoundResult> results = dataStore.row(username).values().stream()
                    .filter((roundResult) -> roundResult.getRoundId().equals(roundId))
                    .collect(Collectors.toList());
            // if the round doesn't exist, throw exception that will return an HTTP 404 through the spring rest controller
            if(results.isEmpty()){
                throw new ResourceNotFoundException("No round with id " + roundId + " for user " + username + " was found");
            }else {
                return Mono.just(results.get(0)).flux();
            }
        }
        return dataStore.subscribe(username);

    }

}
