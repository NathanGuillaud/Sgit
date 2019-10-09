package util

import java.io.File
import java.nio.file.{Files, Paths}

object StageManagement {

  //Add a file to the stage
  def addFileInStage(path: File, hashId: String, currentBranch: String): Unit = {
    val pathBranchStage = s".sgit${File.separator}stages${File.separator}${currentBranch}"
    if(Files.notExists(Paths.get(pathBranchStage))) {
      new File(pathBranchStage).createNewFile()
    } else {
      //Remove the last version of the file from the stage
      removeFileFromStage(path.toString, currentBranch)
    }
    //Add the new version of the file to the stage
    val stageContent = FileManagement.readFile(new File(s".sgit${File.separator}stages${File.separator}" + currentBranch))
    FileManagement.writeFile(pathBranchStage, stageContent + path.toString + " " + hashId + "\n")
  }

  //Remove a file from the stage if the file exists on the stage
  def removeFileFromStage(filePath: String, currentBranch: String): Unit = {
    val pathBranchStage = s".sgit${File.separator}stages${File.separator}${currentBranch}"
    val stage = new File(pathBranchStage)
    val files = FileManagement.readFile(stage)
    val stageContent = files.split("\n").map(x => x.split(" "))
    if(stageContent.length > 0 && files != "") {
      var newContent = ""
      stageContent.map(line =>
        if(line(0) != filePath) {
          newContent = newContent + line(0) + " " + line(1) + "\n"
        }
      )
      //Write new content in stage
      FileManagement.writeFile(pathBranchStage, newContent)
    }
  }

}
