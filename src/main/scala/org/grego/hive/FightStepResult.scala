package org.grego.hive

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class FightStepResult(requestMessageId: String, fightId: String, error: Option[String] = None)

object FightStepResult {
  def apply(requestMessageId: String, fightId: String, error: String): FightStepResult =
    FightStepResult(requestMessageId, fightId, Some(error))

  implicit val FightResultDecoder: Decoder[FightStepResult] = deriveDecoder[FightStepResult]
  implicit val FightResultEncoder: Encoder[FightStepResult] = deriveEncoder[FightStepResult]
}