package action

import java.io.File
import java.nio.file.{Files, Paths}
import util.FileManagement

case class Init()

object Init {
  def init(): Unit = {
    println("INIT")
    val directory = System.getProperty("user.dir")
    val files = FileManagement.getListOfFilesAndDirectories(directory)
    println(files(0))
    println(files(1))
    initSgitRepo()
  }

  def initSgitRepo(): Unit ={
    val listFolders = List(".sgit", ".sgit/objects/tree", ".sgit/objects/blob", ".sgit/objects/commit", ".sgit/branches", ".sgit/config", ".sgit/refs/heads", ".sgit/refs/tags")
    val listFiles = List(".sgit/HEAD")
    if(Files.notExists(Paths.get(".sgit"))){
      listFolders.map(folder => new File(folder).mkdirs())
      listFiles.map(file => new File(file).createNewFile())
    }else{
      println("Le sgit a déjà été initialisé pour ce répertoire")
    }
  }
}
