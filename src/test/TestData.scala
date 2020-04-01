package genrs.test

import genrs._
import genrs.given
import genrs.JsonObject.dsl._

import scala.util.Success

trait TestData

  def genericResourceFunction[T: Resource](res: T): Boolean = true

  class StrangeResource

  given as Resource[StrangeResource]:
    def isCompound(sr: StrangeResource) = false
    def rtype(sr: StrangeResource) = "strange_resource"
    def toJson(sr: StrangeResource) = "\"I am a strange resource in text form\""
    def fromJson(json: ujson.Value) = Success(new StrangeResource)

  val object0 = jsonObject(
    "name" -> v("object0"),
    "description" -> v("A key-value object"),
    "data" -> o(
      "l0" -> l("Hello", "World"),
      "l1" -> l(1, 2, -6, 10000, 7.3)
    )
  )

  val object0_json =
    "{\"name\":\"object0\",\"description\":\"A key-value object\",\"data\":"
    + "{\"l0\":[\"Hello\",\"World\"],\"l1\":[1.0,2.0,-6.0,10000.0,7.3]}}"
