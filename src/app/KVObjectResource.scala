package genrs

import genrs._
import genrs.Resource.given

import scala.util.{Try, Success, Failure}
import scala.reflect.ClassTag

/**
 * Resource implementation of an untyped key-value object, like Javascript/JSON
 * objects. Keys must be strings, Values must be wrapped resources.
 */
case class KVObjectResource(kv: Map[String, ResourceWrapper[_]]) extends GenResource
  def isCompound = true
  def rtype = "kv"
  def textual = "(kv)"
  def json = "{" + kv.map((k, v) => "\"" + k + "\":" + v.json).mkString(",") + "}"

  def apply(key: String) = kv(key)

  def get[R: Resource: ClassTag](key: String): Try[R] =
    Try(kv(key)).flatMap(_.extract[R])

  def getUnsafe[R: Resource: ClassTag](key: String) =
    kv(key).extractUnsafe[R]
