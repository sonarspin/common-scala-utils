package com.thiefspin.worker

import com.sonarspin.scala.lang.Enum

sealed trait JobStatus extends JobStatus.Value

object JobStatus extends Enum[JobStatus] {
  override val values: Seq[JobStatus] = Seq(
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
  )

  case object PENDING extends JobStatus

  case object IN_PROGRESS extends JobStatus

  case object COMPLETED extends JobStatus

  case object FAILED extends JobStatus

}
