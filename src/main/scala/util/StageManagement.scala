package util

import java.io.File
import java.nio.file.{Files, Paths}

object StageManagement {

  /**
   * Add a file to the stage if the file has been modified since the last adding
   * @param path : path of the file to add in the stage
   * @param hashId : hash of the file to add in the stage
   * @param currentBranch : current branch
   */
  def addFileInStage(path: String, hashId: String, currentBranch: String): Unit = {
    val pathToAdd = path.replaceAllLiterally("./", "")
    val pathBranchStage = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    if(Files.notExists(Paths.get(pathBranchStage))) {
      FileManagement.writeFile(pathBranchStage, pathToAdd + " " + hashId + " added new\n")
    } else if (fileHasChange(pathToAdd, hashId, currentBranch)) {
      val fileState = if(fileIsInStage(pathToAdd, currentBranch)) "modified" else "new"
      //If the file is in the stage
      if(fileIsInStage(pathToAdd, currentBranch)) {
        //Remove the last version of the file from the stage
        removeFileFromStage(pathToAdd, currentBranch)
      }
      //Add the new version of the file to the stage
      val stageContent = FileManagement.readFile(new File(pathBranchStage))
      FileManagement.writeFile(pathBranchStage, stageContent + pathToAdd + " " + hashId + " added " + fileState + "\n")
    }
  }

  /**
   * Remove a file from the stage if the file exists on the stage
   * @param filePath : path of the file to remove from the stage
   * @param currentBranch : current branch
   */
  def removeFileFromStage(filePath: String, currentBranch: String): Unit = {
    val pathBranchStage = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    val files = FileManagement.readFile(new File(pathBranchStage))
    val stageContent = getStageContent(currentBranch)
    if(stageContent.length > 0 && files != "") {
      val newContent = stageContent.filter(line => line(0) != filePath).map(line => line(0) + " " + line(1) + " " + line(2) + " " + line(3) + "\n").mkString("")
      //Write new content in stage
      FileManagement.writeFile(pathBranchStage, newContent)
    }
  }

  /**
   * To knox if a file is in the stage
   * @param filePath : path of the file to search in the stage
   * @param currentBranch : current branch
   * @return true if the file in parameters is in the stage (already add or commit), else return false
   */
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

  /**
   * Archive a file from the stage after a commit (change the state from "added" to "commited")
   * @param currentBranch : current branch
   */
  def archiveFilesFromStage(currentBranch: String): Unit = {
    val pathBranchStage = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    val stageContent = getStageContent(currentBranch)
    val newContent = stageContent.map(line => line(0) + " " + line(1) + " commited -\n").mkString("")
    //Write new content in stage
    FileManagement.writeFile(pathBranchStage, newContent)
  }

  /**
   * To know if the stage contains new files (not commited)
   * @param currentBranch : current branch
   * @return true if the stage contains at least 1 new file since the last commit, else return false
   */
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

  /**
   * Get the content of the stage file for a given branch
   * @param currentBranch : current branch
   * @return All the content of the stage split by line.
   *         Each line is an array of 4 String :
   *         1 : the path of the file from the root of the project
   *         2 : the hash of the file
   *         3 : the state ("added" or "commited")
   *         4 : if the file is "new", "modified", or "-" (for sgit status)
   */
  def getStageContent(currentBranch: String): Array[Array[String]] = {
    val pathBranchStage = s"${PathManagement.getSgitPath().get}${File.separator}stages${File.separator}${currentBranch}"
    val stage = new File(pathBranchStage)
    val files = FileManagement.readFile(stage)
    if(files != "") files.split("\n").map(x => x.split(" "))
    else Array[Array[String]]()
  }

  /**
   * Get all files added since the last commit in the stage
   * @param currentBranch : current branch
   * @return a list of tuples (files) with the state (new or modified), the path and the hash
   */
  def getAddedFiles(currentBranch: String): List[(String, String, String)] = {
    val stageContent = getStageContent(currentBranch)
    stageContent.filter(line => line(2) == "added").map(line => (line(3), line(0), line(1))).toList
  }

  /**
   * To know if a file has been modified
   * @param path : path of the file concerned
   * @param hashId : new hash of the file
   * @param currentBranch : current branch
   * @return true if the file give in parameter has changed since the last adding, else return false
   */
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
