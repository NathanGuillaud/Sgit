package action

import java.io.File
import java.nio.file.{Files, Paths}

import action.CommitAction.createLogDirectory
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
      new File(".sgit/refs/heads/" + command(0)).createNewFile()
      FileManagement.writeFile(".sgit/refs/heads/" + command(0), currentCommit)
    }
  }

  def branchAV(): Unit = {
    println("BRANCH -AV")
  }

}
