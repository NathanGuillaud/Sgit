package main
import action._

object Main extends App{
  if (args.length == 0) {
    println("Vous devez mentionner une action")
  } else {
    val command = args
    eval(command)
  }

  def eval(command: Array[String]): Unit = command(0) match {
    case "init" => init(command)
    case "status" => status(command)
    case "diff" => diff(command)
    case "add" => add(command)
    case "commit" => commit(command)
    case "log" => log(command)
    case "branch" => branch(command)
    case "checkout" => checkout(command)
    case "tag" => tag(command)
    case "merge" => merge(command)
    case "rebase" => rebase(command)
    case default => println("Cette action n'existe pas")
  }

  def init(command: Array[String]): Unit = command match {
    case Array("init") => Init.init()
    case default => println("La commande sgit init ne contient pas d'option")
  }

  def status(command: Array[String]): Unit = command match {
    case Array("status") => Status.status()
    case default => println("La commande sgit status ne contient pas d'option")
  }

  def diff(command: Array[String]): Unit = command match {
    case Array("diff") => Diff.diff()
    case default => println("La commande sgit diff ne contient pas d'option")
  }

  def add(command: Array[String]): Unit = command match {
    case Array("add") => println("Vous devez préciser les fichiers à ajouter avec la commande sgit add <files>")
    case default => Add.add(command.tail)
  }

  def commit(command: Array[String]): Unit = command match {
    case Array("commit") => CommitAction.commit()
    case default => println("La commande sgit commit ne contient pas d'option")
  }

  def log(command: Array[String]): Unit = command match {
    case Array("log") => Log.log()
    case Array("log", "-p") => Log.logP()
    case Array("log", "--stat") => Log.logStat()
    case Array("log", "-p", _*) => println("La commande sgit log -p ne contient pas d'autres options")
    case Array("log", "--stat", _*) => println("La commande sgit log --stat ne contient pas d'autres options")
    case default => println("La commande log ne contient pas cette option, essayez sgit log -p ou sgit log --stat")
  }

  def branch(command: Array[String]): Unit = command match {
    case Array("branch") => println("Vous devez préciser le nom de la branche à créer")
    case Array("branch", "-av") => Branch.branchAV()
    case Array("branch", _) => Branch.branch(command.tail)
    case Array("branch", "-av", _*) => println("La commande sgit branch -av ne contient pas d'autres options")
    case default => println("Le nom de la branche à créer ne doit pas contenir d'espace. Essayez sgit branch <branch> ou sgit branch -av")
  }

  def checkout(command: Array[String]): Unit = command match {
    case Array("checkout") => println("Vous devez préciser le nom de la branche, du tag ou le numéro du commit sur lequel se placer")
    case Array("checkout", _) => Checkout.checkout(command.tail)
    case default => println("La branche, le tag ou le numéro du commit ne doivent pas contenir d'espace")
  }

  def tag(command: Array[String]): Unit = command match {
    case Array("tag") => println("Vous devez préciser le nom du tag")
    case Array("tag", _) => Tag.tag(command.tail)
    case default => println("Le nom du tag ne doit pas contenir d'espace")
  }

  def merge(command: Array[String]): Unit = command match {
    case Array("merge") => println("Vous devez préciser le nom de la branche pour le merge")
    case Array("merge", _) => Merge.merge(command.tail)
    case default => println("Le nom de la branche pour le merge ne doit pas contenir d'espace")
  }

  def rebase(command: Array[String]): Unit = command match {
    case Array("rebase") | Array("rebase", "-i") => println("Vous devez préciser le nom de la branche pour le rebase")
    case Array("rebase", "-i", _) => Rebase.rebaseI(command.tail)
    case Array("rebase", _) => Rebase.rebase(command.tail)
    case default => println("Le nom de la branche à créer ne doit pas contenir d'espace. Essayez sgit rebase <branch> ou sgit rebase -i <branch or commit>")
  }
}