package org.grego.hive.util

import akka.actor.ActorSystem
import akka.stream.alpakka.googlecloud.pubsub.grpc.PubSubSettings
import akka.stream.alpakka.googlecloud.pubsub.grpc.scaladsl.{GrpcPublisher, GrpcSubscriber}
import akka.stream.{ActorMaterializer, Materializer}
import com.google.pubsub.v1.pubsub.{PushConfig, Subscription, Topic}
import org.grego.hive.Config._

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Preparation extends App {
  private implicit val system: ActorSystem = ActorSystem("preparator", GrpcConfiguration)
  private implicit val mat: Materializer = ActorMaterializer()
  private implicit val ec: ExecutionContextExecutor = system.dispatcher

  val settings = PubSubSettings(system)
  val publisher = GrpcPublisher(settings)
  val subscriber = GrpcSubscriber(settings)

  val AttackSubscription = s"projects/$ProjectId/subscriptions/attack"
  val RequestSubscription = s"projects/$ProjectId/subscriptions/request"
  val ResultSubscription = s"projects/$ProjectId/subscriptions/result"

  val AllTopics: List[Topic] = List(AttackTopic, FightRequestTopic, FightResultTopic).map(Topic(_))
  val AllSubscriptions: List[Subscription] = List(
    Subscription(AttackSubscription, AttackTopic, Some(PushConfig("http://localhost:7077/start"))),
    Subscription(RequestSubscription, FightRequestTopic, Some(PushConfig("http://localhost:7078"))),
    Subscription(ResultSubscription, FightResultTopic, Some(PushConfig("http://localhost:7077/result")))
  )

  Future
    .sequence(AllTopics.map(createTopic))
    .flatMap(_ => Future.sequence(AllSubscriptions.map(createSubscription)))
    .transformWith {
      case Success(_)  =>
        println("Done")
        shutdown()
      case Failure(ex) =>
        println(s"Failed! ${ex.getMessage}")
        shutdown()
    }

  def shutdown(): Future[Unit] = for {
    _ <- system.terminate()
    _ <- publisher.client.close()
    _ <- subscriber.client.close()
  } yield ()

  def createTopic(topic: Topic): Future[Unit] = {
    publisher.client.createTopic(topic).map {
      _ => println(s"Created topic ${topic.name}")
    }
  }

  def createSubscription(subscription: Subscription): Future[Unit] = {
    subscriber.client.createSubscription(subscription).map {
      _ => println(s"Created push subscription ${subscription.name} to topic ${subscription.topic}, url: ${subscription.pushConfig.map(_.pushEndpoint)}")
    }
  }
}
