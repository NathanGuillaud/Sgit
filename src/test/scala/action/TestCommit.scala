package action

import java.io.File
import java.nio.file.{Files, Paths}

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import util.{FileManagement, PathManagement, SgitTools, StageManagement, TestEnvironment}

import scala.io.Source

class TestCommit extends FunSuite with BeforeAndAfterEach {

  override protected def beforeEach(): Unit = {
    TestEnvironment.createTestDirectory()
    TestEnvironment.goToTestDirectory()
    createTmpDirectories()
  }
  override protected def afterEach(): Unit = {
    TestEnvironment.deleteTestDirectory()
  }

  test("sgit commit add 2 trees in .sgit directory") {
    Init.init()
    val nbOfTree = FileManagement.getFilesFromDirectory(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}tree")).length
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(FileManagement.getFilesFromDirectory(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}tree")).length == nbOfTree + 2)
  }

  test("sgit commit add 1 commit in .sgit directory") {
    Init.init()
    val nbOfCommit = FileManagement.getFilesFromDirectory(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}commit")).length
    Add.add(Array("rootTestCommit.txt"))

    CommitAction.commit()

    assert(FileManagement.getFilesFromDirectory(new File(s"${PathManagement.getSgitPath().get}${File.separator}objects${File.separator}commit")).length == nbOfCommit + 1)
  }

  test("sgit commit add 1 line in logs/HEAD") {
    Init.init()
    var nbOfCommits = 0
    if(Files.exists(Paths.get(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD"))) {
      nbOfCommits = Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD").getLines.length
    }
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD").getLines.length == nbOfCommits + 1)
  }

  test("sgit commit add 1 line in refs/heads/master") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(new File(s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").exists())
    assert(Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.length == 1)
  }

  test("before sgit commit files are added in stage") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    assert(StageManagement.containsNewFiles(currentBranch))
  }

  test("sgit commit archived all files in stage") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()
    Add.add(Array("testCommit", "rootTestCommit.txt"))

    CommitAction.commit()

    assert(!StageManagement.containsNewFiles(currentBranch))
  }

  def createTmpDirectories(): Unit = {
    new File(s"${System.getProperty("user.dir")}${File.separator}testCommit").mkdirs()
    new File(s"${System.getProperty("user.dir")}${File.separator}testCommit${File.separator}testCommit.txt").createNewFile()
    new File(s"${System.getProperty("user.dir")}${File.separator}rootTestCommit.txt").createNewFile()
  }

}