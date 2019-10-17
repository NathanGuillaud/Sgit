package action

import java.io.File

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import util.{FileManagement, SgitTools, TestEnvironment}

class TestAdd extends FunSuite with BeforeAndAfterEach {

  override protected def beforeEach(): Unit = {
    TestEnvironment.createTestDirectory()
    TestEnvironment.goToTestDirectory()
    createTmpDirectory()
  }
  override protected def afterEach(): Unit = {
    TestEnvironment.deleteTestDirectory()
  }

  test("sgit add add blobs in .sgit directory") {
    Init.init()
    val nbOfBlobDirs = FileManagement.getFilesFromDirectory(new File(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}objects${File.separator}blob")).length

    Add.add(Array("testAdd", "rootTestAdd.txt"))

    assert(FileManagement.getFilesFromDirectory(new File(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}objects${File.separator}blob")).length == nbOfBlobDirs + 3)
  }

  test("sgit add fill the stage file of the current branch") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()

    Add.add(Array("testAdd", "rootTestAdd.txt"))

    assert(FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}stages${File.separator}${currentBranch}")).split("\n").length == 3)
  }

  test("sgit add replace a file if the file is already in the stage") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()
    Add.add(Array("testAdd", "rootTestAdd.txt"))
    val nbLinesInStage = FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}stages${File.separator}${currentBranch}")).split(File.separator).length

    Add.add(Array("testAdd", "rootTestAdd.txt"))

    assert(FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}.sgit${File.separator}stages${File.separator}${currentBranch}")).split(File.separator).length == nbLinesInStage)
  }

  def createTmpDirectory(): Unit = {
    new File(s"${System.getProperty("user.dir")}${File.separator}testAdd${File.separator}subDir").mkdirs()
    new File(s"${System.getProperty("user.dir")}${File.separator}testAdd${File.separator}testAdd.txt").createNewFile()
    new File(s"${System.getProperty("user.dir")}${File.separator}testAdd${File.separator}subDir${File.separator}subDirTestAdd.txt").createNewFile()
    new File(s"${System.getProperty("user.dir")}${File.separator}rootTestAdd.txt").createNewFile()
  }

}