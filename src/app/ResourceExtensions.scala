package genrs

/**
 *  Contains extension methods for all types implementing the [[genrs.Resource]]
 *  typeclass.
 */
trait ResourceExtensions
  extension on[T: Resource](t: T):
    def isCompound = Resource[T].isCompound(t)
    def rtype = Resource[T].rtype(t)
    def toJson = Resource[T].toJson(t)
