package action

import java.io.File

import org.scalatest.FunSuite

import scala.io.Source

class TestInit extends FunSuite {
  test("sgit init should create .sgit directory with directories and files") {
    Init.init()
    assert(new File(".sgit").exists())
    assert(new File(".sgit/objects/tree").exists())
    assert(new File(".sgit/objects/blob").exists())
    assert(new File(".sgit/objects/commit").exists())
    assert(new File(".sgit/HEAD").exists())
    assert(new File(".sgit/refs/tags").exists())
    assert(new File(".sgit/refs/tags").exists())
    assert(new File(".sgit/config").exists())
    assert(new File(".sgit/branches").exists())
  }
  test("sgit HEAD should be positionned at master") {
    Init.init()
    assert(Source.fromFile(".sgit/HEAD").getLines.mkString == "ref: refs/heads/master")
  }
}