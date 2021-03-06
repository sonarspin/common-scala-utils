package com.thiefspin.worker

import akka.actor.{Actor, ActorLogging, Props}
import com.thiefspin.monitoring.Metrics
import com.thiefspin.worker.Worker.{Init, Work}

class Worker[A](groupIdentifier: String, name: String)(job: Job[A])
               (implicit metrics: Metrics) extends Actor with ActorLogging {

  override def receive: Receive = {
    case Init =>
      log.info(s"Worker $name ready and waiting for work!")
      metrics.inc(s"Worker.Pool.$groupIdentifier.Init.$name")
    case Work => {
      log.info(s"Worker $name executing job")
      metrics.inc(s"Worker.Pool.$groupIdentifier.Work.$name")
      job.doJob()
    }
  }
}

object Worker {

  def apply[A](groupIdentifier: String, name: String)(job: Job[A])
              (implicit metrics: Metrics): Props = {
    Props(new Worker(groupIdentifier: String, name)(job))
  }

  final case object Init

  final case object Work

}
