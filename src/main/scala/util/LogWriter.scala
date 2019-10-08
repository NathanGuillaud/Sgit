package util

import java.io.File
import java.nio.file.{Files, Paths}

import model.Commit

case class LogWriter()

object LogWriter {
  def updateLogs(commit: Commit, currentBranch: String): Unit ={
    //If logs directory not exists
    if(Files.notExists(Paths.get(s".sgit${File.separator}logs"))){
      createLogDirectory(commit, currentBranch)
    }else{
      updateLogDirectory(commit, currentBranch)
    }
  }

  def createLogDirectory(commit: Commit, branch: String): Unit = {
    new File(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads").mkdirs()
    new File(s".sgit${File.separator}logs${File.separator}HEAD").createNewFile()
    FileManagement.writeFile(s".sgit${File.separator}logs${File.separator}HEAD", commit.toStringForLogs())
    createLogFileForBranch(commit, branch)
  }

  def updateLogDirectory(commit: Commit, branch: String): Unit = {
    val headFileContent = FileManagement.readFile(new File(s".sgit${File.separator}logs${File.separator}HEAD"))
    FileManagement.writeFile(s".sgit${File.separator}logs${File.separator}HEAD", headFileContent + "\n" + commit.toStringForLogs())
    //If logs directory for the current branch not exists
    if(Files.notExists(Paths.get(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${branch}"))) {
      createLogFileForBranch(commit, branch)
    }
    //If logs directory for the current branch exists
    else {
      val branchFileContent = FileManagement.readFile(new File(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${branch}"))
      FileManagement.writeFile(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${branch}", branchFileContent + "\n" + commit.toStringForLogs())
    }
  }

  def createLogFileForBranch(commit: Commit, branch: String): Unit = {
    new File(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${branch}").createNewFile()
    FileManagement.writeFile(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${branch}", commit.toStringForLogs())
  }

}