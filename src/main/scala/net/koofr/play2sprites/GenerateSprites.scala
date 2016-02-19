package net.koofr.play2sprites

import sbt._
import Keys._
import net.koofr.spritegen.SpriteGen

case class SpriteInfo(file: File, width: Int, height: Int, offsetY: Int, cssClass: String)

class SbtLogger(s: TaskStreams) extends net.koofr.spritegen.Logger {
  def info(message: String) {
    s.log.info(message)
  }

  def error(message: String) {
    s.log.error(message)
  }
}

class GenerateSprites(prefix: String) extends Plugin {
  val pfx = if (prefix.isEmpty) "" else prefix + "-"

  val spritesSrcImages = SettingKey[PathFinder](
    pfx + "sprites-src-images",
    "source images for sprites"
  )

  val spritesDestImage = SettingKey[File](
    pfx + "sprites-dest-image",
    "destination sprite image file"
  )

  val spritesCssSpritePath = SettingKey[String](
    pfx + "sprites-css-sprite-path",
    "path to sprite image relative to css file"
  )

  val spritesCssClassPrefix = SettingKey[String](
    pfx + "sprites-css-class-prefix",
    "css class prefix"
  )

  val spritesPadding = SettingKey[Int](
    pfx + "sprites-padding",
    "padding between images"
  )

  val spritesDestCss = SettingKey[File](
    pfx + "sprites-dest-css",
    "destination css file"
  )

  val spritesGen = TaskKey[Seq[File]](
    pfx + "sprites-gen",
    "generate sprite from images"
  )

  val genSpritesSettings: Seq[Setting[_]] = Seq(

    spritesCssClassPrefix := "",

    spritesPadding := 0,

    spritesGen <<= (
      spritesSrcImages,
      spritesDestImage,
      spritesCssSpritePath,
      spritesCssClassPrefix,
      spritesPadding,
      spritesDestCss,
      cacheDirectory,
      streams
    ) map { (srcImages, destImage, relPath, cssClassPrefix, padding, cssFile, cache, s) =>
      val files = srcImages.get.sortBy(_.getName)

      val cacheFile = cache / (pfx + "sprites")
      val currentInfos = files.map(f => f -> FileInfo.lastModified(f)).toMap

      val (previousRelation, previousInfo) = Sync.readInfo(cacheFile)(FileInfo.lastModified.format)

      if (!files.isEmpty && (previousInfo != currentInfos || !destImage.exists || !cssFile.exists)) {
        val sg = new SpriteGen(
          files = files,
          destImage = destImage,
          relPath = relPath,
          cssClassPrefix = cssClassPrefix,
          padding = padding,
          cssFile = cssFile,
          logger = new SbtLogger(s)
        )

        val generated = sg.generateSprites()

        Sync.writeInfo(cacheFile,
          Relation.empty[File, File] ++ generated,
          currentInfos)(FileInfo.lastModified.format)
      }

      Seq()
    }
  )
}

object GenerateSprites extends GenerateSprites("")
