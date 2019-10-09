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
    val listFolders = List(".sgit", s".sgit${File.separator}objects${File.separator}tree", s".sgit${File.separator}objects${File.separator}blob", s".sgit${File.separator}objects${File.separator}commit", s".sgit${File.separator}config", s".sgit${File.separator}refs${File.separator}heads", s".sgit${File.separator}refs${File.separator}tags", s".sgit${File.separator}stages")
    val listFiles = List(s".sgit${File.separator}HEAD", s".sgit${File.separator}stages${File.separator}master")
    if(Files.notExists(Paths.get(".sgit"))){
      listFolders.map(folder => new File(folder).mkdirs())
      listFiles.map(file => new File(file).createNewFile())
      FileManagement.writeFile(s".sgit${File.separator}HEAD", s"ref: refs${File.separator}heads${File.separator}master")
      println("Sgit repo initialized in " + System.getProperty("user.dir").toString + s"${File.separator}.sgit")
    }else{
      println("Le sgit a déjà été initialisé pour ce répertoire")
    }
  }
}
