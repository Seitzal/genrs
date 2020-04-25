package genrs.server

import genrs._
import ujson._
import java.io.File


final case class SchemaLoadException(msg: String) extends RuntimeException(msg)

final case class SchemaLoader(schemas: Map[String, Schema]) {

  private def resolveSpec(spec: String): Schema = spec match {
    case "Str" => 
      SimpleSchema("Str")
    case "Int" => 
      SimpleSchema("Int")
    case "Num" => 
      SimpleSchema("Num")
    case "Bool" => 
      SimpleSchema("Bool")
    case s"Seq:$inner" if inner != "" =>
      SeqSchema(resolveSpec(inner))
    case other if schemas.contains(other) =>
      schemas(other)
    case _ => throw SchemaLoadException("Unknown resource type specification")
  }

  def load(name: String, schema: Schema): SchemaLoader =
    if (!schemas.contains(name))
      SchemaLoader(this.schemas + (name -> schema))
    else this
  
  def loadAll(schemas: Seq[(String, Schema)]): SchemaLoader =
    if (schemas.isEmpty) this
    else this
      .load(schemas.head._1, schemas.head._2)
      .loadAll(schemas.tail)

  def loadJson(name: String, json: Value): SchemaLoader = json match {
    case Obj(kvs) => {
      val schemaStructure = kvs.toMap.map {
        case (key, Str(spec)) => (key, resolveSpec(spec))
        case _ => throw SchemaLoadException("Malformed json")
      }
      SchemaLoader(this.schemas + (name -> ObjectSchema(schemaStructure)))
    }
    case _ => throw SchemaLoadException("Malformed json")
  }

  def loadAllJson(schemas: Seq[(String, Value)]): SchemaLoader =
    if (schemas.isEmpty) this
    else this
      .loadJson(schemas.head._1, schemas.head._2)
      .loadAllJson(schemas.tail)

  def loadAllJson(filePath: String): SchemaLoader =
    ujson.read(new File(filePath)) match {
      case Obj(kvs) => loadAllJson(kvs.toList)
      case _ => throw SchemaLoadException("Malformed json")
    }

}

object SchemaLoader {
  def init: SchemaLoader = SchemaLoader(Map())
}
