package util

import java.io.{BufferedWriter, File, FileWriter}
import java.math.BigInteger
import java.nio.file.{Files, Paths}
import java.security.MessageDigest

case class FileManagement()

object FileManagement {
  def getListOfFilesAndDirectories(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.toList
    } else {
      List[File]()
    }
  }

  def hashFile(fileName: String, fileContent: String): String = {
    String.format("%032x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest((fileName + fileContent).getBytes("UTF-8"))))
    //new String(MessageDigest.getInstance("SHA1").digest((fileName + fileContent).getBytes("UTF-8")))
  }

  def writeFile(filename: String, s: String): Unit = {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(s)
    bw.close()
  }

  def hashTreeOrCommit(content: String): String = {
    String.format("%032x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(content.getBytes("UTF-8"))))
  }

  def readFile(file: File): String = {
    new String(Files.readAllBytes(Paths.get(file.getAbsolutePath)))
  }
}