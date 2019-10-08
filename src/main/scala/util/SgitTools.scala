package util

import java.io.File
import java.nio.file.{Files, Paths}

import scala.io.Source

case class SgitTools()

object SgitTools {

  def getCurrentBranch(): String = {
    val headFileContent = Source.fromFile(s".sgit${File.separator}HEAD").getLines.mkString("\n")
    val contentSplit = headFileContent.split("/")
    contentSplit.last
  }

  //Get the current commit from references files
  def getCurrentCommit(currentBranch: String): String = {
    if(Files.exists(Paths.get(s".sgit${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"))) {
      FileManagement.readFile(new File(s".sgit${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"))
    } else {
      "Nil"
    }
  }

  //Get the path of the parent for a directory
  //Return a String corresponding to the path, None if the path has no parent
  def getParentPath(path: String): Option[String] = {
    val pathSplit = path.split("/")
    if(pathSplit.length <= 1){
      None
    } else {
      var parentPath = ""
      var first_dir = true
      val lastValue = pathSplit.last
      pathSplit.map(x => if(x != lastValue){
        if(first_dir){
          parentPath = x
          first_dir = false
        } else {
          parentPath = parentPath + File.separator + x
        }
      })
      Some(parentPath)
    }
  }

}