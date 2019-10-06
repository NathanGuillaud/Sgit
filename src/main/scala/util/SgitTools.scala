package util

import scala.io.Source

case class SgitTools()

object SgitTools {

  def getCurrentBranch(): String = {
    val headFileContent = Source.fromFile(".sgit/HEAD").getLines.mkString("\n")
    val contentSplit = headFileContent.split("/")
    contentSplit.last
  }

}