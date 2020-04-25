package genrs

import genrs._
import upickle.default._

package object test {

  case class Album(year: Int, title: String, remasters: List[Int])
  case class Artist(name: String, genres: List[String], albums: List[Album])

  implicit val albumRW = macroRW[Album]
  implicit val artistRW = macroRW[Artist]

  val opeth = Artist(
    "Opeth",
    List(
      "Progressive Rock",
      "Progressive Metal",
      "Death Metal"
    ),
    List(
      Album(1995, "Orchid", Nil),
      Album(1998, "My Arms, Your Hearse", Nil),
      Album(1999, "Still Life", Nil),
      Album(2001, "Blackwater Park", Nil),
      Album(2002, "Deliverance", List(2015)),
      Album(2003, "Damnation", List(2015)),
      Album(2005, "Ghost Reveries", Nil),
      Album(2008, "Watershed", Nil),
      Album(2011, "Heritage", Nil),
      Album(2014, "Pale Communion", Nil),
      Album(2016, "Sorceress", Nil),
      Album(2019, "In Cauda Venenum", Nil)
    )
  )

  val albumSchema = ObjectSchema(
    "year" -> SimpleSchema("Int"),
    "title" -> SimpleSchema("Str"),
    "remasters" -> SeqSchema("Int")
  )

  val artistSchema = ObjectSchema(
    "name" -> SimpleSchema("Str"),
    "albums" -> SeqSchema(albumSchema),
    "genres" -> SeqSchema("Str")
  )

}
