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
    if(command.size <= 1){
      println("Vous devez préciser les fichiers à ajouter")
    }else {
      println("ADD " + command)
    }
  }

  def commit(): Unit = {
    println("COMMIT")
  }
}
