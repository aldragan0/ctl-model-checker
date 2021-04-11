package helper

object MapHelper {
  def invert[T](value: Map[T, Set[T]]): Map[T, Set[T]] =
    (for ((k, v) <- value.toList; el <- v) yield (el, k))
      .groupMap(_._1)(_._2)
      .view
      .mapValues(_.toSet)
      .toMap
}
