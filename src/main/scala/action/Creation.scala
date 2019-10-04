package action

import java.io.File
import java.nio.file.{Files, Paths}

case class Creation()

object Creation {
  def init(): Unit = {
    println("INIT")
    val directory = System.getProperty("user.dir")
    val files = getListOfFiles(directory)
    println(files(0))
    println(files(1))
    initSgitRepo()
  }

  def initSgitRepo(): Unit ={
    val listFolders = List(".sgit", ".sgit/objects", ".sgit/branches", ".sgit/config", ".sgit/refs/heads", ".sgit/refs/tags")
    val listFiles = List(".sgit/HEAD")
    if(Files.notExists(Paths.get(".sgit"))){
      listFolders.map(folder => new File(folder).mkdirs())
      listFiles.map(file => new File(file).createNewFile())
    }else{
      println("Le sgit est déjà initialisé")
    }


  }

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }
}
