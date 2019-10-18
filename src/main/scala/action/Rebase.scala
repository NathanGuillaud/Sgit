package action

import util.PathManagement

object Rebase {

  def rebase(command: Array[String]): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      println("REBASE : TO DO")
    }
  }

  def rebaseI(command: Array[String]): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      println("REBASE -I : TO DO")
    }
  }
}
