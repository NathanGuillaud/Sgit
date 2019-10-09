package action

import java.io.File

import org.scalatest.FunSuite
import util.{FileManagement, SgitTools}

import scala.reflect.io.Directory

class TestAdd extends FunSuite {
  test("sgit add add blobs in .sgit directory") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()
    createTmpDirectory()
    val nbOfBlobDirs = FileManagement.exploreDirectory(new File(s".sgit${File.separator}objects${File.separator}blob")).length

    Add.add(Array("testAdd", "rootTestAdd.txt"))

    assert(FileManagement.exploreDirectory(new File(s".sgit${File.separator}objects${File.separator}blob")).length == nbOfBlobDirs + 3)

    removeTmpDirectories()
    SgitTools.clearStage(currentBranch)
  }

  test("sgit add fill the stage file of the current branch") {
    Init.init()
    createTmpDirectory()
    val currentBranch = SgitTools.getCurrentBranch()
    val nbLinesInStage = FileManagement.readFile(new File(s".sgit${File.separator}stages${File.separator}${currentBranch}")).split(File.separator).length

    Add.add(Array("testAdd", "rootTestAdd.txt"))

    assert(FileManagement.readFile(new File(s".sgit${File.separator}stages${File.separator}${currentBranch}")).split(File.separator).length == nbLinesInStage + 3)

    removeTmpDirectories()
    SgitTools.clearStage(currentBranch)
  }

  test("sgit add replace a file if it is already in the stage") {
    Init.init()
    createTmpDirectory()
    val currentBranch = SgitTools.getCurrentBranch()
    Add.add(Array("testAdd", "rootTestAdd.txt"))
    val nbLinesInStage = FileManagement.readFile(new File(s".sgit${File.separator}stages${File.separator}${currentBranch}")).split(File.separator).length

    Add.add(Array("testAdd", "rootTestAdd.txt"))

    assert(FileManagement.readFile(new File(s".sgit${File.separator}stages${File.separator}${currentBranch}")).split(File.separator).length == nbLinesInStage)

    removeTmpDirectories()
    SgitTools.clearStage(currentBranch)
  }

  def createTmpDirectory(): Unit = {
    new File("testAdd/subDir").mkdirs()
    new File("testAdd/testAdd.txt").createNewFile()
    new File("testAdd/subDir/subDirTestAdd.txt").createNewFile()
    new File("rootTestAdd.txt").createNewFile()
  }

  def removeTmpDirectories(): Unit = {
    //Remove directories and files added for test
    new Directory(new File("testAdd")).deleteRecursively()
    new File("rootTestAdd.txt").delete()

    //Remove files in .sgit
    new File(".sgit/objects/blob/5f/b4a5dafc86609af8d51f26f4d3cbddeb809ff372e0d18a1f511a5340799a4d").delete()
    new File(".sgit/objects/blob/e5/a536016f35d3a95c4237092ad38ad69e9595d6501603d6076e523bcd38f089").delete()
    new File(".sgit/objects/blob/fd/11e87548101d72352a3d8f57ce65ebf482aa8bc5afaee0dcbfdd99e3425ae9").delete()

    //Remove directories in .sgit if they are empty
    if(FileManagement.getListOfFilesAndDirectories(".sgit/objects/blob/5f").isEmpty) {
      new Directory(new File(".sgit/objects/blob/5f")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(".sgit/objects/blob/e5").isEmpty) {
      new Directory(new File(".sgit/objects/blob/e5")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(".sgit/objects/blob/fd").isEmpty) {
      new Directory(new File(".sgit/objects/blob/fd")).deleteRecursively()
    }
  }

}