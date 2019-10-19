package action

import java.io.File

import model.{Commit, Element, Tree}
import util.{FileManagement, LogWriter, PathManagement, SgitTools, StageManagement}

import scala.annotation.tailrec

object CommitAction {

  /**
   * Commit all the files add previously (on the stage)
   */
  def commit(): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      val currentBranch = SgitTools.getCurrentBranch()
      //If the stage is empty, nothing to commit
      if(!StageManagement.containsNewFiles(currentBranch)) {
        println("Nothing to commit")
      } else {
        val (stage, rootBlobs) = getStageFiles(currentBranch)
        val rootTrees = addTrees(stage, None)

        //Create the tree for commit
        val treeForCommit = new Tree(rootBlobs, rootTrees)
        treeForCommit.saveTreeFile()

        //Create the new commit
        val currentCommitId = SgitTools.getCurrentCommit(currentBranch)
        val commit = new Commit(treeForCommit.id, currentCommitId)

        //Retrieve changes
        val (nbFilesChanged, nbInsertions, nbDeletions, newFiles) = Diff.getDeltasBetweenFilesAndCommit(StageManagement.getAddedFiles(currentBranch), currentCommitId)

        //Write commit in logs, refs and objects
        LogWriter.updateLogs(commit, currentBranch)
        SgitTools.updateRef(commit.id, currentBranch)
        commit.saveCommitFile()

        //Update content from the stage of the current branch to commited
        StageManagement.archiveFilesFromStage(currentBranch)

        println("[" + currentBranch + " " + commit.id.substring(0,7) + "]")
        println(" " + nbFilesChanged + " file(s) changed, " + nbInsertions + " insertions(+), " + nbDeletions + " deletions(-)")
        newFiles.map(file => println(" create " + file))
      }
    }
  }

  /**
   * Retrieve all elements of the stage
   * @param currentBranch : the current branch to get the right stage
   * @return 2 lists of elements : 1 with files not at root and 1 with files at root
   */
  def getStageFiles(currentBranch: String): (List[Element], List[Element]) = {
    val stageContent = StageManagement.getStageContent(SgitTools.getCurrentBranch())
    //Get the paths
    val paths = stageContent.filter(file => !PathManagement.getParentPath(file(0)).isEmpty).map(file => file(0))
    //Get the blobs at root
    val rootBlobs = stageContent.filter(file => PathManagement.getParentPath(file(0)).isEmpty).map(file => new Element(file(0), file(1), "blob")).toList
    val blob = List.fill(paths.size)("blob")
    //Get the hashs
    val hashs = stageContent.filter(file => !PathManagement.getParentPath(file(0)).isEmpty).map(file => file(1))

    //Merging the result
    val elements = (paths,hashs,blob).zipped.toList.map(elem => Element(elem._1, elem._2, elem._3))
    (elements, rootBlobs)
  }

  /**
   * Add trees to the .sgit with a list of elements added
   * @param l : list of elements
   * @param rootTrees : an accumulator for trees at root
   * @return the list of trees at root when it's completed
   */
    @tailrec
  def addTrees(l: List[Element], rootTrees: Option[List[Element]]): List[Element] = {
    if(l.size == 0){
      if(rootTrees.isEmpty) {
        List[Element]()
      } else {
        rootTrees.get
      }
    } else {
      val (deeperList, restList, parentPath) = getDeeperDirectory(l)
      val hash = Tree.createTree(deeperList)
      if(parentPath.isEmpty) {
        if (rootTrees.isEmpty){
          addTrees(restList, Some(List(new Element(PathManagement.getParentPath(deeperList(0).path).get, hash, "tree"))))
        } else {
          addTrees(restList, Some(new Element(PathManagement.getParentPath(deeperList(0).path).get, hash, "tree") :: rootTrees.get))
        }
      } else {
        addTrees(new Element(PathManagement.getParentPath(deeperList(0).path).get, hash, "tree") :: restList, rootTrees)
      }
    }
  }

  /**
   * Find the deeper directory of a list
   * @param l : the list of elements
   * @return a list with elements of the deeper directory, a list with the rest and the parent path of the deeper directory
   */
  def getDeeperDirectory(l: List[Element]): (List[Element], List[Element], Option[String]) = {
    var max = 0
    var pathForMax = ""

    l.map(line => if (PathManagement.getParentPath(line.path).get.split("/").size >= max) {
      max = PathManagement.getParentPath(line.path).get.split("/").size
      pathForMax = PathManagement.getParentPath(line.path).get
    })

    val rest = l.filter(x => !(PathManagement.getParentPath(x.path).get.equals(pathForMax)))
    val deepest = l.filter(x => PathManagement.getParentPath(x.path).get.equals(pathForMax))

    val parentPath = PathManagement.getParentPath(pathForMax)
    (deepest, rest, parentPath)
  }

}