package net.koofr.spritegen

import java.io._
import java.nio.file.Files
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

case class SpriteImage(file: File, name: String, width: Int, height: Int, offsetY: Int, cssClass: String, ratio: Int, image: BufferedImage)
case class Sprite(width: Int, height: Int, images: Seq[SpriteImage], ratio: Int, destImage: File)

trait Logger {
  def info(message: String)
  def error(message: String)
}

class SpriteGen(files: Seq[File], destImage: File, relPath: String,
    cssClassPrefix: String, padding: Int, cssFile: File, logger: Logger) {

  val cssImageTpl = """|%s {
    |  background-position: 0 -%dpx;
    |  width: %dpx;
    |  height: %dpx;
    |}""".stripMargin

  val cssSpriteTpl = """|%s {
      |  background: url('%s') no-repeat;
      |  background-size: %dpx %dpx;
      |}""".stripMargin

  def addRatio(fileName: String, ratio: Int): String = {
    ratio match {
      case 1 =>
        fileName

      case r =>
        val parts = fileName.split("\\.")
        parts.init.mkString(".") + "@" + r + "x." + parts.last
    }
  }

  def getImages(): Seq[(File, BufferedImage)] = {
    val eitherImages: Seq[(File, Either[Exception, BufferedImage])] = files.map { file =>
      try {
        file -> Option(ImageIO.read(file)).map(Right(_)).getOrElse(Left(new NullPointerException))
      } catch {
        case e: IOException => file -> Left(e)
      }
    }

    val images = eitherImages.collect { case (file, Right(img)) => file -> img }

    val errors = eitherImages.collect { case (file, Left(e)) => file -> e }

    errors map {
      case (file, error) =>
        logger.error("Image %s could not be loaded: %s." format (file, error))
    }

    images
  }

  def ensurePaths() {
    destImage.getParentFile.mkdirs()
    cssFile.getParentFile.mkdirs()
  }

  def getCleanNameRatio(name: String): (String, Int) = {
    val cleanName = name.toLowerCase.split("\\.").init.mkString("-").replace("_", "-")

    cleanName.split('@') match {
      case Array(cleanName) =>
        (cleanName, 1)

      case Array(cleanName, suffix) =>
        (cleanName, suffix.replaceAll("x", "").toInt)

      case _ =>
        throw new Exception("Invalid name: " + name)
    }
  }

  def getSpriteImages(images: Seq[(File, BufferedImage)]): Seq[SpriteImage] = {
    images.foldLeft((Seq[SpriteImage](), 0)) {
      case ((processed, y), (file, img)) =>
        val (cleanName, ratio) = getCleanNameRatio(file.getName)

        val cssClass = "." + cssClassPrefix + cleanName

        val info = SpriteImage(file, cleanName, img.getWidth, img.getHeight, y, cssClass, ratio, img)

        (processed :+ info, y + info.height + padding * ratio)
    }._1
  }

  def getFullSize(images: Seq[SpriteImage], ratio: Int): (Int, Int) = {
    if (images.length > 0) {
      val width = images.map(_.width).max
      val imagesHeight = images.map(_.height).sum
      val imagesPadding = ((images.length - 1) max 0) * padding * ratio
      val height = imagesHeight + imagesPadding

      (width, height)
    } else {
      (0, 0)
    }
  }

  def getSprites(images: Seq[(File, BufferedImage)]): Seq[Sprite] = {
    val groupped = images groupBy {
      case (file, _) =>
        getCleanNameRatio(file.getName)._2
    }

    groupped.toSeq.sortBy(_._1) map {
      case (ratio, images) =>
        val spriteImages = getSpriteImages(images.sortBy(_._1.getName))

        val (width, height) = getFullSize(spriteImages, ratio)

        val destImageWithRatio = new File(addRatio(destImage.getAbsolutePath, ratio))

        Sprite(width, height, spriteImages, ratio, destImageWithRatio)
    }
  }

  def drawSprite(sprite: Sprite): BufferedImage = {
    val spriteImage = new BufferedImage(sprite.width, sprite.height, BufferedImage.TYPE_INT_ARGB)

    sprite.images foreach { image =>
      spriteImage.createGraphics().drawImage(image.image, 0, image.offsetY, null)
    }

    spriteImage
  }

  def writeSprite(sprite: Sprite, spriteImage: BufferedImage) {
    ImageIO.write(spriteImage, "png", sprite.destImage)
  }

  def getSpriteCss(sprite: Sprite): String = {
    val cssImages = sprite.images map { image =>
      cssImageTpl.format(
        image.cssClass,
        image.offsetY / sprite.ratio,
        image.width / image.ratio,
        image.height / image.ratio
      )
    }

    val allCssClasses = sprite.images.map(_.cssClass).mkString(",\n")

    val cssSprite = cssSpriteTpl.format(
      allCssClasses,
      addRatio(relPath, sprite.ratio),
      sprite.width / sprite.ratio,
      sprite.height / sprite.ratio
    )

    val css = (cssSprite +: cssImages).mkString("\n\n")

    sprite.ratio match {
      case 1 =>
        css

      case r =>
        "@media only screen and (-webkit-min-device-pixel-ratio: " + r + "), only screen and (min-device-pixel-ratio: " + r + ") {\n" +
          css.split('\n').map("  " + _).mkString("\n") +
          "\n}"
    }
  }

  def getCss(sprites: Seq[Sprite]): String = {
    sprites.map(getSpriteCss).mkString("\n\n")
  }

  def writeCss(css: String) {
    Files.write(cssFile.toPath, css.getBytes("utf-8"))
  }

  def generateSprites(): Seq[(File, File)] = {
    logger.info("Generating sprites for %d images" format (files.length))

    val images = getImages()

    val sprites = getSprites(images)

    ensurePaths()

    sprites foreach { sprite =>
      val img = drawSprite(sprite)

      writeSprite(sprite, img)
    }

    val css = getCss(sprites)

    writeCss(css)

    (sprites flatMap { sprite =>
      sprite.images.map(_.file -> sprite.destImage)
    }) ++ (sprites flatMap { sprite =>
      sprite.images.map(_.file -> cssFile)
    })
  }

}
