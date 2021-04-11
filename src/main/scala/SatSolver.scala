import internal.Model
import internal.Expression
import internal.True
import internal.False
import internal.NamedAtom
import internal.Not
import internal.Or
import internal.AU
import internal.EU
import internal.EX
import helper.MapHelper

object SatSolver {
  private def satEX(
      transitions: Set[(String, String)],
      satStates: Set[String]
  ): Set[String] =
    transitions
      .filter(pair => satStates.contains(pair._2))
      .map(_._1)

  private def satAU(
      lhs: Expression,
      rhs: Expression,
      model: Model
  ): Set[String] =
    computeSatAU(
      solve(rhs, model),
      Set.empty,
      solve(lhs, model),
      model.transitions.groupMap(_._1)(_._2)
    )

  private def satEU(
      lhs: Expression,
      rhs: Expression,
      model: Model
  ): Set[String] =
    computeSatEU(
      solve(rhs, model),
      Set.empty,
      solve(lhs, model),
      model.transitions
    )

  private def computeSatAU(
      curr: Set[String],
      prev: Set[String],
      leftSat: Set[String],
      transitions: Map[String, Set[String]]
  ): Set[String] =
    if (curr == prev) curr
    else
      computeSatAU(
        // Q ← Q ∪ (Sat(ψ,M) ∩ {s∈S|∀s′∈δ(s). s′∈Q})
        curr | (leftSat & transitions.filter(_._2.subsetOf(curr)).keySet),
        curr,
        leftSat,
        transitions
      )

  private def computeSatEU(
      curr: Set[String],
      prev: Set[String],
      leftSat: Set[String],
      transitions: Set[(String, String)]
  ): Set[String] =
    if (curr == prev) curr
    else
      computeSatEU(
        // Q ← Q ∪ (Sat(ψ,M) ∩ {s∈S|∃s′∈δ(s) ∩ Q})
        curr | (leftSat & transitions
          .filter(p => curr.contains(p._2))
          .map(_._1)),
        curr,
        leftSat,
        transitions
      )

  def solve(formula: Expression, model: Model): Set[String] =
    //TODO: reuse the inverted map
    formula match {
      case True()           => model.states
      case False()          => Set.empty
      case NamedAtom(label) => MapHelper.invert(model.stateLabels)(label)
      case Not(expr)        => model.states &~ solve(expr, model)
      case Or(lhs, rhs)     => solve(lhs, model) | solve(rhs, model)
      case EX(expr)         => satEX(model.transitions, solve(expr, model))
      case AU(lhs, rhs)     => satAU(lhs, rhs, model)
      case EU(lhs, rhs)     => satEU(lhs, rhs, model)
      case op =>
        throw new Error(s"Encountered invalid operation: $op in: $formula")
    }
}
