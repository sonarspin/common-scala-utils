package com.thiefspin.kafka

import akka.actor.ActorSystem
import com.thiefspin.kafka.publish.{KafkaPublisher, MessagePublisher}
import com.thiefspin.kafka.subscribe.{KafkaSubscriber, MessageSubscriber}
import com.thiefspin.monitoring.Metrics
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.producer.RecordMetadata

import scala.concurrent.{ExecutionContext, Future}

class KafkaPublisherSpec {

  //These values need to be set up
  implicit val system = ActorSystem("kafka-test")
  implicit val ec = system.dispatcher
  val config: Config = ConfigFactory.load()

  //Before setting up the KafkaContext

  class CustomMetrics() extends Metrics {
    override def inc(name: String): Unit = ???
    override def time[A](name: String)(f: => A): A = ???
    override def timeAsync[A](name: String)(f: => Future[A])(implicit ec: ExecutionContext): Future[A] = ???
    override def timeAndInc[A](name: String)(f: => A): A = ???
    override def timeAndIncAsync[A](name: String)(f: => Future[A])(implicit ec: ExecutionContext): Future[A] = ???
    override def incBy(name: String, value: Long): Unit = ???
    override def gauge(name: String, value: Long): Unit = ???
    override def recordTime(name: String, value: Long): Unit = ???
  }

  implicit val customMetrics = new CustomMetrics()

//  implicit val customMetrics = new Metrics {
//    override def inc(name: String): Unit = ???
//    override def time[A](name: String)(f: => A): A = ???
//    override def timeAsync[A](name: String)(f: => Future[A])(implicit ec: ExecutionContext): Future[A] = ???
//    override def timeAndInc[A](name: String)(f: => A): A = ???
//    override def timeAndIncAsync[A](name: String)(f: => Future[A])(implicit ec: ExecutionContext): Future[A] = ???
//    override def incBy(name: String, value: Long): Unit = ???
//    override def gauge(name: String, value: Long): Unit = ???
//    override def recordTime(name: String, value: Long): Unit = ???
//  }

  val ctx = new KafkaContext(config)(customMetrics)

  final case class Message(id: Long, text: String)

  val topic: String = "test-topic"
  val message = Message(1L, "some text")

  val publisher: MessagePublisher[Message] = KafkaPublisher[Message](ctx)

  //Simple publish with default callback
  publisher.publish(topic, message.id.toString, message)

  //Publisher will return Future[RecordMetaData]
  publisher.publishF(topic, message.id.toString, message).map { metaData: RecordMetadata =>
    //handle record meta data
  }

  val subscriber: MessageSubscriber[Message] = KafkaSubscriber[Message](topic, ctx) { message: Message =>
    //Do whatever with message
  }

}
