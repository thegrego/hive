package org.grego.hive.admiral

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

// TODO this code is pure evil, can cause nightmares. dont try to repeat it at home
class FileStorageService {
  type FightId = String
  private val state = TrieMap.empty[FightId, Map[WarStep, StepStatus]]

  def start(fightId: FightId): Future[Unit] = {
    val steps = WarStep.all.map(_ -> StepStatus.Waiting).toMap
    state.update(fightId, steps)
    Future.unit
  }

  def startSteps(fightId: FightId, runningSteps: List[(WarStep, StepStatus.Running)]): Future[Unit] = {
    runningSteps.foreach { case (step, status) =>
      val fightState = fight(fightId)
      state.update(fightId, fightState.updated(step, status))
    }
    Future.unit
  }

  def finishStep(fightId: FightId, requestMessageId: String, status: StepStatus): Future[Unit] = {
    val fightState = fight(fightId)

    val step =
      fightState
        .collectFirst { case (step, StepStatus.Running(id)) if id == requestMessageId => step }
        .getOrElse(sys.error(s"Step with $requestMessageId not found")) // TODO proper error

    state.update(fightId, fightState.updated(step, status))

    Future.unit
  }

  def finished(fightId: FightId): Boolean = {
    fight(fightId).forall { case (_, status) => status == StepStatus.Done }
  }

  private def fight(fightId: FightId): Map[WarStep, StepStatus] =
    state.getOrElse(fightId, sys.error(s"State with $fightId not found")) // TODO proper error

  def readyToRun(fightId: FightId): List[WarStep] = {
    def status(step: WarStep): StepStatus =
      fight(fightId)
        .getOrElse(step, sys.error(s"Unexpected step: $step")) // TODO proper error

    def depsReady(step: WarStep): Boolean =
      step.dependencies.forall(status(_) == StepStatus.Done)

    fight(fightId)
      .collect { case (step, status) if status == StepStatus.Waiting && depsReady(step) => step }
      .toList
  }
}
