package genrs

import ujson._

sealed trait Schema {
  def matches(value: Value): Boolean
}

final case class SimpleSchema(rtype: String) extends Schema {
  def matches(value: Value): Boolean = value.rtype == rtype
}

final case class SeqSchema(eltype: Schema) extends Schema {
  def matches(value: Value): Boolean = value match {
    case Arr(els) if els.isEmpty => true
    case Arr(els) => eltype.matches(els.head)
    case _ => false
  }
}

object SeqSchema {
  def apply(rtype: String): SeqSchema = SeqSchema(SimpleSchema(rtype))
}

final case class ObjectSchema(structure: Map[String, Schema]) extends Schema {

  def matches(value: Value): Boolean = value match {
    case obj: Obj => structure.forall {
      case (key, schema) if obj.value.contains(key) =>
        schema.matches(obj(key))
      case _ => false
    }
    case _ => false
  }

}

object ObjectSchema {
  def apply(kvs: (String, Schema)*): ObjectSchema = ObjectSchema(kvs.toMap)
}
