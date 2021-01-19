import Dependencies._

lazy val root = (project in file("."))
  .settings(
    name := "common-scala-utils"
  ).aggregate(kafka)

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

  crossScalaVersions := Seq("2.11.12", "2.12.12", "2.13.4"),

  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
  fork in Test := true,
  javaOptions in Test += "-Duser.timezone=UTC"
)
