import scala.io.Source

object Main extends App {
  //TODO: test the solver on some input expressions
  //TODO: write some documentation for the functions and files
  //Model, Parser, SatSolver, Expression, MapHelper

  try {
    Source
      .fromURL(getClass.getResource("formula.txt"))
      .getLines()
      .filter(!_.startsWith("#"))
      .map(_.replaceAll(" ", ""))
      .map(Parser.parse)
      .foreach(Console.println)
  } catch {
    case e: Error => e.printStackTrace()
  }
}
