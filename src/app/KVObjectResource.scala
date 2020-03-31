package genrs

import genrs._
import genrs.Resource.given

import scala.util.{Try, Success, Failure}
import scala.reflect.ClassTag

/**
 * Resource implementation of an untyped key-value object, like Javascript/JSON
 * objects. Keys must be strings, Values must be wrapped resources.
 */
case class KVObjectResource(kv: Map[String, ResourceWrapper[_]])
  def isCompound = true
  def rtype = "kv"
  def textual = "(kv)"
  def toJson = 
    "{" +
    kv.map((k, v) =>
      "\"" + k + "\":" + v.toJson
    ).mkString(",")
    + "}"

  def apply(key: String) = kv(key)

  def get[R: Resource: ClassTag](key: String): Try[R] =
    Try(kv(key)).flatMap(_.extract[R])

  def getUnsafe[R: Resource: ClassTag](key: String) =
    kv(key).extractUnsafe[R]

object KVObjectResource
  given as Resource[KVObjectResource]:
    def isCompound(kvo: KVObjectResource) = true
    def rtype(kvo: KVObjectResource) = "kv"
    def textual(kvo: KVObjectResource) = "(kv)"
    def toJson(kvo: KVObjectResource) = kvo.toJson
    def fromJson(json: ujson.Value) =
      JsonDecoder.decode(json).flatMap(_.extract[KVObjectResource])
