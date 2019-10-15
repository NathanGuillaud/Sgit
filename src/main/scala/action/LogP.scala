package action

import java.io.File
import java.nio.file.{Files, Paths}

import model.{Blob, Commit}
import util.{FileManagement, PathManagement, SgitTools}

import scala.io.Source

object LogP {

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

  def printDeltasBetweenCommits(commitParent: String, commitChild: String): Unit = {
    //Print commit infos


    //print commit deltas
    var filesForParentCommit = List[(String, String)]()
    val filesForChildCommit = Blob.getAllBlobsForCommit(commitChild)
    //If this is not the first commit
    if(commitParent != "Nil") {
      //Update the list of blobs for parent commit
      filesForParentCommit = Blob.getAllBlobsForCommit(commitParent)
    }
    filesForChildCommit.map(file =>
      if(FileManagement.fileIsInList(file._1, filesForParentCommit))
        Diff.printDiff(
          file._1,
          FileManagement.getFileHashFromList(file._1, filesForParentCommit),
          file._2,
          Diff.getDeltasBetweenFiles(FileManagement.getFileHashFromList(file._1, filesForParentCommit), file._2)
        )
      else
        Diff.printDiff(
          file._1,
          "0000000",
          file._2,
          Diff.getDeltasBetweenFiles("0000000", file._2)
        )
    )
  }

}
