package action

import java.io.File
import java.nio.file.{Files, Paths}

import model.{Blob, Commit}
import util.{FileManagement, PathManagement, SgitTools}

import scala.io.Source

object LogP {

  /**
   * Print all the commits with differences with the previous
   */
  def logP(): Unit = {
    //If .sgit is not found
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      val pathLogsHead = s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD"
      //If the logs directory is found (at least one commit)
      if(Files.exists(Paths.get(pathLogsHead)) && (Source.fromFile(pathLogsHead).getLines.length != 0)) {
        val commitsArray = FileManagement.readFile(new File(pathLogsHead)).split("\n").toList.reverse
        commitsArray.map(commitLine => {
          Log.printCommit(commitLine)
          printDeltasBetweenCommits(Commit.getParentCommit(commitLine.split("::")(0)), commitLine.split("::")(0))
        }
          )
      } else {
        println("No commit for the moment")
      }
    }
  }

  /**
   * Print the deltas details between 2 commits
   * @param commitParent : the hash of the previous commit
   * @param commitChild : the hash of the current commit
   */
  def printDeltasBetweenCommits(commitParent: String, commitChild: String): Unit = {
    val filesForParentCommit = if(commitParent != "Nil") Blob.getAllBlobsForCommit(commitParent) else List[(String, String)]()
    val filesForChildCommit = Blob.getAllBlobsForCommit(commitChild)
    filesForChildCommit.map(file => {
      println(file._1 +" "+FileManagement.fileIsInList(file._1, filesForParentCommit))
      if (FileManagement.fileIsInList(file._1, filesForParentCommit)) {
        println(file._1 + " " + FileManagement.getFileHashFromList(file._1, filesForParentCommit))
        Diff.printDiff(
          file._1,
          FileManagement.getFileHashFromList(file._1, filesForParentCommit),
          file._2,
          Diff.getDeltasBetweenFiles(FileManagement.getFileHashFromList(file._1, filesForParentCommit), Some(file._1))
        )
      } else
        Diff.printDiff(
          file._1,
          "0000000",
          file._2,
          Diff.getDeltasBetweenFiles("0000000", Some(file._1))
        )
    })
  }

}
