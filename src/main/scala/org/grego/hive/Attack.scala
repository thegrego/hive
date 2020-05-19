package org.grego.hive

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Attack(fightId: String)

object Attack {
  implicit val StartDecoder: Decoder[Attack] = deriveDecoder[Attack]
  implicit val StartEncoder: Encoder[Attack] = deriveEncoder[Attack]
}
