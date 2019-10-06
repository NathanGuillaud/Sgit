package model

import java.util.Date

import util.FileManagement

case class Commit(
                 var id: String = "",
                 val date: Date = new Date(),
                 val author: String = "Nathan Guillaud",
                 var treeId: String
                 ) {
  def this(t: Tree) = this("", new Date(), "Nathan Guillaud", t.id)

  def generateId(): String = {
    val hashId = FileManagement.hashTreeOrCommit(this.toString())
    this.id = hashId
    hashId
  }

  override def toString(): String = {
    "date " + this.date + "\n" + "author " + this.author + "\n" + "tree " + this.treeId
  }

  def toStringForLogs(): String = {
    this.id + "::" + this.author + "::" + this.date
  }
}

object Commit {
  def apply(tId: String): Commit = new Commit("", new Date(), "Nathan Guillaud", tId)
}
