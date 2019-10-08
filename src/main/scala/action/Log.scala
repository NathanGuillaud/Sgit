package action

import java.io.File
import java.nio.file.{Files, Paths}

import scala.io.Source

case class Log()

object Log {
  def log(): Unit = {
    if(Files.exists(Paths.get(s".sgit${File.separator}logs${File.separator}HEAD")) && (Source.fromFile(s".sgit${File.separator}logs${File.separator}HEAD").getLines.length != 0)) {
      val commitsArray = Source.fromFile(s".sgit${File.separator}logs${File.separator}HEAD").getLines.toArray
      commitsArray.map(commitLine => printCommit(commitLine))
    } else {
      println("No commit for the moment")
    }
  }

  def printCommit(commitLine: String): Unit = {
    val commitValues = commitLine.split("::")
    println("commit " + commitValues(0) + "\n" + "author: " + commitValues(1) + "\n" + "date: " + commitValues(2) + "\n")
  }

  def logP(): Unit = {
    println("LOG -P")
  }

  def logStat(): Unit = {
    println("LOG --STAT")
  }
}
