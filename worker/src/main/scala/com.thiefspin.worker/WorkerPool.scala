package com.thiefspin.worker

import akka.actor.{ActorRef, ActorSystem}
import com.thiefspin.monitoring.Metrics
import com.thiefspin.worker.Worker.{Init, Work}

class WorkerPool[A](name: String, numWorkers: Int)(job: Job[A])
  (implicit system: ActorSystem, metrics: Metrics) {

  val workers: List[ActorRef] = createWorkers()

  def work(): Unit = {
    workers.foreach(_ ! Work)
  }

  private def createWorkers(): List[ActorRef] = {
    (1 to numWorkers).map { i =>
      init {
        system.actorOf(Worker(name, s"$name-$i")(job))
      }
    }.toList
  }

  private def init(ref: ActorRef): ActorRef = {
    ref ! Init
    ref
  }

}
