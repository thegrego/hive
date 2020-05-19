package org.grego.hive.warriors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import org.grego.hive.{Config, ErrorHandler, Attack}
import Attack._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import scala.concurrent.ExecutionContextExecutor

object Fighting extends App {
  private implicit val system: ActorSystem = ActorSystem("fighting", Config.GrpcConfiguration)
  private implicit val mat: Materializer = ActorMaterializer()
  private implicit val ec: ExecutionContextExecutor = system.dispatcher

  val port = 8002
  val service = new FightingService

  lazy val routes: Route =
    post {
      entity(as[Attack]) { request =>
        val fightId = request.fightId

        handleExceptions(ErrorHandler.exceptionHandler) {
          complete(service.fight(fightId))
        }
      }
    }

  Http().bindAndHandle(routes, "localhost", port)
  println(s"Server online at http://localhost:$port/")
}
