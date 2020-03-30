package genrs

import genrs._
import genrs.Resource.given

/**
 * Wrapper class to allow for wildcard resource type parameters
 */
case class ResourceWrapper[T: Resource](res: T) extends GenResource

  def isCompound = res.isCompound
  def rtype = res.rtype
  def textual = res.textual
  def json = res.json

  // EXPERIMENTAL: Equality operator should ignore wrapping
  override def equals(other: Any) = other match
    case rw: ResourceWrapper[_] => this.res == rw.res
    case _ => res == other
