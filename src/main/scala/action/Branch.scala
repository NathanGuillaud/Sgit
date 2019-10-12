package action

import java.io.File
import java.nio.file.{Files, Paths}

import util.{FileManagement, PathManagement, SgitTools}

import scala.io.Source

object Branch {

  //Create a branch with the name of the arg
  def branch(command: Array[String]): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      //Retrieve current branch
      val currentBranch = SgitTools.getCurrentBranch()
      val pathRefsHeads = s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads${File.separator}"
      val pathLogsNewBranch = s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${command(0)}"
      if(Files.notExists(Paths.get(pathRefsHeads + s"${currentBranch}"))) {
        println("You have to make a first commit before create a new branch")
      }
      //If the branch already exists
      else if(Files.exists(Paths.get(s"${pathRefsHeads}${command(0)}"))){
        println("The branch " + command(0) + " already exists")
      } else {
        //Retrieve current commit
        val currentCommit = FileManagement.readFile(new File(s"${pathRefsHeads}${currentBranch}"))

        //Create new file for stage
        val currentStageContent = FileManagement.readFile(new File(s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"))
        FileManagement.writeFile(s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${command(0)}", currentStageContent)

        //Write head into refs
        FileManagement.writeFile(s"${pathRefsHeads}${command(0)}", currentCommit)

        //Write in logs
        val lastCommitForCurrentBranch = FileManagement.readFile(new File(s"${pathRefsHeads}${currentBranch}")).split("\n").last
        FileManagement.writeFile(pathLogsNewBranch, lastCommitForCurrentBranch + "::branch: Created from " + currentBranch)
      }
    }
  }

  //List all the branches and give the current branch
  def branchAV(): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      val pathRefsHeads = s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads"
      if(Files.exists(Paths.get(pathRefsHeads))) {
        val currentBranch = SgitTools.getCurrentBranch()
        val branches = FileManagement.getListOfFilesAndDirectories(pathRefsHeads + s"${File.separator}")
        branches.map(branchFile => printBranch(branchFile.toString.split("/").last, Source.fromFile(branchFile).getLines.mkString("\n"), currentBranch))
      }
    }
  }

  //Print a branch
  def printBranch(branchName: String, lastCommit: String, currentBranch: String): Unit = {
    if(branchName == currentBranch) {
      println(Console.GREEN + "* " + branchName + "\t" + lastCommit + Console.WHITE)
    } else {
      println("  " + branchName + "\t" + lastCommit)
    }
  }

}
