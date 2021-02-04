package com.thiefspin.worker

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.thiefspin.monitoring.Metrics
import com.thiefspin.worker.Worker.{Init, Work}
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

  var inits: Int = 0
  var jobs: Int = 0

  implicit val metrics: Metrics = new TestMetrics(inc => {
    if (inc == "Worker.Pool.test-group.Init.test-worker") {
      inits = inits + 1
    }
    if (inc == "Worker.Pool.test-group.Work.test-worker_2") {
      jobs = jobs + 1
    }
  })

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A Worker" should {

    "initialise itself" in {
      var jobDone = false
      val worker = createTestActor[Unit]("test-worker"){ () =>
        jobDone = true
      }
      val message = Init
      worker ! message
      eventually {
        assert(inits == 1)
        assert(!jobDone)
      }
    }

    "execute a job " in {
      var jobDone = false
      val worker = createTestActor[Unit]("test-worker_2") {
        () => jobDone = true
      }
      assert(!jobDone)
      val message = Work
      worker ! message
      eventually {
        assert(jobs == 1)
        assert(jobDone)
      }
    }

  }

  private def createTestActor[A](name: String)(job: Job[A]): ActorRef = {
    system.actorOf(Worker[A](
      "test-group",
      name)(job), name)
  }

}
