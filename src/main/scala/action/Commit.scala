package action

case class Commit()

object Commit {
  def commit(): Unit = {
    println("COMMIT")
  }
}