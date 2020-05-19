package org.grego.hive.admiral

import akka.actor.ActorSystem
import org.grego.hive.{Config, FightStepRequest, FightStepResult, Publisher}

import scala.concurrent.{ExecutionContext, Future}

class AdmiralService(
  fileStorage: FileStorageService,
  serviceLocator: ServiceLocator,
  publisher: Publisher
)(implicit system: ActorSystem, ec: ExecutionContext) {
  type FightId = String
  def startFight(fightId: FightId): Future[Unit] = {
    println(s"[$fightId] Starting fight")

    for {
      _ <- fileStorage.start(fightId)
      _ <- requestStepFight(fightId)
    } yield ()
  }

  def onFailure(result: FightStepResult): Future[Unit] = {
    println(s"[${result.fightId}] Step ${result.requestMessageId} has failed: ${result.error}")

    for {
      _ <- fileStorage.finishStep(result.fightId, result.requestMessageId, StepStatus.Failed)
      _ <- Future.successful(println(s"[${result.fightId}] Sending failure notifications..")) // TODO proper notifications
    } yield ()
  }

  def onSuccess(result: FightStepResult): Future[Unit] = {
    println(s"[${result.fightId}] Step ${result.requestMessageId} has finished successfully")

    for {
      _ <- fileStorage.finishStep(result.fightId, result.requestMessageId, StepStatus.Done)
      _ <- finishOrProceed(result.fightId)
    } yield ()
  }

  private def finishOrProceed(fightId: FightId): Future[Unit] = {
    if (fileStorage.finished(fightId)) {
      println(s"s[$fightId] Fight is finished")
      Future.unit
    } else {
      requestStepFight(fightId)
    }
  }

  private def requestStepFight(fightId: FightId): Future[Unit] = {
    val stepsToRun = fileStorage.readyToRun(fightId).headOption // TODO remove .headOption to allow parallel fight

    stepsToRun.map { step =>
      println(s"[$fightId] Starting fight step $step")
      val request = FightStepRequest(serviceLocator.serviceUrl(step), Config.FightResultTopic, fightId)

      publisher
        .publish(Config.FightRequestTopic, request)
        .map { messageId =>
          println(s"[${request.fightId}] Published request to run $step with id $messageId to topic ${Config.FightRequestTopic}")
          step -> StepStatus.Running(messageId)
        }
        .flatMap(runningSteps => fileStorage.startSteps(fightId, List(runningSteps)))
    }.getOrElse(Future.unit)
  }
}
