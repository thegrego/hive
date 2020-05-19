package org.grego.hive

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.googlecloud.pubsub.grpc.scaladsl.GooglePubSub
import akka.stream.scaladsl.{Sink, Source}
import com.google.protobuf.ByteString
import com.google.pubsub.v1.pubsub.{PublishRequest, PubsubMessage}
import io.circe.Encoder
import io.circe.syntax._

import scala.concurrent.{ExecutionContext, Future}

class Publisher(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {
  def publish[T: Encoder](topic: String, message: T): Future[String] = {
    val pubSubMessage = PubsubMessage(ByteString.copyFromUtf8(message.asJson.noSpaces))

    Source
      .single(PublishRequest(topic, Seq(pubSubMessage)))
      .via(GooglePubSub.publish(parallelism = 1))
      .runWith(Sink.head)
      .map(_.messageIds.head) // TODO .head
  }
}
