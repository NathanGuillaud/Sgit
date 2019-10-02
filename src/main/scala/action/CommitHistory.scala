package action

case class CommitHistory()

object CommitHistory {
  def log(): Unit = {
    println("LOG")
  }

  def logP(): Unit = {
    println("LOG -P")
  }

  def logStat(): Unit = {
    println("LOG --STAT")
  }
}
