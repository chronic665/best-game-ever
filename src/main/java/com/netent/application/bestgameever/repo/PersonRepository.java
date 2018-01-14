package com.netent.application.bestgameever.repo;

import com.netent.application.bestgameever.entity.User;

public interface PersonRepository {

    boolean addUser(String username);

    User find(String username);

    double updateBalance(String username, Double amount);

}
