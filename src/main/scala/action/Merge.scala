package action

import util.PathManagement

object Merge {

  def merge(command: Array[String]): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      println("MERGE " + command.toString)
    }
  }

}