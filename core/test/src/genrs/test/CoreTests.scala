package genrs.test

import genrs._

import org.scalatest._
import org.scalatest.funsuite._
import upickle.default._
import ujson.Obj

class CoreTests extends AnyFunSuite {

  test("Schema checking") {
    assert(artistSchema.matches(ujson.read(upickle.default.write(opeth))))
  }

}