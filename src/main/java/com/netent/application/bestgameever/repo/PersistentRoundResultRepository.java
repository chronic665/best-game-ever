package com.netent.application.bestgameever.repo;

import com.netent.application.bestgameever.entity.RoundResult;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Profile("persistent")
public interface PersistentRoundResultRepository extends ReactiveCrudRepository<RoundResult, String> {

    Mono<RoundResult> findByRoundId(String roundId);
}
