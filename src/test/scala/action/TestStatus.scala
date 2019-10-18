package action

import java.io.File

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import util.{FileManagement, PathManagement, SgitTools, StageManagement, TestEnvironment}

class TestStatus extends FunSuite with BeforeAndAfterEach {

  override protected def beforeEach(): Unit = {
    TestEnvironment.createTestDirectory()
    TestEnvironment.goToTestDirectory()
    createTmpDirectory()
  }
  override protected def afterEach(): Unit = {
    TestEnvironment.deleteTestDirectory()
  }

  test("file state in the stage should be added after sgit add new files") {
    Init.init()
    Add.add(Array("testStatus", "rootTestStatus.txt"))

    assert(StageManagement.getAddedFiles(SgitTools.getCurrentBranch()).length == 3)
  }

  test("file state in the stage should become commited after sgit commit") {
    Init.init()
    Add.add(Array("testStatus", "rootTestStatus.txt"))
    CommitAction.commit()

    assert(StageManagement.getAddedFiles(SgitTools.getCurrentBranch()).length == 0)
  }

  test("file state in the stage should become added after sgit commit just for files changed") {
    Init.init()
    Add.add(Array("testStatus", "rootTestStatus.txt"))
    CommitAction.commit()
    updateTmpFile()
    Add.add(Array("testStatus", "rootTestStatus.txt"))

    assert(StageManagement.getAddedFiles(SgitTools.getCurrentBranch()).length == 1)
    assert(StageManagement.getAddedFiles(SgitTools.getCurrentBranch())(0)._2 == PathManagement.getFilePathFromProjectRoot(s"${System.getProperty("user.dir")}${File.separator}rootTestStatus.txt").get)
  }

  test("file state in the stage should become new for new files and modified for modified files") {
    Init.init()
    Add.add(Array("testStatus", "rootTestStatus.txt"))
    CommitAction.commit()
    createNewTmpFile()
    updateTmpFile()
    Add.add(Array("testStatus", "rootTestStatus.txt"))

    assert(StageManagement.getAddedFiles(SgitTools.getCurrentBranch()).length == 2)
    assert(StageManagement.getAddedFiles(SgitTools.getCurrentBranch())
      .filter(file => file._2 == PathManagement.getFilePathFromProjectRoot(s"${System.getProperty("user.dir")}${File.separator}rootTestStatus.txt").get)(0)._1 == "modified")
    assert(StageManagement.getAddedFiles(SgitTools.getCurrentBranch())
      .filter(file => file._2 == PathManagement.getFilePathFromProjectRoot(s"${System.getProperty("user.dir")}${File.separator}testStatus${File.separator}newFile.txt").get)(0)._1 == "new")
  }

  test("getUpdatedFiles should return an empty list if no files are in the stage") {
    Init.init()
    Add.add(Array("testStatus", "rootTestStatus.txt"))

    assert(Status.getUpdatedFiles(SgitTools.getCurrentBranch()).length == 0)
  }

  test("getUpdatedFiles should return only files already added but modified since the last adding") {
    Init.init()
    Add.add(Array("testStatus", "rootTestStatus.txt"))
    updateTmpFile()
    val filesUpdatedBeforeCommit = Status.getUpdatedFiles(SgitTools.getCurrentBranch())
    CommitAction.commit()
    val filesUpdatedAfterCommit = Status.getUpdatedFiles(SgitTools.getCurrentBranch())

    assert(filesUpdatedAfterCommit.length == 1)
    assert(filesUpdatedAfterCommit(0) == filesUpdatedBeforeCommit(0))
  }

  test("getUntrackedFiles should return an empty list if all files in the directory are added") {
    Init.init()
    Add.add(Array("testStatus", "rootTestStatus.txt"))

    assert(Status.getUntrackedFiles(SgitTools.getCurrentBranch()).length == 0)
  }

  test("getUntrackedFiles should return files never added in the directory") {
    Init.init()
    Add.add(Array("rootTestStatus.txt"))

    assert(Status.getUntrackedFiles(SgitTools.getCurrentBranch()).length == 2)
  }

  def createTmpDirectory(): Unit = {
    new File(s"${System.getProperty("user.dir")}${File.separator}testStatus${File.separator}subDir").mkdirs()
    new File(s"${System.getProperty("user.dir")}${File.separator}testStatus${File.separator}testStatus.txt").createNewFile()
    new File(s"${System.getProperty("user.dir")}${File.separator}testStatus${File.separator}subDir${File.separator}subDirTestStatus.txt").createNewFile()
    new File(s"${System.getProperty("user.dir")}${File.separator}rootTestStatus.txt").createNewFile()
  }

  def updateTmpFile(): Unit = {
    FileManagement.writeFile(s"${System.getProperty("user.dir")}${File.separator}rootTestStatus.txt", "new content")
  }

  def createNewTmpFile(): Unit = {
    new File(s"${System.getProperty("user.dir")}${File.separator}testStatus${File.separator}newFile.txt").createNewFile()
  }

}