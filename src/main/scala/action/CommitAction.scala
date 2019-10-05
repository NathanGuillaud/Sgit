package action

import java.io.File

import action.Add.addElement
import model.Tree
import model.Commit
import util.FileManagement

case class CommitAction()

object CommitAction {
  def commit(): Unit = {
    println("COMMIT")

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

    //Add commit
    val commit = new Commit(tree)
    //Commit hash
    val commitHashValue = commit.generateId()
    println("Hash du tree à ajouter : " + commitHashValue)
    val commitFolderHash = commitHashValue.substring(0,2)
    val commitFileHash = commitHashValue.substring(2)

    //Add the commit file
    new File(".sgit/objects/commit/" + commitFolderHash).mkdirs()
    new File(".sgit/objects/commit/" + commitFolderHash + "/" + commitFileHash).createNewFile()
    FileManagement.writeFile(".sgit/objects/commit/" + commitFolderHash + "/" + commitFileHash, commit.toString())
  }
}