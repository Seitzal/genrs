package genrs

import scala.util.{Try, Success, Failure}
import scala.reflect.ClassTag

/**
 * Wrapper class to allow for easy abstraction over arbitrary resource types.
 * Should be used with caution, as it carries the performance cost of boxing,
 * and also moves type checking from compile time to runtime.
 */
final case class ResourceWrapper[T : Resource](res: T)

  def isCompound = res.isCompound
  def rtype = res.rtype
  def toJson = res.toJson

  /** Returns the resource contained within the wrapper.
   *  Recursively unwraps nested wrappers, and will never return a wrapper.
   *  @tparam R The type of resource to be extracted.
   *  @throws ResourceTypeException
   */
  def extractUnsafe[R : Resource : ClassTag]: R = res match
    case rw: ResourceWrapper[_] => rw.extractUnsafe[R]
    case r: R => r
    case anything => throw errors.ResourceTypeException()

  /** Tries to return the resource contained within the wrapper.
   *  Recursively unwraps nested wrappers, and will never return a wrapper.
   *  @tparam R The type of resource to be extracted.
   */
  def extract[R : Resource : ClassTag]: Try[R] = Try(extractUnsafe[R])

  /** Optionally returns the resource contained within the wrapper. 
   *  Recursively unwraps nested wrappers, and will never return a wrapper.
   *  @tparam R The type of resource to be extracted.
   */
  def extractOpt[R : Resource : ClassTag]: Option[R] = extract[R].toOption

object ResourceWrapper
  given as Resource[ResourceWrapper[_]]:
    def isCompound(rw: ResourceWrapper[_]) = rw.isCompound
    def rtype(rw: ResourceWrapper[_]) = rw.rtype
    def toJson(rw: ResourceWrapper[_]) = rw.toJson
    def fromJson(json: ujson.Value) =
      Failure(Error(
        "Cannot directly read resource wrappers from Json due to the lack of " +
        "existential types in Scala 3. Try reading the resource directly and " +
        "then wrapping it instead."
      ))
