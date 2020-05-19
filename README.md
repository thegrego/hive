## Running
1. Start Pub/Sub locally. For this you must have `gcloud` installed locally. This will start Pub/Sub on port 8085 and create project with id `hive`.:

`gcloud beta emulators pubsub start --project=hive`

2. Create topics and push-subscriptions by running `Preparation.scala`:

`sbt runMain org.grego.hive.util.Preparation`

3. Run all components separately:

```sbt "runMain org.grego.hive.configuration.Configuration"```

```sbt "runMain org.grego.hive.admiral.Admiral"```

```sbt "runMain org.grego.hive.fighting.Fighting"```

```sbt "runMain org.grego.hive.supplier.FoodSupplier"```

```sbt "runMain org.grego.hive.gateway.Gateway"```

4. Send a "start fight" to pub/sub using `StartFight.scala`:

```sbt "runMain org.grego.hive.util.StartFight"```

Notes: 
* It is possible to run Pub/Sub [in docker](https://github.com/marcelcorso/gcloud-pubsub-emulator). However, for this we need to give docker possibility to call our component endpoints, which are deployed on OS level.
* Pub/Sub emulator is not persistent so every time you restart it you have to run `Preparation`
* `Config.scala` should be replaced by proper configuration
* Multiple dependencies will cause sporadic failures because FileStorage is not thread safe and the whole state is overridden from outside