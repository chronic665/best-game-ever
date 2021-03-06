package com.netent.application.bestgameever.repo;

import com.mongodb.client.model.Aggregates;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.session.ClientSession;
import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.RoundResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static java.util.Arrays.asList;

@Repository
@Profile("persistent")
public class PersistentGameRepository implements GameRepository {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final PersistentRoundResultRepository repo;
    private final ReactiveMongoTemplate mongoTemplate;

    @Autowired
    public PersistentGameRepository(PersistentRoundResultRepository repo,
                                    ReactiveMongoTemplate mongoTemplate) {
        this.repo = repo;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public String storeRound(ResultType result, String username, boolean freeRound) {
        log.debug("Persisting to MongoDB");
        Mono<RoundResult> save = this.repo.save(new RoundResult(roundId(), username, result, freeRound));
        return save.doOnEach(event -> log.debug("Persisted: {}", event)).block().getRoundId();
    }

    String roundId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Flux<RoundResult> subscribeToResults(String username, String roundId) {
        // if roundId is provided only resolve a single element (=Mono)
        if(null != roundId) {
            return repo.findByRoundId(roundId)
                    .doOnEach(each -> System.out.println(each))
                        .filter(round -> username.equals(round.getUsername()))
                        .flux();
        }
        // if no roundId is provided, register a Subscriber to the $changeStream functionality of the
        // reactive mongo driver and stream out the result
        return mongoTemplate.createFlux(mongoDatabase -> {
            MongoCollection< Document > collection = mongoDatabase.getCollection(RoundResult.COLLECTION);
            return Flux.from(collection.watch(filters(username), Document.class))
                    .map(csDoc -> new RoundResult(csDoc))
                    // Debug output
//                    .log()
//                    .checkpoint()
                    ;
        });
    }

    private List<Bson> filters(final String username) {
        return asList(
                Aggregates.match( and( asList(
                        in("operationType", asList("insert")),
                        eq("fullDocument.username", username)))
                )
        );
    }
}
