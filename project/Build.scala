import sbt._
import Keys._

object Play2SpritesPlugin extends Build {
  lazy val root = Project(
    id = "play2-sprites",
    base = file("."),
    settings = Defaults.defaultSettings
  ).settings(
    name := "play2-sprites",
    organization := "net.koofr",
    version := "0.5.0",
    scalaVersion := "2.10.4",
    sbtPlugin := true,
    CrossBuilding.crossSbtVersions := Seq("0.12", "0.13")
  ).settings(net.virtualvoid.sbt.cross.CrossPlugin.crossBuildingSettings: _*)
}
