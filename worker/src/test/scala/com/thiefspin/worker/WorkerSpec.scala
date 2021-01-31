package com.thiefspin.worker

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.thiefspin.monitoring.Metrics
import com.thiefspin.worker.Worker.Init
import com.thiefspin.worker.metrics.TestMetrics
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class WorkerSpec extends TestKit(ActorSystem("WorkerSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with Eventually {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A Worker" should {

    "initialise itself" in {
      var inits: Int = 0
      implicit val metrics: Metrics = new TestMetrics(inc => {
        if (inc == "Worker.Pool.test-group.Init.test-worker") {
          inits = inits + 1
        }
      })
      val worker = system.actorOf(Worker[String]("test-group", "test-worker")(() => "job done"), "test-worker")
      val message = Init
      worker ! message
      expectMsg(message)
      eventually {
        assert(inits == 1)
      }
    }

  }

}
