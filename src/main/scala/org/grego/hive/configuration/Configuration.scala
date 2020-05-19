package org.grego.hive.configuration

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import org.grego.hive.{Config, Attack}
import Attack._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import scala.concurrent.ExecutionContextExecutor

object Configuration extends App {
  private implicit val system: ActorSystem = ActorSystem("configuration", Config.GrpcConfiguration)
  private implicit val mat: Materializer = ActorMaterializer()
  private implicit val ec: ExecutionContextExecutor = system.dispatcher

  val port = 8000

  lazy val routes: Route =
    post {
      entity(as[Attack]) { _ =>
        complete(StatusCodes.OK)
      }
    }

  Http().bindAndHandle(routes, "localhost", port)
  println(s"Server online at http://localhost:$port/")
}
