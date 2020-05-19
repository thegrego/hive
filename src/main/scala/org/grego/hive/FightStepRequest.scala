package org.grego.hive

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class FightStepRequest(requestUrl: String, resultTopic: String, fightId: String)

object FightStepRequest {
  implicit val FightStepRequestDecoder: Decoder[FightStepRequest] = deriveDecoder[FightStepRequest]
  implicit val FightStepRequestEncoder: Encoder[FightStepRequest] = deriveEncoder[FightStepRequest]
}