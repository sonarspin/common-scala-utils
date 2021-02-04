package com.thiefspin.worker

import akka.actor.ActorSystem
import com.thiefspin.worker.JobStatus._
import com.typesafe.scalalogging.LazyLogging

import java.util.UUID
import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class JobExecutor[A](jobs: List[Job[A]])
  (implicit system: ActorSystem, ec: ExecutionContext) extends LazyLogging {

  system.registerOnTermination { () =>
    onShutdown()
  }

  private val jobList: TrieMap[String, Job[A]] = new TrieMap[String, Job[A]]()
    .addAll(jobs.map(UUID.randomUUID().toString -> _))

  def total: Int = jobs.size

  def completed: List[Job[A]] = filter(jobList, COMPLETED)

  def inProgress: List[Job[A]] = filter(jobList, IN_PROGRESS)

  def pending: List[Job[A]] = filter(jobList, PENDING)

  def failed: List[Job[A]] = filter(jobList, FAILED)

  def isDone: Boolean = total == completed.size + failed.size

  def execute(): Unit = {
    jobList.foreach { case (uuid, job) =>
      logger.info(s"Starting execution of job $uuid")
      ec.execute(() => {
        replace(uuid, job.copy(status = IN_PROGRESS))
        job.task.apply().onComplete {
          case Failure(ex) =>
            logger.error(s"Job $uuid failed with reason: ${ex.getMessage}")
            replace(uuid, job.copy(status = FAILED))
          case Success(value) =>
            logger.info(s"Job execution $uuid completed successfully")
            replace(uuid, job.copy(status = COMPLETED, result = Some(value)))
        }
      })
    }
  }

  def addJob(job: Job[A]): JobExecutor[A] = {
    jobList.addOne(UUID.randomUUID().toString, job)
    this
  }

  def addJob(job: () => Future[A]): JobExecutor[A] = {
    jobList.addOne(UUID.randomUUID().toString, Job(job))
    this
  }

  private def replace(uuid: String, job: Job[A]): Unit = {
    jobList.remove(uuid)
    jobList.addOne(uuid, job)
  }

  private def filter(m: TrieMap[String, Job[A]], status: JobStatus): List[Job[A]] = {
    m.filter(_._2.status == status).values.toList
  }

  private def onShutdown(): Unit = {
    if (!isDone) {
      system.scheduler.scheduleAtFixedRate(
        0.seconds,
        1.second) {
        () => logger.info(s"Waiting for ${total - (completed.size + failed.size)} jobs to complete")
      }
    }
  }
}
