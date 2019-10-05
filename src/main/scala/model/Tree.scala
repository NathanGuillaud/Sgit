package model

import util.FileManagement

case class Tree(
               var id: Int = 0,
               var content: List[(String,String)] = List()
               ) {
  def addElement(elementType: String, id: String){
    this.content = content.appended(elementType, id)
  }

  def generateId(): String = {
    FileManagement.hashTree(this.toString())
  }

  override def toString(): String = {
    var display: String = ""
    this.content.map(x => display = display + x._1 + " " + x._2 + "\n")
    println("Contenu final du tree : " + display)
    display
  }
}

object Tree {
  def apply(): Tree = {
    var tree = new Tree
    tree
  }
}
