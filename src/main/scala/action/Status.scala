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

  //Display the files updated since the last commit but not added
  def displayUpdatedFiles(currentBranch: String): Unit = {
    //Retrieve files from stage
    val updatedFiles = getUpdatedFiles(currentBranch)
    if(!updatedFiles.isEmpty) {
      println("Changes not staged for commit:")
      //Display added files in stage
      updatedFiles.map(file =>
        println(Console.RED + "\t" + file)
      )
    }
  }

  //Display files never added
  def displayUntrackedFiles(currentBranch: String): Unit = {

  }

  //Return an array with all updated files in the stage since the last add
  def getUpdatedFiles(currentBranch: String): Array[String] = {
    var updatedFiles = Array[String]()
    val filesInStage = StageManagement.getStageContent(currentBranch)
    filesInStage.map(line =>
      if(fileIsUpdated(PathManagement.getParentPath(PathManagement.getSgitPath().get).get + line(0), line(1))) {
        updatedFiles = updatedFiles :+ line(0)
      }
    )
    updatedFiles
  }

  //Return true if the file has been updated since the last add (with the hash)
  def fileIsUpdated(filePath: String, fileHash: String): Boolean = {
    true
  }

 }