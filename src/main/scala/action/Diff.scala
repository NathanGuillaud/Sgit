package action

import util.PathManagement

object Diff {

  def diff(): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      println("DIFF")
    }
  }

}