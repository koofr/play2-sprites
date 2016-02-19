package de.choffmeister.specs2

// https://choffmeister.de/posts/2014/09/20/temporary-directories-with-specs2.html

import java.io.{ File, FileOutputStream }
import java.util.UUID

import org.specs2.execute._
import org.specs2.mutable._

object TempDirectory {
  def apply[R: AsResult](a: File ⇒ R) = {
    val temp = createTemporaryDirectory("")
    try {
      AsResult.effectively(a(temp))
    } finally {
      removeTemporaryDirectory(temp)
    }
  }

  /** Creates a new temporary directory and returns it's location. */
  def createTemporaryDirectory(suffix: String): File = {
    val base = new File(new File(System.getProperty("java.io.tmpdir")), "my-prefix")
    val dir = new File(base, UUID.randomUUID().toString + suffix)
    dir.mkdirs()
    dir
  }

  /** Removes a directory (recursively). */
  def removeTemporaryDirectory(dir: File): Unit = {
    def recursion(f: File): Unit = {
      if (f.isDirectory) {
        f.listFiles().foreach(child ⇒ recursion(child))
      }
      f.delete()
    }
    recursion(dir)
  }
}
