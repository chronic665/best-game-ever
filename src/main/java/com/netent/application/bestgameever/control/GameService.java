package com.netent.application.bestgameever.control;

import com.netent.application.bestgameever.entity.GameConfig;
import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.RoundResult;
import com.netent.application.bestgameever.exception.UserDoesNotExistException;
import com.netent.application.bestgameever.repo.GameRepository;
import com.netent.application.bestgameever.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static java.lang.Math.random;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PersonRepository personRepository;

    @Autowired
    public GameService(GameRepository gameRepository, PersonRepository personRepository) {
        this.gameRepository = gameRepository;
        this.personRepository = personRepository;
    }

    public String play(final String username) {
        if (personRepository.find(username) == null) {
            throw new UserDoesNotExistException(username);
        }
        // 1. get rules
        final GameConfig config = gameRepository.getGameConfig();
        // 2. play round
        // 2.1 throw dice
        final ResultType result = throwDice(config);
        // 2.3 store result
        final String roundId = gameRepository.storeRound(result, username);
        // 2.4 update balance
        double currentBalance = personRepository.updateBalance(username, result, config);
        if (currentBalance <= config.getCost()) {
            gameRepository.storeRound(ResultType.FILL_UP_BALANCE, username);
            personRepository.updateBalance(username, ResultType.FILL_UP_BALANCE, config);
        }
        // 3. return roundId
        return roundId;
    }

    private ResultType throwDice(final GameConfig config) {
        ResultType type = ResultType.LOSE;
        // roll dice for win
        if (Math.random() <= 0.3) {
            type = ResultType.WIN;
        }
        // roll extra dice for free round
        if (Math.random() <= 0.1) {
            // add the free round to the previously rolled result type
            type = (type == ResultType.WIN)
                    ? ResultType.WIN_AND_FREE_ROUND
                    : ResultType.FREE_ROUND;
        }
        return type;
    }

    public Flux<RoundResult> subscribeToResults(final String username, final String roundId) {
        return gameRepository.subscribeToResults(username, roundId);
    }
}
