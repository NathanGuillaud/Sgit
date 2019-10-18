package model

import java.io.File

import util.{FileManagement, PathManagement}

case class Tree(
               var id: String = "",
               var content: List[Element] = List()
               ) {

  def this(blobs: List[Element], trees: List[Element]) = {
    this()
    trees.map(t =>
      this.content = Element(t.path, t.hash, t.elemType) :: this.content
    )
    blobs.map(b =>
      this.content = Element(b.path, b.hash, b.elemType) :: this.content
    )
    this.id = generateId()
  }

  def this(elements: List[Element]) = {
    this()
    elements.map(element => this.content = Element(element.path, element.hash, element.elemType) :: this.content)
    this.id = generateId()
  }

  def generateId(): String = {
    FileManagement.hashTreeOrCommit(this.toString())
  }

  /*def fillWithBlobsAndTrees(blobs: List[Element], trees: List[Element]): Unit = {
    trees.map(t =>
      this.set_content(this.addElement(t.get_elem_type(), t.get_hash(), t.get_path()))
    )
    blobs.map(b =>
      this.set_content(this.addElement(b.get_elem_type(), b.get_hash(), b.get_path()))
    )
  }*/

  override def toString(): String = {
    var display: String = ""
    this.content.map(x => display = display + x.elemType + " " + x.hash + " " + x.path + "\n")
    display
  }

  def saveTreeFile(): Unit = {
    val treeHashValue = this.id
    val treeFolderHash = treeHashValue.substring(0,2)
    val treeFileHash = treeHashValue.substring(2)

    //Add the tree file
    val pathTreeDirectory = s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}tree${File.separator}${treeFolderHash}"
    new File(pathTreeDirectory).mkdirs()
    new File(pathTreeDirectory + s"${File.separator}${treeFileHash}").createNewFile()
    FileManagement.writeFile(pathTreeDirectory + s"${File.separator}${treeFileHash}", this.toString())
  }
}

object Tree {
  def apply(): Tree = {
    new Tree
  }


}
