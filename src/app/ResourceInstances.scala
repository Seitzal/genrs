package genrs

import genrs.errors._

import scala.util.{Try, Success, Failure}

/** Contains instances of the [[genrs.Resource]] typeclass for several types
 *  from the Scala Standard Library.
 */
trait ResourceInstances

  given as Resource[String]:
    def isCompound(str: String) = false
    def rtype(str: String) = "string"
    def textual(str: String) = str
    def toJson(str: String) = "\"" + str + "\""
    def fromJson(json: ujson.Value): Try[String] = json match
      case ujson.Str(str) => Success(str)
      case _ => Failure(JsonConversionException("String"))

  given as Resource[Int]:
    def isCompound(int: Int) = false
    def rtype(int: Int) = "int"
    def textual(int: Int) = int.toString
    def toJson(int: Int) = int.toString
    def fromJson(json: ujson.Value): Try[Int] = json match
      case ujson.Num(x) =>
        if x.isValidInt then Success(x.toInt)
        else Failure(InvalidIntException())
      case _ => Failure(JsonConversionException("Int"))

  given as Resource[Double]:
    def isCompound(d: Double) = false
    def rtype(d: Double) = "double"
    def textual(d: Double) = d.toString
    def toJson(d: Double) = d.toString
    def fromJson(json: ujson.Value): Try[Double] = json match
      case ujson.Num(x) => Success(x)
      case _ => Failure(JsonConversionException("Double"))

  given as Resource[Boolean]:
    def isCompound(b: Boolean) = false
    def rtype(b: Boolean) = "boolean"
    def textual(b: Boolean) = b.toString
    def toJson(b: Boolean) = b.toString
    def fromJson(json: ujson.Value): Try[Boolean] = json match
      case ujson.True => Success(true)
      case ujson.False => Success(false)
      case _ => Failure(JsonConversionException("Boolean"))

  given [T: Resource] as Resource[List[T]]:
    def isCompound(list: List[T]) = true
    def rtype(list: List[T]) = "list"
    def textual(list: List[T]) = "(list)"
    def toJson(list: List[T]) = "[" + list.map(_.toJson).mkString(",") + "]"
    def fromJson(json: ujson.Value): Try[List[T]] = json match
      case ujson.Arr(ts) =>
        Try(ts.toList.map(Resource[T].fromJson(_)).map(_.get))
      case _ => Failure(JsonConversionException("List"))
