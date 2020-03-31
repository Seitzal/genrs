package genrs

import genrs._
import genrs.Resource.given

import scala.util.{Try, Success, Failure}
import scala.reflect.ClassTag

/**
 * Wrapper class to allow for wildcard resource type parameters
 */
final case class ResourceWrapper[T: Resource](res: T) extends GenResource

  def isCompound = res.isCompound
  def rtype = res.rtype
  def textual = res.textual
  def json = res.json

  // Equality operator should ignore wrapping
  override def equals(other: Any) = other match
    case rw: ResourceWrapper[_] => 
      this.res == rw.res // hidden recursive call
    case _ => 
      res == other // Base case: Default to reference implementation

  /** Returns the resource contained within the wrapper.
   *  Recursively unwraps nested wrappers, and will never return a wrapper.
   *  @tparam R The type of resource to be extracted.
   *  @throws ResourceTypeException
   */
  def extractUnsafe[R: Resource: ClassTag]: R = res match
    case rw: ResourceWrapper[_] => rw.extractUnsafe[R]
    case r: R => r
    case anything => throw ResourceTypeException()

  /** Tries to return the resource contained within the wrapper.
   *  Recursively unwraps nested wrappers, and will never return a wrapper.
   *  @tparam R The type of resource to be extracted.
   */
  def extract[R: Resource: ClassTag]: Try[R] = Try(extractUnsafe[R])

  /** Optionally returns the resource contained within the wrapper. 
   *  Recursively unwraps nested wrappers, and will never return a wrapper.
   *  @tparam R The type of resource to be extracted.
   */
  def extractOpt[R: Resource: ClassTag]: Option[R] = extract[R].toOption
