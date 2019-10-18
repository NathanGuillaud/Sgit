package action

import java.io.File

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import util.{FileManagement, PathManagement, SgitTools, TestEnvironment}

class TestDiff extends FunSuite with BeforeAndAfterEach {

  override protected def beforeEach(): Unit = {
    TestEnvironment.createTestDirectory()
    TestEnvironment.goToTestDirectory()
    createTmpDirectory()
  }
  override protected def afterEach(): Unit = {
    TestEnvironment.deleteTestDirectory()
  }

  test("diff between 2 empty files is null") {
    //No old file (hash 0000000) and no new file
    assert(Diff.getDeltasBetweenFiles("0000000", None).length == 0)
  }

  test("diff between 1 empty file and 1 non empty file should return a list with the number of lines of the new file") {
    val nbLinesOfNewFile = FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt")).split("\n").length

    assert(Diff.getDeltasBetweenFiles("0000000", PathManagement.getFilePathFromProjectRoot(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt")).length == nbLinesOfNewFile)
  }

  test("diff between 1 empty file and 1 non empty file should return a list with only insertions") {
    val nbLinesOfNewFile = FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt")).split("\n").length
    val deltas = Diff.getDeltasBetweenFiles("0000000", PathManagement.getFilePathFromProjectRoot(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt"))

    assert(LogStat.getNumberOfActionInDeltas("+", deltas) == nbLinesOfNewFile)
    assert(LogStat.getNumberOfActionInDeltas("-", deltas) == 0)
  }

  test("diff between 1 non empty file and 1 empty file should return a list with the number of lines of the old file") {
    Init.init()
    Add.add(Array("rootTestDiff.txt"))
    val hash = FileManagement.hashFile("rootTestDiff.txt", FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt")))
    val nbLinesOfNewFile = FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt")).split("\n").length

    assert(Diff.getDeltasBetweenFiles(hash, None).length == nbLinesOfNewFile)
  }

  test("diff between 1 non empty file and 1 empty file should return a list with only deletions") {
    Init.init()
    Add.add(Array("rootTestDiff.txt"))
    val hash = FileManagement.hashFile("rootTestDiff.txt", FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt")))
    val nbLinesOfNewFile = FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt")).split("\n").length
    val deltas = Diff.getDeltasBetweenFiles(hash, None)
    
    assert(LogStat.getNumberOfActionInDeltas("-", deltas) == nbLinesOfNewFile)
    assert(LogStat.getNumberOfActionInDeltas("+", deltas) == 0)
  }

  def createTmpDirectory(): Unit = {
    new File(s"${System.getProperty("user.dir")}${File.separator}testDiff${File.separator}subDir").mkdirs()
    new File(s"${System.getProperty("user.dir")}${File.separator}testDiff${File.separator}testDiff.txt").createNewFile()
    new File(s"${System.getProperty("user.dir")}${File.separator}testDiff${File.separator}subDir${File.separator}subDirTestDiff.txt").createNewFile()
    new File(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt").createNewFile()
    FileManagement.writeFile(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt", "Hello\nWorld")
  }

}