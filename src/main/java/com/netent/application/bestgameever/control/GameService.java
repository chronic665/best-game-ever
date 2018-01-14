package com.netent.application.bestgameever.control;

import com.netent.application.bestgameever.entity.GameConfig;
import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.RoundResult;
import com.netent.application.bestgameever.repo.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GameService {

    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public String play(final String username) {

        // 1. get rules
        final GameConfig config = gameRepository.getGameConfig();

        // 2. play round
            // 2.1 throw dice
            final ResultType result = throwDice(config);
            // 2.2 evaluate result

            // 2.3 store result
            final String roundId = gameRepository.storeRound(result, username);
            // 2.4 TODO update balance
        // 3. prepare response
        return roundId;
    }

    private ResultType throwDice(final GameConfig config) {
        // TODO move into extra service
        return ResultType.LOSE;
    }

    public Flux<RoundResult> subscribeToResults(final String username, final String roundId) {
        return gameRepository.subscribeToResults(username, roundId);
    }
}
