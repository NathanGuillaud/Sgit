package model

import java.util.Date

import util.FileManagement

case class Commit(
                 val id: Int = 0,
                 val date: Date = new Date(),
                 val author: String = "Nathan Guillaud",
                 var tree: Tree
                 ) {
  def this(t: Tree) = this(0, new Date(), "Nathan Guillaud", t)

  def generateId(): String = {
    FileManagement.hashTreeOrCommit(this.toString())
  }

  override def toString(): String = {
    "date " + this.date + "\n" + "author " + this.author + "\n" + "tree " + this.tree
  }
}

object Commit {
  def apply(t: Tree): Commit = new Commit(0, new Date(), "Nathan Guillaud", t)
}
