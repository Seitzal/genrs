package genrs.test

import genrs._
import genrs.Resource.given

import org.scalatest.funsuite.AnyFunSuite

import scala.util.{Try, Success, Failure}

class ResourceSuite extends AnyFunSuite

  val (r0, r1, r2, r3) = ("Hello World", -12, 3.5638, false)

  def genericResourceFunction[T: Resource](res: T): Boolean = res.textual != ""

  object StrangeResource extends GenResource
    def isCompound = false
    def rtype = "strange_resource"
    def textual = "I am a strange resource in text form"
    def json = "\"I am a strange resource in text form\""

  val l0 = List("Hello", "World")
  val l1 = List(1, 2, -6, 10000, 7.3)
  val l2 = List(l1, List(13.3, -2220.0))

  val kvObject0 = KVObjectResource(Map(
    "name" -> ResourceWrapper("kvObject0"),
    "description" -> ResourceWrapper("A key-value object"),
    "data" -> ResourceWrapper(KVObjectResource(Map(
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

  test("Simple resources should return their proper string representation") {
    assert(r0.textual == "Hello World")
    assert(r1.textual == "-12")
    assert(r2.textual == "3.5638")
    assert(r3.textual == "false")
  }

  test("Context-bounded type parameters should work for all resource types") {
    assert(genericResourceFunction(r0))
    assert(genericResourceFunction(r1))
    assert(genericResourceFunction(r2))
    assert(genericResourceFunction(r3))
  }

  test("Members of GenResource trait should typecheck as resources") {
    assert(genericResourceFunction(StrangeResource))
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

  test("KVObjectResource should typecheck as resource") {
    assert(genericResourceFunction(kvObject0))
  }

  test("KVObjectResource should allow untyped lookup") {
    val name = kvObject0.kv("name")
    assert(name.rtype == "string")
    assert(name.res.isInstanceOf[String])
  }

  test("KVObjectResource should allow typed lookup") {
    assert(kvObject0.getUnsafe[String]("name") == "kvObject0")
  }

  test("Typed lookup should fail if a wrong type is looked up") {
    assertThrows[ResourceTypeException] {
      kvObject0.getUnsafe[Double]("name")
    }
  }

  test("Typed lookup should fail if a nondefined key is looked up") {
    assertThrows[NoSuchElementException] {
      kvObject0.getUnsafe[String]("invalid_key")
    }
  }

  test("Pattern matching on wrapped resources should reveal their type") {
    def identify(rw: ResourceWrapper[_]) = rw.res match
      case str: String => "string"
      case l: List[_] => "list"
      case kvo: KVObjectResource => "kvo"
    assert(identify(kvObject0("name")) == "string")
    assert(identify(kvObject0("data")) == "kvo")
    assert(identify(ResourceWrapper(l0)) == "list")
  }

  test("JSON encoding: string") {
    assert(r0.json == "\"Hello World\"")
  }

  test("JSON encoding: int") {
    assert(r1.json == "-12")
  }

  test("JSON encoding: double") {
    assert(r2.json == "3.5638")
  }

  test("JSON encoding: boolean") {
    assert(r3.json == "false")
  }

  test("JSON encoding: empty list") {
    assert(List[String]().json == "[]")
  }

  test("JSON encoding: flat lists") {
    assert(l0.json == "[\"Hello\",\"World\"]")
    assert(l1.json == "[1.0,2.0,-6.0,10000.0,7.3]")
  }

  test("JSON encoding: nested list") {
    assert(l2.json == "[[1.0,2.0,-6.0,10000.0,7.3],[13.3,-2220.0]]")
  }

  test("JSON encoding: KVObjectResource") {
    val expected =
      "{\"name\":\"kvObject0\",\"description\":\"A key-value object\",\"data\":"
      + "{\"l0\":[\"Hello\",\"World\"],\"l1\":[1.0,2.0,-6.0,10000.0,7.3]}}"
    assert(kvObject0.json == expected)
  }

  test("JSON roundtripping a complex resource should not change it "
  + "(apart from wrapping)") {
    assert(JSONDecoder.decode(kvObject0.json).res == kvObject0)
  }
