package model

import java.io.File

import util.{FileManagement, PathManagement, StageManagement}

object Blob{

  //Add a blob
  def addBlob(path: File, currentBranch: String): Unit = {
    //Not add file if it is in .sgit directory
    if(!path.toString.contains(".sgit")) {
      //Get the name and the content of the file
      val decomposedFilePath = path.toString().split("/")
      val fileName = decomposedFilePath(decomposedFilePath.length-1)
      val fileContent = FileManagement.readFile(path)

      //File hash
      val hashValue = FileManagement.hashFile(fileName, fileContent)
      val folderHash = hashValue.substring(0,2)
      val fileHash = hashValue.substring(2)

      //Blob creation in directory
      val blobDirectory = s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}blob${File.separator}${folderHash}"
      new File(blobDirectory).mkdirs()
      new File(blobDirectory + s"${File.separator}${fileHash}").createNewFile()
      FileManagement.writeFile(blobDirectory + s"${File.separator}${fileHash}", fileContent)

      StageManagement.addFileInStage(PathManagement.getFilePathFromProjectRoot(path.getPath).get, hashValue, currentBranch)
    }
  }

  //Return true if the file exists in the commit in parameters, else return false
  def fileIsInCommit(filePath: String, commitHash: String): Boolean = {
    def elemIsInTree(currentPath: String, treeHash: String): Boolean = {
      var isInTree = false
      FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/tree/" + treeHash.substring(0,2) + "/" + treeHash.substring(2))).split("\n").map(x => x.split(" "))
        .map(line =>
          if(filePath == currentPath && currentPath == line(2)) isInTree = true
          else if(currentPath == line(2)) isInTree = elemIsInTree(currentPath + "/" + filePath.substring(currentPath.length+1).split("/")(0), line(1))
        )
      isInTree
    }
    elemIsInTree(filePath.split("/")(0), Commit.getTreeForCommit(commitHash))
  }

  //Return the hash of a file for a commit
  def getFileHashInCommit(filePath: String, commitHash: String): String = {
    def getFileHashInTree(currentPath: String, treeHash: String): String = {
      var fileHash = ""
      FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/tree/" + treeHash.substring(0,2) + "/" + treeHash.substring(2))).split("\n").map(x => x.split(" "))
        .map(line =>
          if(filePath == currentPath && currentPath == line(2)) {
            fileHash = line(1)
          }
          else if(currentPath == line(2)) {
            fileHash = getFileHashInTree(currentPath + "/" + filePath.substring(currentPath.length+1).split("/")(0), line(1))
          }
        )
      fileHash
    }
    getFileHashInTree(filePath.split("/")(0), Commit.getTreeForCommit(commitHash))
  }


  //Return a list with all the blobs for the commit in parameters
  //The list is a list of tuples with the path of the file from the project, and the hash of the blob
  def getAllBlobsForCommit(commitHash: String): List[(String, String)] = {
    def getBlobsForTree(treeHash: String): List[(String, String)] = {
      FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/tree/" + treeHash.substring(0, 2) + "/" + treeHash.substring(2))).split("\n").map(x => x.split(" ")).toList
        .flatMap(line =>
          if (line(0) == "tree") getBlobsForTree(line(1))
          else List((line(2), line(1)))
        )
    }
    val commitTree = Commit.getTreeForCommit(commitHash)
    getBlobsForTree(commitTree)
  }

}
