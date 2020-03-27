package genrs

import genrs._
import genrs.Resource.ResourceOps

/**
 * Wrapper class to allow for wildcard resource type parameters
 */
case class ResourceWrapper[T: Resource](res: T) extends GenResource {
  def isCompound = res.isCompound
  def rtype = res.rtype
  def textual = res.textual
  def json = res.json
}
