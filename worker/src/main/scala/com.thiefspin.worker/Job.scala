package com.thiefspin.worker

trait Job[A] {
  def doJob(): A
}
