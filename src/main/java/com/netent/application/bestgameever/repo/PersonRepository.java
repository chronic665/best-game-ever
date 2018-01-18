package com.netent.application.bestgameever.repo;

import com.netent.application.bestgameever.entity.User;

public interface PersonRepository {

    boolean addUser(String username);

    User find(String username);

    /**
     * Adds the absolute 'amount' given to the current balance.
     * @param username username
     * @param amount Positive or negative double value to be added (or subtracted if negative) to the users balance
     * @return
     */
    double updateBalance(String username, Double amount);

}
