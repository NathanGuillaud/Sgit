package action

import java.io.File

import util.{FileManagement, PathManagement, SgitTools, StageManagement}

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
    val stagePath = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    if(FileManagement.readFile(new File(stagePath)) != "") {
      //Retrieve useful data
      val addedFiles = StageManagement.getAddedFiles(currentBranch)
      if(!addedFiles.isEmpty) {
        println("Changes staged for commit:")
        //Display added files in stage
        addedFiles.map(file =>
          println(Console.GREEN + "\t" + file)
        )
      }
    }
  }

  //Display the files updated since the last commit but not added
  def displayUpdatedFiles(currentBranch: String): Unit = {

  }

  //Display files never added
  def displayUntrackedFiles(currentBranch: String): Unit = {

  }

 }