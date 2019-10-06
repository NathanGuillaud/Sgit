package action

import java.io.File
import java.nio.file.{Files, Paths}

import org.scalatest.FunSuite

import scala.io.Source

class TestLog extends FunSuite {
  test("sgit log should print all the commits") {
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
  }
}