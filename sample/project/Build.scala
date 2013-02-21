import sbt._
import Keys._
import play.Project._
import net.koofr.play2sprites.GenerateSprites._

object ApplicationBuild extends Build {

  val appName         = "sample"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    
  )

  val main = play.Project(
    appName,
    appVersion,
    appDependencies,
    settings = Defaults.defaultSettings ++
        genSpritesSettings
  ).settings(
    spritesSrcImages <<= baseDirectory( (base: File) => base / "public/images/sprites" * "*.png" ),
    spritesDestImage <<= baseDirectory( (base: File) => base / "public/images/sprites.png" ),
    spritesCssSpritePath := "../images/sprites.png",
    spritesCssClassPrefix := "",
    spritesDestCss <<= baseDirectory( (base: File) => base / "app/assets/stylesheets/_sprites.less" ),

    resourceGenerators in Compile <+= spritesGen,
    resourceGenerators in Compile ~= { gens => gens.last +: gens.init }
  )

}
