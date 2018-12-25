import sbt._

object Dependencies {

  object version {
    val sql = "3.8.7"
    val slick = "3.2.1"
    val logback = "1.2.3"
    val logging = "3.9.0"
    val jodaTime = "2.10.1"
    val akkaHttp = "10.0.9"
    val akka = "2.5.19"
    val akkaSession = "0.5.6"
  }

  val configTypesafe = Seq (
    "com.typesafe" % "config" % "1.3.2"
  )
  
  val sql = Seq(
    "com.h2database" % "h2" % "1.4.192",
    "com.typesafe.slick" %% "slick" % version.slick,
    "org.slf4j" % "slf4j-simple" % "1.7.12",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0",
  )

  val scalaTest = Seq (
    "org.scalactic" %% "scalactic" % "3.0.4",
    "org.scalatest" %% "scalatest" % "3.0.4",
    "org.scalacheck" %% "scalacheck" % "1.13.4"

  )

  val logging = Seq (
    "ch.qos.logback" % "logback-classic" % version.logback,
    "com.typesafe.scala-logging" %% "scala-logging" % version.logging
  )

  val jodaTime = Seq (
    "joda-time" % "joda-time" % version.jodaTime
  )

  val akka = Seq(
    "com.typesafe.akka" %% "akka-http" % version.akkaHttp,
    "com.typesafe.akka" %% "akka-stream" % "2.5.19",
    "com.typesafe.akka" %% "akka-http-spray-json" % version.akkaHttp,
    "com.typesafe.akka" %% "akka-actor" % version.akka,
    "com.typesafe.akka" %% "akka-http-testkit" % version.akkaHttp,
    "com.softwaremill.akka-http-session" %% "core" % version.akkaSession
  )

  val bcrypt = Seq(
    "org.mindrot" % "jbcrypt" % "0.3m"
  )
}