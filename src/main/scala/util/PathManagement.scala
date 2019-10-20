package util

import java.io.File

import scala.annotation.tailrec

object PathManagement {

  /**
   * Get the path of the parent for a directory
   * @param path : the path which we want his parent
   * @return the parent path of the given path if exists
   */
  def getParentPath(path: String): Option[String] = {
    val pathSplited: List[String] = path.split("/").toList
    if (pathSplited.length <= 1) None
    else {
      val parentPath = pathSplited.init.map(x => x+File.separator).mkString.dropRight(1)
      Some(parentPath)
    }
  }

  /**
   * Get the absolute path of a the .sgit repository for the project
   * @param currentDirectory : the source directory to search the sgit path
   * @return the path of the closest .sgit if exists
   */
  @tailrec
  def getSgitPath(currentDirectory: File = new File(System.getProperty("user.dir"))): Option[String] = {
    val currentCanonical = currentDirectory.getCanonicalFile()
    if (new File(s"${currentDirectory.getAbsolutePath()}${File.separatorChar}.sgit").isDirectory())
      Some(s"${currentCanonical.getAbsolutePath()}${File.separator}.sgit")
    else {
      if (currentCanonical.getParentFile() == null) None
      else getSgitPath(currentCanonical.getParentFile())
    }
  }

  /**
   * Get the project path from the home directory
   * @return the path if exists
   */
  def getProjectPath(): Option[String] = {
    if(PathManagement.getSgitPath().isEmpty) None
    else if(PathManagement.getParentPath(PathManagement.getSgitPath().get).isEmpty) None
    else Some(PathManagement.getParentPath(PathManagement.getSgitPath().get).get)
  }

  /**
   * Get the path of a file from the root of the project
   * @param filePath : the path of the file concerned
   * @return the path of the file from the root of the project
   */
  def getFilePathFromProjectRoot(filePath: String): Option[String] = {
    val projectPath = if(!getSgitPath().isEmpty && !getParentPath(getSgitPath().get).isEmpty) getParentPath(getSgitPath().get).get else ""
    if(projectPath.isEmpty) None
    else Some(filePath.substring(projectPath.length+1))
  }

}
