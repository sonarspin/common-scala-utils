package com.thiefspin.health

import scala.concurrent.ExecutionContext

trait HealthCheck {
  def apply()(implicit executionContext: ExecutionContext): ServiceStatus
}