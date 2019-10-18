package action

import java.io.File

import model.{Commit, Tree, Element}
import util.{FileManagement, SgitTools, LogWriter, PathManagement, StageManagement}

object CommitAction {

  //Commit all the files add previously (on the stage)
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

  //Create a list with all elements of the stage
  def getStageFiles(currentBranch: String): (List[Element], List[Element]) = {
    var rootBlobs = List[Element]()
    //Retrieve useful data
    val stage = new File(s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}")
    val files = FileManagement.readFile(stage)

    //Split lines
    val stage_content = files.split("\n").map(x => x.split(" "))

    //Cleaning from the filenames
    var paths = List[String]()
    stage_content.map(x =>
      if(PathManagement.getParentPath(x(0)).isEmpty) {
        rootBlobs = new Element(x(0), x(1), "blob") :: rootBlobs
      } else {
        paths = x(0) :: paths
      }
    )
    var hashs = List[String]()
    stage_content.map(x =>
      if(!PathManagement.getParentPath(x(0)).isEmpty) {
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
  //Return a list of trees at root
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
          addTrees(restList, Some(List(new Element(PathManagement.getParentPath(deeperList(0).path).get, hash, "tree"))))
        } else {
          addTrees(restList, Some(new Element(PathManagement.getParentPath(deeperList(0).path).get, hash, "tree") :: rootTrees.get))
        }
      } else {
        addTrees(new Element(PathManagement.getParentPath(deeperList(0).path).get, hash, "tree") :: restList, rootTrees)
      }
    }
  }

  //Create a tree and return his hash value
  def createTree(deeper: List[Element]): String = {
    val tree = new Tree(deeper)
    tree.saveTreeFile()
    tree.id
  }

  //Find the deeper directory of a list
  //Return a list with entries of the deeper directory, a list with the rest and the parent path of the deeper directory
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