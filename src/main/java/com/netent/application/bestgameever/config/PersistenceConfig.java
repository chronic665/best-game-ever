package com.netent.application.bestgameever.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.netent.application.bestgameever.repo.PersistentRoundResultRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = PersistentRoundResultRepository.class)
@Profile("persistent")
public class PersistenceConfig extends AbstractReactiveMongoConfiguration {
    private final Environment environment;

    public PersistenceConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    @Bean
    public MongoClient reactiveMongoClient() {
//        int port = environment.getProperty("local.mongo.port", Integer.class);
        int port = 27017;
        return MongoClients.create(String.format("mongodb://localhost:%d", port));
    }

    @Override
    protected String getDatabaseName() {
        return "best-game-ever";
    }

}
