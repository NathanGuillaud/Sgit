package util

import java.io.File
import java.nio.file.{Files, Paths}

import scala.io.Source

object SgitTools {

  /**
   * Get the current branch
   * @return a String which correspond to the name of the current branch
   */
  def getCurrentBranch(): String = {
    val headFileContent = Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}HEAD").getLines.mkString("\n")
    val contentSplit = headFileContent.split("/")
    contentSplit.last
  }

  /**
   * Get the current (last) commit
   * @param currentBranch : current branch
   * @return the last commit for the current branch
   */
  def getCurrentCommit(currentBranch: String): String = {
    val pathRefsBranch = s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"
    if(Files.exists(Paths.get(pathRefsBranch))) {
      FileManagement.readFile(new File(pathRefsBranch))
    } else {
      "Nil"
    }
  }

  /**
   * Update references in .sgit directory
   * @param commitId : hash of the new commit
   * @param currentBranch : current branch
   */
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

  /**
   * Update the HEAD file in .sgit directory
   * @param newBranch : new branch to write in HEAD file
   */
  def updateHead(newBranch: String): Unit = {
    FileManagement.writeFile(s"${PathManagement.getSgitPath().get}${File.separator}HEAD", s"ref: refs/heads/${newBranch}")
  }

}