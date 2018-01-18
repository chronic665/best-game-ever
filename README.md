# Notes
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
