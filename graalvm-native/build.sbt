// give the user a nice default project!
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.13.4"

lazy val root = (project in file("."))
  .enablePlugins(NativeImagePlugin)
  .settings(
    name := "graalvm-native",
    version := "0.0.1",
    nativeImageOptions ++= List(
      "--initialize-at-build-time",
      "--no-fallback",
      "--no-server"
    ),
    nativeImageJvm := "graalvm",
    libraryDependencies ++= Seq(
      // CLI option parser
      "com.github.scopt" %% "scopt" % "3.7.1",
      // JSON parser
      "com.lihaoyi" %% "ujson" % "1.1.0",
      // HTTP client
      "com.softwaremill.sttp.client" %% "core" % "2.2.1",
      // Logging
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      // Test
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    ),
    Compile / mainClass := Some("com.example.Main"),
  )
