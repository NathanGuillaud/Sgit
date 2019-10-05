package action

import java.io.File
import util.FileManagement
import model.Tree

import scala.io.Source

case class Add()

object Add {

  def add(command: Array[String]): Unit = {
    println("ADD")
    command.map(elem => addElement(elem, None))
  }

  def addElement(elem: String, parent: Option[Tree]): Unit = {
    if(new java.io.File(elem).isDirectory){
      addTree(elem, parent)
    } else if(new java.io.File(elem).isFile){
      addBlob(elem, parent)
    } else {
      println("Pas encore implémenté : " + elem)
    }
  }

  def addTree(folderPath: String, parent: Option[Tree]): Option[Tree] = {
    val tree = new Tree()
    //Add the content of the tree
    val files = FileManagement.getListOfFilesAndDirectories(folderPath)
    files.map(elem => addElement(elem.toString, Some(tree)))

    //Tree hash
    val hashValue = tree.generateId()
    println("Hash du tree à ajouter : " + hashValue)
    val folderHash = hashValue.substring(0,2)
    val fileHash = hashValue.substring(2)

    //Add the tree file
    new File(".sgit/objects/tree/" + folderHash).mkdirs()
    new File(".sgit/objects/tree/" + folderHash + "/" + fileHash).createNewFile()
    FileManagement.writeFile(".sgit/objects/tree/" + folderHash + "/" + fileHash, tree.toString())

    //Add the tree to his parent (if he has one)
    if(!parent.isEmpty){
      parent.get.addElement("tree", hashValue)
    }
    parent
  }

  def addBlob(filePath: String, parent: Option[Tree]): Option[Tree] = {
    //Get the name and the content of the file
    val decomposedFilePath = filePath.split("/")
    val fileName = decomposedFilePath(decomposedFilePath.length-1)
    val fileContent = Source.fromFile(filePath).getLines.mkString

    //Hachage du fichier
    val hashValue = FileManagement.hashFile(fileName, fileContent)
    println("Hash du blob à ajouter : " + hashValue)
    val folderHash = hashValue.substring(0,2)
    val fileHash = hashValue.substring(2)

    //Création du blob dans son répertoire
    new File(".sgit/objects/blob/" + folderHash).mkdirs()
    new File(".sgit/objects/blob/" + folderHash + "/" + fileHash).createNewFile()
    FileManagement.writeFile(".sgit/objects/blob/" + folderHash + "/" + fileHash, fileContent)

    //Ajout du blob à son parent (s'il en a un)
    if(!parent.isEmpty){
      parent.get.addElement("blob", hashValue)
    }
    parent
  }

}
