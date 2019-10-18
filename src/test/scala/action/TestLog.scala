package action

import java.io.File
import java.nio.file.{Files, Paths}

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import util.{PathManagement, SgitTools, TestEnvironment}

import scala.io.Source

class TestLog extends FunSuite with BeforeAndAfterEach {

  override protected def beforeEach(): Unit = {
    TestEnvironment.createTestDirectory()
    TestEnvironment.goToTestDirectory()
    createTmpDirectories()
  }
  override protected def afterEach(): Unit = {
    TestEnvironment.deleteTestDirectory()
  }

  /*test("sgit log should print all the commits") {
    Init.init()
    Log.log()
    var nbCommits = 0
    if(Files.exists(Paths.get(".sgit/logs/HEAD"))) {
      nbCommits = Source.fromFile(".sgit/logs/HEAD").getLines.length
    }
    var nbLinesForLog = 1
    if(nbCommits > 0){
      var nbLinesForLog = nbCommits*4
    }
    //RETRIEVE NB LINES PRINTED IN CONSOLE
    val nbLinesPrinted = nbLinesForLog
    assert(nbLinesPrinted == nbLinesForLog)
  }*/

  test("1 line is added in logs/refs/heads/master file when commit") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()
    var nbOfCommitMaster = 0
    if(Files.exists(Paths.get(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}"))) {
      nbOfCommitMaster = Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.length
    }
    Add.add(Array("testLog", "rootTestLog.txt"))

    CommitAction.commit()

    assert(Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.length == nbOfCommitMaster + 1)
  }

  test("1 line is added in logs/HEAD file when commit") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()
    var nbOfCommitMaster = 0
    if(Files.exists(Paths.get(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD"))) {
      nbOfCommitMaster = Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}HEAD").getLines.length
    }
    Add.add(Array("testLog", "rootTestLog.txt"))

    CommitAction.commit()

    assert(Source.fromFile(s"${PathManagement.getSgitPath().get}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${currentBranch}").getLines.length == nbOfCommitMaster + 1)
  }

  def createTmpDirectories(): Unit = {
    new File(s"${System.getProperty("user.dir")}${File.separator}testLog").mkdirs()
    new File(s"${System.getProperty("user.dir")}${File.separator}testLog${File.separator}testLog.txt").createNewFile()
    new File(s"${System.getProperty("user.dir")}${File.separator}rootTestLog.txt").createNewFile()
  }

}