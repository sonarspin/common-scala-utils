package com.thiefspin.kafka.publish

import org.apache.kafka.clients.producer.RecordMetadata

import scala.concurrent.Future

trait MessagePublisher[A] {

  def publish(topic: String, key: String, message: A): Unit
  def publishF(topic: String, key: String, message: A): Future[RecordMetadata]
}