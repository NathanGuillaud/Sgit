package util

import java.io.File
import java.nio.file.{Files, Paths}

import model.Commit

object LogWriter {
  def updateLogs(commit: Commit, currentBranch: String): Unit ={
    //If logs directory not exists
    if(Files.notExists(Paths.get(s"${PathManagement.getSgitPath().get}${File.separator}logs"))){
      createLogDirectory(commit, currentBranch)
    }else{
      updateLogDirectory(commit, currentBranch)
    }
  }

  def createLogDirectory(commit: Commit, branch: String): Unit = {
    val pathLogsHead = s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD"
    new File(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads").mkdirs()
    new File(pathLogsHead).createNewFile()
    FileManagement.writeFile(pathLogsHead, commit.toStringForLogs())
    createLogFileForBranch(commit, branch)
  }

  def updateLogDirectory(commit: Commit, branch: String): Unit = {
    val pathLogsHead = s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD"
    val pathLogsBranch = s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${branch}"
    val headFileContent = FileManagement.readFile(new File(pathLogsHead))
    FileManagement.writeFile(pathLogsHead, headFileContent + "\n" + commit.toStringForLogs())
    //If logs directory for the current branch not exists
    if(Files.notExists(Paths.get(pathLogsBranch))) {
      createLogFileForBranch(commit, branch)
    }
    //If logs directory for the current branch exists
    else {
      val branchFileContent = FileManagement.readFile(new File(pathLogsBranch))
      FileManagement.writeFile(pathLogsBranch, branchFileContent + "\n" + commit.toStringForLogs())
    }
  }

  def createLogFileForBranch(commit: Commit, branch: String): Unit = {
    val pathLogsBranch = s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${branch}"
    new File(pathLogsBranch).createNewFile()
    FileManagement.writeFile(pathLogsBranch, commit.toStringForLogs())
  }

}