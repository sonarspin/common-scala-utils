# Kafka

The model contains some code to ease the use of Kafka in Scala applications. 

It does not re-invent the wheel but rather just adds an ease of use wrapper for existing Kafka implementations
and adds some nice to have features for example metrics.

## Usage
### Adding a dependency
The binary is not just yet being published via CI/CD so manual building of the binary for your project is still required.

### Publishing a message
```scala
    //These values need to be set up
    implicit val system = ActorSystem("kafka-test") //Used for coordinated shutdowns
    implicit val ec = system.dispatcher //Only for publishF
    val config: Config = ConfigFactory.load()
    val ctx = new KafkaContext(config)

    final case class Message(id: Long, text: String)
    val topic: String = "test-topic"
    val message = Message(1L, "some text")
    
    val publisher: MessagePublisher[Message] = KafkaPublisher[Message](ctx)
    
    //Simple publish with default callback
    publisher.publish(topic, message.id.toString, message)
    
    //Publisher will return Future[RecordMetaData]
    publisher.publishF(topic, message.id.toString, message)
      .map{ metaData: RecordMetadata =>
      //handle record meta data
    }
```

### Subscribing
```scala
 val subscriber: MessageSubscriber[Message] = KafkaSubscriber[Message](topic, ctx) { message: Message =>
  //Do whatever with message
}
```

### Metrics
By default the library just stubs the metric calls. 
If you want to add real metrics to your application check out the example below.

```scala
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

  val ctx = new KafkaContext(config)(customMetrics)
```