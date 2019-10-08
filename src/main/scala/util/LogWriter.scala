package util

import java.io.File
import java.nio.file.{Files, Paths}

import model.Commit

import scala.io.Source

case class LogWriter()

object LogWriter {
  def updateLogs(commit: Commit, currentBranch: String): Unit ={
    //If logs directory not exists
    if(Files.notExists(Paths.get(".sgit/logs"))){
      createLogDirectory(commit, currentBranch)
    }else{
      updateLogDirectory(commit, currentBranch)
    }
  }

  def createLogDirectory(commit: Commit, branch: String): Unit = {
    new File(".sgit/logs/refs/heads").mkdirs()
    new File(".sgit/logs/HEAD").createNewFile()
    FileManagement.writeFile(".sgit/logs/HEAD", commit.toStringForLogs())
    createLogFileForBranch(commit, branch)
  }

  def updateLogDirectory(commit: Commit, branch: String): Unit = {
    val headFileContent = Source.fromFile(".sgit/logs/HEAD").getLines.mkString("\n")
    FileManagement.writeFile(".sgit/logs/HEAD", headFileContent + "\n" + commit.toStringForLogs())
    //If logs directory for the current branch not exists
    if(Files.notExists(Paths.get(".sgit/logs/refs/heads/" + branch))) {
      createLogFileForBranch(commit, branch)
    }
    //If logs directory for the current branch exists
    else {
      val branchFileContent = Source.fromFile(".sgit/logs/refs/heads/" + branch).getLines.mkString("\n")
      FileManagement.writeFile(".sgit/logs/refs/heads/" + branch, branchFileContent + "\n" + commit.toStringForLogs())
    }
  }

  def createLogFileForBranch(commit: Commit, branch: String): Unit = {
    new File(".sgit/logs/refs/heads/" + branch).createNewFile()
    FileManagement.writeFile(".sgit/logs/refs/heads/" + branch, commit.toStringForLogs())
  }

}