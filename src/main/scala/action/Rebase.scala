package action

case class Rebase()

object Rebase {


  def rebase(command: Array[String]): Unit = {
    println("REBASE " + command.toString)
  }

  def rebaseI(command: Array[String]): Unit = {
    println("REBASE -I " + command.toString)
  }
}
