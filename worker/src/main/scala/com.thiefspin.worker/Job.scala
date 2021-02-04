package com.thiefspin.worker

import com.thiefspin.worker.JobStatus.PENDING

import scala.concurrent.Future

final case class Job[A](
  task: () => Future[A],
  result: Option[A] = None,
  status: JobStatus = PENDING,
)
