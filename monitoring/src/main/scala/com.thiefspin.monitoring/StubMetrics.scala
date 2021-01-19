package com.thiefspin.monitoring
import scala.concurrent.{ExecutionContext, Future}

class StubMetrics extends Metrics {

  override def inc(name: String): Unit = {}
  override def time[A](name: String)(f: => A): A = f
  override def timeAsync[A](name: String)(f: => Future[A])(implicit ec: ExecutionContext): Future[A] = f
  override def timeAndInc[A](name: String)(f: => A): A = f
  override def timeAndIncAsync[A](name: String)(f: => Future[A])(implicit ec: ExecutionContext): Future[A] = f
  override def incBy(name: String, value: Long): Unit = {}
  override def gauge(name: String, value: Long): Unit = {}
  override def recordTime(name: String, value: Long): Unit = {}
}
