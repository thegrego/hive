package org.grego.hive.gateway

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{as, complete, entity, post}
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import org.grego.hive._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.syntax._

import scala.concurrent.ExecutionContextExecutor

object Gateway extends App {
  private implicit val system: ActorSystem = ActorSystem("gateway", Config.GrpcConfiguration)
  private implicit val mat: Materializer = ActorMaterializer()
  private implicit val ec: ExecutionContextExecutor = system.dispatcher
  private val port: Int = 7078

  val publisher = new Publisher
  val resultHandler = new FightStepResultHandler(publisher)

  lazy val routes: Route =
    post {
      entity(as[MessageEnvelope]) { envelope =>
        val request = envelope.content[FightStepRequest]
        val requestMessageId = envelope.message.messageId
        val fightId = request.fightId

        complete {
          val httpRequest = HttpRequest(
            method = HttpMethods.POST,
            uri = Uri(request.requestUrl),
            entity = HttpEntity(Attack(fightId).asJson.noSpaces)
          )

          Http()
            .singleRequest(httpRequest)
            .map(response => resultHandler.handle(requestMessageId, request, response))
        }
      }
    }

  Http().bindAndHandle(routes, "localhost", port)
  println(s"Server online at http://localhost:$port/")
}
