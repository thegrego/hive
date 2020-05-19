package org.grego.hive.gateway

import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.Materializer
import org.grego.hive.{FightStepRequest, FightStepResult, Publisher}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class FightStepResultHandler(publisher: Publisher)(implicit mat: Materializer, ec: ExecutionContext) {
  private val duration = 1.minute // todo random number

  def handle(messageId: String, request: FightStepRequest, response: HttpResponse): Future[String] = {
    response.status.intValue() match {
      case status if 200 to 299 contains status =>
        val result = FightStepResult(messageId, request.fightId)
        publisher.publish(request.resultTopic, result)
      case 429                                  =>
        Future.failed(new IllegalStateException("GCP infrastructure has failed"))
      case status if 400 to 499 contains status =>
        handleFailure(response) { entity =>
          val result = FightStepResult(messageId, request.fightId, entity.data.utf8String)
          publisher.publish(request.resultTopic, result)
        }
      case status                               =>
        handleFailure(response) { entity =>
          Future.failed(new IllegalStateException(s"Cloud Run has encountered unexpected error with status $status: ${entity.data.utf8String}"))
        }
    }
  }

  private def handleFailure(response: HttpResponse)(f: HttpEntity.Strict => Future[String]): Future[String] = {
    response
      .entity
      .toStrict(duration)
      .flatMap(f)
  }
}
