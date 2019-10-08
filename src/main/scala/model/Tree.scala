package model

import java.io.File

import util.FileManagement

case class Tree(
               var id: String = "",
               var content: List[Element] = List()
               ) {

  def get_content(): List[Element] = {
    this.content
  }

  def set_content(c: List[Element]): Unit = {
    this.content = c
  }

  def get_id(): String = {
    this.id
  }

  def set_id(id: String): Unit = {
    this.id = id
  }

  def addElement(elementType: String, id: String, name: String): List[Element] = {
    new Element(name, id, elementType) :: get_content()
  }

  def generateId(): String = {
    FileManagement.hashTreeOrCommit(this.toString())
  }

  def fillWithBlobsAndTrees(blobs: List[Element], trees: List[Element]): Unit = {
    trees.map(t =>
      this.set_content(this.addElement(t.get_elem_type(), t.get_hash(), t.get_path()))
    )
    blobs.map(b =>
      this.set_content(this.addElement(b.get_elem_type(), b.get_hash(), b.get_path()))
    )
  }

  override def toString(): String = {
    var display: String = ""
    this.get_content().map(x => display = display + x.get_elem_type() + " " + x.get_hash() + " " + x.get_path() + "\n")
    display
  }

  def saveTreeFile(): Unit = {
    val treeHashValue = this.get_id()
    val treeFolderHash = treeHashValue.substring(0,2)
    val treeFileHash = treeHashValue.substring(2)

    //Add the tree file
    new File(s".sgit${File.separator}objects${File.separator}tree${File.separator}${treeFolderHash}").mkdirs()
    new File(s".sgit${File.separator}objects${File.separator}tree${File.separator}${treeFolderHash}${File.separator}${treeFileHash}").createNewFile()
    FileManagement.writeFile(s".sgit${File.separator}objects${File.separator}tree${File.separator}${treeFolderHash}${File.separator}${treeFileHash}", this.toString())
  }
}

object Tree {
  def apply(): Tree = {
    new Tree
  }


}
