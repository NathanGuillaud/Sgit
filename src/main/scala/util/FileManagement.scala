package util

import java.io.{BufferedWriter, File, FileWriter}
import java.math.BigInteger
import java.nio.file.{Files, Paths}
import java.security.MessageDigest

import model.Commit

import scala.annotation.tailrec

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

  //Return true if the file exists in the commit in parameters, else return false
  def fileIsInCommit(filePath: String, commitHash: String): Boolean = {
    def elemIsInTree(currentPath: String, treeHash: String): Boolean = {
      var isInTree = false
      FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/tree/" + treeHash.substring(0,2) + "/" + treeHash.substring(2))).split("\n").map(x => x.split(" "))
        .map(line =>
          if(filePath == currentPath && currentPath == line(2)) isInTree = true
          else if(currentPath == line(2)) isInTree = elemIsInTree(currentPath + "/" + filePath.substring(currentPath.length+1).split("/")(0), line(1))
        )
      isInTree
    }
    elemIsInTree(filePath.split("/")(0), Commit.getTreeForCommit(commitHash))
  }

  //Return the content of a file for a commit
  //The file must be in the commit
  def getFileContentForCommit(filePath: String, commitHash: String): String = {
    def getFileContentForTree(currentPath: String, treeHash: String): String = {
      var fileContent = ""
      FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/tree/" + treeHash.substring(0,2) + "/" + treeHash.substring(2))).split("\n").map(x => x.split(" "))
        .map(line =>
          if(filePath == currentPath && currentPath == line(2)) {
            fileContent = FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/blob/" + line(1).substring(0,2) + "/" + line(1).substring(2)))
          }
          else if(currentPath == line(2)) {
            fileContent = getFileContentForTree(currentPath + "/" + filePath.substring(currentPath.length+1).split("/")(0), line(1))
          }
        )
      fileContent
    }
    getFileContentForTree(filePath.split("/")(0), Commit.getTreeForCommit(commitHash))
  }

}