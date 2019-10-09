package util

import java.io.File
import java.nio.file.{Files, Paths}

import model.Commit

import scala.io.Source

case class SgitTools()

object SgitTools {

  def getCurrentBranch(): String = {
    val headFileContent = Source.fromFile(s".sgit${File.separator}HEAD").getLines.mkString("\n")
    val contentSplit = headFileContent.split("/")
    contentSplit.last
  }

  //Get the current commit from references files
  def getCurrentCommit(currentBranch: String): String = {
    if(Files.exists(Paths.get(s".sgit${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"))) {
      FileManagement.readFile(new File(s".sgit${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"))
    } else {
      "Nil"
    }
  }

  //Get the path of the parent for a directory
  //Return a String corresponding to the path, None if the path has no parent
  def getParentPath(path: String): Option[String] = {
    val pathSplit = path.split("/")
    if(pathSplit.length <= 1){
      None
    } else {
      var parentPath = ""
      var first_dir = true
      var index = 0
      pathSplit.map(x =>
        if(index < pathSplit.length-1){
          if(first_dir){
            parentPath = x
            first_dir = false
          } else {
            parentPath = parentPath + File.separator + x
          }
          index = index + 1
      }
      )
      Some(parentPath)
    }
  }

  //Update references in .sgit directory
  def updateRef(commitId: String, currentBranch: String): Unit = {
    val filePath = s".sgit${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"
    //Case in test
    if(commitId == "Nil") {
      if(Files.exists(Paths.get(filePath))) {
        new File(filePath).delete()
      }
    }
    //If the commit is not nil
    else {
      if(Files.notExists(Paths.get(filePath))) {
        new File(filePath).createNewFile()
      }
      //Update the HEAD of the branch
      FileManagement.writeFile(filePath, commitId)
    }
  }

  def clearStage(currentBranch: String): Unit = {
    FileManagement.writeFile(s".sgit${File.separator}stages${File.separator}${currentBranch}", "")
  }

}