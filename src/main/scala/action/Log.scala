package action

import java.io.File
import java.nio.file.{Files, Paths}

import scala.io.Source

case class Log()

object Log {
  def log(): Unit = {
    val pathLogsHead = s".sgit${File.separator}logs${File.separator}HEAD"
    if(Files.exists(Paths.get(pathLogsHead)) && (Source.fromFile(pathLogsHead).getLines.length != 0)) {
      val commitsArray = Source.fromFile(pathLogsHead).getLines.toArray
      commitsArray.map(commitLine => printCommit(commitLine))
    } else {
      println("No commit for the moment")
    }
  }

  def printCommit(commitLine: String): Unit = {
    val commitValues = commitLine.split("::")
    println(Console.YELLOW + "commit:" + "\t" + commitValues(0))
    println(Console.WHITE + "author:" + "\t" + commitValues(1) + "\n" + "date:" + "\t" + commitValues(2) + "\n")
  }

  def logP(): Unit = {
    println("LOG -P")
  }

  def logStat(): Unit = {
    println("LOG --STAT")
  }
}
