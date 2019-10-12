package action

import java.io.File

import util.{FileManagement, SgitTools, StageManagement, PathManagement}

object Add {

  //Add arguments in the stage
  def add(command: Array[String]): Unit = {
    val currentBranch = SgitTools.getCurrentBranch()
    command.map(elem => addElement(new File(new File(elem).getAbsolutePath), currentBranch))
  }

  //Add one element to the stage (file or directory)
  def addElement(path: File, currentBranch: String): Unit = {
    if(path.isDirectory){
      val listFiles = FileManagement.getFilesFromDirectory(path)
      listFiles.map(file => addBlob(file, currentBranch))
    } else if(path.isFile) {
      addBlob(path, currentBranch)
    } else {
      println("The file or directory is unknow")
    }
  }

  //Add a blob
  def addBlob(path: File, currentBranch: String): Unit = {
    //Not add file if it is in .sgit directory
    if(!path.toString.contains(".sgit")) {
      //Get the name and the content of the file
      val decomposedFilePath = path.toString().split("/")
      val fileName = decomposedFilePath(decomposedFilePath.length-1)
      val fileContent = FileManagement.readFile(path)

      //Hachage du fichier
      val hashValue = FileManagement.hashFile(fileName, fileContent)
      val folderHash = hashValue.substring(0,2)
      val fileHash = hashValue.substring(2)

      //Création du blob dans son répertoire
      val blobDirectory = s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}blob${File.separator}${folderHash}"
      new File(blobDirectory).mkdirs()
      new File(blobDirectory + s"${File.separator}${fileHash}").createNewFile()
      FileManagement.writeFile(blobDirectory + s"${File.separator}${fileHash}", fileContent)

      StageManagement.addFileInStage(PathManagement.getFilePathFromProjectRoot(path.getPath).get, hashValue, currentBranch)
    }
  }

}
