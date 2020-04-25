package genrs

import upickle.default._

final case class Group(id: Int, name: String)

object Group {
  implicit val rw: ReadWriter[Group] = macroRW
}
