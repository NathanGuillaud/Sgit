package util

import java.io.File
import java.nio.file.{Files, Paths}

object StageManagement {

  //Add a file to the stage
  def addFileInStage(path: String, hashId: String, currentBranch: String): Unit = {
    val pathToAdd = path.replaceAllLiterally("./", "")
    val pathBranchStage = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    if(Files.notExists(Paths.get(pathBranchStage))) {
      new File(pathBranchStage).createNewFile()
    } else {
      //Remove the last version of the file from the stage
      removeFileFromStage(pathToAdd, currentBranch)
    }
    //Add the new version of the file to the stage
    val stageContent = FileManagement.readFile(new File(pathBranchStage))
    FileManagement.writeFile(pathBranchStage, stageContent + pathToAdd + " " + hashId + " added\n")
  }

  //Remove a file from the stage if the file exists on the stage
  def removeFileFromStage(filePath: String, currentBranch: String): Unit = {
    val pathBranchStage = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    val files = FileManagement.readFile(new File(pathBranchStage))
    val stageContent = getStageContent(currentBranch)
    if(stageContent.length > 0 && files != "") {
      var newContent = ""
      stageContent.map(line =>
        if(line(0) != filePath) {
          newContent = newContent + line(0) + " " + line(1) + " " + line(2) + "\n"
        }
      )
      //Write new content in stage
      FileManagement.writeFile(pathBranchStage, newContent)
    }
  }

  //Archive a file from the stage after a commit (remove the *)
  def archiveFilesFromStage(currentBranch: String): Unit = {
    val pathBranchStage = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    val stageContent = getStageContent(currentBranch)
    var newContent = ""
    stageContent.map(line =>
      newContent = newContent + line(0) + " " + line(1) + " commited" + "\n"
    )
    //Write new content in stage
    FileManagement.writeFile(pathBranchStage, newContent)
  }

  //Return true if the stage contains at least 1 new file since the last commit, else return false
  def containsNewFiles(currentBranch: String): Boolean = {
    var containsNewFile = false
    val stageContent = getStageContent(currentBranch)
    stageContent.map(line =>
      if(line(2) == "added") {
        containsNewFile = true
      }
    )
    containsNewFile
  }

  //Get the content of the stage file for a given branch
  def getStageContent(currentBranch: String): Array[Array[String]] = {
    var stageContent = Array[Array[String]]()
    val pathBranchStage = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    val stage = new File(pathBranchStage)
    val files = FileManagement.readFile(stage)
    if(files != "") {
      stageContent = files.split("\n").map(x => x.split(" "))
    }
    stageContent
  }

  def clearStage(currentBranch: String): Unit = {
    FileManagement.writeFile(s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}", "")
  }

  def getAddedFiles(currentBranch: String): Array[String] = {
    var addedFiles = Array[String]()
    val stageContent = getStageContent(currentBranch)
    stageContent.map(line =>
      if(line(2) == "added") {
        addedFiles = addedFiles :+ line(0)
      }
    )
    addedFiles
  }

}
