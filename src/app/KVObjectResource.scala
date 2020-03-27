package genrs

import genrs._
import genrs.Resource.ResourceOps

/**
 * Resource implementation of an untyped key-value object, like Javascript/JSON
 * objects. Keys must be strings, Values must be resources.
 */
case class KVObjectResource(kv: Map[String, ResourceWrapper[_]]) extends GenResource {
  def isCompound = true
  def rtype = "kv"
  def textual = "(kv)"
  def json = "{" + kv.map((k, v) => "\"" + k + "\":" + v.json).mkString(",") + "}"

  def apply[T: Resource](key: String) = kv(key).res.asInstanceOf[T]
}
