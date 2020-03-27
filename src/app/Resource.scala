package genrs

/**
 * Typeclass for generic resource operations
 */
trait Resource[T]
  def isCompound(t: T): Boolean
  def rtype(t: T): String
  def textual(t: T): String

object Resource

  def apply[T](implicit resource: Resource[T]): Resource[T] =
    resource

  implicit class ResourceOps[T: Resource](t: T)
    def isCompound = Resource[T].isCompound(t)
    def rtype = Resource[T].rtype(t)
    def textual = Resource[T].textual(t)

  implicit val stringResource: Resource[String] =
    new Resource[String]:
      def isCompound(str: String) = false
      def rtype(str: String) = "string"
      def textual(str: String) = str

  implicit val intResource: Resource[Int] =
    new Resource[Int]:
      def isCompound(int: Int) = false
      def rtype(int: Int) = "int"
      def textual(int: Int) = int.toString

  implicit val doubleResource: Resource[Double] = 
    new Resource[Double]:
      def isCompound(d: Double) = false
      def rtype(d: Double) = "double"
      def textual(d: Double) = d.toString

  implicit val booleanResource: Resource[Boolean] = 
    new Resource[Boolean]:
      def isCompound(b: Boolean) = false
      def rtype(b: Boolean) = "boolean"
      def textual(b: Boolean) = b.toString

  implicit def genResource[T <: GenResource]: Resource[T] = 
    new Resource[T]:
      def isCompound(genRes: T) = genRes.isCompound
      def rtype(genRes: T) = genRes.rtype
      def textual(genRes: T) = genRes.textual

  implicit def listResource[T: Resource]: Resource[List[T]] = 
    new Resource[List[T]]:
      def isCompound(list: List[T]) = true
      def rtype(list: List[T]) = "list"
      def textual(list: List[T]) = "(list)"
