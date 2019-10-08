package action

import java.io.File
import java.nio.file.{Files, Paths}

import util.{FileManagement, SgitTools}

import scala.io.Source

case class Tag()

object Tag {
  def tag(command: Array[String]): Unit = {
    val currentBranch = SgitTools.getCurrentBranch()
    if(Files.notExists(Paths.get(s".sgit${File.separator}refs${File.separator}tags"))) {
      println("You have to make a first commit before create a new tag")
    }
    //If the tag already exists
    else if(Files.exists(Paths.get(s".sgit${File.separator}refs${File.separator}tags${File.separator}${command(0)}"))){
      println("The tag " + command(0) + " already exists")
    } else {
      //Retrieve current commit
      val currentCommit = Source.fromFile(s".sgit${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.mkString("\n")
      //Write head into refs
      new File(s".sgit${File.separator}refs${File.separator}tags${File.separator}${command(0)}").createNewFile()
      FileManagement.writeFile(s".sgit${File.separator}refs${File.separator}tags${File.separator}${command(0)}", currentCommit)
    }
  }
}