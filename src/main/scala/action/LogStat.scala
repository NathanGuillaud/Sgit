package action

import java.io.File
import java.nio.file.{Files, Paths}

import model.{Blob, Commit, Delta}
import util.{FileManagement, PathManagement}

import scala.io.Source

object LogStat {

  /**
   * Print all the commits with number of insertions and deletions for each files updated
   */
  def logStat(): Unit = {
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
          printModificationsBetweenCommits(Commit.getParentCommit(commitLine.split("::")(0)), commitLine.split("::")(0))
        }
        )
      } else {
        println("No commit for the moment")
      }
    }
  }

  /**
   * Print modifications betwenn 2 commits with number of insertions and deletions for each files updated
   * @param commitParent : the hash of the previous commit
   * @param commitChild : the hash of the current commit
   */
  def printModificationsBetweenCommits(commitParent: String, commitChild: String): Unit = {
    var filesForParentCommit = List[(String, String)]()
    val filesForChildCommit = Blob.getAllBlobsForCommit(commitChild)
    //If this is not the first commit
    if(commitParent != "Nil") {
      //Update the list of blobs for parent commit
      filesForParentCommit = Blob.getAllBlobsForCommit(commitParent)
    }
    //This val contains a tuple of int : the number of files changed, the number of additions and the number of deletions
    val totalModifications = filesForChildCommit.map(file =>
      printModificationsBetweenFiles(
        file._1,
        Diff.getDeltasBetweenFiles(FileManagement.getFileHashFromList(file._1, filesForParentCommit), Some(file._1))
      )
    ).reduce((acc, value) => (acc._1 + value._1, acc._2 + value._2, acc._3 + value._3))
    println(totalModifications._1 + " file(s) changed, " + totalModifications._2 + " insertion(s), " + totalModifications._3 + " deletion(s)")
  }

  /**
   * Print the modifications between 2 files to the console
   * @param filePath : the path of the file that we want to print modifications
   * @param deltas : the difference between the 2 versions of the file
   * @return a tuple of int corresponds to modifications between 2 files
   *         The first int is 0 if the files has no changes, else 1,
   *         the second int is the number of additions,
   *         the third is the number of deletions between the 2 files
   */
  def printModificationsBetweenFiles(filePath: String, deltas: List[Delta]): (Int, Int, Int) = {
    if(!deltas.isEmpty) {
      val nbInsertions = getNumberOfActionInDeltas("+", deltas)
      val nbDeletions = getNumberOfActionInDeltas("-", deltas)
      println(filePath + "\t| " + (nbInsertions+nbDeletions) + " " + Console.GREEN + "+"*nbInsertions + Console.RED + "-"*nbDeletions + Console.WHITE)
      (1, nbInsertions, nbDeletions)
    } else {
      (0,0,0)
    }
  }

  /**
   * Get the number of a given action in a list of differences
   * @param action : insertion (+) or deletion (-)
   * @param deltas : the list of differences
   * @return the number of the action given in the list
   */
  def getNumberOfActionInDeltas(action: String, deltas: List[Delta]): Int = {
    deltas.filter(delta => delta.action == action).length
  }

  /**
   * Redefinition of the + for tuples with (Int, Int)
   * @param t : a tuple of (Int, Int)
   */
  implicit class TuppleAdd(t: (Int, Int)) {
    def +(p: (Int, Int)) = (p._1 + t._1, p._2 + t._2)
  }

}
