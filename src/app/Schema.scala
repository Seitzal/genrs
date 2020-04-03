package genrs

import scala.util.{Try, Success, Failure}

case class Schema(items: Map[String, String | Schema])
  def allows(obj: JsonObject): Boolean = items.forall { (key, value) =>
    if obj has key then value match
      case rtype: String =>
        if(rtype startsWith "list:") 
          obj(key).rtype == rtype || obj(key).rtype == "list"
        else if(rtype == "list")
          obj(key).rtype startsWith "list"
        else obj(key).rtype == rtype
      case schema: Schema => obj.get[JsonObject](key) match
        case Success(childObj) => schema allows childObj
        case Failure(_) => false
    else false
  }

object Schema
  def apply(items: (String, String | Schema)*): Schema =
    Schema(items.toMap[String, String | Schema])
