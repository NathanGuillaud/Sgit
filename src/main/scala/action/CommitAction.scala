package action

import java.io.File
import java.nio.file.{Files, Paths}

import model.{Commit, Tree}
import util.FileManagement
import util.SgitTools
import util.LogWriter

case class CommitAction()

object CommitAction {

  def commit(): Unit = {
    val currentBranch = SgitTools.getCurrentBranch()
    //If the stage is empty, nothing to commit
    if(FileManagement.readFile(new File(s".sgit/stages/${currentBranch}")) == "") {
      println("Nothing to commit")
    } else {
      val (stage, rootBlobs) = getStageFiles(currentBranch)
      val rootTrees = addTrees(stage, None)

      //Create the tree for commit
      val treeForCommit = new Tree()
      treeForCommit.fillWithBlobsAndTrees(rootBlobs, rootTrees)
      treeForCommit.set_id(treeForCommit.generateId())

      //Create the new commit
      val currentCommitId = getCurrentCommit(currentBranch)
      val commit = new Commit(treeForCommit.get_id(), currentCommitId)
      commit.set_id(commit.generateId())

      //Write commit in logs, refs and objects
      LogWriter.updateLogs(commit, currentBranch)
      updateRef(commit, currentBranch)
      commit.saveCommitFile()

      //Delete content from the stage of the current branch
      FileManagement.writeFile(".sgit/stages/" + currentBranch, "")

      println("[" + currentBranch + " " + commit.id + "]")
    }
  }

  //Returns a list containing the path to a file that has been converted to a Blob (because it's in the STAGE) and its Hash
  //OUTPUT is something like this:
  //(src/main/scala/objects,a7dbb76b0406d104b116766a40f2e80a79f40a0349533017253d52ea750d9144)
  //(src/main/scala/utils,29ee69c28399de6f830f3f0f55140ad97c211fc851240901f9e030aaaf2e13a0)
  def getStageFiles(currentBranch: String): (List[(String,String,String)], List[(String, String)]) = {
    var rootBlobs = List[(String, String)]()
    //Retrieve useful data
    val stage = new File(s".sgit${File.separator}stages${File.separator}${currentBranch}")
    val files = FileManagement.readFile(stage)

    //Split lines
    val stage_content = files.split("\n").map(x => x.split(" "))

    //Cleaning from the filenames
    var paths = List[String]()
    stage_content.map(x =>
      if(getParentPath(x(0)).isEmpty) {
        rootBlobs = (x(0), x(1)) :: rootBlobs
      } else {
        paths = getParentPath(x(0)).get :: paths
      }
    )
    var hashs = List[String]()
    stage_content.map(x =>
      if(!getParentPath(x(0)).isEmpty) {
        hashs = x(1) :: hashs
      }
    )
    val blob = List.fill(paths.size)("blob")

    //Merging the result
    ((paths,hashs,blob).zipped.toList, rootBlobs)
  }

  //def getBlobsAtRoot(currentBranch: String):

  def addTrees(l: List[(String, String, String)], rootTrees: Option[List[(String, String)]]): List[(String, String)] = {
    if(l.size == 0){
      if(rootTrees.isEmpty) {
        List[(String, String)]()
      } else {
        rootTrees.get
      }
    } else {
      val (deeper, rest, parent) = getDeeperDirectory(l)
      val hash = createTree(deeper)
      if(parent.isEmpty) {
        if (rootTrees.isEmpty){
          addTrees(rest, Some(List((deeper(0)._1, hash))))
        } else {
          addTrees(rest, Some((deeper(0)._1, hash) :: rootTrees.get))
        }
      } else {
        addTrees((parent.get, hash, "tree") :: rest, rootTrees)
      }
    }
  }

  //Return the name of the tree and the hash
  def createTree(deeper: List[(String, String, String)]): String = {
    val tree = new Tree()
    deeper.map(element => tree.set_content(tree.addElement(element._3, element._2, element._1)))
    val hash = tree.generateId()
    tree.set_id(hash)
    tree.saveTreeFile()
    tree.get_id()
  }

  def getDeeperDirectory(l: List[(String, String, String)]): (List[(String,String, String)], List[(String,String, String)], Option[String]) = {
    var max = 0
    var pathForMax = ""

    l.map(line => if (line._1.split("/").size >= max) {
      max = line._1.split("/").size
      pathForMax = line._1
    })

    val rest = l.filter(x => !(x._1.equals(pathForMax)))
    val deepest = l.filter(x => x._1.equals(pathForMax))

    val parentPath = getParentPath(pathForMax)
    (deepest, rest, parentPath)
  }

  def getParentPath(path: String): Option[String] = {
    val pathSplit = path.split("/")
    if(pathSplit.length <= 1){
      None
    } else {
      var parentPath = ""
      var first_dir = true
      val lastValue = pathSplit.last
      pathSplit.map(x => if(x != lastValue){
        if(first_dir){
          parentPath = x
          first_dir = false
        } else {
          parentPath = parentPath + File.separator + x
        }
      })
      Some(parentPath)
    }
  }

  def updateRef(commit: Commit, currentBranch: String): Unit = {
    if(Files.notExists(Paths.get(".sgit/refs/heads/" + currentBranch))) {
      new File(".sgit/refs/heads/" + currentBranch).createNewFile()
    }
    //Update the HEAD of the branch
    FileManagement.writeFile(".sgit/refs/heads/" + currentBranch, commit.id)
  }

  def getCurrentCommit(currentBranch: String): String = {
    if(Files.exists(Paths.get(".sgit/refs/heads/" + currentBranch))) {
      FileManagement.readFile(new File(".sgit/refs/heads/" + currentBranch))
    } else {
      "Nil"
    }
  }

}