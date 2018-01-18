package com.netent.application.bestgameever.repo;

import com.netent.application.bestgameever.entity.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Profile("default")
public class InMemoryPersonRepository implements PersonRepository {

    private final Map<String, User> users;

    public InMemoryPersonRepository() {
        this.users = new HashMap<>();
    }

    @Override
    public User addUser(String username) {
        if(!users.containsKey(username)) {
            users.put(username, new User(username, 1000));
        }
        return users.get(username);
    }

    @Override
    public User find(String username) {
        return users.get(username);
    }

    @Override
    public double updateBalance(String username, Double amount) {
        final User user = users.get(username);
        user.setBalance(user.getBalance() + amount);
        return user.getBalance();
    }
}
