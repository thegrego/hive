package org.grego.hive.admiral

import org.grego.hive.admiral.WarStep.{Fight, RetrieveFightingPlan, RetrieveFood, RetrieveMedicine}

class ServiceLocator {
  def serviceUrl(fightStep: WarStep): String = fightStep match {
    case RetrieveFightingPlan => "http://localhost:8000"
    case RetrieveMedicine     => "http://localhost:8001"
    case Fight                => "http://localhost:8002"
    case RetrieveFood         => "http://localhost:8003"
  }
}
