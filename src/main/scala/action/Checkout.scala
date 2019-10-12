package action

import java.io.File
import java.nio.file.{Files, Paths}

import util.{FileManagement, PathManagement}

object Checkout {

  def checkout(command: Array[String]): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      constructProjectFromCommit(command(0))
    }
  }

  def constructProjectFromCommit(commitHash: String): Unit = {
    //Get the tree hash of the commit
    val commitTreeHash = getTreeForCommit(commitHash)

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

  def constructBlob(blobPath: String, blobHash: String): Unit = {
    //Search the blob in objects
    val blobContent = getBlobContent(blobHash)
    FileManagement.writeFile(PathManagement.getProjectPath().get + File.separator + blobPath, blobContent)
  }

  def getTreeContent(treeHash: String): Array[Array[String]] = {
    val treeFolder = treeHash.substring(0,2)
    val treeFile = treeHash.substring(2)
    FileManagement.readFile(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}tree${File.separator}${treeFolder}${File.separator}${treeFile}")).split("\n").map(x => x.split(" "))
  }

  def getBlobContent(blobHash: String): String = {
    val blobFolder = blobHash.substring(0,2)
    val blobFile = blobHash.substring(2)
    FileManagement.readFile(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}blob${File.separator}${blobFolder}${File.separator}${blobFile}"))
  }

  def getTreeForCommit(commitHash: String): String = {
    val commitFolder = commitHash.substring(0,2)
    val commitFile = commitHash.substring(2)
    val commitContent = FileManagement.readFile(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}commit${File.separator}${commitFolder}${File.separator}${commitFile}")).split("\n").map(x => x.split("::"))
    var treeHash = ""
    commitContent.map(line =>
      if(line(0) == "tree"){
        treeHash = line(1)
      }
    )
    treeHash
  }

}