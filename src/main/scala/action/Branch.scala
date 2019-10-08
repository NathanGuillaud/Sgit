package action

import java.io.File
import java.nio.file.{Files, Paths}

import util.{FileManagement, SgitTools}

import scala.io.Source

case class Branch()

object Branch {

  //Create a branch with the name of the arg
  def branch(command: Array[String]): Unit = {
    //Retrieve current branch
    val currentBranch = SgitTools.getCurrentBranch()
    if(Files.notExists(Paths.get(s".sgit${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"))) {
      println("You have to make a first commit before create a new branch")
    }
    //If the branch already exists
    else if(Files.exists(Paths.get(s".sgit${File.separator}refs${File.separator}heads${File.separator}${command(0)}"))){
      println("The branch " + command(0) + " already exists")
    } else {
      //Retrieve current commit
      val currentCommit = Source.fromFile(s".sgit${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.mkString("\n")
      //Create new file for stage
      new File(s".sgit${File.separator}stages${File.separator}${command(0)}").createNewFile()
      //Write head into refs
      new File(s".sgit${File.separator}refs${File.separator}heads${File.separator}${command(0)}").createNewFile()
      FileManagement.writeFile(s".sgit${File.separator}refs${File.separator}heads${File.separator}${command(0)}", currentCommit)
      //Write in logs
      val lastCommitForCurrentBranch = Source.fromFile(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.toArray.last
      new File(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${command(0)}").createNewFile()
      FileManagement.writeFile(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${command(0)}", lastCommitForCurrentBranch + "::branch: Created from " + currentBranch)
    }
  }

  //List all the branches and give the current branch
  def branchAV(): Unit = {
    if(Files.exists(Paths.get(s".sgit${File.separator}refs${File.separator}heads"))) {
      val currentBranch = SgitTools.getCurrentBranch()
      val branches = FileManagement.getListOfFilesAndDirectories(s".sgit${File.separator}refs${File.separator}heads${File.separator}")
      branches.map(branchFile => printBranch(branchFile.toString.split("/").last, Source.fromFile(branchFile).getLines.mkString("\n"), currentBranch))
    }
  }

  //Print a branch
  def printBranch(branchName: String, lastCommit: String, currentBranch: String): Unit = {
    if(branchName == currentBranch) {
      println("* " + branchName + " " + lastCommit)
    } else {
      println("  " + branchName + " " + lastCommit)
    }
  }

}
