package com.thiefspin.kafka

import akka.kafka.ConsumerSettings
import com.thiefspin.monitoring.{Metrics, StubMetrics}

import java.util.Properties
import com.typesafe.config.{Config, ConfigValueType}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._

/**
 * Defines global application context for all things Kafka.
 *
 * @param config Typesafe Config
 */


class KafkaContext(config: Config)(implicit val metrics: Metrics = new StubMetrics()) extends LazyLogging {

  /**
   * Converts producer config into [[java.util.Properties]].
   * Expects config to be at key 'kafka.producer' and 'kafka.bootstrap.servers'
   */

  val producerProperties: Properties = {
    val properties = new Properties()
    properties.put("bootstrap.servers", config.getString("kafka.bootstrap.servers"))
    config.getConfig("kafka.producer")
      .entrySet()
      .asScala
      .foreach { c =>
        c.getValue.valueType() match {
          case ConfigValueType.NUMBER => properties.put(c.getKey, config.getInt(s"kafka.producer.${c.getKey}").toString)
          case ConfigValueType.BOOLEAN => properties.put(c.getKey, config.getBoolean(s"kafka.producer.${c.getKey}").toString)
          case ConfigValueType.STRING => properties.put(c.getKey, config.getString(s"kafka.producer.${c.getKey}"))
          case _ => logger.warn(s"Unknown config passed to to Kafka $c")
        }
      }
    properties
  }

  /**
   * Default consumer settings. Reads from application.conf
   * @tparam K
   * @tparam V
   * @return [[akka.kafka.ConsumerSettings]]
   */

  def defaultConsumerSettings[K, V](): ConsumerSettings[K, V] =
    ConsumerSettings[K, V](config.getConfig("kafka.consumer"), None, None)

}