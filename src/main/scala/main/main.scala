package main
import action._

object Main extends App{
  if (args.length == 0) {
    println("You must specify an action")
  } else {
    val command = args
    eval(command)
  }

  /**
   * Dispatch to the right action
   * @param command : arguments given with the action
   */
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
    case default => println("This action does not exist")
  }

  /**
   * sgit init
   * @param command : array of string with arguments given
   */
  def init(command: Array[String]): Unit = command match {
    case Array("init") => Init.init()
    case default => println("Command sgit init has no option")
  }

  /**
   * sgit status
   * @param command : array of string with arguments given
   */
  def status(command: Array[String]): Unit = command match {
    case Array("status") => Status.status()
    case default => println("Command sgit status has no option")
  }

  /**
   * sgit diff
   * @param command : array of string with arguments given
   */
  def diff(command: Array[String]): Unit = command match {
    case Array("diff") => Diff.diff()
    case default => println("Command sgit diff has no option")
  }

  /**
   * sgit add
   * @param command : array of string with arguments given
   */
  def add(command: Array[String]): Unit = command match {
    case Array("add") => println("You must specify files to add with the command sgit add <files>")
    case default => Add.add(command.tail)
  }

  /**
   * sgit commit
   * @param command : array of string with arguments given
   */
  def commit(command: Array[String]): Unit = command match {
    case Array("commit") => CommitAction.commit()
    case default => println("Command sgit commit has no option")
  }

  /**
   * sgit log
   * @param command : array of string with arguments given
   */
  def log(command: Array[String]): Unit = command match {
    case Array("log") => Log.log()
    case Array("log", "-p") => LogP.logP()
    case Array("log", "--stat") => LogStat.logStat()
    case Array("log", "-p", _*) => println("Command sgit log -p does not contain any other option")
    case Array("log", "--stat", _*) => println("Command sgit log --stat does not contain any other option")
    case default => println("Command log has no option, try sgit log -p or sgit log --stat")
  }

  /**
   * sgit branch
   * @param command : array of string with arguments given
   */
  def branch(command: Array[String]): Unit = command match {
    case Array("branch") => println("You must specify the branch name")
    case Array("branch", "-av") => Branch.branchAV()
    case Array("branch", _) => Branch.branch(command.tail)
    case Array("branch", "-av", _*) => println("Command sgit branch -av does not contain any other option")
    case default => println("The branch name must not contain spaces. Try sgit branch <branch> or sgit branch -av")
  }

  /**
   * sgit checkout
   * @param command : array of string with arguments given
   */
  def checkout(command: Array[String]): Unit = command match {
    case Array("checkout") => println("You must specify the name of the branch, the tag or the commit hash on which to place")
    case Array("checkout", _) => Checkout.checkout(command.tail)
    case default => println("The branch, the tag or the commit hash must not contain spaces")
  }

  /**
   * sgit tag
   * @param command : array of string with arguments given
   */
  def tag(command: Array[String]): Unit = command match {
    case Array("tag") => println("You must specify the tag name")
    case Array("tag", _) => Tag.tag(command.tail)
    case default => println("The tag name must not contain spaces")
  }

  /**
   * sgit merge
   * @param command : array of string with arguments given
   */
  def merge(command: Array[String]): Unit = command match {
    case Array("merge") => println("You must specify the branch name for the merge command")
    case Array("merge", _) => Merge.merge(command.tail)
    case default => println("The branch name must not contain spaces")
  }

  /**
   * sgit rebase
   * @param command : array of string with arguments given
   */
  def rebase(command: Array[String]): Unit = command match {
    case Array("rebase") | Array("rebase", "-i") => println("You must specify the branch name for the rebase command")
    case Array("rebase", "-i", _) => Rebase.rebaseI(command.tail)
    case Array("rebase", _) => Rebase.rebase(command.tail)
    case default => println("The branch name must not contain spaces. Try sgit rebase <branch> or sgit rebase -i <branch or commit>")
  }
}