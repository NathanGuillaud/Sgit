package util

import java.io.File
import java.nio.file.{Files, Paths}

import scala.io.Source

case class SgitTools()

object SgitTools {

  def getCurrentBranch(): String = {
    val headFileContent = Source.fromFile(".sgit/HEAD").getLines.mkString("\n")
    val contentSplit = headFileContent.split("/")
    contentSplit.last
  }

  //Get the current commit from references files
  def getCurrentCommit(currentBranch: String): String = {
    if(Files.exists(Paths.get(".sgit/refs/heads/" + currentBranch))) {
      FileManagement.readFile(new File(".sgit/refs/heads/" + currentBranch))
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