package util

import java.io.File

import scala.annotation.tailrec

object PathManagement {

  //Get the path of the parent for a directory
  //Return a String corresponding to the path, None if the path has no parent
  def getParentPath(path: String): Option[String] = {
    val pathSplit = path.split("/")
    if(pathSplit.length <= 1){
      None
    } else {
      var parentPath = ""
      var first_dir = true
      var index = 0
      pathSplit.map(x =>
        if(index < pathSplit.length-1){
          if(first_dir){
            parentPath = x
            first_dir = false
          } else {
            parentPath = parentPath + File.separator + x
          }
          index = index + 1
        }
      )
      Some(parentPath)
    }
  }

  //Get the absolute path of a the .sgit repository for the project
  @tailrec
  def getSgitPath(currentDirectory: File = new File(".")): Option[String] = {
    val currentCanonical = currentDirectory.getCanonicalFile()
    if (new File(s"${currentDirectory.getAbsolutePath()}${File.separatorChar}.sgit").isDirectory())
      Some(s"${currentCanonical.getAbsolutePath()}${File.separator}.sgit")
    else {
      if (currentCanonical.getParentFile() == null) None
      else getSgitPath(currentCanonical.getParentFile())
    }
  }

  def getProjectPath(): Option[String] = {
    if(PathManagement.getSgitPath().isEmpty) None
    else if(PathManagement.getParentPath(PathManagement.getSgitPath().get).isEmpty) None
    else Some(PathManagement.getParentPath(PathManagement.getSgitPath().get).get)
  }

  def getFilePathFromProjectRoot(filePath: String): Option[String] = {
    var projectPath = ""
    if(!getParentPath(getSgitPath().get).isEmpty) {
      projectPath = getParentPath(getSgitPath().get).get
    }
    if(projectPath.isEmpty) None
    else Some(filePath.substring(projectPath.length+1))
  }

}
