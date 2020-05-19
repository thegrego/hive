package org.grego.hive.supplier

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import org.grego.hive.Attack._
import org.grego.hive.{Attack, Config, ErrorHandler}

import scala.concurrent.ExecutionContextExecutor

object FoodSupplier extends App {
  private implicit val system: ActorSystem = ActorSystem("food-supplier", Config.GrpcConfiguration)
  private implicit val mat: Materializer = ActorMaterializer()
  private implicit val ec: ExecutionContextExecutor = system.dispatcher

  val port = 8003
  val service = new FoodSupplierService

  lazy val routes: Route =
    post {
      entity(as[Attack]) { request =>
        val fightId = request.fightId

        handleExceptions(ErrorHandler.exceptionHandler) {
          complete(service.getFood(fightId))
        }
      }
    }

  Http().bindAndHandle(routes, "localhost", port)
  println(s"Server online at http://localhost:$port/")
}