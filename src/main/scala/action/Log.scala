package action

import java.nio.file.{Files, Paths}

import action.CommitAction.createLogFileForBranch

import scala.io.Source

case class Log()

object Log {
  def log(): Unit = {
    if(Files.exists(Paths.get(".sgit/logs/HEAD")) && (Source.fromFile(".sgit/logs/HEAD").getLines.length != 0)) {
      val commitsArray = Source.fromFile(".sgit/logs/HEAD").getLines.toArray
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
