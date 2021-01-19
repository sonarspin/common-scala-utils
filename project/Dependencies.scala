import sbt._

object Dependencies {

  lazy val loggingDependencies: Seq[ModuleID] = Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  lazy val kafkaDependencies: Seq[ModuleID] = Seq(
    "org.apache.kafka" % "kafka-streams" % "2.2.0",
    "com.typesafe.akka" %% "akka-stream-kafka" % "1.1.0"
  )

  lazy val testDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.2" % Test
  )

  lazy val configDependencies: Seq[ModuleID] = Seq(
    "com.typesafe" % "config" % "1.4.0"
  )

  lazy val jsonDependencies: Seq[ModuleID] = Seq(
    "com.typesafe.play" %% "play-json" % "2.7.4",
    "com.typesafe.play" %% "play-json-joda" % "2.7.4"
  )
}
