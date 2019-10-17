package action

import java.io.File

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import util.{FileManagement, PathManagement, SgitTools, StageManagement, TestEnvironment}

import scala.reflect.io.Directory

class TestCheckout extends FunSuite with BeforeAndAfterEach {

  override protected def beforeEach(): Unit = {
    TestEnvironment.createTestDirectory()
    TestEnvironment.goToTestDirectory()
    createTmpFile()
  }
  override protected def afterEach(): Unit = {
    TestEnvironment.deleteTestDirectory()
  }

  test("sgit checkout with commit hash give an old version of a file") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()
    //First commit
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    val firstCommit = SgitTools.getCurrentCommit(currentBranch)
    //Second commit
    updateTmpFile()
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    Checkout.checkout(Array(firstCommit))

    assert(FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}testCheckout.txt")) == "version1")
  }

  test("sgit checkout with branch name give an old version of a file") {
    Init.init()
    val branchName = "azsq"
    //First commit
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    //Second commit
    Branch.branch(Array(branchName))
    updateTmpFile()
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    Checkout.checkout(Array(branchName))

    assert(FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}testCheckout.txt")) == "version1")
  }

  test("sgit checkout with tag name give an old version of a file") {
    Init.init()
    val tagName = "1254.3587"
    //First commit
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    //Second commit
    Tag.tag(Array(tagName))
    updateTmpFile()
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    Checkout.checkout(Array(tagName))

    assert(FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}testCheckout.txt")) == "version1")
  }

  def createTmpFile(): Unit = {
    FileManagement.writeFile(s"${System.getProperty("user.dir")}${File.separator}testCheckout.txt", "version1")
  }

  def updateTmpFile(): Unit = {
    FileManagement.writeFile(s"${System.getProperty("user.dir")}${File.separator}testCheckout.txt", "version2")
  }

}