package genrs

import genrs.errors.JsonConversionException

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

  def patternDecode(ujv: ujson.Value): ResourceWrapper[_] = ujv match
    case ujson.Obj(value) =>
      val kvs = value.toMap.map((k, v) => (k, patternDecode(v)))
      ResourceWrapper(JsonObject(kvs))
    case ujson.Arr(value) =>
      ResourceWrapper(value.toList.map(patternDecode))
    case ujson.Num(value) =>
      if value.isValidInt
        then ResourceWrapper(value.toInt)
        else ResourceWrapper(value)
    case ujson.Str(value) =>
      ResourceWrapper(value)
    case ujson.False =>
      ResourceWrapper(false)
    case ujson.True =>
      ResourceWrapper(true)
    case _ =>
      throw JsonConversionException("JsonObject")

  given as Resource[JsonObject]:
    def isCompound(kvo: JsonObject) = true
    def rtype(kvo: JsonObject) = "kv"
    def textual(kvo: JsonObject) = "(kv)"
    def toJson(kvo: JsonObject) = kvo.toJson
    def fromJson(json: ujson.Value) =
      Try(patternDecode(json))
        .flatMap(_.extract[JsonObject])
        .orElse(Failure(JsonConversionException("JsonObject")))
