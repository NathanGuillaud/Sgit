package action

import java.io.File

import org.scalatest.FunSuite

import scala.io.Source

class TestInit extends FunSuite {
  test("sgit init should create .sgit directory with directories and files") {
    Init.init()
    assert(new File(".sgit").exists())
    assert(new File(s".sgit${File.separator}objects${File.separator}tree").exists())
    assert(new File(s".sgit${File.separator}objects${File.separator}blob").exists())
    assert(new File(s".sgit${File.separator}objects${File.separator}commit").exists())
    assert(new File(s".sgit${File.separator}HEAD").exists())
    assert(new File(s".sgit${File.separator}refs${File.separator}tags").exists())
    assert(new File(s".sgit${File.separator}refs${File.separator}tags").exists())
    assert(new File(s".sgit${File.separator}config").exists())
    assert(new File(s".sgit${File.separator}stages${File.separator}master").exists())
  }
  test("sgit HEAD should be positionned at master") {
    Init.init()
    assert(Source.fromFile(s".sgit${File.separator}HEAD").getLines.mkString == s"ref: refs${File.separator}heads${File.separator}master")
  }
}