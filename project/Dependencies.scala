import sbt._

object Dependencies {

  object version {
    val sql = "3.8.7"
    val slick = "3.2.1"
    val logback = "1.2.3"
    val logging = "3.9.0"
  }

  val configTypesafe = Seq (
    "com.typesafe" % "config" % "1.3.2"
  )
  
  val sql = Seq(
    "org.xerial" % "sqlite-jdbc" % version.sql,
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
}