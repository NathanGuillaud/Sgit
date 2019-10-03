package main
import action.Creation
import action.LocalChanges
import action.CommitHistory
import action.BranchesAndTags
import action.MergeAndRebase

object Main extends App{
  if (args.length == 0) {
    println("Vous devez mentionner une action")
  } else {
    val command = args
    eval(command)
  }

  def eval(command: Array[String]): Unit = command match {
    case Array("init", _*) => init(command)
    case Array("status", _*) => status(command)
    case Array("diff", _*) => diff(command)
    case Array("add", _*) => add(command)
    case Array("commit", _*) => commit(command)
    case Array("log", _*) => log(command)
    case Array("branch", _*) => branch(command)
    case Array("checkout", _*) => checkout(command)
    case Array("tag", _*) => tag(command)
    case Array("merge", _*) => merge(command)
    case Array("rebase", _*) => rebase(command)
    case default => println("Cette action n'existe pas")
  }

  def init(command: Array[String]): Unit = command match {
    case Array("init") => Creation.init()
    case default => println("La commande sgit init ne contient pas d'option")
  }

  def status(command: Array[String]): Unit = command match {
    case Array("status") => LocalChanges.status()
    case default => println("La commande sgit status ne contient pas d'option")
  }

  def diff(command: Array[String]): Unit = command match {
    case Array("diff") => LocalChanges.diff()
    case default => println("La commande sgit diff ne contient pas d'option")
  }

  def add(command: Array[String]): Unit = command match {
    case Array("add") => println("Vous devez préciser les fichiers à ajouter avec la commande sgit add <files>")
    case default => LocalChanges.add(command)
  }

  def commit(command: Array[String]): Unit = command match {
    case Array("commit") => LocalChanges.commit()
    case default => println("La commande sgit commit ne contient pas d'option")
  }

  def log(command: Array[String]): Unit = command match {
    case Array("log") => CommitHistory.log()
    case Array("log", "-p") => CommitHistory.logP()
    case Array("log", "--stat") => CommitHistory.logStat()
    case Array("log", "-p", _*) => println("La commande sgit log -p ne contient pas d'autres options")
    case Array("log", "--stat", _*) => println("La commande sgit log --stat ne contient pas d'autres options")
    case default => println("La commande log ne contient pas cette option, essayez sgit log -p ou sgit log --stat")
  }

  def branch(command: Array[String]): Unit = command match {
    case Array("branch") => println("Vous devez préciser le nom de la branche à créer")
    case Array("branch", "-av") => BranchesAndTags.branchAV()
    case Array("branch", _) => BranchesAndTags.branch(command)
    case Array("branch", "-av", _*) => println("La commande sgit branch -av ne contient pas d'autres options")
    case default => println("Le nom de la branche à créer ne doit pas contenir d'espace. Essayez sgit branch <branch> ou sgit branch -av")
  }

  def checkout(command: Array[String]): Unit = command match {
    case Array("checkout") => println("Vous devez préciser le nom de la branche, du tag ou le numéro du commit sur lequel se placer")
    case Array("checkout", _) => BranchesAndTags.checkout(command)
    case default => println("La branche, le tag ou le numéro du commit ne doivent pas contenir d'espace")
  }

  def tag(command: Array[String]): Unit = command match {
    case Array("tag") => println("Vous devez préciser le nom du tag")
    case Array("tag", _) => BranchesAndTags.tag(command)
    case default => println("Le nom du tag ne doit pas contenir d'espace")
  }

  def merge(command: Array[String]): Unit = command match {
    case Array("merge") => println("Vous devez préciser le nom de la branche pour le merge")
    case Array("merge", _) => MergeAndRebase.merge(command)
    case default => println("Le nom de la branche pour le merge ne doit pas contenir d'espace")
  }

  def rebase(command: Array[String]): Unit = command match {
    case Array("rebase") | Array("rebase", "-i") => println("Vous devez préciser le nom de la branche pour le rebase")
    case Array("rebase", "-i", _) => MergeAndRebase.rebaseI(command)
    case Array("rebase", _) => MergeAndRebase.rebase(command)
    case default => println("Le nom de la branche à créer ne doit pas contenir d'espace. Essayez sgit rebase <branch> ou sgit rebase -i <branch or commit>")
  }
}