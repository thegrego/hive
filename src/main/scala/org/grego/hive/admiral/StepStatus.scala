package org.grego.hive.admiral

sealed trait StepStatus

object StepStatus {
  case object Waiting extends StepStatus
  case class Running(messageId: String) extends StepStatus
  case object Done extends StepStatus
  case object Failed extends StepStatus
  case object Skipped extends StepStatus
}
