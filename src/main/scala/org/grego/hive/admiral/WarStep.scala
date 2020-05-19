package org.grego.hive.admiral

sealed trait WarStep {
  def dependencies: List[WarStep]
}

object WarStep {
  sealed trait NoDependencyStep extends WarStep {
    def dependencies: List[WarStep] = List.empty
  }
  case object RetrieveFightingPlan extends NoDependencyStep
  case object RetrieveMedicine extends NoDependencyStep

  case object Fight extends WarStep {
    def dependencies: List[WarStep] = List(RetrieveFightingPlan, RetrieveMedicine)
  }

  case object RetrieveFood extends WarStep {
    def dependencies: List[WarStep] = List(Fight)
  }

  def all: List[WarStep] = List(RetrieveFightingPlan, RetrieveMedicine, Fight, RetrieveFood)
}