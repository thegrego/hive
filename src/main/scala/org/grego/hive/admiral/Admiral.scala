package org.grego.hive.admiral

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, path, post}
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import org.grego.hive._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import scala.concurrent.ExecutionContextExecutor

object Admiral extends App {
  private implicit val system: ActorSystem = ActorSystem("admiral", Config.GrpcConfiguration)
  private implicit val mat: Materializer = ActorMaterializer()
  private implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val port: Int = 7077

  val publisher = new Publisher
  val fileStorageService = new FileStorageService
  val serviceLocator = new ServiceLocator
  val service = new AdmiralService(fileStorageService, serviceLocator, publisher)

  lazy val routes: Route =
    post {
      concat(
        path("start") {
          entity(as[MessageEnvelope]) { envelope =>
            val start = envelope.content[Attack]

            complete(service.startFight(start.fightId))
          }
        },
        path("result") {
          entity(as[MessageEnvelope]) { envelope =>
            val fightStepResult = envelope.content[FightStepResult]

            complete {
              fightStepResult
                .error
                .map(_ => service.onSuccess(fightStepResult))
                .getOrElse(service.onFailure(fightStepResult))
                .map(_ => StatusCodes.OK)
            }
          }
        }
      )
    }

  Http().bindAndHandle(routes, "localhost", port)
  println(s"Server online at http://localhost:$port/")
}
