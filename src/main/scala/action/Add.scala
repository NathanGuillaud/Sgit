package action

import java.io.File

import model.Blob
import util.{FileManagement, PathManagement, SgitTools}

object Add {

  //Add arguments in the stage
  def add(command: Array[String]): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      val currentBranch = SgitTools.getCurrentBranch()
      command.map(elem => addElement(new File(System.getProperty("user.dir") + File.separator + elem), currentBranch))
    }
  }

  //Add one element to the stage (file or directory)
  def addElement(path: File, currentBranch: String): Unit = {
    if(path.isDirectory){
      val listFiles = FileManagement.getFilesFromDirectory(path)
      listFiles.map(file => Blob.addBlob(file, currentBranch))
    } else if(path.isFile) {
      Blob.addBlob(path, currentBranch)
    } else {
      println("The file or directory is unknow")
    }
  }



}
