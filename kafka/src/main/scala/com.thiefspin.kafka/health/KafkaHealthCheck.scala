package com.thiefspin.kafka.health

import akka.actor.ActorSystem
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import com.thiefspin.kafka.KafkaContext
import com.thiefspin.health._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._

class KafkaHealthCheck(ctx: KafkaContext, topic: String = "ping", attempts: Int = 3, timeout: Duration = 5.seconds)
  (implicit system: ActorSystem) extends HealthCheck {

  private val producer = new KafkaProducer[String, Array[Byte]](ctx.producerProperties)

  override def apply()(implicit executionContext: ExecutionContext): ServiceStatus = {
    Try(check()) match {
      case Failure(e) => UnhealthyStatus("Kafka", s"Sending message to Kafka failed: ${e.getMessage}")
      case Success(value) =>
        val responseTimes = Await.result(value, timeout)
        HealthyStatus("Kafka", s"Brokers response times (ms): ${responseTimes.mkString(", ")}.")
    }
  }

  private def check()(implicit executionContext: ExecutionContext): Future[List[Long]] = {
    Future.sequence {
      (1 to attempts)
        .map(_ => ping().future).toList
    }
  }

  private def ping(): Promise[Long] = {
    val before = System.currentTimeMillis()
    val producerRecord = new ProducerRecord(topic, "HealthCheck", "ping".getBytes)
    val promise = Promise[Long]()
    producer.send(producerRecord, new Callback() {
      def onCompletion(metadata: RecordMetadata, e: Exception) {
        val after = System.currentTimeMillis()
        if (e != null) {
          promise.failure(e)
        } else {
          promise.success(after - before)
        }
      }
    })
    promise
  }
}