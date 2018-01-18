# Best Game Ever!!1!

## Reactive web game

The game makes use of RESTful endpoints and server side event streams. This decouples client and server and allows easy
consumption of game data from multiple clients. Using the 'persistent' profile, the game events are directly streamed from within the database to the HTTP client

The HTTP call examples are using the [HTTPie](https://httpie.org/) library for improved readability, but any 
HTTP client that supports server side events can be used.

## Running the server

Run the server via Spring Boot's maven plugin. The default configuration starts the server with in-memory data storage. See 'persistent profile' section to run with MongoDB backend. 
```
    mvn spring-boot:run
```

## How to play

A player must first register / login using the /login endpoint.
If the server is not started with the "persistent" Spring profile, neither the game nor the player data is not persisted, 
so after a server restart the players must re-login.
On login a user is created or, if the user already exists, the client must state that the existing user should be used.

```
    http post http://localhost:8080/login?username=foobar&useExisting=true
```

There is not real authorization implemented, so everybody can watch every player's games or play for any (existing) player.

```
    http post http://localhost:8080/plays/foobar
```

To receive the game results a client musst listen to a stream of server side events provided by the following endpoint:

```
    http get http://localhost:8080/plays/foobar --stream
```

To access a certain round the same endpoint can be called with the game round's id as a parameter. In this case, if a stream is requested
the stream will automatically be closed after the first event is returned.
```
    http get http://localhost:8080/plays/foobar?roundId=4711 --stream
    http get http://localhost:8080/plays/foobar?roundId=4711
```

## Frontend
Running the server also delivers a small Thymeleaf frontend under http://localhost:8080/game

Log in with a random username. Clicking on play once will also register the frontend as a listener to the game event stream.

Logging in with several browser windows with the same user name will result in all users receiving all events for that user name (click 'Play!' once to register for listening) 

## persistent profile

The persistent profile makes use of a MongoDB feature called [$changeStream](http://mongodb.github.io/mongo-java-driver/3.6/driver-async/tutorials/change-streams/), which is available starting with version 3.6.0.
This feature is built on the replica set synchronization logs, so it can only be used when the server is configured as a replica set (local installation can be configured as a single node RS).

Tests for the Persistent Repositories have been suspended until the []Embedded MongoDB project](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo) supports MongoDB 3.6.0 features. Without that, a running, compatible MongoDB would be required for building and running even the in-memory version of this game.

### start mongodb container

```
docker run \\
    -p 27017:27017 \\
    --name bge-mongo -d \
    mongo:3.6.2 mongod --replSet my-mongo-set
```
### configure mongodb as replica set
```
$ mongo
> config = { "_id" : "my-mongo-set", "members" : [{ "_id": 0, "host" : "localhost:27017"}]}
> rs.initiate(config)
```
### activate mongodb 3.6.x features 
must be run on primary node in replica set (hard to get wrong on local single node installation) ([reference](https://blog.codecentric.de/en/2018/01/change-streams-mongodb-3-6/))
```
$ mongo
> use admin
> db.adminCommand( { setFeatureCompatibilityVersion: "3.6" } )
```

