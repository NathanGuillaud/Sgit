package action

import java.io.File
import java.nio.file.{Files, Paths}

import model.Delta
import util.{FileManagement, PathManagement, SgitTools, StageManagement}

object Diff {

  def diff(): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      val currentBranch = SgitTools.getCurrentBranch()
      val files = StageManagement.getStageContent(currentBranch)
      //Print deltas for each file in the stage
      files.map(file =>
        if(Files.exists(Paths.get(PathManagement.getSgitPath().get + "/objects/blob/" + file(1).substring(0,2) + "/" + file(1).substring(2)))) {
          //If the old and new files exists
          if(Files.exists(Paths.get(PathManagement.getProjectPath().get + "/" + file(0)))) {
            val newFileContent = FileManagement.readFile(new File(PathManagement.getProjectPath().get + "/" + file(0)))
            val newFileHash = FileManagement.hashFile(file(0).split("/").last, newFileContent)
            printDiff(file(0), file(1), newFileHash, getDeltasBetweenFiles(FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/blob/" + file(1).substring(0,2) + "/" + file(1).substring(2))).split("\n").toList, newFileContent.split("\n").toList))
          }
          //If the new file was removed
          else {
            printDiff(file(0), file(1), "0000000", getDeltasBetweenFiles(FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/blob/" + file(1).substring(0,2) + "/" + file(1).substring(2))).split("\n").toList, List[String]()))
          }
        }
      )
    }
  }

  //Print the differences to the console
  def printDiff(filePath: String, oldFileHash: String, newFileHash: String, deltas: List[Delta]): Unit = {
    if(!deltas.isEmpty) {
      println("diff --git a/" + filePath + " b/" + filePath)
      if(newFileHash == "0000000") println("deleted file")
      println("index " + oldFileHash.substring(0,7) + ".." + newFileHash.substring(0,7))
      deltas.map(delta =>
        if(delta.action == "+") println(Console.GREEN + delta.action + delta.content + Console.WHITE)
        else println(Console.RED + delta.action + delta.content + Console.WHITE)
      )
      println()
    }
  }

  def getDeltasBetweenFiles(oldList: List[String], newList: List[String]): List[Delta] = {
    //If the newFile is empty
    if(newList.isEmpty){
      var deltasToReturn = List[Delta]()
      oldList.map(line => deltasToReturn = new Delta(0, "-", line) :: deltasToReturn)
      deltasToReturn
    } else {
      //Create an empty matrix
      var matrix = initializeMatrix(oldList, newList)
      //Fill the matrix with deltas between the 2 lists in parameters
      matrix = fillMatrix(oldList, newList, 1, 1, matrix)
      //Retrieve the deltas from the matrix
      getDeltasFromMatrix(oldList, newList, oldList.length, newList.length, matrix, List[Delta]())
    }
  }

  //Create an empty matrix with a size of oldList+1 X newList+1
  def initializeMatrix(oldList: List[String], newList: List[String]): Array[Array[Int]] = {
    Array.fill(oldList.length+1, newList.length+1)(0)
  }

  //Fill the matrix in parameters with deltas between the 2 lists in parameters
  def fillMatrix(oldList: List[String], newList: List[String], i: Int, j: Int, matrix: Array[Array[Int]]): Array[Array[Int]] = {
    //If the matrix is fill
    if(i == oldList.length+1 && j == 1) matrix
    else {
      //If values of the old list and the new list are the same
      if(oldList(i-1) == newList(j-1)) matrix(i)(j) = matrix(i-1)(j-1)+1
      else matrix(i)(j) = Math.max(matrix(i)(j-1), matrix(i-1)(j))
      //If it is the end of the line
      if(j == newList.length) fillMatrix(oldList, newList, i+1, 1, matrix)
      else fillMatrix(oldList, newList, i, j+1, matrix)
    }

  }

  //Go up the matrix to know elements added or deleted
  def getDeltasFromMatrix(oldList: List[String], newList: List[String], i: Int, j: Int, matrix: Array[Array[Int]], deltas: List[Delta]): List[Delta] = {
    //If we go up the 2 lists
    if(i == 0 && j == 0) deltas
    else {
      //If the new list contains a new element
      if(i == 0 || (j > 0 && matrix(i)(j-1) == matrix(i)(j))) getDeltasFromMatrix(oldList, newList, i, j-1, matrix, new Delta(j, "+", newList(j-1)) :: deltas)
      //If the old list contains an element removed
      else if(j == 0 || (i > 0 && matrix(i-1)(j) == matrix(i)(j))) getDeltasFromMatrix(oldList, newList, i-1, j, matrix, new Delta(j, "-", oldList(i-1)) :: deltas)
      else getDeltasFromMatrix(oldList, newList, i-1, j-1, matrix, deltas)
    }
  }

  //Get the difference between files and the same files in the commit in parameters
  //Return the number of files changed, the number of insertions, the number of deletions and a list with files created
  def getDeltasBetweenFilesAndCommit(files: List[(String, String, String)], commitHash: String): (Int, Int, Int, List[String]) = {
    //If it's not the first commit
    if(commitHash != "Nil") {
      var nbInsertions = 0
      var nbDeletions = 0
      var newFiles = List[String]()
      files.map(file =>
        //If the current file is not a new file
        if(FileManagement.fileIsInCommit(file._2, commitHash)) {
          getDeltasBetweenFiles(FileManagement.getFileContentForCommit(file._2, commitHash).split("\n").toList, FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/blob/" + file._3.substring(0,2) + "/" + file._3.substring(2))).split("\n").toList)
            .map(delta =>
              if(delta.action == "+") nbInsertions = nbInsertions + 1
              else nbDeletions = nbDeletions + 1
            )
        } else {
          newFiles = file._2 :: newFiles
        }
      )
      (files.length, nbInsertions, nbDeletions, newFiles)
    }
    //If the commit is the first
    else {
      var nbInsertions = 0
      files.map(file =>
        FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/blob/" + file._3.substring(0,2) + "/" + file._3.substring(2))).split("\n")
          .map(line => nbInsertions = nbInsertions + 1)
      )
      var newFiles = List[String]()
      files.map(file => newFiles = file._2 :: newFiles)
      (files.length, nbInsertions, 0, newFiles)
    }
  }

}