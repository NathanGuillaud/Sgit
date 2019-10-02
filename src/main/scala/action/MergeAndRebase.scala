package action

case class MergeAndRebase()

object MergeAndRebase {
  def merge(): Unit = {
    println("MERGE")
  }

  def rebase(): Unit = {
    println("REBASE")
  }

  def rebaseI(): Unit = {
    println("REBASE -I")
  }
}
