package action

import java.io.File
import java.nio.file.{Files, Paths}

import util.{FileManagement, PathManagement, SgitTools}

import scala.io.Source

object Tag {

  /**
   * Create a new tag
   * @param command : arguments given with the name of the tag to create
   */
  def tag(command: Array[String]): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      //Retrieve current branch
      val currentBranch = SgitTools.getCurrentBranch()
      val pathRefsHeads = s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads${File.separator}"
      val pathTags = s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}tags"
      if(Files.notExists(Paths.get(pathRefsHeads + s"${currentBranch}"))) {
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
}