package action

case class Log()

object Log {
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
