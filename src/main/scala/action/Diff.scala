package action

import model.Delta
import util.PathManagement

object Diff {

  def diff(): Unit = {
    if(PathManagement.getSgitPath().isEmpty){
      println("fatal: Not a sgit repository (or any of the parent directories): .sgit")
    } else {
      println("DIFF")
      val deltas = getDeltasBetweenString(List("e","b","d"), List("a","b","d","e"))
      println("----------")
      println("Voici les deltas :")
      deltas.map(x => println(x.line + " " + x.action + " " + x.content))
    }
  }

  def getDeltasBetweenString(oldList: List[String], newList: List[String]): List[Delta] = {
    //Create an empty matrix
    var matrix = initializeMatrix(oldList, newList)
    println("Matrice avec 0 :")
    println("Pour 0,0 : " + matrix(0)(0))
    println("Pour le dernier elem : " + matrix(oldList.length)(newList.length))
    //Fill the matrix with deltas between the 2 lists in parameters
    matrix = fillMatrix(oldList, newList, 1, 1, matrix)
    //Retrieve the deltas from the matrix
    getDeltasFromMatrix(oldList, newList, oldList.length, newList.length, matrix, List[Delta]())
  }

  //Create an empty matrix with a size of oldList+1 X newList+1
  def initializeMatrix(oldList: List[String], newList: List[String]): Array[Array[Int]] = {
    Array.fill(oldList.length+1, newList.length+1)(0)
  }

  //Fill the matrix in parameters with deltas between the 2 lists in parameters
  def fillMatrix(oldList: List[String], newList: List[String], i: Int, j: Int, matrix: Array[Array[Int]]): Array[Array[Int]] = {
    //If the matrix is fill
    if(i == oldList.length+1 && j == 1) matrix
    else {
      //If values of the old list and the new list are the same
      if(oldList(i-1) == newList(j-1)) matrix(i)(j) = matrix(i-1)(j-1)+1
      else matrix(i)(j) = Math.max(matrix(i)(j-1), matrix(i-1)(j))
      println(i + "-" + j + " : " + matrix(i)(j))
      //If it is the end of the line
      if(j == newList.length) fillMatrix(oldList, newList, i+1, 1, matrix)
      else fillMatrix(oldList, newList, i, j+1, matrix)
    }

  }

  def getDeltasFromMatrix(oldList: List[String], newList: List[String], i: Int, j: Int, matrix: Array[Array[Int]], deltas: List[Delta]): List[Delta] = {
    //If we go up the 2 lists
    if(i == 0 && j == 0) deltas
    else {
      println(i + "-" + j)
      //If the new list contains a new element
      if(i == 0 || (j > 0 && matrix(i)(j-1) == matrix(i)(j))) getDeltasFromMatrix(oldList, newList, i, j-1, matrix, new Delta(j, "+", newList(j-1)) :: deltas)
      //If the old list contains an element removed
      else if(j == 0 || (i > 0 && matrix(i-1)(j) == matrix(i)(j))) getDeltasFromMatrix(oldList, newList, i-1, j, matrix, new Delta(j, "-", oldList(i-1)) :: deltas)
      else getDeltasFromMatrix(oldList, newList, i-1, j-1, matrix, deltas)
    }
  }

}