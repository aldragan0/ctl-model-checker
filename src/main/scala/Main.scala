import scala.io.Source
import internal.Model

object Main extends App {
  def parseToTupleSet(
      value: String,
      separator: Char = ','
  ): Set[(String, String)] =
    value
      .replaceAll(raw"[ \(\)]", "")
      .split(separator)
      .sliding(2, 2)
      .map { case Array(a, b) =>
        (a, b)
      }
      .toSet

  def parseToSet(value: String, separator: Char = ','): Set[String] =
    value.replaceAll(" ", "").split(separator).toSet

  def parseToMap(
      values: Array[String],
      keyValueSeparator: Char = '-',
      separator: Char = ','
  ): Map[String, Set[String]] =
    values
      .slice(3, values.length)
      .map(_.replaceAll(" ", ""))
      .map(s => s.splitAt(s.indexOf(keyValueSeparator)))
      .map(p => (p._1, p._2.split(separator).toSet))
      .toMap

  def parseModel(values: Array[String]): Model = {
    val initialState = values(0)
    val states = parseToSet(values(1))
    val transitions = parseToTupleSet(values(2))
    val stateLabels = parseToMap(values.slice(3, values.length))

    return new Model(states, transitions, stateLabels, initialState)
  }

  //TODO: test the solver on some input expressions
  //TODO: write some documentation for the functions and files
  //Model, Parser, SatSolver, Expression, MapHelper

  try {
    val model = parseModel(
      Source
        .fromURL(getClass().getResource("model.txt"))
        .getLines()
        .toArray
    )

    Source
      .fromURL(getClass.getResource("formula.txt"))
      .getLines()
      .filter(!_.startsWith("#"))
      .map(_.replaceAll(" ", ""))
      .map(Parser.parse)
      .map(f => SatSolver.solve(f, model))
      .foreach(Console.println)
  } catch {
    case e: Error => e.printStackTrace()
  }
}
