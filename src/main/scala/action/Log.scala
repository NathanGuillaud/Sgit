package action

import java.io.File
import java.nio.file.{Files, Paths}

import util.{FileManagement, PathManagement}

import scala.io.Source

object Log {

  def log(): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      val pathLogsHead = s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD"
      if(Files.exists(Paths.get(pathLogsHead)) && (Source.fromFile(pathLogsHead).getLines.length != 0)) {
        val commitsArray = FileManagement.readFile(new File(pathLogsHead)).split("\n").toList.reverse
        commitsArray.map(commitLine => printCommit(commitLine))
      } else {
        println("No commit for the moment")
      }
    }
  }

  def printCommit(commitLine: String): Unit = {
    val commitValues = commitLine.split("::")
    println(Console.YELLOW + "commit:" + "\t" + commitValues(0) + Console.WHITE)
    println("author:" + "\t" + commitValues(1) + "\n" + "date:" + "\t" + commitValues(2) + "\n")
  }

}
