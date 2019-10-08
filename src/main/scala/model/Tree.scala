package model

import java.io.File

import util.FileManagement

case class Tree(
               var id: String = "",
               var content: List[(String,String,String)] = List()
               ) {

  def get_content(): List[(String,String,String)] = {
    this.content
  }

  def set_content(c: List[(String,String,String)]): Unit = {
    this.content = c
  }

  def get_id(): String = {
    this.id
  }

  def set_id(id: String): Unit = {
    this.id = id
  }

  def addElement(elementType: String, id: String, name: String): List[(String,String,String)] = {
    (elementType, id, name) :: get_content()
  }

  def generateId(): String = {
    FileManagement.hashTreeOrCommit(this.toString())
  }

  def fillWithBlobsAndTrees(blobs: List[(String, String)], trees: List[(String, String)]): Unit = {
    trees.map(t =>
      this.set_content(this.addElement("tree", t._2, t._1))
    )
    blobs.map(t =>
      this.set_content(this.addElement("blob", t._2, t._1))
    )
  }

  override def toString(): String = {
    var display: String = ""
    this.get_content().map(x => display = display + x._1 + " " + x._2 + " " + x._3 + "\n")
    display
  }

  def saveTreeFile(): Unit = {
    val treeHashValue = this.get_id()
    val treeFolderHash = treeHashValue.substring(0,2)
    val treeFileHash = treeHashValue.substring(2)

    //Add the tree file
    new File(".sgit/objects/tree/" + treeFolderHash).mkdirs()
    new File(".sgit/objects/tree/" + treeFolderHash + "/" + treeFileHash).createNewFile()
    FileManagement.writeFile(".sgit/objects/tree/" + treeFolderHash + "/" + treeFileHash, this.toString())
  }
}

object Tree {
  def apply(): Tree = {
    new Tree
  }


}
