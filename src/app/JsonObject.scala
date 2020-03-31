package genrs

import scala.util.{Try, Success, Failure}
import scala.reflect.ClassTag

/**
 * A key-value object, as found in Json.
 * Keys must be strings, Values must be wrapped resources.
 */
case class JsonObject(kv: Map[String, ResourceWrapper[_]])
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

object JsonObject
  given as Resource[JsonObject]:
    def isCompound(kvo: JsonObject) = true
    def rtype(kvo: JsonObject) = "kv"
    def textual(kvo: JsonObject) = "(kv)"
    def toJson(kvo: JsonObject) = kvo.toJson
    def fromJson(json: ujson.Value) =
      JsonDecoder.decode(json).flatMap(_.extract[JsonObject])
