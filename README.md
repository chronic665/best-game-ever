# Best Game Ever!!1!

## Reactive web game

The game makes use of RESTful endpoints and server side event streams. This decouples client and server and allows easy
consumption of game data from multiple clients.

The HTTP call examples are using the [HTTPie](https://httpie.org/] library for improved readability.
Any HTTP client that supports server side events can be used.

## How to play

A player must first register / login using the /login endpoint.
The player data is not persisted, so after a server restart the players must re-login.
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


 see: https://blog.codecentric.de/en/2018/01/change-streams-mongodb-3-6/
 also: http://mongodb.github.io/mongo-java-driver/3.6/driver/tutorials/change-streams/

## persistent profile

### start mongodb container

docker run \
-p 27017:27017 \
--name bge-mongo -d \
mongo:3.6.2 mongod --replSet my-mongo-set

### configure mongodb as replica set to allow $changeStreams to work
$ mongo
> config = { "_id" : "my-mongo-set", "members" : [{ "_id": 0, "host" : "localhost:27017"}]}
> rs.initiate(config)

### activate mongodb 3.6.x features (must be run on primary node in replica set)
$ mongo
> use admin
> db.adminCommand( { setFeatureCompatibilityVersion: "3.6" } )
