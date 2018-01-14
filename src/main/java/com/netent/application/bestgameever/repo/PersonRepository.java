package com.netent.application.bestgameever.repo;

import com.netent.application.bestgameever.entity.GameConfig;
import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.User;

public interface PersonRepository {
    User find(String username);

    double updateBalance(String username, ResultType result, GameConfig config);

}
