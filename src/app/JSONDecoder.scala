package genrs

object JSONDecoder

  def decode(ujv: ujson.Value): ResourceWrapper[_] = ujv match

    case ujson.Obj(value) =>
      ResourceWrapper(KVObjectResource(
        value.toMap.map((k, v) => (k, decode(v)))
      ))

    case ujson.Arr(value) =>
      ResourceWrapper(value.toList.map(decode))

    case ujson.Num(value) =>
      ResourceWrapper(value)

    case ujson.Str(value) =>
      ResourceWrapper(value)

    case ujson.False =>
      ResourceWrapper(false)

    case ujson.True =>
      ResourceWrapper(true)

    case ujson.Null =>
      val errMsg = "Error while decoding resource from JSON: Encountered null"
      throw new Error(errMsg)

  def decode(str: String): ResourceWrapper[_] =
    decode(ujson.read(ujson.Readable.fromString(str)))
