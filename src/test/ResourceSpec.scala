package genrs.test

import genrs._
import genrs.given
import genrs.errors._

import org.scalatest.funsuite.AnyFunSuite

import scala.util.{Try, Success, Failure}

class ResourceSuite extends AnyFunSuite

  val (r0, r1, r2, r3) = ("Hello World", -12, 3.5638, false)

  def genericResourceFunction[T: Resource](res: T): Boolean = true

  class StrangeResource

  given as Resource[StrangeResource]:
    def isCompound(sr: StrangeResource) = false
    def rtype(sr: StrangeResource) = "strange_resource"
    def toJson(sr: StrangeResource) = "\"I am a strange resource in text form\""
    def fromJson(json: ujson.Value) = Success(new StrangeResource)

  val l0 = List("Hello", "World")
  val l1 = List(1, 2, -6, 10000, 7.3)
  val l2 = List(l1, List(13.3, -2220.0))

  val object0 = JsonObject(Map(
    "name" -> ResourceWrapper("object0"),
    "description" -> ResourceWrapper("A key-value object"),
    "data" -> ResourceWrapper(JsonObject(Map(
      "l0" -> ResourceWrapper(l0),
      "l1" -> ResourceWrapper(l1)
    )))
  ))

  test("Simple resources should be flagged as non-compound") {
    assert(!r0.isCompound)
    assert(!r1.isCompound)
    assert(!r2.isCompound)
    assert(!r3.isCompound)
  }

  test("Simple resources should know their type") {
    assert(r0.rtype == "string")
    assert(r1.rtype == "int")
    assert(r2.rtype == "double")
    assert(r3.rtype == "boolean")
  }

  test("Context-bounded type parameters should work for all resource types") {
    assert(genericResourceFunction(r0))
    assert(genericResourceFunction(r1))
    assert(genericResourceFunction(r2))
    assert(genericResourceFunction(r3))
  }

  test("Members of GenResource trait should typecheck as resources") {
    assert(genericResourceFunction(new StrangeResource))
  }

  test("Lists of any resource type should typecheck as resources") {
    assert(genericResourceFunction(l0))
    assert(genericResourceFunction(l1))
    assert(genericResourceFunction(l2))
  }

  test("Lists should be flagged as compound resources") {
    assert(l0.isCompound)
    assert(l1.isCompound)
    assert(l2.isCompound)
  }

  test("JsonObject should typecheck as resource") {
    assert(genericResourceFunction(object0))
  }

  test("JsonObject should allow untyped lookup") {
    val name = object0.kv("name")
    assert(name.rtype == "string")
    assert(name.res.isInstanceOf[String])
  }

  test("JsonObject should allow typed lookup") {
    assert(object0.getUnsafe[String]("name") == "object0")
  }

  test("Typed lookup should fail if a wrong type is looked up") {
    assertThrows[ResourceTypeException] {
      object0.getUnsafe[Double]("name")
    }
  }

  test("Typed lookup should fail if a nondefined key is looked up") {
    assertThrows[NoSuchElementException] {
      object0.getUnsafe[String]("invalid_key")
    }
  }

  test("Pattern matching on wrapped resources should reveal their type") {
    def identify(rw: ResourceWrapper[_]) = rw.res match
      case str: String => "string"
      case l: List[_] => "list"
      case kvo: JsonObject => "kvo"
    assert(identify(object0("name")) == "string")
    assert(identify(object0("data")) == "kvo")
    assert(identify(ResourceWrapper(l0)) == "list")
  }

  test("Json encoding / decoding should work for strings") {
    assert("Hello World".toJson == "\"Hello World\"")
    assert(Resource[String].fromJsonUnsafe("\"Hello World\"") == "Hello World")
  }

  test("Json encoding / decoding should work for ints") {
    assert(12.toJson == "12")
    assert(Resource[Int].fromJsonUnsafe("12") == 12)
  }

  test("Json encoding / decoding should work for doubles") {
    assert(3.5638.toJson == "3.5638")
    assert(Resource[Double].fromJsonUnsafe("3.5638") == 3.5638)
  }

  test("Json encoding / decoding should work for booleans") {
    assert(true.toJson == "true")
    assert(false.toJson == "false")
    assert(Resource[Boolean].fromJsonUnsafe("true") == true)
    assert(Resource[Boolean].fromJsonUnsafe("false") == false)
  }

  test("Json encoding / decoding should work for empty lists") {
    assert(List[String]().toJson == "[]")
    assert(Resource[List[String]].fromJsonUnsafe("[]") == List())
    assert(List[StrangeResource]().toJson == "[]")
    assert(Resource[List[StrangeResource]].fromJsonUnsafe("[]") == List())
  }

  test("Json encoding / decoding should work for flat lists") { 
    assert(List("Hello", "World").toJson == "[\"Hello\",\"World\"]")
    assert(List(1, 2, -6, 10000, 7.3).toJson == "[1.0,2.0,-6.0,10000.0,7.3]")
    assert(
      Resource[List[String]].fromJsonUnsafe("[\"Hello\",\"World\"]") ==
      List("Hello", "World"))
    assert(
      Resource[List[Double]].fromJsonUnsafe("[1.0,2.0,-6.0,10000.0,7.3]") ==
      List(1, 2, -6, 10000, 7.3))
  }

  test("Json encoding / decoding should work for nested lists") {
    val l = List(List(1, 2, -6, 10000, 7.3), List(13.3, -2220.0))
    val l_json = "[[1.0,2.0,-6.0,10000.0,7.3],[13.3,-2220.0]]"
    assert(l.toJson == l_json)
    assert(Resource[List[List[Double]]].fromJsonUnsafe(l_json) == l)
  }

  test("Json encoding / decoding should work for JSON objects") {
    val object0_json =
      "{\"name\":\"object0\",\"description\":\"A key-value object\",\"data\":"
      + "{\"l0\":[\"Hello\",\"World\"],\"l1\":[1.0,2.0,-6.0,10000.0,7.3]}}"
    assert(object0.toJson == object0.toJson)
    assert(Resource[JsonObject].fromJsonUnsafe(object0_json) == object0)
  }
