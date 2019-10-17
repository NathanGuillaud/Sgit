package action

import java.io.File

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import util.{FileManagement, SgitTools, TestEnvironment}

import scala.io.Source

class TestTag extends FunSuite with BeforeAndAfterEach {

  override protected def beforeEach(): Unit = {
    TestEnvironment.createTestDirectory()
    TestEnvironment.goToTestDirectory()
    createTmpDirectories()
  }
  override protected def afterEach(): Unit = {
    TestEnvironment.deleteTestDirectory()
  }

  test("sgit tag should not create a reference of the tag in .sgit directory if no commit was performed") {
    Init.init()
    Tag.tag(Array("q4s5d6"))
    assert(!new File(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}refs${File.separator}tags${File.separator}q4s5d6").exists())
  }

  test("sgit tag should create a reference of the tag in .sgit directory") {
    Init.init()
    Add.add(Array("testCommit", "rootTestCommit.txt"))
    CommitAction.commit()
    Tag.tag(Array("q4s5d6"))
    assert(new File(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}refs${File.separator}tags${File.separator}q4s5d6").exists())
  }

  test("tag file in .sgit directory should contains 1 line with the current commit") {
    Init.init()
    Add.add(Array("testCommit", "rootTestCommit.txt"))
    CommitAction.commit()
    val currentCommit = SgitTools.getCurrentCommit(SgitTools.getCurrentBranch())
    Tag.tag(Array("q4s5d6"))
    assert(Source.fromFile(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}refs${File.separator}tags${File.separator}q4s5d6").getLines.length == 1)
    assert(FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}refs${File.separator}tags${File.separator}q4s5d6")) == currentCommit)
  }

  def createTmpDirectories(): Unit = {
    new File(s"${System.getProperty("user.dir")}${File.separator}testCommit").mkdirs()
    new File(s"${System.getProperty("user.dir")}${File.separator}testCommit${File.separator}testCommit.txt").createNewFile()
    new File(s"${System.getProperty("user.dir")}${File.separator}rootTestCommit.txt").createNewFile()
  }

}