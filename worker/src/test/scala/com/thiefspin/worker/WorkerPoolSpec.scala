package com.thiefspin.worker

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.thiefspin.worker.metrics.TestMetrics
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class WorkerPoolSpec extends TestKit(ActorSystem("WorkerPoolSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with Eventually{

  var inits: Int = 0
  var jobMetrics: Int = 0

  implicit val metrics: TestMetrics = new TestMetrics((inc) => {
    if (inc.startsWith("Worker.Pool.test-pool-a.Init.test-pool-a")) {
      inits = inits + 1
    }
    if (inc.startsWith("Worker.Pool.test-pool-b.Work.test-pool-b")) {
      jobMetrics = jobMetrics + 1
    }
  })

  "A Worker pool " should {
    "initialise the correct number of workers " in {
      var jobsDone: Int = 0
      val job = new Job[Unit] {
        override def doJob(): Unit = {
          jobsDone = jobsDone + 1
        }
      }
      val pool = new WorkerPool[Unit]("test-pool-a", 10)(job)
      assert(jobsDone == 0)
      assert(pool.workers.size == 10)
      eventually {
        assert(inits == 10)
      }
    }

    "have all it's workers do their job " in {
      var jobsDone: Int = 0
      val job = new Job[Unit] {
        override def doJob(): Unit = {
          jobsDone = jobsDone + 1
        }
      }
      val pool = new WorkerPool[Unit]("test-pool-b", 10)(job)
      assert(jobsDone == 0)
      assert(pool.workers.size == 10)
      pool.work()
      eventually {
        assert(jobsDone == 10)
        assert(jobMetrics == 10)
      }
    }
  }
}
