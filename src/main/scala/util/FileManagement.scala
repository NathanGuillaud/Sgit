package util

import java.io.{BufferedWriter, File, FileWriter}
import java.math.BigInteger
import java.nio.file.{Files, Paths}
import java.security.MessageDigest

object FileManagement {

  /**
   * List all the files and directories in a directory
   * @param dir : the directory to browse
   * @return a list of File with files and directories
   */
  def getListOfFilesAndDirectories(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.toList
    } else {
      List[File]()
    }
  }

  /**
   * Hash a file with his name and content
   * @param fileName : the name of the file to hash
   * @param fileContent : the content of the file to hash
   * @return a String which correspond to the hash
   */
  def hashFile(fileName: String, fileContent: String): String = {
    String.format("%032x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest((fileName + fileContent).getBytes("UTF-8"))))
    //new String(MessageDigest.getInstance("SHA1").digest((fileName + fileContent).getBytes("UTF-8")))
  }

  /**
   * Write in a file
   * @param filename : file to write in
   * @param s : string to write in
   */
  def writeFile(filename: String, s: String): Unit = {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(s)
    bw.close()
  }

  /**
   * Hash a tree or a commit with his content
   * @param content : content of the commit or the tree
   * @return a String which correspond to the hash
   */
  def hashTreeOrCommit(content: String): String = {
    String.format("%032x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(content.getBytes("UTF-8"))))
  }

  /**
   * Read a file
   * @param file : file to read
   * @return the content of the file
   */
  def readFile(file: File): String = {
    new String(Files.readAllBytes(Paths.get(file.getAbsolutePath)))
  }

  /**
   * Get all the files for a directory
   * @param path : path of the directory to browse
   * @return a list of file with all the files in the directory
   */
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

  /**
   * To know if a file is in a list
   * @param filePath : path of the file to search in the list
   * @param fileList : list of tuples (file path and file hash) to browse
   * @return true if the file in parameters is in the list
   */
  def fileIsInList(filePath: String, fileList: List[(String, String)]): Boolean = {
    var inList = false
    fileList.map(file => if(file._1 == filePath) inList = true)
    inList
  }

  /**
   * Get the hash of a file from a list
   * @param filePath : path of the file to get the hash
   * @param fileList : list of tuples (file path and file hash) to browse
   * @return the hash of the file give in parameters
   */
  def getFileHashFromList(filePath: String, fileList: List[(String, String)]): String = {
    if(fileIsInList(filePath, fileList)) {
      fileList.filter(file => file._1 == filePath).map(file => file._2).last
    } else {
      "0000000"
    }
  }

}