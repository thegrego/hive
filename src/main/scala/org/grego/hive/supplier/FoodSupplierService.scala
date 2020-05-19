package org.grego.hive.supplier

import scala.concurrent.Future

class FoodSupplierService {
  def getFood(fightId: String): Future[Unit] = {
    println(s"[$fightId] Getting food supplier coordinates")
    println(s"[$fightId] Getting current fight situation")
    println(s"[$fightId] Sending troops for food")

    Future.successful(())
  }
}
