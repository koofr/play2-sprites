// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.0")

resolvers += "Koofr repo" at "http://koofr.github.com/repo/maven/"

addSbtPlugin("net.koofr" % "play2-sprites" % "0.6.0")
