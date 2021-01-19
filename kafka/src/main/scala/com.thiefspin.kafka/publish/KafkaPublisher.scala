package com.thiefspin.kafka.publish

import akka.actor.{ActorSystem, CoordinatedShutdown}
import com.thiefspin.kafka.KafkaContext
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import scala.concurrent.{Future, Promise}

class KafkaPublisher[A](ctx: KafkaContext)(implicit system: ActorSystem) extends MessagePublisher[A] with LazyLogging {

  private val producer = new KafkaProducer[String, A](ctx.producerProperties)

  /**
   * Important to do a coordinated shutdown to ensure messages are not lost in the Kafka buffer.
   */

  CoordinatedShutdown(system).addJvmShutdownHook {
    () => producer.close()
  }

  /**
   * Publish a message to Kafka in Avro format.
   *
   * @param topic   Kafka topic
   * @param key     Partition key
   * @param message Message
   */

  override def publish(topic: String, key: String, message: A): Unit = {
    val record = new ProducerRecord(topic, key, message)
    producer.send(record, callback())
  }

  /**
   * Publish a message to Kafka in Avro format. Returns Future of RecordMetaData
   *
   * @param topic   Kafka topic
   * @param key     Partition key
   * @param message Message
   * @return [[org.apache.kafka.clients.producer.RecordMetadata]]
   */

  override def publishF(topic: String, key: String, message: A): Future[RecordMetadata] = {
    val record = new ProducerRecord(topic, key, message)
    val promise = Promise[RecordMetadata]
    producer.send(record, callbackF(promise))
    promise.future
  }

  private def callback(): Callback = {
    new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        if (exception != null) {
          ctx.metrics.inc(s"Kafka.Publisher.Failure.${metadata.topic()}")
          logger.error(s"Error producing message to Kafka. Cause: ${exception.getMessage}")
        }
        else {
          ctx.metrics.inc(s"Kafka.Publisher.Success.${metadata.topic()}")
          logger.info(s"Successfully produced message to Kafka. $metadata")
        }
      }
    }
  }

  private def callbackF(promise: Promise[RecordMetadata]): Callback = {
    new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        if (exception != null) {
          ctx.metrics.inc(s"Kafka.Publisher.Failure.${metadata.topic()}")
          logger.error(s"Error producing message to Kafka. Cause: ${exception.getMessage}")
          promise.failure(exception)
        }
        else {
          ctx.metrics.inc(s"Kafka.Publisher.Success.${metadata.topic()}")
          logger.info(s"Successfully produced message to Kafka. $metadata")
          promise.success(metadata)
        }
      }
    }
  }
}

object KafkaPublisher {

  def apply[A](config: Config)(implicit system: ActorSystem): MessagePublisher[A] = {
    new KafkaPublisher[A](new KafkaContext(config))
  }

  def apply[A](context: KafkaContext)(implicit system: ActorSystem): MessagePublisher[A] = {
    new KafkaPublisher[A](context)
  }
}