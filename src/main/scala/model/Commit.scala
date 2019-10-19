package model

import java.io.File
import java.util.Date

import util.{FileManagement, PathManagement}

case class Commit(
                 var id: String = "",
                 date: Date = new Date(),
                 author: String = "Nathan Guillaud",
                 var treeId: String = "",
                 var parentCommit: String = ""
                 ) {
  /**
   * Commit constructor
   * @param treeId : The id of the root tree
   * @param parentCommit : The parent commit
   */
  def this(treeId: String, parentCommit: String) = {
    this("", new Date(), "Nathan Guillaud", treeId, parentCommit)
    this.id = generateId()
  }

  /**
   * Generate a hash with the content of a commit
   * @return a String (hash)
   */
  def generateId(): String = {
    FileManagement.hashTreeOrCommit(this.toString())
  }

  /**
   * Get all the content for a commit
   * @return a String with all the attributes of a commit
   */
  override def toString(): String = {
    "date::" + this.date + "\n" + "author::" + this.author + "\n" + "tree::" + this.treeId + "\n" + "parent::" + this.parentCommit
  }

  /**
   * Get the line to add to logs for a commit
   * @return a Sring that correspond to a line in logs
   */
  def toStringForLogs(): String = {
    this.id + "::" + this.author + "::" + this.date
  }

  /**
   * Save a commit into objects
   */
  def saveCommitFile(): Unit = {
    val commitHashValue = this.id
    val commitFolderHash = commitHashValue.substring(0,2)
    val commitFileHash = commitHashValue.substring(2)

    //Add the tree file
    val pathCommitDirectory = s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}commit${File.separator}${commitFolderHash}"
    new File(pathCommitDirectory).mkdirs()
    new File(pathCommitDirectory + s"${File.separator}${commitFileHash}").createNewFile()
    FileManagement.writeFile(pathCommitDirectory + s"${File.separator}${commitFileHash}", this.toString())
  }

}

object Commit {

  /**
   * Retrieve the root tree for the commit
   * @param commitHash : hash of the commit concerned
   * @return The hash of the root tree of the commit
   */
  def getTreeForCommit(commitHash: String): String = {
    val commitFolder = commitHash.substring(0,2)
    val commitFile = commitHash.substring(2)
    val commitContent = FileManagement.readFile(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}commit${File.separator}${commitFolder}${File.separator}${commitFile}")).split("\n").map(x => x.split("::"))
    commitContent.filter(line => line(0) == "tree").map(line => line(1)).last
  }

  /**
   * Get the parent commit of a commit
   * @param commitHash : hash of the commit child
   * @return the parent commit of the commit in parameters.
   *         If the commit is the first, return "Nil"
   */
  def getParentCommit(commitHash: String): String = {
    val commitFolder = commitHash.substring(0,2)
    val commitFile = commitHash.substring(2)
    val commitContent = FileManagement.readFile(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}commit${File.separator}${commitFolder}${File.separator}${commitFile}")).split("\n").map(x => x.split("::"))
    commitContent.filter(line => line(0) == "parent").map(line => line(1)).last
  }
}
