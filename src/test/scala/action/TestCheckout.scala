package action

import java.io.File

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import util.{FileManagement, SgitTools, TestEnvironment}

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
    //Initialisation of a sgit repository
    Init.init()
    //Create a branch name
    val branchName = "azsq"
    //Add a file with content "version1"
    Add.add(Array("testCheckout.txt"))
    //Commit the stage with the file "testCheckout.txt"
    CommitAction.commit()
    //Create a branch
    Branch.branch(Array(branchName))
    //Update the content of the file to "version2"
    updateTmpFile()
    //Add the file in the branch master
    Add.add(Array("testCheckout.txt"))
    //Commit the file in the branch master
    CommitAction.commit()
    //Go to the branch "azsq" with the first version of the file
    Checkout.checkout(Array(branchName))

    //Check that the content is "version1"
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