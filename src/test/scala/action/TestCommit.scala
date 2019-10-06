package action

import java.io.File
import java.nio.file.{Files, Paths}

import action.CommitAction.createLogFileForBranch
import org.scalatest.FunSuite
import util.FileManagement

import scala.io.Source
import scala.reflect.io.Directory

class TestCommit extends FunSuite {
  test("sgit commit add 1 tree and 1 commit in .sgit directory") {
    val nbOfTreesDirs = FileManagement.getListOfFilesAndDirectories(".sgit/objects/tree").length
    val nbOfCommitDirs = FileManagement.getListOfFilesAndDirectories(".sgit/objects/commit").length
    Init.init()
    CommitAction.commit()
    assert(FileManagement.getListOfFilesAndDirectories(".sgit/objects/tree").length == nbOfTreesDirs + 1)
    assert(FileManagement.getListOfFilesAndDirectories(".sgit/objects/commit").length == nbOfCommitDirs + 1)
    removeTmpDirectories()
    removeTmpLogs()
  }

  test("sgit commit add 1 line in logs/HEAD, logs/refs/heads/master and refs/heads/master") {
    var nbOfCommits = 0
    var nbOfCommitMaster = 0
    if(Files.exists(Paths.get(".sgit/logs/HEAD"))) {
      nbOfCommits = Source.fromFile(".sgit/logs/HEAD").getLines.length
      if(Files.exists(Paths.get(".sgit/logs/refs/heads/master"))) {
        nbOfCommitMaster = Source.fromFile(".sgit/logs/refs/heads/master").getLines.length
      }
    }
    CommitAction.commit()
    assert(Source.fromFile(".sgit/logs/HEAD").getLines.length == nbOfCommits + 1)
    assert(Source.fromFile(".sgit/logs/refs/heads/master").getLines.length == nbOfCommitMaster + 1)
    assert(new File(".sgit/refs/heads/master").exists())
    assert(Source.fromFile(".sgit/refs/heads/master").getLines.length == 1)
    removeTmpDirectories()
    removeTmpLogs()
  }

  def removeTmpDirectories(): Unit = {
    //Remove files in .sgit
    new File(".sgit/objects/tree/e3/b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855").delete()
    new File(".sgit/objects/commit/1f/1fd455c11a668fdf8c38a01f9534f66a9278ea3ee2e959827dabf5008c908c").delete()

    //Remove directories in .sgit if they are empty
    if(FileManagement.getListOfFilesAndDirectories(".sgit/objects/tree/e3").isEmpty) {
      new Directory(new File(".sgit/objects/tree/e3")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(".sgit/objects/commit/1f").isEmpty) {
      new Directory(new File(".sgit/objects/commit/1f")).deleteRecursively()
    }
  }

  def removeTmpLogs(): Unit = {
    val linesHead = Source.fromFile(".sgit/logs/HEAD").getLines.drop(1).mkString("\n")
    FileManagement.writeFile(".sgit/logs/HEAD", linesHead)
    val linesMaster = Source.fromFile(".sgit/logs/refs/heads/master").getLines.drop(1).mkString("\n")
    FileManagement.writeFile(".sgit/logs/refs/heads/master", linesMaster)
  }
}