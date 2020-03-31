package genrs

import scala.util.Try

/** Contains instances of the [[genrs.Resource]] typeclass for several types
 *  from the Scala Standard Library.
 */
trait ResourceInstances

  given as Resource[String]:
    def isCompound(str: String) = false
    def rtype(str: String) = "string"
    def textual(str: String) = str
    def toJson(str: String) = "\"" + str + "\""
    def fromJson(json: ujson.Value): Try[String] =
      JsonDecoder.decode(json).flatMap(_.extract[String])

  given as Resource[Int]:
    def isCompound(int: Int) = false
    def rtype(int: Int) = "int"
    def textual(int: Int) = int.toString
    def toJson(int: Int) = int.toString
    def fromJson(json: ujson.Value): Try[Int] =
      JsonDecoder.decode(json).flatMap(_.extract[Double]).map(_.toInt)

  given as Resource[Double]:
    def isCompound(d: Double) = false
    def rtype(d: Double) = "double"
    def textual(d: Double) = d.toString
    def toJson(d: Double) = d.toString
    def fromJson(json: ujson.Value): Try[Double] =
      JsonDecoder.decode(json).flatMap(_.extract[Double])

  given as Resource[Boolean]:
    def isCompound(b: Boolean) = false
    def rtype(b: Boolean) = "boolean"
    def textual(b: Boolean) = b.toString
    def toJson(b: Boolean) = b.toString
    def fromJson(json: ujson.Value): Try[Boolean] =
      JsonDecoder.decode(json).flatMap(_.extract[Boolean])

  given [T: Resource] as Resource[List[T]]:
    def isCompound(list: List[T]) = true
    def rtype(list: List[T]) = "list"
    def textual(list: List[T]) = "(list)"
    def toJson(list: List[T]) = "[" + list.map(_.toJson).mkString(",") + "]"
    def fromJson(json: ujson.Value): Try[List[T]] =
      JsonDecoder.decode(json).flatMap(_.extract[List[T]])
