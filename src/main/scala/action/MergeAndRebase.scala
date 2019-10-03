package action

case class MergeAndRebase()

object MergeAndRebase {
  def merge(command: Array[String]): Unit = {
    println("MERGE " + command.toString)
  }

  def rebase(command: Array[String]): Unit = {
    println("REBASE " + command.toString)
  }

  def rebaseI(command: Array[String]): Unit = {
    println("REBASE -I " + command.toString)
  }
}
