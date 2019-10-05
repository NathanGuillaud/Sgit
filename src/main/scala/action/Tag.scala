package action

case class Tag()

object Tag {
  def tag(command: Array[String]): Unit = {
    println("TAG " + command.toString)
  }
}