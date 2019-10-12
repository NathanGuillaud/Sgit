package action

import java.io.File
import java.nio.file.{Files, Paths}

import model.Commit
import org.scalatest.FunSuite
import util.{FileManagement, PathManagement, SgitTools, StageManagement}

import scala.io.Source
import scala.reflect.io.Directory

class TestCommit extends FunSuite {
  test("sgit commit add 2 trees in .sgit directory") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    val currentCommit = SgitTools.getCurrentCommit(currentBranch)
    val nbOfTree = FileManagement.getFilesFromDirectory(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}tree")).length
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(FileManagement.getFilesFromDirectory(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}tree")).length == nbOfTree + 2)

    removeTmpDirectories()
    removeTmpLogs(currentBranch)
    SgitTools.updateRef(currentCommit, currentBranch)
    StageManagement.clearStage(currentBranch)
  }

  test("sgit commit add 1 commit in .sgit directory") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    val currentCommit = SgitTools.getCurrentCommit(currentBranch)
    val nbOfCommit = FileManagement.getFilesFromDirectory(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}commit")).length
    Add.add(Array("rootTestCommit.txt"))

    CommitAction.commit()

    assert(FileManagement.getFilesFromDirectory(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}commit")).length == nbOfCommit + 1)

    removeTmpDirectories()
    removeTmpLogs(currentBranch)
    SgitTools.updateRef(currentCommit, currentBranch)
    StageManagement.clearStage(currentBranch)
  }

  test("sgit commit add 1 line in logs/HEAD") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    val currentCommit = SgitTools.getCurrentCommit(currentBranch)
    var nbOfCommits = 0
    if(Files.exists(Paths.get(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD"))) {
      nbOfCommits = Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD").getLines.length
    }
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD").getLines.length == nbOfCommits + 1)

    removeTmpDirectories()
    removeTmpLogs(currentBranch)
    SgitTools.updateRef(currentCommit, currentBranch)
    StageManagement.clearStage(currentBranch)
  }

  test("sgit commit add 1 line in logs/refs/heads/master") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    val currentCommit = SgitTools.getCurrentCommit(currentBranch)
    var nbOfCommitMaster = 0
    if(Files.exists(Paths.get(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"))) {
        nbOfCommitMaster = Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.length
    }
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.length == nbOfCommitMaster + 1)

    removeTmpDirectories()
    removeTmpLogs(currentBranch)
    SgitTools.updateRef(currentCommit, currentBranch)
    StageManagement.clearStage(currentBranch)
  }

  test("sgit commit add 1 line in refs/heads/master") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    val currentCommit = SgitTools.getCurrentCommit(currentBranch)
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(new File(s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").exists())
    assert(Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.length == 1)

    removeTmpDirectories()
    removeTmpLogs(currentBranch)
    SgitTools.updateRef(currentCommit, currentBranch)
    StageManagement.clearStage(currentBranch)
  }

  test("before sgit commit files are added in stage") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    assert(StageManagement.containsNewFiles(currentBranch))

    removeTmpDirectories()
    StageManagement.clearStage(currentBranch)
  }

  test("sgit commit archived all files in stage") {
    Init.init()
    createTmpDirectories()
    val currentBranch = SgitTools.getCurrentBranch()
    val currentCommit = SgitTools.getCurrentCommit(currentBranch)
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(!StageManagement.containsNewFiles(currentBranch))

    removeTmpDirectories()
    removeTmpLogs(currentBranch)
    SgitTools.updateRef(currentCommit, currentBranch)
    StageManagement.clearStage(currentBranch)
  }

  def createTmpDirectories(): Unit = {
    new File("testCommit").mkdirs()
    new File(s"testCommit${File.separator}testCommit.txt").createNewFile()
    new File("rootTestCommit.txt").createNewFile()
  }

  def removeTmpDirectories(): Unit = {
    val pathSgit = PathManagement.getSgitPath().get
    //Remove directory and files added for test
    new Directory(new File("testCommit")).deleteRecursively()
    new File("rootTestCommit.txt").delete()

    //Remove files in .sgit
    new File(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}2e${File.separator}e73a5c8b6af285417173f63d33b45e5b6e4cbeb7792929fa9b9310d9ce44f9").delete()
    new File(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}3f${File.separator}8477badde5c5fc540d68770cff5b88251fe8949f621dc77779925232a2debf").delete()
    new File(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}81${File.separator}0872640304fb4f3739b1ec1afca1fd0d0e3ceee663a1a00eb2b27ee5b4942e").delete()

    //Remove directories in .sgit if they are empty
    if(FileManagement.getListOfFilesAndDirectories(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}2e").isEmpty) {
      new Directory(new File(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}2e")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}3f").isEmpty) {
      new Directory(new File(s"${pathSgit}${File.separator}objects${File.separator}commit${File.separator}3f")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}81").isEmpty) {
      new Directory(new File(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}81")).deleteRecursively()
    }
  }

  def removeTmpLogs(currentBranch: String): Unit = {
    val linesHead = Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD").getLines.drop(1).mkString("\n")
    if(linesHead != "") {
      FileManagement.writeFile(s".sgit${File.separator}logs${File.separator}HEAD", linesHead)
    }

    val linesMaster = Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.drop(1).mkString("\n")
    if(linesMaster != "") {
      FileManagement.writeFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}", linesMaster)
    }
  }

}