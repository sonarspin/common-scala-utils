package com.thiefspin.monitoring

import scala.concurrent.{ExecutionContext, Future}

trait Metrics {

  /**
   * Increments a metric counter by name.
   *
   * @param name Metric name
   */

  def inc(name: String): Unit

  /**
   * Records how long a function takes to complete.
   *
   * @param name Metric name
   * @param f    Function to time
   * @tparam A
   * @return
   */

  def time[A](name: String)(f: => A): A

  /**
   * Records how long an asynchronous function takes to complete it's future.
   *
   * @param name Metric name
   * @param f    Function to time
   * @param ec   Execution Context
   * @tparam A
   * @return
   */

  def timeAsync[A](name: String)(f: => Future[A])(implicit ec: ExecutionContext): Future[A]

  /**
   * Records how long a function takes to complete.
   * Also increments a counter.
   *
   * @param name Metric name
   * @param f    Function to time
   * @tparam A
   * @return
   */

  def timeAndInc[A](name: String)(f: => A): A

  /**
   * Records how long an asynchronous function takes to complete it's future.
   * Also increments a counter.
   *
   * @param name Metric name
   * @param f    Function to time
   * @param ec   Execution Context
   * @tparam A
   * @return
   */

  def timeAndIncAsync[A](name: String)(f: => Future[A])(implicit ec: ExecutionContext): Future[A]

  /**
   * Increments a metric counter by name and specified value
   *
   * @param name  Metric name
   * @param value Value to increment by.
   */

  def incBy(name: String, value: Long): Unit

  /**
   * Gauges a given metric by a given value
   *
   * @param name  Metric name
   * @param value Value to increment by
   */

  def gauge(name: String, value: Long): Unit

  /**
   * Records a time value for a given metric.
   *
   * @param name  Metric name
   * @param value Value to increment by
   */

  def recordTime(name: String, value: Long): Unit

}
