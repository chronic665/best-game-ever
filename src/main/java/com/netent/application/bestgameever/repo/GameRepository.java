package com.netent.application.bestgameever.repo;

import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.RoundResult;
import reactor.core.publisher.Flux;

public interface GameRepository {

    String storeRound(ResultType result, String username, boolean freeRound);

    Flux<RoundResult> subscribeToResults(String username, String roundId);
}
