package com.thiefspin.kafka.subscribe

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.thiefspin.kafka.KafkaContext
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class KafkaSubscriber[A](ctx: KafkaContext, topic: String)(handler: A => Unit)
  (implicit system: ActorSystem) extends MessageSubscriber[A] with LazyLogging {

  private implicit val mat: ActorMaterializer = ActorMaterializer()

  def apply(): KafkaSubscriber[A] = {
    ctx.metrics.inc(s"Kafka.Subscriber.Constructor.$topic")
    consume()
    this
  }

  private def consume(): Future[Done] = {
    source()
      .runWith(
        sink()
      )
  }

  private def source(): Source[CommittableMessage[String, A], Consumer.Control] = {
    Consumer.committableSource[String, A](
      ctx.defaultConsumerSettings[String, A](),
      Subscriptions.topics(topic)
    )
  }

  private def sink(): Sink[CommittableMessage[String, A], Future[Done]] = {
    Sink.foreach[CommittableMessage[String, A]] { msg =>
      Try {
        onMessage(msg.record.value())
      } match {
        case Failure(ex) =>
          ctx.metrics.inc(s"Kafka.Subscriber.Failure.$topic")
          logger.error(s"Kafka consumer for topics: $topic finished with error: ${ex.getMessage}")
          apply()
        case Success(_) =>
          ctx.metrics.inc(s"Kafka.Subscriber.Success.$topic")
          logger.info(s"Kafka consumer for topics: $topic finished successfully")
          apply()
      }
    }
  }

  override def onMessage(message: A): Unit = {
    ctx.metrics.timeAndInc(s"Kafka.Subscriber.OnMessage.$topic") {
      handler(message)
    }
  }
}

object KafkaSubscriber {

  def apply[A](topic: String, config: Config)(handler: A => Unit)
    (implicit system: ActorSystem): MessageSubscriber[A] = {
    new KafkaSubscriber[A](new KafkaContext(config), topic)(handler)
  }

  def apply[A](topic: String, ctx: KafkaContext)(handler: A => Unit)
    (implicit system: ActorSystem): MessageSubscriber[A] = {
    new KafkaSubscriber[A](ctx, topic)(handler)
  }
}