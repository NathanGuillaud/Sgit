package model

import java.util.Date

import util.FileManagement

case class Commit(
                 var id: String = "",
                 val date: Date = new Date(),
                 val author: String = "Nathan Guillaud",
                 var tree: Tree
                 ) {
  def this(t: Tree) = this("", new Date(), "Nathan Guillaud", t)

  def generateId(): String = {
    val hashId = FileManagement.hashTreeOrCommit(this.toString())
    this.id = hashId
    hashId
  }

  override def toString(): String = {
    "date " + this.date + "\n" + "author " + this.author + "\n" + "tree " + this.tree
  }
}

object Commit {
  def apply(t: Tree): Commit = new Commit("", new Date(), "Nathan Guillaud", t)
}
