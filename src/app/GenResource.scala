package genrs

/**
 * Trait to allow for simple resource type definition without knowledge of typeclasses
 */
trait GenResource
  def isCompound: Boolean
  def rtype: String
  def textual: String
