package genrs

/**
 * Typeclass for generic resource operations
 */
trait Resource[T]
  def isCompound(t: T): Boolean
  def rtype(t: T): String
  def textual(t: T): String
  def json(t: T): String

object Resource

  def apply[T](using resource: Resource[T]) = resource

  extension on[T: Resource](t: T):
    def isCompound = Resource[T].isCompound(t)
    def rtype = Resource[T].rtype(t)
    def textual = Resource[T].textual(t)
    def json = Resource[T].json(t)

  given as Resource[String]:
    def isCompound(str: String) = false
    def rtype(str: String) = "string"
    def textual(str: String) = str
    def json(str: String) = "\"" + str + "\""

  given as Resource[Int]:
    def isCompound(int: Int) = false
    def rtype(int: Int) = "int"
    def textual(int: Int) = int.toString
    def json(int: Int) = int.toString

  given as Resource[Double]:
    def isCompound(d: Double) = false
    def rtype(d: Double) = "double"
    def textual(d: Double) = d.toString
    def json(d: Double) = d.toString

  given as Resource[Boolean]:
    def isCompound(b: Boolean) = false
    def rtype(b: Boolean) = "boolean"
    def textual(b: Boolean) = b.toString
    def json(b: Boolean) = b.toString

  given [T <: GenResource] as Resource[T]:
    def isCompound(genRes: T) = genRes.isCompound
    def rtype(genRes: T) = genRes.rtype
    def textual(genRes: T) = genRes.textual
    def json(genRes: T) = genRes.json

  given [T: Resource] as Resource[List[T]]:
    def isCompound(list: List[T]) = true
    def rtype(list: List[T]) = "list"
    def textual(list: List[T]) = "(list)"
    def json(list: List[T]) = "[" + list.map(_.json).mkString(",") + "]"
