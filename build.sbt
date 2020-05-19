name := "hive"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-google-cloud-pub-sub-grpc" % "2.0.0-RC1"

val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies += ("de.heikoseeberger" %% "akka-http-circe" % "1.31.0") excludeAll(ExclusionRule(organization = "com.typesafe.akka"))