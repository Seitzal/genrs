package genrs

import scala.util.{Try, Success, Failure}
import scala.reflect.ClassTag

/**
 * Typeclass for generic resource operations
 */
trait Resource[T: ClassTag]

  def isCompound(t: T): Boolean

  def rtype(t: T): String

  def textual(t: T): String

  def toJson(t: T): String

  def fromJson(json: ujson.Value): Try[T]

  def fromJsonUnsafe(json: ujson.Value): T =
    fromJson(json).get
  
  def fromJson(str: String): Try[T] =
    JsonDecoder.read(str).flatMap(fromJson)

  def fromJsonUnsafe(str: String): T =
    fromJsonUnsafe(JsonDecoder.readUnsafe(str))

object Resource

  def apply[T](using resource: Resource[T]) = resource

  extension on[T: Resource](t: T):
    def isCompound = Resource[T].isCompound(t)
    def rtype = Resource[T].rtype(t)
    def textual = Resource[T].textual(t)
    def toJson = Resource[T].toJson(t)

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
