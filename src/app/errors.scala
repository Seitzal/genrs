package genrs.errors

final case class ResourceTypeException() extends RuntimeException(
  "Wrapper content is not of the specified resource type.")

final case class JsonConversionException(_type: String) extends RuntimeException(
  s"Couldn't convert JSON AST to the specified resource type '$_type'")

final case class InvalidIntException() extends RuntimeException(
  "Numeric is not a valid integer, it is either fractional or exceeds bounds.")
