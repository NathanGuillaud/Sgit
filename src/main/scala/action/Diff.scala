package action

import java.io.File
import java.nio.file.{Files, Paths}

import model.{Blob, Delta}
import util.{FileManagement, PathManagement, SgitTools, StageManagement}

import scala.annotation.tailrec

object Diff {

  /**
   * Get the difference between files of the working directory and files added
   */
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
            printDiff(file(0), file(1), newFileHash, getDeltasBetweenFiles(file(1), Some(file(0))))
          }
          //If the new file was removed
          else {
            printDiff(file(0), file(1), "0000000", getDeltasBetweenFiles(file(1), None))
          }
        }
      )
    }
  }

  /**
   * Print the differences between 2 files to the console
   * @param filePath : the path of the file
   * @param oldFileHash : the hash of the first version of the file
   * @param newFileHash : the hash of the second version of the file
   * @param deltas : the list of differences between the 2 versions of the file
   */
  def printDiff(filePath: String, oldFileHash: String, newFileHash: String, deltas: List[Delta]): Unit = {
    if(!deltas.isEmpty) {
      println("diff --git a/" + filePath + " b/" + filePath)
      if(newFileHash == "0000000") println("deleted file")
      if(oldFileHash == "0000000") println("new file")
      println("index " + oldFileHash.substring(0,7) + ".." + newFileHash.substring(0,7))
      deltas.map(delta =>
        if(delta.action == "+") println(Console.GREEN + delta.action + delta.content + Console.WHITE)
        else println(Console.RED + delta.action + delta.content + Console.WHITE)
      )
      println()
    }
  }

  /**
   * Get differences between 2 files
   * @param oldFileHash : the hash of the first version of the file
   * @param newFilePath : the hash of the new version of the file
   * @return a list of deltas between 2 files
   */
  def getDeltasBetweenFiles(oldFileHash: String, newFilePath: Option[String]): List[Delta] = {

    /**
     * Get differences between 2 lists
     * @param oldList : the first list to compare with the second
     * @param newList : the second list to compare with the first
     * @return a list of deltas between 2 lists
     */
    def getDeltasBetweenLists(oldList: List[String], newList: List[String]): List[Delta] = {
      //If the newFile is empty
      if(newList.isEmpty){
        println("---------------")
        println("EMPTYYYYYYYYY")
        println("---------------")
        oldList.map(line => Delta(0, "-", line))
      } else {
        //Create and fill a matrix with deltas between the 2 lists in parameters
        val matrix = fillMatrix(oldList, newList, 1, 1, Array.fill(oldList.length+1, newList.length+1)(0))
        //Retrieve the deltas from the matrix
        getDeltasFromMatrix(oldList, newList, oldList.length, newList.length, matrix, List[Delta]())
      }
    }

    val newFileContent = if(!newFilePath.isEmpty) {
      FileManagement.readFile(new File(PathManagement.getProjectPath().get + "/" + newFilePath.get)).split("\n").toList
    } else {
      List[String]()
    }
    val oldFileContent = if(oldFileHash != "0000000") {
      FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/blob/" + oldFileHash.substring(0,2) + "/" + oldFileHash.substring(2))).split("\n").toList
    } else {
      List[String]()
    }

    getDeltasBetweenLists(oldFileContent, newFileContent)
  }

  /**
   * Fill a matrix with differences between 2 lists
   * @param oldList : the first list to compare with the second
   * @param newList : the second list to compare to the first
   * @param i : the current line
   * @param j : the current column
   * @param matrix : an accumulator matrix to fill
   * @return a matrix filled with differences between the 2 lists
   */
  @tailrec
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
  /**
   * Get the differences from a matrix
   * @param oldList : the first list to compare with the second
   * @param newList : the second list to compare to the first
   * @param i : the current line
   * @param j : the current column
   * @param matrix : the matrix to browse
   * @param deltas : an accumulator, a list of differences
   * @return the list of differences at the end
   */
  @tailrec
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

  /**
   * Get the difference between files and the same files in the commit in parameters
   * @param files : a list of files to compare to the same files in the commit
   * @param commitHash : the hash of the commit to compare
   * @return the number of files changed, the number of insertions, the number of deletions and a list with files created
   */
  def getDeltasBetweenFilesAndCommit(files: List[(String, String, String)], commitHash: String): (Int, Int, Int, List[String]) = {
    //If it's not the first commit
    if(commitHash != "Nil") {
      var nbInsertions = 0
      var nbDeletions = 0
      var newFiles = List[String]()
      files.map(file => {
        //If the current file is not a new file
        var oldFileHash = "0000000"
        if(Blob.fileIsInCommit(file._2, commitHash)) {
          oldFileHash = Blob.getFileHashInCommit(file._2, commitHash)
        } else {
          newFiles = file._2 :: newFiles
        }
        getDeltasBetweenFiles(oldFileHash, Some(file._2))
          .map(delta =>
            if(delta.action == "+") nbInsertions = nbInsertions + 1
            else nbDeletions = nbDeletions + 1
          )
      })
      (files.length, nbInsertions, nbDeletions, newFiles)
    }
    //If the commit is the first
    else {
      val nbInsertions = files.map(file =>
        FileManagement.readFile(new File(PathManagement.getSgitPath().get + "/objects/blob/" + file._3.substring(0,2) + "/" + file._3.substring(2))).split("\n")
          .length
      ).sum
      val newFiles = files.map(file => file._2)
      (files.length, nbInsertions, 0, newFiles)
    }
  }

}