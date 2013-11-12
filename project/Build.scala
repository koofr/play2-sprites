import sbt._
import Keys._

object Play2SpritesPlugin extends Build {
  lazy val root = Project(
    id = "play2-sprites",
    base = file(".")
  ).settings(
    name := "play2-sprites",
    organization := "net.koofr",
    version := "0.4.0-SNAPSHOT",
    sbtPlugin := true,
    scalaVersion := "2.10.2"
  )
}
