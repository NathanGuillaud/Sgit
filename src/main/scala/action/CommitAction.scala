package action

import java.io.File
import java.nio.file.{Files, Paths}

import action.Add.addElement
import model.Tree
import model.Commit
import util.FileManagement

import scala.io.Source

case class CommitAction()

object CommitAction {
  def commit(): Unit = {
    println("COMMIT")
    val treeForCommit = createTree()
    val commit = createCommit(treeForCommit)
    updateLogs(commit)
  }

  def createTree(): Tree = {
    val tree = new Tree()
    //Tree hash
    val treeHashValue = tree.generateId()
    println("Hash du tree à ajouter : " + treeHashValue)
    val treeFolderHash = treeHashValue.substring(0,2)
    val treeFileHash = treeHashValue.substring(2)

    //Add the tree file
    new File(".sgit/objects/tree/" + treeFolderHash).mkdirs()
    new File(".sgit/objects/tree/" + treeFolderHash + "/" + treeFileHash).createNewFile()
    FileManagement.writeFile(".sgit/objects/tree/" + treeFolderHash + "/" + treeFileHash, tree.toString())

    tree
  }

  def createCommit(t: Tree): Commit = {
    //Add commit
    val commit = new Commit(t)
    //Commit hash
    val commitHashValue = commit.generateId()
    println("Hash du commit à ajouter : " + commitHashValue)
    val commitFolderHash = commitHashValue.substring(0,2)
    val commitFileHash = commitHashValue.substring(2)

    //Add the commit file
    new File(".sgit/objects/commit/" + commitFolderHash).mkdirs()
    new File(".sgit/objects/commit/" + commitFolderHash + "/" + commitFileHash).createNewFile()
    FileManagement.writeFile(".sgit/objects/commit/" + commitFolderHash + "/" + commitFileHash, commit.toString())

    commit
  }

  def updateLogs(commit: Commit): Unit ={
    val currentBranch = getBranch()
    //If logs directory not exists
    if(Files.notExists(Paths.get(".sgit/logs"))){
      createLogDirectory(commit, currentBranch)
    }else{
      updateLogDirectory(commit, currentBranch)
    }
  }

  def createLogDirectory(commit: Commit, branch: String): Unit = {
    new File(".sgit/logs/refs/heads").mkdirs()
    new File(".sgit/logs/HEAD").createNewFile()
    FileManagement.writeFile(".sgit/logs/HEAD", commit.toStringForLogs())
    createLogFileForBranch(commit, branch)
  }

  def updateLogDirectory(commit: Commit, branch: String): Unit = {
    val headFileContent = Source.fromFile(".sgit/logs/HEAD").getLines.mkString("\n")
    FileManagement.writeFile(".sgit/logs/HEAD", commit.toStringForLogs() + "\n" + headFileContent)
    //If logs directory for the current branch not exists
    if(Files.notExists(Paths.get(".sgit/logs/refs/heads/" + branch))) {
      createLogFileForBranch(commit, branch)
    }
    //If logs directory for the current branch exists
    else {
      val branchFileContent = Source.fromFile(".sgit/logs/HEAD").getLines.mkString("\n")
      FileManagement.writeFile(".sgit/logs/refs/heads/" + branch, commit.toStringForLogs() + "\n" + branchFileContent)
    }
  }

  def createLogFileForBranch(commit: Commit, branch: String): Unit = {
    new File(".sgit/logs/refs/heads/" + branch).createNewFile()
    FileManagement.writeFile(".sgit/logs/refs/heads/" + branch, commit.toStringForLogs())
  }

  def getBranch(): String = {
    "master"
  }
}