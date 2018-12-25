import Dependencies._

name := "learningManagementSystem"

scalaVersion := "2.12.8"

Global / concurrentRestrictions += Tags.limit(Tags.Test, 1)

scalaVersion := "2.12.4"

libraryDependencies ++= configTypesafe
libraryDependencies ++= scalaTest
libraryDependencies ++= logging
libraryDependencies ++= jodaTime
libraryDependencies ++= sql
libraryDependencies ++= akka
libraryDependencies ++= bcrypt

