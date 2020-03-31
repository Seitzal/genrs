package genrs

import scala.reflect.ClassTag
import scala.util.Try

/**
 * Typeclass for generic resource operations.
 */
trait Resource[T : ClassTag]

  def isCompound(t: T): Boolean

  def rtype(t: T): String

  def textual(t: T): String

  def toJson(t: T): String

  def fromJson(json: ujson.Value): Try[T]

  def fromJsonUnsafe(json: ujson.Value): T =
    fromJson(json).get

  private def readJsonUnsafe(str: String) = 
    ujson.read(ujson.Readable.fromString(str))

  def fromJsonUnsafe(str: String): T =
    fromJsonUnsafe(readJsonUnsafe(str))
  
  def fromJson(str: String): Try[T] =
    Try(readJsonUnsafe(str)).flatMap(fromJson)

object Resource extends ResourceInstances with ResourceExtensions

  /** Summons the Resource instance for a type T from implicit scope. */
  def apply[T](using resource: Resource[T]) = resource
