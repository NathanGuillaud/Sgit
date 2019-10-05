package action

case class Branch()

object Branch {

  def branch(command: Array[String]): Unit = {
    println("BRANCH " + command.toString)
  }

  def branchAV(): Unit = {
    println("BRANCH -AV")
  }

}
