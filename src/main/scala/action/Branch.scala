package action

import java.io.File
import java.nio.file.{Files, Paths}

import util.{FileManagement, SgitTools}

import scala.io.Source

case class Branch()

object Branch {

  def branch(command: Array[String]): Unit = {
    //Retrieve current branch
    val currentBranch = SgitTools.getCurrentBranch()
    if(Files.notExists(Paths.get(".sgit/refs/heads/" + currentBranch))) {
      println("You have to make a first commit before create a new branch")
    }
    //If the branch already exists
    else if(Files.exists(Paths.get(".sgit/refs/heads/" + command(0)))){
      println("The branch " + command(0) + " already exists")
    } else {
      //Retrieve current commit
      val currentCommit = Source.fromFile(".sgit/refs/heads/" + currentBranch).getLines.mkString("\n")
      //Create new file for stage
      new File(s".sgit/stages/${command(0)}").createNewFile()
      //Write head into refs
      new File(".sgit/refs/heads/" + command(0)).createNewFile()
      FileManagement.writeFile(".sgit/refs/heads/" + command(0), currentCommit)
      //Write in logs
      val lastCommitForCurrentBranch = Source.fromFile(".sgit/logs/refs/heads/" + currentBranch).getLines.toArray.last
      new File(".sgit/logs/refs/heads/" + command(0)).createNewFile()
      FileManagement.writeFile(".sgit/logs/refs/heads/" + command(0), lastCommitForCurrentBranch + "::branch: Created from " + currentBranch)
    }
  }

  def branchAV(): Unit = {
    if(Files.exists(Paths.get(".sgit/refs/heads"))) {
      val currentBranch = SgitTools.getCurrentBranch()
      val branches = FileManagement.getListOfFilesAndDirectories(".sgit/refs/heads/")
      branches.map(branchFile => printBranch(branchFile.toString.split("/").last, Source.fromFile(branchFile).getLines.mkString("\n"), currentBranch))
    }
  }

  def printBranch(branchName: String, lastCommit: String, currentBranch: String): Unit = {
    if(branchName == currentBranch) {
      println("* " + branchName + " " + lastCommit)
    } else {
      println("  " + branchName + " " + lastCommit)
    }
  }

}
