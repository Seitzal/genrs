package genrs.errors

final case class ResourceTypeException() extends RuntimeException(
  "Wrapper content is not of the specified resource type.")

final case class JsonDecodeException() extends RuntimeException(
  "Error converting ujson AST to GenRS resource types.")
