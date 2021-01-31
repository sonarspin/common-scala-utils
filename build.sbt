import Dependencies._

lazy val root = (project in file("."))
  .settings(
    name := "common-scala-utils"
  ).aggregate(
  monitoring,
  health,
  kafka, worker
)

lazy val kafka = project
  .settings(
    name := "kafka",
    commonSettings,
    libraryDependencies ++=
      loggingDependencies ++
        kafkaDependencies ++
        configDependencies ++
        testDependencies
  ).dependsOn(health, monitoring)

lazy val worker = project
  .settings(
    name := "worker",
    commonSettings,
    libraryDependencies ++=
      akkaDependencies ++
        loggingDependencies ++
        testDependencies
  ).dependsOn(monitoring)

lazy val health = project
  .settings(
    name := "health",
    commonSettings
  )

lazy val monitoring = project
  .settings(
    name := "monitoring",
    commonSettings
  )

lazy val commonSettings = Seq(
  organization := "thiefspin",

  scalaVersion := "2.13.4",

  crossScalaVersions := Seq("2.12.13", "2.13.4"),

  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
  fork in Test := true,
  javaOptions in Test += "-Duser.timezone=UTC"
)
