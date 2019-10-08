package action

import java.io.File
import java.nio.file.{Files, Paths}
import util.FileManagement

case class Init()

object Init {
  def init(): Unit = {
    initSgitRepo()
  }

  def initSgitRepo(): Unit ={
    val listFolders = List(".sgit", ".sgit/objects/tree", ".sgit/objects/blob", ".sgit/objects/commit", ".sgit/branches", ".sgit/config", ".sgit/refs/heads", ".sgit/refs/tags", ".sgit/stages")
    val listFiles = List(".sgit/HEAD", ".sgit/stages/master")
    if(Files.notExists(Paths.get(".sgit"))){
      listFolders.map(folder => new File(folder).mkdirs())
      listFiles.map(file => new File(file).createNewFile())
      FileManagement.writeFile(".sgit/HEAD", "ref: refs/heads/master")
      println("Sgit repo initialized in " + System.getProperty("user.dir").toString + "/.sgit")
    }else{
      println("Le sgit a déjà été initialisé pour ce répertoire")
    }
  }
}
