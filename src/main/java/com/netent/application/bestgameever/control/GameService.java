package com.netent.application.bestgameever.control;

import com.netent.application.bestgameever.dto.ResultPage;
import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.exception.UserDoesNotExistException;
import com.netent.application.bestgameever.framework.GameFramework;
import com.netent.application.bestgameever.repo.GameRepository;
import com.netent.application.bestgameever.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PersonRepository personRepository;
    private final GameFramework framework;

    @Autowired
    public GameService(GameRepository gameRepository,
                       PersonRepository personRepository,
                       GameFramework framework) {
        this.gameRepository = gameRepository;
        this.personRepository = personRepository;
        this.framework = framework;
    }

    public String play(final String username) {
        // 1. sanity check
        if (personRepository.find(username) == null) {
            throw new UserDoesNotExistException(username);
        }
        // 2. play round
        // 2.1 throw dice
        final ResultType result = framework.throwDice();
        // 2.3 store result
        final String roundId = gameRepository.storeRound(result, username, false);
        // 2.4 update balance
        double currentBalance = personRepository.updateBalance(username, framework.calculateAmount(result, false));
        // 2.5 top up user if out-of-funds
        if (framework.outOfFunds(currentBalance)) {
            gameRepository.storeRound(ResultType.FILL_UP_BALANCE, username, false);
            personRepository.updateBalance(username, framework.getFillUpAmount());
        }
        // 3. if free round was won, trigger new round
        if(wonAFreeRound(result)) {
            freeRound(username);
        }
        // 4. return roundId
        return roundId;
    }

    private boolean wonAFreeRound(ResultType result) {
        return result == ResultType.WIN_AND_FREE_ROUND || result == ResultType.FREE_ROUND;
    }

    private void freeRound(final String username) {
        final ResultType result = framework.throwDice();
        gameRepository.storeRound(result, username, true);
        personRepository.updateBalance(username, framework.calculateAmount(result, true));
        if(wonAFreeRound(result) ) {
            freeRound(username);
        }
    }

    public Flux<ResultPage> subscribeToResults(final String username, final String roundId) {
        return gameRepository.subscribeToResults(username, roundId)
                .map(roundResult ->
                    new ResultPage(roundResult, personRepository.find(username))
                );
    }
}
