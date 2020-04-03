package genrs.test

import genrs._
import genrs.given
import genrs.errors._

import org.scalatest.funsuite.AnyFunSuite

import scala.util.{Try, Success, Failure}

class ResourceSuite extends AnyFunSuite with TestData

  test("Simple resources should be flagged as non-compound") {
    assert("Hello World".isCompound == false)
    assert(-12.isCompound == false)
    assert(3.5638.isCompound == false)
    assert(false.isCompound == false)
  }

  test("Simple resources should know their type") {
    assert("Hello World".rtype == "string")
    assert(-12.rtype == "int")
    assert(3.5638.rtype == "double")
    assert(false.rtype == "boolean")
  }

  test("Lists should be flagged as compound resources") {
    assert(List("Hello", "World").isCompound)
  }

  test("Non-empty lists should know their rtype") {
    assert(List[String]().rtype == "list")
    assert(List("Hello", "World").rtype == "list:string")
    assert(List(2, -0.5, 10.34).rtype == "list:double")
    assert(List(List("Hello", "World")).rtype == "list:list:string")
  }

  test("JsonObjects should be flagged as compound resources") {
    assert(object0.isCompound)
  }

  test("JsonObjects should allow unsafe lookup") {
    val name = object0("name")
    assert(name.rtype == "string")
    assert(name.res.isInstanceOf[String])
  }

  test("JsonObjects should allow safe lookup") {
    assert(object0.get[String]("name") == Success("object0"))
  }

  test("Safe lookup should fail if a wrong type is looked up") {
    val result = object0.get[Double]("name")
    assertThrows[ResourceTypeException] { result.get }
  }

  test("Safe lookup should fail if a nondefined key is looked up") {
    val result = object0.get[String]("invalid_key")
    assertThrows[NoSuchElementException] { result.get }
  }

  test("JsonObjects should allow typed lookup through a query syntax") {
    assert(object0.query[String]("name") == Success("object0"))
    assertThrows[ResourceTypeException](object0.query[Double]("name").get)
    assert(object0.query[List[String]]("data.l0") == Success(List("Hello", "World")))
    assert(object0.query[List[Double]]("data.l0").isSuccess)
    assert(object0.query[String]("data.l0[0]") == Success("Hello"))
    assert(object0.query[Double]("data.l0[0]").isFailure)
    assertThrows[IndexOutOfBoundsException](object0.query[String]("data.l0[3]").get)
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

  test("Json encoding / decoding should work for JsonObjects") {
    assert(object0.toJson == object0_json)
    assert(Resource[JsonObject].fromJsonUnsafe(object0_json) ~= object0)
  }

  test("Schemas should properly detect matching objects") {
    assert(object0 matches schema0)
    assert(object1 matches schema0)
    assert(!(object2 matches schema0))
    assert(!(object3 matches schema0))
  }
