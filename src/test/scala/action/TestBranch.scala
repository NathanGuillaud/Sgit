package action

import java.io.File

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import util.{FileManagement, SgitTools, TestEnvironment}

import scala.io.Source

class TestBranch extends FunSuite with BeforeAndAfterEach {

  override protected def beforeEach(): Unit = {
    TestEnvironment.createTestDirectory()
    TestEnvironment.goToTestDirectory()
    createTmpDirectories()
  }
  override protected def afterEach(): Unit = {
    TestEnvironment.deleteTestDirectory()
  }

  test("sgit branch should not create a reference of the branch in .sgit directory if no commit was performed") {
    Init.init()
    Branch.branch(Array("a7z8e9"))
    assert(!new File(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}refs${File.separator}heads${File.separator}a7z8e9").exists())
  }

  test("sgit branch should create new reference file") {
    Init.init()
    Add.add(Array("testCommit", "rootTestCommit.txt"))
    CommitAction.commit()
    Branch.branch(Array("a7z8e9"))
    assert(new File(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}refs${File.separator}heads${File.separator}a7z8e9").exists())
  }

  test("branch file in .sgit directory should contains 1 line with the current commit") {
    Init.init()
    Add.add(Array("testCommit", "rootTestCommit.txt"))
    CommitAction.commit()
    val currentCommit = SgitTools.getCurrentCommit(SgitTools.getCurrentBranch())
    Branch.branch(Array("a7z8e9"))
    assert(Source.fromFile(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}refs${File.separator}heads${File.separator}a7z8e9").getLines.length == 1)
    assert(FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}refs${File.separator}heads${File.separator}a7z8e9")) == currentCommit)
  }

  def createTmpDirectories(): Unit = {
    new File(s"${System.getProperty("user.dir")}${File.separator}testCommit").mkdirs()
    new File(s"${System.getProperty("user.dir")}${File.separator}testCommit${File.separator}testCommit.txt").createNewFile()
    new File(s"${System.getProperty("user.dir")}${File.separator}rootTestCommit.txt").createNewFile()
  }

}