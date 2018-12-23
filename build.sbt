import Dependencies._

name := "learningManagementSystem"

scalaVersion := "2.12.8"

val commonSettings = Seq(
  scalaVersion := "2.12.4",
  libraryDependencies ++= configTypesafe,
  libraryDependencies ++= scalaTest,
  libraryDependencies ++= logging,
  libraryDependencies ++= jodaTime
)

lazy val adminModule = (project in file("AdminModule"))
  .settings(commonSettings)
  .settings(libraryDependencies ++= sql)
