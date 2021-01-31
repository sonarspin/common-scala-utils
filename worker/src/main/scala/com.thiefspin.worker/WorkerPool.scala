package com.thiefspin.worker

import akka.actor.{ActorSystem, Props}
import com.thiefspin.monitoring.Metrics

class WorkerPool[A, B](name: String, numWorkers: Int)(job: () => A)(implicit system: ActorSystem, metrics: Metrics) {

  init()

  private def init(): Unit = {
    (0 to numWorkers).map { i =>
      system.actorOf(
        Props(
          new Worker[A](name, s"$name-$i")(job)
        )
      )
    }
  }

}
