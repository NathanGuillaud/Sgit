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

  test("diff between 2 files should return 1 insertion") {
    Init.init()
    Add.add(Array("rootTestDiff.txt"))
    val oldHash = FileManagement.hashFile("rootTestDiff.txt", FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt")))
    updateFileOneInsertion()
    val deltas = Diff.getDeltasBetweenFiles(oldHash, Some("rootTestDiff.txt"))

    assert(LogStat.getNumberOfActionInDeltas("-", deltas) == 0)
    assert(LogStat.getNumberOfActionInDeltas("+", deltas) == 1)
  }

  test("diff between 2 files should return 1 deletion") {
    Init.init()
    Add.add(Array("rootTestDiff.txt"))
    val oldHash = FileManagement.hashFile("rootTestDiff.txt", FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt")))
    updateFileOneDeletion()
    val deltas = Diff.getDeltasBetweenFiles(oldHash, Some("rootTestDiff.txt"))

    assert(LogStat.getNumberOfActionInDeltas("-", deltas) == 1)
    assert(LogStat.getNumberOfActionInDeltas("+", deltas) == 0)
  }

  test("diff between 2 files should return 1 insertion and 2 deletions") {
    Init.init()
    Add.add(Array("rootTestDiff.txt"))
    val oldHash = FileManagement.hashFile("rootTestDiff.txt", FileManagement.readFile(new File(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt")))
    updateFileOneInsertionTwoDeletions()
    val deltas = Diff.getDeltasBetweenFiles(oldHash, Some("rootTestDiff.txt"))

    assert(LogStat.getNumberOfActionInDeltas("-", deltas) == 2)
    assert(LogStat.getNumberOfActionInDeltas("+", deltas) == 1)
  }

  def createTmpDirectory(): Unit = {
    new File(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt").createNewFile()
    FileManagement.writeFile(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt", "Hello\nWorld")
  }

  def updateFileOneInsertion(): Unit = {
    FileManagement.writeFile(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt", "Hello\nWorld\ntest")
  }

  def updateFileOneDeletion(): Unit = {
    FileManagement.writeFile(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt", "World")
  }

  def updateFileOneInsertionTwoDeletions(): Unit = {
    FileManagement.writeFile(s"${System.getProperty("user.dir")}${File.separator}rootTestDiff.txt", "Test")
  }

}