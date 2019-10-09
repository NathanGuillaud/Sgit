package action

import java.io.File
import java.nio.file.{Files, Paths}

import util.{FileManagement, SgitTools}

case class Add()

object Add {

  //Add arguments in the stage
  def add(command: Array[String]): Unit = {
    val currentBranch = SgitTools.getCurrentBranch()
    command.map(elem => addElement(new File(elem), currentBranch))
  }

  //Add one element to the stage (file or directory)
  def addElement(path: File, currentBranch: String): Unit = {
    if(path.isDirectory){
      val listFiles = FileManagement.exploreDirectory(path)
      listFiles.map(file => addBlob(file, currentBranch))
    } else if(path.isFile) {
      addBlob(path, currentBranch)
    } else {
      println("Not implemented yet")
    }
  }

  //Add a blob
  def addBlob(path: File, currentBranch: String): Unit = {
    //Get the name and the content of the file
    val decomposedFilePath = path.toString().split("/")
    val fileName = decomposedFilePath(decomposedFilePath.length-1)
    val fileContent = FileManagement.readFile(path)

    //Hachage du fichier
    val hashValue = FileManagement.hashFile(fileName, fileContent)
    val folderHash = hashValue.substring(0,2)
    val fileHash = hashValue.substring(2)

    //Création du blob dans son répertoire
    new File(s".sgit${File.separator}objects${File.separator}blob${File.separator}${folderHash}").mkdirs()
    new File(s".sgit${File.separator}objects${File.separator}blob${File.separator}${folderHash}${File.separator}${fileHash}").createNewFile()
    FileManagement.writeFile(s".sgit${File.separator}objects${File.separator}blob${File.separator}${folderHash}/${fileHash}", fileContent)

    addFileInStage(path, hashValue, currentBranch)
  }

  //Add a file to the stage
  def addFileInStage(path: File, hashId: String, currentBranch: String): Unit = {
    if(Files.notExists(Paths.get(s".sgit${File.separator}stages${File.separator}${currentBranch}"))) {
      new File(s".sgit${File.separator}stages${File.separator}${currentBranch}").createNewFile()
    } else {
      //Remove the last version of the file from the stage
      removeFileFromStage(path.toString, currentBranch)
    }
    //Add the new version of the file to the stage
    val stageContent = FileManagement.readFile(new File(s".sgit${File.separator}stages${File.separator}" + currentBranch))
    FileManagement.writeFile(s".sgit${File.separator}stages${File.separator}${currentBranch}", stageContent + path.toString + " " + hashId + "\n")
  }

  //Remove a file from the stage if the file exists on the stage
  def removeFileFromStage(filePath: String, currentBranch: String): Unit = {
    val stage = new File(s".sgit${File.separator}stages${File.separator}${currentBranch}")
    val files = FileManagement.readFile(stage)
    val stageContent = files.split("\n").map(x => x.split(" "))
    if(stageContent.length > 0 && files != "") {
      var newContent = ""
      stageContent.map(line =>
        if(line(0) != filePath) {
          newContent = newContent + line(0) + " " + line(1) + "\n"
        }
      )
      //Write new content in stage
      FileManagement.writeFile(s".sgit${File.separator}stages${File.separator}${currentBranch}", newContent)
    }
  }

}
