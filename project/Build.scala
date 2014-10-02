import sbt._
import Keys._

object Play2SpritesPlugin extends Build {
  lazy val root = Project(
    id = "play2-sprites",
    base = file("."),
    settings = Defaults.defaultSettings
  )
  .settings(net.virtualvoid.sbt.cross.CrossPlugin.crossBuildingSettings: _*)
  .settings(
    name := "play2-sprites",
    organization := "net.koofr",
    version := "0.6.0",
    scalaVersion := "2.10.4",
    sbtPlugin := true,
    CrossBuilding.crossSbtVersions := Seq("0.12", "0.13"),
    publishMavenStyle := true,
    publishTo <<= isSnapshot { isSnapshot =>
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomExtra :=
        <url>https://github.com/koofr/play2-sprites</url>
        <licenses>
          <license>
            <name>MIT</name>
            <url>http://opensource.org/licenses/MIT</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>https://github.com/koofr/play2-sprites.git</url>
        </scm>
        <developers>
          <developer>
            <id>bancek</id>
            <name>Luka Zakrajsek</name>
          </developer>
          <developer>
            <id>edofic</id>
            <name>Andraz Bajt</name>
          </developer>
        </developers>
  )
}
