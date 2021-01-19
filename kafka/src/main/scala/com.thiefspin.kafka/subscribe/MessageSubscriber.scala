package com.thiefspin.kafka.subscribe

trait MessageSubscriber[A] {

  def onMessage(message: A): Unit

}