package com.thiefspin.worker

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.{ExecutionContext, Future}

class JobExecutorSpec extends TestKit(ActorSystem("JobExecutorSpec"))
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with Eventually {

  override implicit def patienceConfig: PatienceConfig = {
    PatienceConfig(timeout = Span(3000, Seconds))
  }

  private implicit val ec: ExecutionContext = system.dispatcher

  "The JobExecutor " should {
    "create a collection of jobs " in {
      val jobs = List.fill(10)(Job(() => Future.successful(true)))
      val jobber = new JobExecutor[Boolean](jobs)
      assert(jobber.inProgress.isEmpty)
      assert(jobber.completed.isEmpty)
      assert(jobber.failed.isEmpty)
      assert(jobber.pending.size == 10)
      assert(!jobber.isDone)
    }

    "add a job " in {
      val jobs = List.fill(10)(Job(() => Future.successful(true)))
      val jobber = new JobExecutor[Boolean](jobs)
      assert(jobber.pending.size == 10)
      jobber.addJob(() => Future.successful(true))
      assert(jobber.pending.size == 11)
      assert(!jobber.isDone)
    }

    "execute all jobs " in {
      val jobs = List.fill(10)(Job(() => Future.successful(true)))
      val jobber = new JobExecutor[Boolean](jobs)
      assert(jobber.pending.size == 10)
      jobber.execute()
      eventually {
        assert(jobber.pending.isEmpty)
        assert(jobber.inProgress.isEmpty)
        assert(jobber.failed.isEmpty)
        assert(jobber.completed.size == 10)
        assert(jobber.isDone)
      }
    }

    "mark failed jobs " in {
      val jobs = List.fill(10)(Job(() => Future.failed[Boolean](new Exception())))
      val jobber = new JobExecutor[Boolean](jobs)
      assert(jobber.pending.size == 10)
      jobber.execute()
      eventually {
        assert(jobber.pending.isEmpty)
        assert(jobber.inProgress.isEmpty)
        assert(jobber.failed.size == 10)
        assert(jobber.completed.isEmpty)
        assert(jobber.isDone)
      }
    }

    "handle shutdowns gracefully " in {
      val jobs = List.fill(1000)(Job(() => Future.successful(true)))
      val jobber = new JobExecutor[Boolean](jobs)
      assert(jobber.pending.size == 1000)
      jobber.execute()
      system.terminate()
      eventually {
        assert(jobber.pending.isEmpty)
        assert(jobber.inProgress.isEmpty)
        assert(jobber.failed.isEmpty)
        assert(jobber.completed.size == 1000)
        assert(jobber.isDone)
      }
    }

    "handle critical failures " in {
      val jobs = List(Job(() => {
        system.terminate()
        Thread.sleep(2000)
        Future.successful(true)
      }))
      val jobber = new JobExecutor[Boolean](jobs)
      assert(jobber.pending.size == 1)
      jobber.execute()
      system.terminate()
      eventually {
        assert(jobber.pending.isEmpty)
        assert(jobber.inProgress.isEmpty)
        assert(jobber.failed.isEmpty)
        assert(jobber.completed.size == 1)
        assert(jobber.isDone)
      }
    }
  }

}
