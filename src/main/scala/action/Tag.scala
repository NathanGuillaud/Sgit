package action

import java.io.File
import java.nio.file.{Files, Paths}

import util.{FileManagement, PathManagement, SgitTools}

import scala.io.Source

object Tag {
  def tag(command: Array[String]): Unit = {
    val currentBranch = SgitTools.getCurrentBranch()
    val pathTags = s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}tags"
    if(Files.notExists(Paths.get(pathTags))) {
      println("You have to make a first commit before create a new tag")
    }
    //If the tag already exists
    else if(Files.exists(Paths.get(pathTags + s"${File.separator}${command(0)}"))){
      println("The tag " + command(0) + " already exists")
    } else {
      //Retrieve current commit
      val currentCommit = SgitTools.getCurrentCommit(currentBranch)
      //Write head into refs
      new File(pathTags + s"${File.separator}${command(0)}").createNewFile()
      FileManagement.writeFile(pathTags + s"${File.separator}${command(0)}", currentCommit)
    }
  }
}