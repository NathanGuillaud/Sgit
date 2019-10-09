package action

import java.io.File

import util.{FileManagement, SgitTools}

case class Status()

object Status {
   def status(): Unit = {
     val currentBranch = SgitTools.getCurrentBranch()
     println("On the branch " + currentBranch)
     displayAddedFiles(currentBranch)
     displayUpdatedFiles(currentBranch)
     displayUntrackedFiles(currentBranch)
   }

  //Display files added since the last commit
  def displayAddedFiles(currentBranch: String): Unit = {
    val stagePath = s".sgit${File.separator}stages${File.separator}${currentBranch}"
    if(FileManagement.readFile(new File(stagePath)) != "") {
      //Retrieve useful data
      val stage = new File(stagePath)
      val files = FileManagement.readFile(stage)
      val stage_content = files.split("\n").map(x => x.split(" "))

      //Display each file in stage
      stage_content.map(path =>
        println(Console.GREEN + "\t" + path(0))
      )
    }
  }

  //Display the files updated since the last commit but not added
  def displayUpdatedFiles(currentBranch: String): Unit = {

  }

  //Display files never added
  def displayUntrackedFiles(currentBranch: String): Unit = {

  }

 }