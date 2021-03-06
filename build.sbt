name := "TwitterBot"

version := "0.1"

scalaVersion := "2.13.4"

val akkaVersion = "2.6.6"
val akkaHttpVersion = "10.2.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
)

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.danielasfregola" %% "twitter4s" % "6.2"