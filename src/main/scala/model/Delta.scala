package model

case class Delta(
                  var line: Int,
                  var action: String,
                  var content: String
                )
