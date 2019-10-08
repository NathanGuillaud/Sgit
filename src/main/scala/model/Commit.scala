package model

import java.io.File
import java.util.Date

import util.FileManagement

case class Commit(
                 var id: String = "",
                 val date: Date = new Date(),
                 val author: String = "Nathan Guillaud",
                 var treeId: String = "",
                 var parentCommit: String = ""
                 ) {
  def this(treeId: String, parentCommit: String) = this("", new Date(), "Nathan Guillaud", treeId, parentCommit)

  def get_id(): String = {
    this.id
  }

  def set_id(id: String): Unit = {
    this.id = id
  }

  def get_date(): Date = {
    this.date
  }

  def get_author(): String = {
    this.author
  }

  def get_tree_id(): String = {
    this.treeId
  }

  def get_parent_commit(): String = {
    this.parentCommit
  }

  def generateId(): String = {
    FileManagement.hashTreeOrCommit(this.toString())
  }

  override def toString(): String = {
    "date " + this.get_date() + "\n" + "author " + this.get_author() + "\n" + "tree " + this.get_tree_id() + "\n" + "parent " + this.get_parent_commit()
  }

  def toStringForLogs(): String = {
    this.id + "::" + this.author + "::" + this.date
  }

  def saveCommitFile(): Unit = {
    val commitHashValue = this.get_id()
    val commitFolderHash = commitHashValue.substring(0,2)
    val commitFileHash = commitHashValue.substring(2)

    //Add the tree file
    new File(".sgit/objects/commit/" + commitFolderHash).mkdirs()
    new File(".sgit/objects/commit/" + commitFolderHash + "/" + commitFileHash).createNewFile()
    FileManagement.writeFile(".sgit/objects/commit/" + commitFolderHash + "/" + commitFileHash, this.toString())
  }
}

object Commit {
  //def apply(tId: String): Commit = new Commit("", new Date(), "Nathan Guillaud", tId)
}
