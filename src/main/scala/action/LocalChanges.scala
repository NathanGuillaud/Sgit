package action

case class LocalChanges()

object LocalChanges {
  def status(): Unit = {
    println("STATUS")
  }

  def diff(): Unit = {
    println("DIFF")
  }

  def add(command: Array[String]): Unit = {
    println("ADD " + command.toString)
  }

  def commit(): Unit = {
    println("COMMIT")
  }
}
