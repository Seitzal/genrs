package genrs.test

import genrs._
import genrs.Resource.ResourceOps

import org.scalatest.funsuite.AnyFunSuite

class ResourceSuite extends AnyFunSuite

  val (r0, r1, r2, r3) = ("Hello World", -12, 3.5638, false)

  def genericResourceFunction[T: Resource](res: T): Boolean = res.textual != ""

  object StrangeResource extends GenResource
    def isCompound = false
    def rtype = "strange_resource"
    def textual = "I am a strange resource in text form"

  val l0 = List("Hello", "World")
  val l1 = List(1, 2, -6, 10000, 7.3)
  val l2 = List(l1, List(13.3, -2220.0))

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
