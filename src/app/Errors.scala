package genrs

final case class ResourceTypeException() extends RuntimeException(
  "Wrapper content is not of the specified resource type.")
