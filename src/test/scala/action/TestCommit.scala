package action

import java.io.File

import org.scalatest.FunSuite
import util.FileManagement

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
}