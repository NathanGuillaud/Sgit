package action

import java.io.File

import util.{FileManagement, PathManagement, SgitTools, StageManagement}

object Status {
   def status(): Unit = {
     if(PathManagement.getSgitPath().isEmpty){
       println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
     } else {
       if(PathManagement.getProjectPath().isEmpty) {
         println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
       }
       //Display the current branch
       val currentBranch = SgitTools.getCurrentBranch()
       println("On the branch " + currentBranch)

       if (StageManagement.getAddedFiles(currentBranch).isEmpty && getUpdatedFiles(currentBranch).isEmpty && getUntrackedFiles(currentBranch).isEmpty){
         println("Nothing to commit, working directory clean")
       } else {
         displayAddedFiles(currentBranch)
         displayUpdatedFiles(currentBranch)
         displayUntrackedFiles(currentBranch)
       }
     }
   }

  //Display files added since the last commit
  def displayAddedFiles(currentBranch: String): Unit = {
    //Retrieve useful data
    val addedFiles = StageManagement.getAddedFiles(currentBranch)
    if(!addedFiles.isEmpty) {
      println("Changes to be commited:")
      addedFiles.map(file =>
        println(Console.GREEN + "\t" + file._1 + ":\t" + file._2 + Console.WHITE)
      )
    }
  }

  //Display the files updated since the last commit but not added
  def displayUpdatedFiles(currentBranch: String): Unit = {
    //Retrieve files from stage
    val updatedFiles = getUpdatedFiles(currentBranch)
    if(!updatedFiles.isEmpty) {
      println("Changes not staged for commit:")
      println(("use \"sgit add <file>...\" to update what will be committed"))
      updatedFiles.map(file =>
        println(Console.RED + "\t" + file + Console.WHITE)
      )
    }
  }

  //Display files never added
  def displayUntrackedFiles(currentBranch: String): Unit = {
    val untrackedFiles = getUntrackedFiles(currentBranch)
    if(!untrackedFiles.isEmpty) {
      println("Untracked files:")
      println("(use \"git add <file>...\" to include in what will be committed)")
      untrackedFiles.map(file =>
        println(Console.RED + "\t" + file + Console.WHITE)
      )
    }
  }

  //Return an array with all updated files in the stage since the last add
  def getUpdatedFiles(currentBranch: String): Array[String] = {
    var updatedFiles = Array[String]()
    val filesInStage = StageManagement.getStageContent(currentBranch)
    filesInStage.map(line =>
      if(fileIsUpdated(PathManagement.getParentPath(PathManagement.getSgitPath().get).get + File.separator + line(0), line(1))) {
        updatedFiles = updatedFiles :+ line(0)
      }
    )
    updatedFiles
  }

  //Return true if the file has been updated since the last add (with the hash)
  def fileIsUpdated(filePath: String, fileHash: String): Boolean = {
    val fileName = filePath.split(File.separator).last
    val fileContent = FileManagement.readFile(new File(filePath))
    val newFileHash = FileManagement.hashFile(fileName, fileContent)
    newFileHash != fileHash
  }

  //Return an array with all untracked files (not in the stage)
  def getUntrackedFiles(currentBranch: String): List[String] = {
    //Retrieve files from project
    var allFiles = FileManagement.getFilesFromDirectory(new File(PathManagement.getProjectPath().get))
    allFiles = allFiles.filter(file => !file.toString.contains(".sgit"))
    var allFilesPath = List[String]()
    allFiles.map(file =>
      allFilesPath = PathManagement.getFilePathFromProjectRoot(file.toString).get :: allFilesPath
    )

    //Retrieve files from stage
    var stageFiles = List[String]()
    StageManagement.getStageContent(currentBranch).map(file =>
      stageFiles = file(0).toString :: stageFiles
    )

    //Remove files in stage from the list with all the files
    val filesToRemove = stageFiles.toSet
    allFilesPath.filterNot(filesToRemove)
  }

 }