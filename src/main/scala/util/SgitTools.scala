package util

import java.io.File
import java.nio.file.{Files, Paths}

import scala.io.Source

object SgitTools {

  def getCurrentBranch(): String = {
    val headFileContent = Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}HEAD").getLines.mkString("\n")
    val contentSplit = headFileContent.split("/")
    contentSplit.last
  }

  //Get the current commit from references files
  def getCurrentCommit(currentBranch: String): String = {
    val pathRefsBranch = s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"
    if(Files.exists(Paths.get(pathRefsBranch))) {
      FileManagement.readFile(new File(pathRefsBranch))
    } else {
      "Nil"
    }
  }

  //Update references in .sgit directory
  def updateRef(commitId: String, currentBranch: String): Unit = {
    val filePath = s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"
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
    FileManagement.writeFile(s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}", "")
  }

}