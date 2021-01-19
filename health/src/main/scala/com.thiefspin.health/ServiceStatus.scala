package com.thiefspin.health

sealed trait ServiceStatus {
  val service: String
  val isOk: Boolean
  def critical: Boolean = true
}

case class HealthyStatus(service: String, status: String = "Healthy") extends ServiceStatus {
  val isOk = true
}

case class UnhealthyStatus(service: String, status: String = "Not healthy", override val critical: Boolean = true) extends ServiceStatus {
  val isOk = false
}

case class CompositeStatus(service: String, statuses: List[ServiceStatus]) extends ServiceStatus {
  val isOk: Boolean = statuses.forall(status => status.isOk || !status.critical)
}