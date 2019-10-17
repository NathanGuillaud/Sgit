package action

import java.io.File
import java.nio.file.{Files, Paths}

import model.Commit
import util.{FileManagement, PathManagement, SgitTools}

object Checkout {

  def checkout(command: Array[String]): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else if(branchExist(command(0))) {
      constructProjectFromBranch(command(0))
    } else if(tagExist(command(0))) {
      constructProjectFromTag(command(0))
    } else if(commitExist(command(0))) {
      constructProjectFromCommit(command(0))
    } else {
      println(s"error: pathspec ${command(0)} did not match any file(s) known to sgit.")
    }
  }

  //Reset the project with the last commit of the branch given
  def constructProjectFromBranch(branchName: String): Unit = {
    val commitForBranch = FileManagement.readFile(new File(s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads${File.separator}${branchName}"))
    constructProjectFromCommit(commitForBranch)
    SgitTools.updateHead(branchName)
  }

  //Reset the project with the tag given
  def constructProjectFromTag(tagName: String): Unit = {
    val commitForTag = FileManagement.readFile(new File(s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}tags${File.separator}${tagName}"))
    constructProjectFromCommit(commitForTag)
  }

  //Reset the project with the commit hash given
  def constructProjectFromCommit(commitHash: String): Unit = {
    //Get the tree hash of the commit
    val commitTreeHash = Commit.getTreeForCommit(commitHash)

    //Retrieve the content of the tree
    val treeContent = getTreeContent(commitTreeHash)
    treeContent.map(element =>
      if(element(0) == "blob"){
        constructBlob(element(2), element(1))
      } else if(element(0) == "tree"){
        constructTree(element(2), element(1))
      }
    )
  }

  //Reset the tree (folder) give in parameters
  def constructTree(treePath: String, treeHash: String): Unit = {
    //Create folder if not exists
    val pathFromRoot = PathManagement.getProjectPath().get + File.separator + treePath
    if(Files.notExists(Paths.get(pathFromRoot))) {
      new File(pathFromRoot).mkdir()
    }

    //Search the tree in objects
    val treeElements = getTreeContent(treeHash)
    treeElements.map(element =>
      if(element(0) == "blob"){
        constructBlob(element(2), element(1))
      } else if(element(0) == "tree"){
        constructTree(element(2), element(1))
      }
    )
  }

  //Reset the blob (file) give in parameters
  def constructBlob(blobPath: String, blobHash: String): Unit = {
    //Search the blob in objects
    val blobContent = getBlobContent(blobHash)
    FileManagement.writeFile(PathManagement.getProjectPath().get + File.separator + blobPath, blobContent)
  }

  //Retrieve the content of a tree
  //Return an array of array, each line represent an element (a blob or another tree)
  def getTreeContent(treeHash: String): Array[Array[String]] = {
    val treeFolder = treeHash.substring(0,2)
    val treeFile = treeHash.substring(2)
    FileManagement.readFile(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}tree${File.separator}${treeFolder}${File.separator}${treeFile}")).split("\n").map(x => x.split(" "))
  }

  //Retrieve the content of a blob (file content)
  def getBlobContent(blobHash: String): String = {
    val blobFolder = blobHash.substring(0,2)
    val blobFile = blobHash.substring(2)
    FileManagement.readFile(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}blob${File.separator}${blobFolder}${File.separator}${blobFile}"))
  }

  //Return true if the branch give in parameters exists, else return false
  def branchExist(branchName: String): Boolean = {
    Files.exists(Paths.get(s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads${File.separator}${branchName}"))
  }

  //Return true if the tag give in parameters exists, else return false
  def tagExist(tagName: String): Boolean = {
    Files.exists(Paths.get(s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}tags${File.separator}${tagName}"))
  }

  //Return true if the commit hash give in parameters exists, else return false
  def commitExist(commitHash: String): Boolean = {
    val commitFolder = commitHash.substring(0,2)
    val commitFile = commitHash.substring(2)
    Files.exists(Paths.get(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}commit${File.separator}${commitFolder}${File.separator}${commitFile}"))
  }

}