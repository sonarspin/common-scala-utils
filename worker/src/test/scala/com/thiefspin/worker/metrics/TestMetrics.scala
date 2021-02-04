package com.thiefspin.worker.metrics

import com.thiefspin.monitoring.StubMetrics

class TestMetrics(testInc: String => Unit) extends StubMetrics{

  override def inc(name: String): Unit = {
    super.inc(name)
    testInc(name)
  }

}
