package org.grego.hive

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError}
import akka.http.scaladsl.server.Directives.{complete, extractUri}
import akka.http.scaladsl.server.ExceptionHandler

object ErrorHandler {
  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException =>
      extractUri { _ =>
        complete(HttpResponse(BadRequest, entity = e.getMessage))
      }
    case e: RuntimeException =>
      extractUri { _ =>
        complete(HttpResponse(InternalServerError, entity = e.getMessage))
      }
  }
}
