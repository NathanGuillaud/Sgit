package action

import java.io.File

import org.scalatest.FunSuite
import util.FileManagement

import scala.reflect.io.Directory

class TestAdd extends FunSuite {
  test("sgit add add blobs and trees in .sgit directory") {
    createTmpDirectory()
    val nbOfTreesDirs = FileManagement.getListOfFilesAndDirectories(".sgit/objects/tree").length
    val nbOfBlobDirs = FileManagement.getListOfFilesAndDirectories(".sgit/objects/blob").length
    println("TAILLEELELELELE : " + nbOfBlobDirs)
    Init.init()
    Add.add(Array("testAdd"))
    assert(FileManagement.getListOfFilesAndDirectories(".sgit/objects/tree").length == nbOfTreesDirs + 2)
    assert(FileManagement.getListOfFilesAndDirectories(".sgit/objects/blob").length == nbOfBlobDirs + 2)
    removeTmpDirectory()
  }

  def createTmpDirectory(): Unit = {
    new File("testAdd/subDir").mkdirs()
    new File("testAdd/testAdd.txt").createNewFile()
    new File("testAdd/subDir/subDirTestAdd.txt").createNewFile()
  }

  def removeTmpDirectory(): Unit = {
    //Remove directory added
    new Directory(new File("testAdd")).deleteRecursively()

    //Remove files in .sgit
    new File(".sgit/objects/tree/75/76be4616c7bcb5aa4623383497b2f4212fe67d73b0302f8ab57d67e46931d8").delete()
    new File(".sgit/objects/tree/ad/ca97a4b2d1dfea6d078689487f114a57e67fd037a858d260a1cb6c1014f9ec").delete()
    new File(".sgit/objects/blob/5f/b4a5dafc86609af8d51f26f4d3cbddeb809ff372e0d18a1f511a5340799a4d").delete()
    new File(".sgit/objects/blob/e5/a536016f35d3a95c4237092ad38ad69e9595d6501603d6076e523bcd38f089").delete()

    //Remove directories in .sgit if they are empty
    if(FileManagement.getListOfFilesAndDirectories(".sgit/objects/tree/ad").isEmpty) {
      new Directory(new File(".sgit/objects/tree/ad")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(".sgit/objects/tree/75").isEmpty) {
      new Directory(new File(".sgit/objects/tree/75")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(".sgit/objects/blob/5f").isEmpty) {
      new Directory(new File(".sgit/objects/blob/5f")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(".sgit/objects/blob/e5").isEmpty) {
      new Directory(new File(".sgit/objects/blob/e5")).deleteRecursively()
    }
  }
}