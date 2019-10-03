package action

case class BranchesAndTags()

object BranchesAndTags {
  def branch(command: Array[String]): Unit = {
    println("BRANCH " + command.toString)
  }

  def branchAV(): Unit = {
    println("BRANCH -AV")
  }

  def checkout(command: Array[String]): Unit = {
    println("CHECKOUT " + command.toString)
  }

  def tag(command: Array[String]): Unit = {
    println("TAG " + command.toString)
  }
}
