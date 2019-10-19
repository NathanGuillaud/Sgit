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

  override def toString(): String = {
    this.content.map(x => x.elemType + " " + x.hash + " " + x.path).mkString("\n")
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

  //Create a tree and return his hash value
  def createTree(deeper: List[Element]): String = {
    val tree = new Tree(deeper)
    tree.saveTreeFile()
    tree.id
  }

}
