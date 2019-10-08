package model

case class Element(
                  var path: String = "",
                  var hash: String = "",
                  var elemType: String = ""
                  ) {
  def get_path(): String = {
    this.path
  }

  def set_path(path: String): Unit = {
    this.path = path
  }

  def get_hash(): String = {
    this.hash
  }

  def set_hash(hash: String): Unit = {
    this.hash = hash
  }

  def get_elem_type(): String = {
    this.elemType
  }

  def set_elem_type(elemType: String): Unit = {
    this.elemType = elemType
  }
}
