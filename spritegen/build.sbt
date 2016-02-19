name := "spritegen"

organization := "net.koofr"

version := "0.1"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-Xfatal-warnings", "-feature", "-Xlint", 
                      "-language:postfixOps")

libraryDependencies += "org.specs2" %% "specs2" % "2.2.3" % "test"

scalacOptions in Test ++= Seq("-Yrangepos")

parallelExecution in Test := false

testOptions in Test += Tests.Argument("sequential")
