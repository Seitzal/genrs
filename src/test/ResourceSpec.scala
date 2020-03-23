package genrs.test

import genrs._
import genrs.Resource._

import org.scalatest.funsuite.AnyFunSuite

class ResourceSuite extends AnyFunSuite

  val (r0, r1, r2, r3) = ("Hello World", -12, 3.5638, false)
  def genericResourceFunction[T: Resource](res: T): Boolean = res.isCompound

  test("Simple resources should be flagged as non-compound") {
    assert(r0.isCompound == false)
    assert(r1.isCompound == false)
    assert(r2.isCompound == false)
    assert(r3.isCompound == false)
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
    assert(genericResourceFunction(r0) == false)
    assert(genericResourceFunction(r1) == false)
    assert(genericResourceFunction(r2) == false)
    assert(genericResourceFunction(r3) == false)
  }

  test("Members of GenResource trait should behave according to Resource typeclass") {
    object StrangeResource extends GenResource {
      def isCompound = false
      def rtype = "strange_resource"
      def textual = "I am a strange resource in text form"
    }
    assert(genericResourceFunction(StrangeResource) == false)
  }
