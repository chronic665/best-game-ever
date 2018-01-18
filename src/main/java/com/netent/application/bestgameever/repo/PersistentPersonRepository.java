package com.netent.application.bestgameever.repo;

import com.netent.application.bestgameever.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("persistent")
public class PersistentPersonRepository implements PersonRepository {

    private final MongoPersonRepository personRepo;

    @Autowired
    public PersistentPersonRepository(MongoPersonRepository personRepo) {
        this.personRepo = personRepo;
    }

    @Override
    public boolean addUser(String username) {
        User user = personRepo.save(new User(username, 1000d)).block();
        return true;
    }

    @Override
    public User find(String username) {
        return personRepo.findByUsername(username).block();
    }

    @Override
    public double updateBalance(String username, Double amount) {
        final User temp = this.find(username);
        temp.setBalance(temp.getBalance() + amount);
        final User saved = personRepo.save(temp).block();
        return saved.getBalance();
    }
}
