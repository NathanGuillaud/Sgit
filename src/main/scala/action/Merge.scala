package action

import util.PathManagement

object Merge {

  /**
   * Merge 2 branches
   * @param command : the argument given with the name of the branch to merge to the current branch
   */
  def merge(command: Array[String]): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      println("MERGE : TO DO")
    }
  }

}