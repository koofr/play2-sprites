import play.PlayImport._
import net.koofr.play2sprites.GenerateSprites._

lazy val root = (project in file(".")).enablePlugins(PlayScala)

name := "sample"

version := "1.0-SNAPSHOT"

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

genSpritesSettings

spritesSrcImages <<= baseDirectory( (base: File) => base / "public/images/sprites" * "*.png" )

spritesDestImage <<= baseDirectory( (base: File) => base / "public/images/sprites.png" )

spritesCssSpritePath := "../images/sprites.png"

spritesCssClassPrefix := ""

spritesPadding := 50

spritesDestCss <<= baseDirectory( (base: File) => base / "app/assets/stylesheets/_sprites.less" )

resourceGenerators in Compile <<= (resourceGenerators in Compile, spritesGen) { (gens, spritesGen) =>
  spritesGen +: gens
}
