package genrs

import ujson._

trait ResourceTypes {

  implicit class ValueResourceTypes(res: Value) {
    def rtype: String = res match {
      case Null => "Null"
      case True => "Bool"
      case False => "Bool"
      case Str(_) => "Str"
      case Num(num) if num.isValidInt => "Int"
      case Num(_) => "Num"
      case Arr(arr) if !arr.isEmpty => "Seq:" + arr.head.rtype
      case Arr(_) => "Seq"
      case Obj(_) => "Obj"
    }
  }

}