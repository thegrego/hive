package org.grego.hive

import com.typesafe.config.{ConfigFactory, Config => LightbendConfig}

object Config {
  val ProjectId: String = "hive"

  val AttackTopic: String = s"projects/$ProjectId/topics/hive-attack-request"
  val FightRequestTopic: String = s"projects/$ProjectId/topics/hive-fight-request"
  val FightResultTopic: String = s"projects/$ProjectId/topics/hive-fight-result"

  val GrpcConfiguration: LightbendConfig = {
    val str =
      """alpakka.google.cloud.pubsub.grpc {
        |  host = "localhost"
        |  port = 8085
        |  rootCa = "none"
        |  callCredentials = "none"
        |}""".stripMargin

    ConfigFactory.parseString(str)
  }
}
