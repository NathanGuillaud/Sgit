package action

import java.io.File
import java.nio.file.{Files, Paths}

import model.Commit
import org.scalatest.FunSuite
import util.{FileManagement, SgitTools}

import scala.io.Source
import scala.reflect.io.Directory

class TestCommit extends FunSuite {
  test("sgit commit add 2 trees in .sgit directory") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    val currentCommit = SgitTools.getCurrentCommit(currentBranch)
    val nbOfTree = FileManagement.exploreDirectory(new File(s".sgit${File.separator}objects${File.separator}tree")).length
    SgitTools.clearStage(currentBranch)
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(FileManagement.exploreDirectory(new File(s".sgit${File.separator}objects${File.separator}tree")).length == nbOfTree + 2)

    removeTmpDirectories()
    removeTmpLogs(currentBranch)
    SgitTools.updateRef(currentCommit, currentBranch)
  }

  test("sgit commit add 1 commit in .sgit directory") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    val currentCommit = SgitTools.getCurrentCommit(currentBranch)
    val nbOfCommit = FileManagement.exploreDirectory(new File(s".sgit${File.separator}objects${File.separator}commit")).length
    SgitTools.clearStage(currentBranch)
    Add.add(Array("rootTestCommit.txt"))

    CommitAction.commit()

    assert(FileManagement.exploreDirectory(new File(s".sgit${File.separator}objects${File.separator}commit")).length == nbOfCommit + 1)

    removeTmpDirectories()
    removeTmpLogs(currentBranch)
    SgitTools.updateRef(currentCommit, currentBranch)
  }

  test("sgit commit add 1 line in logs/HEAD") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    val currentCommit = SgitTools.getCurrentCommit(currentBranch)
    var nbOfCommits = 0
    if(Files.exists(Paths.get(s".sgit${File.separator}logs${File.separator}HEAD"))) {
      nbOfCommits = Source.fromFile(s".sgit${File.separator}logs${File.separator}HEAD").getLines.length
    }
    SgitTools.clearStage(currentBranch)
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(Source.fromFile(s".sgit${File.separator}logs${File.separator}HEAD").getLines.length == nbOfCommits + 1)

    removeTmpDirectories()
    removeTmpLogs(currentBranch)
    SgitTools.updateRef(currentCommit, currentBranch)
  }

  test("sgit commit add 1 line in logs/refs/heads/master") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    val currentCommit = SgitTools.getCurrentCommit(currentBranch)
    var nbOfCommitMaster = 0
    if(Files.exists(Paths.get(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"))) {
        nbOfCommitMaster = Source.fromFile(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.length
    }
    SgitTools.clearStage(currentBranch)
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(Source.fromFile(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.length == nbOfCommitMaster + 1)

    removeTmpDirectories()
    removeTmpLogs(currentBranch)
    SgitTools.updateRef(currentCommit, currentBranch)
  }

  test("sgit commit add 1 line in refs/heads/master") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    val currentCommit = SgitTools.getCurrentCommit(currentBranch)
    var nbOfCommitMaster = 0
    SgitTools.clearStage(currentBranch)
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(new File(s".sgit${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").exists())
    assert(Source.fromFile(s".sgit${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.length == 1)

    removeTmpDirectories()
    removeTmpLogs(currentBranch)
    SgitTools.updateRef(currentCommit, currentBranch)
  }

  def createTmpDirectories(): Unit = {
    new File("testCommit").mkdirs()
    new File(s"testCommit${File.separator}testCommit.txt").createNewFile()
    new File("rootTestCommit.txt").createNewFile()
  }

  def removeTmpDirectories(): Unit = {
    //Remove directory and files added for test
    new Directory(new File("testCommit")).deleteRecursively()
    new File("rootTestCommit.txt").delete()

    //Remove files in .sgit
    new File(s".sgit${File.separator}objects${File.separator}tree${File.separator}85${File.separator}f4c1225766b7a53adce34923780d75740fe152cbd3effe3676a97a429e39e0").delete()
    new File(s".sgit${File.separator}objects${File.separator}tree${File.separator}c9${File.separator}ed504bb05232de3e31d66efba6173ad859714b350e5268030bc0b6dccfed08").delete()

    //Remove directories in .sgit if they are empty
    if(FileManagement.getListOfFilesAndDirectories(s".sgit${File.separator}objects${File.separator}tree${File.separator}85").isEmpty) {
      new Directory(new File(s".sgit${File.separator}objects${File.separator}tree${File.separator}85")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(s".sgit${File.separator}objects${File.separator}commit${File.separator}c9").isEmpty) {
      new Directory(new File(s".sgit${File.separator}objects${File.separator}commit${File.separator}c9")).deleteRecursively()
    }
  }

  def removeTmpLogs(currentBranch: String): Unit = {
    val linesHead = Source.fromFile(s".sgit${File.separator}logs${File.separator}HEAD").getLines.drop(1).mkString("\n")
    if(linesHead != "") {
      FileManagement.writeFile(s".sgit${File.separator}logs${File.separator}HEAD", linesHead)
    }

    val linesMaster = Source.fromFile(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.drop(1).mkString("\n")
    if(linesMaster != "") {
      FileManagement.writeFile(s".sgit${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}", linesMaster)
    }
  }

}