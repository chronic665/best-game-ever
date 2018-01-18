package com.netent.application.bestgameever.repo;

import com.netent.application.bestgameever.entity.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

@Profile("persistent")
public interface MongoPersonRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByUsername(String username);
}
