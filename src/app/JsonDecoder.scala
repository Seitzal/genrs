package genrs

import scala.util.{Try, Success, Failure}

/** Utility singleton for interop with the uJson AST. */
private[genrs] object JsonDecoder

  def read(str: String) = 
    Try(ujson.read(ujson.Readable.fromString(str)))

  def readUnsafe(str: String) = 
    read(str).get

  // TODO: Move type-specific decode logic to the instance definitions
  def decodeUnsafe(ujv: ujson.Value): ResourceWrapper[_] = ujv match

    case ujson.Obj(value) =>
      ResourceWrapper(KVObjectResource(
        value.toMap.map((k, v) => (k, decodeUnsafe(v)))
      ))

    case ujson.Arr(value) =>
      ResourceWrapper(value.toList.map(decodeUnsafe))

    case ujson.Num(value) =>
      ResourceWrapper(value)

    case ujson.Str(value) =>
      ResourceWrapper(value)

    case ujson.False =>
      ResourceWrapper(false)

    case ujson.True =>
      ResourceWrapper(true)

    case _ =>
      throw new errors.JsonDecodeException()

  def decode(json: ujson.Value): Try[ResourceWrapper[_]] =
    Try(decodeUnsafe(json))

  def decode(str: String): Try[ResourceWrapper[_]] =
    read(str).flatMap(decode)

  def decodeUnsafe(str: String) =
    decode(str).get
