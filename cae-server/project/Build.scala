import sbt._
import Keys._

/* Vanilla test project for playing with my compiler plugin under development */

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq(
    name := "cae-server",
    organization := "none",
    version := "0.0.1",
    scalacOptions ++= Seq("-deprecation"),
    scalaVersion := "2.11.6",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases"),
    //resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
    libraryDependencies += "com.typesafe.play" % "play_2.11" % "2.4.2"
  )
}

object MyBuild extends Build {
  import BuildSettings._

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings 
  )
}
