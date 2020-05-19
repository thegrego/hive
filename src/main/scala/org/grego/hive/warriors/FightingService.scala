package org.grego.hive.warriors

import scala.concurrent.Future

class FightingService {
  def fight(fightId: String): Future[Unit] = {
    println(s"[$fightId] Calling more warriors")
    println(s"[$fightId] Calling air support")
    println(s"[$fightId] Attack!")

    Future.successful(())
  }
}
