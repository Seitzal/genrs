package genrs

trait GlobalExtensions

  /** Test two objects for equality, under the premise that a wrapped resource
   *  is equal to an identical unwrapped resource
   */
  def (left: Any) ~= (right: Any): Boolean = 
    (left, right) match
      case (ResourceWrapper(r_left), ResourceWrapper(r_right)) =>
        r_left ~= r_right
      case (ResourceWrapper(r_left), right) =>
        r_left ~= right
      case (left, ResourceWrapper(r_right)) =>
        left ~= r_right
      case (JsonObject(left), JsonObject(right)) =>
        left.keys == right.keys &&
        left.keys.forall(key => left(key) ~= right(key))
      case (left: Seq[_], right: Seq[_]) =>
        left.length == right.length &&
        (0 until left.length).forall(i => left(i) ~= right(i))
      case (left, right) =>
        left == right

  /** Test two objects for inequality, under the premise that a wrapped resource
   *  is equal to an identical unwrapped resource
   */
  def (left: Any) !~= (right: Any) : Boolean =
    !(left ~= right)
