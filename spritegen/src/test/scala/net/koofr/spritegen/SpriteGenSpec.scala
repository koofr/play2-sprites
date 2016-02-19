package net.koofr.spritegen

import java.io._
import java.nio.file.Files
import org.specs2.mutable._
import de.choffmeister.specs2.TempDirectory

object DummyLogger extends Logger {
  def info(message: String) {

  }

  def error(message: String) {

  }
}

class SpriteGenSpec extends Specification {

  val sprite1 = new File("src/test/resources/sprite1.png")
  val sprite2 = new File("src/test/resources/sprite2.png")
  val sprite3 = new File("src/test/resources/sprite3.png")
  val sprite4 = new File("src/test/resources/sprite4.png")

  val sprite1_2x = new File("src/test/resources/sprite1@2x.png")
  val sprite2_2x = new File("src/test/resources/sprite2@2x.png")
  val sprite3_2x = new File("src/test/resources/sprite3@2x.png")
  val sprite4_2x = new File("src/test/resources/sprite4@2x.png")

  val files = Seq(
    sprite1,
    sprite2,
    sprite3,
    sprite4
  )

  val files_2x = Seq(
    sprite1,
    sprite1_2x,
    sprite2,
    sprite2_2x,
    sprite3,
    sprite3_2x,
    sprite4,
    sprite4_2x
  )

  val htmlFiles_2x = Seq(
    sprite1,
    sprite1_2x,
    sprite2,
    sprite2_2x,
    sprite3,
    sprite3_2x,
    sprite4
    // sprite4_2x intentionally missing
  )

  "generateSprites" should {
    "generate sprites" in TempDirectory { tmp =>
      val destImage = new File(tmp, "sprites.png")
      val cssFile = new File(tmp, "sprites.css")

      val sg = new SpriteGen(
        files = files,
        destImage = destImage,
        relPath = "../images/sprites.png",
        cssClassPrefix = "sprite-",
        padding = 10,
        cssFile = cssFile,
        logger = DummyLogger
      )

      sg.generateSprites() must equalTo(Seq(
        sprite1 -> destImage,
        sprite2 -> destImage,
        sprite3 -> destImage,
        sprite4 -> destImage,
        sprite1 -> cssFile,
        sprite2 -> cssFile,
        sprite3 -> cssFile,
        sprite4 -> cssFile
      ))

      val css = new String(Files.readAllBytes(cssFile.toPath))

      css must equalTo(""".sprite-sprite1,
      |.sprite-sprite2,
      |.sprite-sprite3,
      |.sprite-sprite4 {
      |  background: url('../images/sprites.png') no-repeat;
      |  background-size: 64px 166px;
      |}
      |
      |.sprite-sprite1 {
      |  background-position: 0 -0px;
      |  width: 24px;
      |  height: 24px;
      |}
      |
      |.sprite-sprite2 {
      |  background-position: 0 -34px;
      |  width: 24px;
      |  height: 24px;
      |}
      |
      |.sprite-sprite3 {
      |  background-position: 0 -68px;
      |  width: 24px;
      |  height: 24px;
      |}
      |
      |.sprite-sprite4 {
      |  background-position: 0 -102px;
      |  width: 64px;
      |  height: 64px;
      |}""".stripMargin)
    }

    "generate retina sprites" in TempDirectory { tmp =>
      val destImage = new File(tmp, "sprites.png")
      val destImage_2x = new File(tmp, "sprites@2x.png")
      val cssFile = new File(tmp, "sprites.css")

      val sg = new SpriteGen(
        files = files_2x,
        destImage = destImage,
        relPath = "../images/sprites.png",
        cssClassPrefix = "sprite-",
        padding = 10,
        cssFile = cssFile,
        logger = DummyLogger
      )

      sg.generateSprites() must equalTo(Seq(
        sprite1 -> destImage,
        sprite2 -> destImage,
        sprite3 -> destImage,
        sprite4 -> destImage,
        sprite1_2x -> destImage_2x,
        sprite2_2x -> destImage_2x,
        sprite3_2x -> destImage_2x,
        sprite4_2x -> destImage_2x,
        sprite1 -> cssFile,
        sprite2 -> cssFile,
        sprite3 -> cssFile,
        sprite4 -> cssFile,
        sprite1_2x -> cssFile,
        sprite2_2x -> cssFile,
        sprite3_2x -> cssFile,
        sprite4_2x -> cssFile
      ))

      val css = new String(Files.readAllBytes(cssFile.toPath))

      css must equalTo(""".sprite-sprite1,
        |.sprite-sprite2,
        |.sprite-sprite3,
        |.sprite-sprite4 {
        |  background: url('../images/sprites.png') no-repeat;
        |  background-size: 64px 166px;
        |}
        |
        |.sprite-sprite1 {
        |  background-position: 0 -0px;
        |  width: 24px;
        |  height: 24px;
        |}
        |
        |.sprite-sprite2 {
        |  background-position: 0 -34px;
        |  width: 24px;
        |  height: 24px;
        |}
        |
        |.sprite-sprite3 {
        |  background-position: 0 -68px;
        |  width: 24px;
        |  height: 24px;
        |}
        |
        |.sprite-sprite4 {
        |  background-position: 0 -102px;
        |  width: 64px;
        |  height: 64px;
        |}
        |
        |@media only screen and (-webkit-min-device-pixel-ratio: 2), only screen and (min-device-pixel-ratio: 2) {
        |  .sprite-sprite1,
        |  .sprite-sprite2,
        |  .sprite-sprite3,
        |  .sprite-sprite4 {
        |    background: url('../images/sprites@2x.png') no-repeat;
        |    background-size: 64px 166px;
        |  }
        |  
        |  .sprite-sprite1 {
        |    background-position: 0 -0px;
        |    width: 24px;
        |    height: 24px;
        |  }
        |  
        |  .sprite-sprite2 {
        |    background-position: 0 -34px;
        |    width: 24px;
        |    height: 24px;
        |  }
        |  
        |  .sprite-sprite3 {
        |    background-position: 0 -68px;
        |    width: 24px;
        |    height: 24px;
        |  }
        |  
        |  .sprite-sprite4 {
        |    background-position: 0 -102px;
        |    width: 64px;
        |    height: 64px;
        |  }
        |}""".stripMargin)
    }

    "generate retina sprites (html check)" in {
      val tmp = TempDirectory.createTemporaryDirectory("")

      val destImage = new File(tmp, "sprites.png")
      val cssFile = new File(tmp, "sprites.css")
      val htmlFile = new File(tmp, "index.html")

      val sg = new SpriteGen(
        files = htmlFiles_2x,
        destImage = destImage,
        relPath = "sprites.png",
        cssClassPrefix = "",
        padding = 10,
        cssFile = cssFile,
        logger = DummyLogger
      )

      sg.generateSprites()

      val html = """<!DOCTYPE html>
        |<html>
        |    <head>
        |        <title>Sprites test</title>
        |        <link rel="stylesheet" media="screen" href="sprites.css">
        |    </head>
        |    <body>
        |        <div class="sprite1"></div><br><br>
        |        <div class="sprite2"></div><br><br>
        |        <div class="sprite3"></div><br><br>
        |        <div class="sprite4"></div><br><br>
        |    </body>
        |</html>""".stripMargin

      Files.write(htmlFile.toPath, html.getBytes("utf-8"))

      println("Open this HTML: " + htmlFile.toPath)

      true
    }
  }

}
