package action

case class Merge()

object Merge {
  def merge(command: Array[String]): Unit = {
    println("MERGE " + command.toString)
  }
}