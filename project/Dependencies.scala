import sbt._

object Organisations {
  lazy val typesafeAkka = "com.typesafe.akka"
  lazy val typesafePlay = "com.typesafe.play"
}

object Versions {
  lazy val akkaVersion = "2.6.12"
}

object Dependencies {

  lazy val loggingDependencies: Seq[ModuleID] = Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  lazy val kafkaDependencies: Seq[ModuleID] = Seq(
    "org.apache.kafka" % "kafka-streams" % "2.2.0",
    Organisations.typesafeAkka %% "akka-stream-kafka" % "1.1.0"
  )

  lazy val testDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.2" % Test
  )

  lazy val configDependencies: Seq[ModuleID] = Seq(
    "com.typesafe" % "config" % "1.4.0"
  )

  lazy val jsonDependencies: Seq[ModuleID] = Seq(
    Organisations.typesafePlay %% "play-json" % "2.7.4",
    Organisations.typesafePlay %% "play-json-joda" % "2.7.4"
  )

  lazy val akkaDependencies: Seq[ModuleID] = Seq(
    Organisations.typesafeAkka %% "akka-actor" % Versions.akkaVersion,
    Organisations.typesafeAkka %% "akka-testkit" % Versions.akkaVersion % Test
  )
}
