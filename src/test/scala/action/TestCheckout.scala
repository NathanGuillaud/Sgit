package action

import java.io.File

import org.scalatest.FunSuite
import util.{FileManagement, PathManagement, SgitTools, StageManagement}

import scala.reflect.io.Directory

class TestCheckout extends FunSuite {
  test("sgit checkout with commit hash give an old version of a file") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()
    createTmpFile()
    //First commit
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    val firstCommit = SgitTools.getCurrentCommit(currentBranch)
    //Second commit
    updateTmpFile()
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    Checkout.checkout(Array(firstCommit))

    assert(FileManagement.readFile(new File("testCheckout.txt")) == "version1")

    removeTmpElements()
    StageManagement.clearStage(currentBranch)
  }

  test("sgit checkout with branch name give an old version of a file") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()
    val branchName = "azsq"
    createTmpFile()
    //First commit
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    //Second commit
    Branch.branch(Array(branchName))
    updateTmpFile()
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    Checkout.checkout(Array(branchName))

    assert(FileManagement.readFile(new File("testCheckout.txt")) == "version1")

    removeTmpElements()
    removeBranch(branchName)
    StageManagement.clearStage(currentBranch)
  }

  test("sgit checkout with tag name give an old version of a file") {
    Init.init()
    val currentBranch = SgitTools.getCurrentBranch()
    val tagName = "1254.3587"
    createTmpFile()
    //First commit
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    //Second commit
    Tag.tag(Array(tagName))
    updateTmpFile()
    Add.add(Array("testCheckout.txt"))
    CommitAction.commit()
    Checkout.checkout(Array(tagName))

    assert(FileManagement.readFile(new File("testCheckout.txt")) == "version1")

    removeTmpElements()
    removeTag(tagName)
    StageManagement.clearStage(currentBranch)
  }

  def createTmpFile(): Unit = {
    FileManagement.writeFile("testCheckout.txt", "version1")
  }

  def updateTmpFile(): Unit = {
    FileManagement.writeFile("testCheckout.txt", "version2")
  }

  def removeTmpElements(): Unit = {
    val pathSgit = PathManagement.getSgitPath().get
    //Remove directories and files added for test
    new File("testCheckout.txt").delete()

    //Remove files in .sgit
    new File(s"${pathSgit}${File.separator}objects${File.separator}blob${File.separator}73${File.separator}4339d3d46292c5993ebe02a9a3be08884fe34564abe3db413fd4cf88f286fa").delete()
    new File(s"${pathSgit}${File.separator}objects${File.separator}blob${File.separator}3e${File.separator}502cf33af73a33c4f17ec1117d5ab84ffbc937ca1f8ee5a2856223be343be1").delete()
    new File(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}8b${File.separator}cc642519854564aba2ab3c9f7ea2aa80ec2d73fbc94440ae747ab61dc07c9a").delete()
    new File(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}da${File.separator}9609b9caf8492b43ea66b91d104611eef01430ab223ef7ac949157a8657815").delete()

    //Remove directories in .sgit if they are empty
    if(FileManagement.getListOfFilesAndDirectories(s"${pathSgit}${File.separator}objects${File.separator}blob${File.separator}73").isEmpty) {
      new Directory(new File(s"${pathSgit}${File.separator}objects${File.separator}blob${File.separator}73")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(s"${pathSgit}${File.separator}objects${File.separator}blob${File.separator}3e").isEmpty) {
      new Directory(new File(s"${pathSgit}${File.separator}objects${File.separator}blob${File.separator}3e")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}8b").isEmpty) {
      new Directory(new File(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}8b")).deleteRecursively()
    }
    if(FileManagement.getListOfFilesAndDirectories(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}da").isEmpty) {
      new Directory(new File(s"${pathSgit}${File.separator}objects${File.separator}tree${File.separator}da")).deleteRecursively()
    }
  }

  def removeBranch(branchName: String): Unit = {
    val pathSgit = PathManagement.getSgitPath().get
    new File(s"${pathSgit}${File.separator}stages${File.separator}${branchName}").delete()
    new File(s"${pathSgit}${File.separator}refs${File.separator}heads${File.separator}${branchName}").delete()
    new File(s"${pathSgit}${File.separator}logs${File.separator}refs${File.separator}heads${File.separator}${branchName}").delete()

  }

  def removeTag(tagName: String): Unit = {
    val pathSgit = PathManagement.getSgitPath().get
    new File(s"${pathSgit}${File.separator}refs${File.separator}tags${File.separator}${tagName}").delete()
  }

}