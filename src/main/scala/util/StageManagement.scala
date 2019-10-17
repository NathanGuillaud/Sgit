package util

import java.io.File
import java.nio.file.{Files, Paths}

object StageManagement {

  //Add a file to the stage if the file has been modified since the last adding
  def addFileInStage(path: String, hashId: String, currentBranch: String): Unit = {
    val pathToAdd = path.replaceAllLiterally("./", "")
    val pathBranchStage = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    if(Files.notExists(Paths.get(pathBranchStage))) {
      FileManagement.writeFile(pathBranchStage, pathToAdd + " " + hashId + " added new\n")
    } else if (fileHasChange(path, hashId, currentBranch)) {
      var fileState = "new"
      //If the file is in the stage
      if(fileIsInStage(path, currentBranch)) {
        fileState = "modified"
        //Remove the last version of the file from the stage
        removeFileFromStage(pathToAdd, currentBranch)
      }
      //Add the new version of the file to the stage
      val stageContent = FileManagement.readFile(new File(pathBranchStage))
      FileManagement.writeFile(pathBranchStage, stageContent + pathToAdd + " " + hashId + " added " + fileState + "\n")
    }
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
          newContent = newContent + line(0) + " " + line(1) + " " + line(2) + " " + line(3) + "\n"
        }
      )
      //Write new content in stage
      FileManagement.writeFile(pathBranchStage, newContent)
    }
  }

  //Return true if the file in parameters is in the stage (already add or commit), else return false
  def fileIsInStage(filePath: String, currentBranch: String): Boolean = {
    var fileInStage = false
    val pathBranchStage = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    val files = FileManagement.readFile(new File(pathBranchStage))
    val stageContent = getStageContent(currentBranch)
    if(stageContent.length > 0 && files != "") {
      stageContent.map(line =>
        if(line(0) == filePath) {
          fileInStage = true
        }
      )
    }
    fileInStage
  }

  //Archive a file from the stage after a commit (remove the *)
  def archiveFilesFromStage(currentBranch: String): Unit = {
    val pathBranchStage = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    val stageContent = getStageContent(currentBranch)
    var newContent = ""
    stageContent.map(line =>
      newContent = newContent + line(0) + " " + line(1) + " commited -\n"
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

  //Delete all the content of the stage
  def clearStage(currentBranch: String): Unit = {
    FileManagement.writeFile(s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}", "")
  }

  //Get all files added since the last commit in the stage
  //Return a list of tuples with the stage (new or modified), the path and the hash
  def getAddedFiles(currentBranch: String): List[(String, String, String)] = {
    var addedFiles = List[(String, String, String)]()
    val stageContent = getStageContent(currentBranch)
    stageContent.map(line =>
      if(line(2) == "added") {
        addedFiles = (line(3), line(0), line(1)) :: addedFiles
      }
    )
    addedFiles
  }

  //Return true if the file give in parameter has changed since the last adding, else return false
  def fileHasChange(path: String, hashId: String, currentBranch: String): Boolean = {
    var hasChange = true
    getStageContent(currentBranch).map(file =>
      //Case of the searched file
      if(file(0) == path) {
        //If the file has not the same hash value, it's a change on the file
        if(file(1) == hashId) {
          hasChange = false
        }
      }
    )
    hasChange
  }

}
