package action

import java.io.File
import java.nio.file.{Files, Paths}

import util.{FileManagement, SgitTools}

case class Add()

object Add {

  def add(command: Array[String]): Unit = {
    val currentBranch = SgitTools.getCurrentBranch()
    command.map(elem => addElement(new File(elem), currentBranch))
  }

  def addElement(path: File, currentBranch: String): Unit = {
    if(path.isDirectory){
      val listFiles = exploreDirectory(path)
      listFiles.map(file => addBlob(file, currentBranch))
    } else if(path.isFile) {
      addBlob(path, currentBranch)
    } else {
      println("Not implemented yet")
    }
  }

  def exploreDirectory(path: File): List[File] = {
    val allFiles = path.listFiles().toList
    allFiles.flatMap(elem =>
        if (elem.isDirectory) {
          exploreDirectory(elem)
        }
        else {
          List(elem)
        }
    )
  }

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
    new File(".sgit/objects/blob/" + folderHash).mkdirs()
    new File(".sgit/objects/blob/" + folderHash + "/" + fileHash).createNewFile()
    FileManagement.writeFile(".sgit/objects/blob/" + folderHash + "/" + fileHash, fileContent)

    addFileInStage(path, hashValue, currentBranch)
  }

  def addFileInStage(path: File, hashId: String, currentBranch: String): Unit = {
    if(Files.notExists(Paths.get(".sgit/stages/" + currentBranch))) {
      new File(".sgit/stages/" + currentBranch).createNewFile()
    }
    val stageContent = FileManagement.readFile(new File(".sgit/stages/" + currentBranch))
    FileManagement.writeFile(".sgit/stages/" + currentBranch, stageContent + path.toString + " " + hashId + "\n")
  }

}
