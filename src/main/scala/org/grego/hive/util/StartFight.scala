package org.grego.hive.util

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import org.grego.hive.{Config, Publisher, Attack}

import scala.concurrent.ExecutionContextExecutor

object StartFight extends App {
  private implicit val system: ActorSystem = ActorSystem("publisher", Config.GrpcConfiguration)
  private implicit val mat: Materializer = ActorMaterializer()
  private implicit val ec: ExecutionContextExecutor = system.dispatcher

  val publisher = new Publisher

  publisher
    .publish(Config.AttackTopic, Attack("fightId-3"))
    .foreach(_ => system.terminate())
}
