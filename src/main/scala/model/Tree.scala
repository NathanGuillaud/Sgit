package model

import util.FileManagement

case class Tree(
               var id: String = "",
               var content: List[(String,String)] = List()
               ) {
  def addElement(elementType: String, id: String){
    this.content = content.appended(elementType, id)
  }

  def generateId(): String = {
    val hashId = FileManagement.hashTreeOrCommit(this.toString())
    this.id = hashId
    hashId
  }

  override def toString(): String = {
    var display: String = ""
    this.content.map(x => display = display + x._1 + " " + x._2 + "\n")
    //println("Contenu final du tree : " + display)
    display
  }
}

object Tree {
  def apply(): Tree = {
    val tree = new Tree
    tree
  }
}
