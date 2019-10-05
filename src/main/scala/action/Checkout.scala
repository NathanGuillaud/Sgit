package action

case class Checkout()

object Checkout {
  def checkout(command: Array[String]): Unit = {
    println("CHECKOUT " + command.toString)
  }
}