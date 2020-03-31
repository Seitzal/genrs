package genrs

import scala.reflect.ClassTag
import scala.util.Try

/**
 * Typeclass for generic resource operations.
 */
trait Resource[T : ClassTag]

  /** Whether a resource consists of multiple other resources */
  def isCompound(t: T): Boolean

  /** String uniquely identifying a resource type.
   *  In the interest of maintaining uniqueness, non-base rtypes should be
   *  prefixed with their org and project, i.e. "eu.seitzal.fooproject.bartype".
   */
  def rtype(t: T): String

  /** Serialise a resource as a JSON string. */
  def toJson(t: T): String

  /** Try to derive a resource from a uJson AST */
  def fromJson(json: ujson.Value): Try[T]

  /** Derive a resource from a uJson AST */
  def fromJsonUnsafe(json: ujson.Value): T =
    fromJson(json).get

  /** Alias function to deserialize a uJson AST from a JSON string. */
  private def readJsonUnsafe(str: String) = 
    ujson.read(ujson.Readable.fromString(str))

  /** Deserialize a resource from a JSON string */
  def fromJsonUnsafe(str: String): T =
    fromJsonUnsafe(readJsonUnsafe(str))
  
  /** Try to deserialize a resource from a JSON string */
  def fromJson(str: String): Try[T] =
    Try(readJsonUnsafe(str)).flatMap(fromJson)

object Resource extends ResourceInstances with ResourceExtensions

  /** Summons the Resource instance for a type T from implicit scope. */
  def apply[T](using resource: Resource[T]) = resource
