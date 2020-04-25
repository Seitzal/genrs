package genrs.test

import genrs._
import genrs.server._

import org.scalatest._
import org.scalatest.funsuite._

class ServerTests extends AnyFunSuite {

  test("SchemaLoader: load from .json file") {
    val schemas = SchemaLoader
      .init
      .loadAllJson("testdata/resources/schemas1.json")
      .schemas
    println(schemas("Artist"))
    assert(schemas("Artist") == artistSchema)
    assert(schemas("Album") == albumSchema)
  }

}