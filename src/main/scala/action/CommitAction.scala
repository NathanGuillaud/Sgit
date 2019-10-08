package action

import java.io.File
import java.nio.file.{Files, Paths}

import model.{Commit, Tree, Element}
import util.FileManagement
import util.SgitTools
import util.LogWriter

case class CommitAction()

object CommitAction {

  //Commit all the files add previously (on the stage)
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
      treeForCommit.saveTreeFile()

      //Create the new commit
      val currentCommitId = SgitTools.getCurrentCommit(currentBranch)
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

  //Create a list with all elements of the stage
  def getStageFiles(currentBranch: String): (List[Element], List[Element]) = {
    var rootBlobs = List[Element]()
    //Retrieve useful data
    val stage = new File(s".sgit${File.separator}stages${File.separator}${currentBranch}")
    val files = FileManagement.readFile(stage)

    //Split lines
    val stage_content = files.split("\n").map(x => x.split(" "))

    //Cleaning from the filenames
    var paths = List[String]()
    stage_content.map(x =>
      if(SgitTools.getParentPath(x(0)).isEmpty) {
        rootBlobs = new Element(x(0), x(1), "blob") :: rootBlobs
      } else {
        paths = SgitTools.getParentPath(x(0)).get :: paths
      }
    )
    var hashs = List[String]()
    stage_content.map(x =>
      if(!SgitTools.getParentPath(x(0)).isEmpty) {
        hashs = x(1) :: hashs
      }
    )
    val blob = List.fill(paths.size)("blob")

    //Merging the result
    var elements = List[Element]()
    (paths,hashs,blob).zipped.toList.map(elem =>
      elements = new Element(elem._1, elem._2, elem._3) :: elements
    )
    (elements, rootBlobs)
  }

  //Add trees with a list of paths added
  //Return a list with trees at root (name of the path and hash of the tree)
  def addTrees(l: List[Element], rootTrees: Option[List[Element]]): List[Element] = {
    if(l.size == 0){
      if(rootTrees.isEmpty) {
        List[Element]()
      } else {
        rootTrees.get
      }
    } else {
      val (deeperList, restList, parentPath) = getDeeperDirectory(l)
      val hash = createTree(deeperList)
      if(parentPath.isEmpty) {
        if (rootTrees.isEmpty){
          addTrees(restList, Some(List(new Element(deeperList(0).get_path(), hash, "tree"))))
        } else {
          addTrees(restList, Some(new Element(deeperList(0).get_path(), hash, "tree") :: rootTrees.get))
        }
      } else {
        addTrees(new Element(parentPath.get, hash, "tree") :: restList, rootTrees)
      }
    }
  }

  //Create a tree and return his hash value
  def createTree(deeper: List[Element]): String = {
    val tree = new Tree()
    deeper.map(element => tree.set_content(tree.addElement(element.get_elem_type(), element.get_hash(), element.get_path())))
    val hash = tree.generateId()
    tree.set_id(hash)
    tree.saveTreeFile()
    tree.get_id()
  }

  //Find the deeper directory of a list
  //Return a list with entries of the deeper directory, a list with the rest and the parent path of the deeper directory
  def getDeeperDirectory(l: List[Element]): (List[Element], List[Element], Option[String]) = {
    var max = 0
    var pathForMax = ""

    l.map(line => if (line.get_path().split("/").size >= max) {
      max = line.get_path().split("/").size
      pathForMax = line.get_path()
    })

    val rest = l.filter(x => !(x.get_path().equals(pathForMax)))
    val deepest = l.filter(x => x.get_path().equals(pathForMax))

    val parentPath = SgitTools.getParentPath(pathForMax)
    (deepest, rest, parentPath)
  }

  //Update references in .sgit directory
  def updateRef(commit: Commit, currentBranch: String): Unit = {
    if(Files.notExists(Paths.get(".sgit/refs/heads/" + currentBranch))) {
      new File(".sgit/refs/heads/" + currentBranch).createNewFile()
    }
    //Update the HEAD of the branch
    FileManagement.writeFile(".sgit/refs/heads/" + currentBranch, commit.id)
  }

}