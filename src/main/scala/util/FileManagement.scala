package util

import java.io.{BufferedWriter, File, FileWriter}
import java.math.BigInteger
import java.nio.file.{Files, Paths}
import java.security.MessageDigest

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

  //Get all the files for a directory (recursively)
  def getFilesFromDirectory(path: File): List[File] = {
    val allFiles = path.listFiles().toList
    allFiles.flatMap(elem =>
      if (elem.isDirectory) {
        getFilesFromDirectory(elem)
      }
      else {
        List(elem)
      }
    )
  }

  //Return true if the file in parameters is in the list of tuples (file path and file hash) in parameters
  def fileIsInList(filePath: String, fileList: List[(String, String)]): Boolean = {
    var inList = false
    fileList.map(file => if(file._1 == filePath) inList = true)
    inList
  }

  //Return the hash of the file in parameters in the list in parameters
  def getFileHashFromList(filePath: String, fileList: List[(String, String)]): String = {
    if(fileIsInList(filePath, fileList)) {
      fileList.filter(file => file._1 == filePath).map(file => file._2).last
    } else {
      "0000000"
    }
  }

}