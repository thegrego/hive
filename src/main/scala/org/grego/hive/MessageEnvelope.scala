package org.grego.hive

import java.nio.charset.StandardCharsets
import java.util.Base64

import org.grego.hive.MessageEnvelope.Message
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.{Decoder, Encoder}

case class MessageEnvelope(message: Message) {
  def content[T: Decoder]: T = {
    val bytes = Base64.getDecoder.decode(message.data)
    val string = new String(bytes, StandardCharsets.UTF_8)

    parse(string)
      .flatMap(_.as[T])
      .getOrElse(sys.error(s"Couldn't decode JSON: $string"))
  }
}

object MessageEnvelope {
  case class Message(data: String, messageId: String)

  implicit val MessageDecoder: Decoder[Message] = deriveDecoder[Message]
  implicit val MessageEncoder: Encoder[Message] = deriveEncoder[Message]
  implicit val MessageEnvelopeDecoder: Decoder[MessageEnvelope] = deriveDecoder[MessageEnvelope]
  implicit val MessageEnvelopeEncoder: Encoder[MessageEnvelope] = deriveEncoder[MessageEnvelope]
}
