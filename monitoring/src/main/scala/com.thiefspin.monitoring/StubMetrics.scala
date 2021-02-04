package com.thiefspin.monitoring

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class StubMetrics extends Metrics with LazyLogging {

  override def inc(name: String): Unit = {
    logger.info(s"Increment metric: $name")
  }

  override def time[A](name: String)(f: => A): A = {
    logger.info(s"Time metric: $name")
    f
  }

  override def timeAsync[A](name: String)(f: => Future[A])(implicit ec: ExecutionContext): Future[A] = {
    logger.info(s"TimeAsync metric: $name")
    f
  }

  override def timeAndInc[A](name: String)(f: => A): A = {
    logger.info(s"TimeAndInc metric: $name")
    f
  }

  override def timeAndIncAsync[A](name: String)(f: => Future[A])(implicit ec: ExecutionContext): Future[A] = {
    logger.info(s"TimeAndIncAsync metric: $name")
    f
  }

  override def incBy(name: String, value: Long): Unit = {
    logger.info(s"Increment by $value metric: $name")
  }

  override def gauge(name: String, value: Long): Unit = {
    logger.info(s"Gauge by $value metric: $name")
  }

  override def recordTime(name: String, value: Long): Unit = {
    logger.info(s"Record time by $value metric: $name")
  }
}
