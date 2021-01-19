package com.thiefspin.kafka.subscribe

import akka.stream.scaladsl.Source

trait MessageSubscriberSource[A] {

  def source: Source[A, _]

}