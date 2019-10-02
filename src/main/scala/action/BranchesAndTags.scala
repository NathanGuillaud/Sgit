package action

case class BranchesAndTags()

object BranchesAndTags {
  def branch(): Unit = {
    println("BRANCH")
  }

  def branchAV(): Unit = {
    println("BRANCH -AV")
  }

  def checkout(): Unit = {
    println("CHECKOUT")
  }

  def tag(): Unit = {
    println("TAG")
  }
}
