package com.thiefspin.kafka.subscribe

import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Source
import com.thiefspin.kafka.KafkaContext
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

class KafkaSubscriberSource[A](ctx: KafkaContext, topics: Set[String])
  (implicit system: ActorSystem) extends MessageSubscriberSource[CommittableMessage[String, A]] with LazyLogging {

  override def source: Source[CommittableMessage[String, A], _] = {
    Consumer.committableSource[String, A](
      ctx.defaultConsumerSettings[String, A](),
      Subscriptions.topics(topics)
    )
  }
}

object KafkaSubscriberSource {

  def apply[A](topic: String, config: Config)
    (implicit system: ActorSystem): MessageSubscriberSource[CommittableMessage[String, A]] = {
    new KafkaSubscriberSource[A](new KafkaContext(config), Set(topic))
  }

  def apply[A](topic: String, ctx: KafkaContext)
    (implicit system: ActorSystem): MessageSubscriberSource[CommittableMessage[String, A]] = {
    new KafkaSubscriberSource[A](ctx, Set(topic))
  }
}