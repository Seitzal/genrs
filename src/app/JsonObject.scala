package genrs

import genrs.errors._

import scala.util.{Try, Success, Failure, Either, Right, Left}
import scala.reflect.ClassTag

/**
 * A key-value object, as found in Json.
 * Keys must be strings, Values must be wrapped resources.
 */
case class JsonObject(kvs: Map[String, ResourceWrapper[_]])

  def toJson = 
    "{" +
    kvs.map((k, v) =>
      "\"" + k + "\":" + v.toJson
    ).mkString(",")
    + "}"

  def apply(key: String) = kvs(key)

  def has(name: String) = kvs.isDefinedAt(name)

  def matches(schema: Schema) = schema allows this

  def get[R: Resource: ClassTag](key: String): Try[R] =
    Try(kvs(key)).flatMap(_.extract[R])

  def getUnsafe[R: Resource: ClassTag](key: String) =
    kvs(key).extractUnsafe[R]

  case class BadQueryException(query: String) extends RuntimeException(
    s"Bad query: $query")

  def parseSegment(segment: String): Either[(String, Int), String] =
    """^(.*)\[([0-9]+)\]$""".r.findFirstMatchIn(segment) match
      case Some(expr) => Left((expr.group(1), expr.group(2).toInt))
      case None => Right(segment)

  def querySegment(segment: String): Any =
    parseSegment(segment) match
      case Left((key, index)) =>
        kvs(key).res match
          case l: List[_] => l(index)
          case _ => throw new NoSuchElementException
      case Right(key) =>
        kvs(key)

  def queryUnsafe(chain: List[String]): Any =
    chain match
      case Nil => throw BadQueryException(chain.mkString("."))
      case head :: Nil => querySegment(head)
      case head :: tail =>
        val result = querySegment(head)
        if (result.isInstanceOf[JsonObject])
          result
            .asInstanceOf[JsonObject]
            .queryUnsafe(tail)
        else if (result.isInstanceOf[ResourceWrapper[_]])
          result
            .asInstanceOf[ResourceWrapper[_]]
            .extractUnsafe[JsonObject]
            .queryUnsafe(tail)
        else throw ResourceTypeException()

  // Caveat: Due to type erasure, this currently returns a Success when queried 
  //         for a list if any list is found, regardless of whether that list 
  //         has the correct type - a ClassCastException only occurs when trying 
  //         to coerce the list at the final reference point, since type 
  //         coercion seems to happen lazily in Scala.
  //         To resolve that issue (and a number of others in the library), we
  //         would need type tags, which currently don't seem to work in Dotty.
  def query[T : Resource : ClassTag](queryString: String): Try[T] =
    Try(queryUnsafe(queryString.split('.').toList))
      .flatMap {
        case t: T => Success(t)
        case rw: ResourceWrapper[_] => rw.extract[T]
        case _ => Failure(ResourceTypeException())
      }

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
    def rtype(kvo: JsonObject) = "object"
    def toJson(kvo: JsonObject) = kvo.toJson
    def fromJson(json: ujson.Value) =
      Try(patternDecode(json))
        .flatMap(_.extract[JsonObject])
        .orElse(Failure(JsonConversionException("JsonObject")))

  object dsl

    def jsonObject(kvs: (String, ResourceWrapper[_])*) =
      JsonObject(kvs.toMap)

    def v[R: Resource](v: R) =
      ResourceWrapper(v)

    def l[R: Resource](vs: R*) =
      ResourceWrapper(vs.toList)
    
    def o(kvs: (String, ResourceWrapper[_])*) =
      ResourceWrapper(JsonObject(kvs.toMap))
