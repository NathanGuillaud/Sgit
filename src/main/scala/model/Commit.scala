package model

import java.util.Date
import model.Tree

case class Commit(
                 val id: Int,
                 val date: Date,
                 val content: Tree
                 ) extends Object
