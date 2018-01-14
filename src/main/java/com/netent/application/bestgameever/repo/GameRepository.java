package com.netent.application.bestgameever.repo;

import com.netent.application.bestgameever.entity.GameConfig;
import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.RoundResult;
import reactor.core.publisher.Flux;

public interface GameRepository {

    GameConfig getGameConfig();

    String storeRound(ResultType result, String username);

    Flux<RoundResult> subscribeToResults(String username, String roundId);
}
