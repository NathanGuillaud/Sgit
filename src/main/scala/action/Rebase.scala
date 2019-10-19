package action

import util.PathManagement

object Rebase {

  /**
   * TO DO
   * @param command : TO DO
   */
  def rebase(command: Array[String]): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      println("REBASE : TO DO")
    }
  }

  /**
   * TO DO
   * @param command : TO DO
   */
  def rebaseI(command: Array[String]): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      println("REBASE -I : TO DO")
    }
  }
}
