import sbt.Keys.libraryDependencies

lazy val scalaTestVersion = "3.0.1"
lazy val scalaLoggingVersion = "3.5.0"
lazy val slickVersion = "3.2.0"
lazy val h2Version = "1.4.193"
lazy val logbackVersion = "1.1.2"
lazy val akkaVersion = "2.4.17"
lazy val akkaHttpVersion = "10.0.5"

lazy val commonSettings = Seq(
  organization := "com.stulsoft.scs",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.1",
  ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },
  scalacOptions ++= Seq(
    "-feature",
    "-language:implicitConversions",
    "-language:postfixOps"),
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  )
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val server = (project in file("server"))
  .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "com.h2database" % "h2" % h2Version,
        "com.typesafe.slick" %% "slick" % slickVersion,
        "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
      )
    )
  .settings(
    name := "server"
  )

lazy val client = (project in file("client"))
  .settings(commonSettings: _*)
  .settings(
    name := "client"
  )

lazy val common = (project in file("common"))
  .settings(commonSettings: _*)
  .settings(
    name := "common"
  )
