package util

import java.io.File
import java.nio.file.Paths

import scala.reflect.io.Directory

object TestEnvironment {

  /**
   * Create a temporary directory for tests
   */
  def createTestDirectory(): Unit = {
    new File("tmpDirForTests").mkdirs()
  }

  /**
   * Go to the test directory
   */
  def goToTestDirectory(): Unit = {
    val currentDir = System.getProperty("user.dir")
    System.setProperty("user.dir", s"${currentDir}/tmpDirForTests")
  }

  /**
   * Delete the temporary directory for tests
   */
  def deleteTestDirectory(): Unit = {
    val currentDir = System.getProperty("user.dir")
    val parentPath = Paths.get(currentDir).getParent()
    System.setProperty("user.dir", parentPath.toString)
    new Directory(new File("tmpDirForTests")).deleteRecursively()
  }

}
