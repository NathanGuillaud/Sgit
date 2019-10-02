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
    case Array("init", _*) => Creation.init()
    case Array("status", _*) => LocalChanges.status()
    case Array("diff", _*) => LocalChanges.diff()
    case Array("add", _*) => LocalChanges.add(command)
    case Array("commit", _*) => LocalChanges.commit()
    case Array("log") => CommitHistory.log()
    case Array("log", "-p", _*) => CommitHistory.logP()
    case Array("log", "--stat", _*) => CommitHistory.logStat()
    case Array("branch") => BranchesAndTags.branch()
    case Array("branch", "-av", _*) => BranchesAndTags.branchAV()
    case Array("checkout", _*) => BranchesAndTags.checkout()
    case Array("tag", _*) => BranchesAndTags.tag()
    case Array("merge", _*) => MergeAndRebase.merge()
    case Array("rebase") => MergeAndRebase.rebase()
    case Array("rebase", "-i", _*) => MergeAndRebase.rebaseI()
    case default => println("Cette action n'existe pas")
  }
}