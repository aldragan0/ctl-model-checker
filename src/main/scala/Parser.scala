import internal._
import scala.collection.mutable.Stack

object Parser {

  private def updateWithOp(op: String, outStack: Stack[Expression]) =
    op match {
      case Grammar.unaryOp() => {
        val newOp = Grammar.getUnaryOp(op, outStack.pop())
        outStack.push(newOp)
      }
      case Grammar.binaryOp() => {
        val rhs = outStack.pop()
        val lhs = outStack.pop()
        outStack.push(Grammar.getBinaryOp(op, lhs, rhs))
      }
      case _ => throw new Error(s"Illegal operation encountered: $op")
    }

  /*
    Expression parser that uses a modified version of the Shunting-yard algorithm
    https://en.wikipedia.org/wiki/Shunting-yard_algorithm
   */
  def parse(formula: String): Expression = {
    var expr = formula
    var depth = 0
    val opStack = Stack[String]()
    val outStack = Stack[Expression]()
    val temporalOpStack = Stack[(String, Int)]()

    while (expr.nonEmpty) {
      expr match {
        case Grammar.atom(token, after) => {
          outStack.push(Grammar.getAtom(token))
          expr = after
        }
        case Grammar.function(token, after) => {
          opStack.push(token)
          expr = expr.substring(token.length())
          expr = after
        }
        case Grammar.binaryTemporalOp(token, after) => {
          temporalOpStack.push((token, depth - 1))
          expr = after
        }
        case Grammar.leftParanthesis(token, after) => {
          opStack.push(token)
          depth += 1
          expr = after
        }
        case Grammar.operation(token, after) => {
          while (
            opStack.nonEmpty
            && !Grammar.leftParanthesis.matches(opStack.top)
            && Grammar.precedence(opStack.top) >= Grammar.precedence(token)
          ) {
            updateWithOp(opStack.pop(), outStack)
          }
          opStack.push(token)
          expr = after
        }
        case Grammar.rightParanthesis(token, after) => {
          depth -= 1
          while (
            opStack.nonEmpty && !Grammar.leftParanthesis.matches(opStack.top)
          ) {
            updateWithOp(opStack.pop(), outStack)
          }

          if (opStack.isEmpty) {
            throw new Error(
              s"Missing matching bracket in: $formula at pos:${formula
                .length() - expr.length()}"
            )
          } else {
            opStack.pop()
          }

          if (opStack.nonEmpty && Grammar.function.matches(opStack.top)) {
            var functionName = opStack.pop()
            if (temporalOpStack.nonEmpty && temporalOpStack.top._2 == depth) {
              functionName += temporalOpStack.pop()._1
            }
            updateWithOp(functionName, outStack)
          }
          expr = after
        }
        case _ => throw new Error(s"Failed to parse expression: $expr")
      }
    }

    while (opStack.nonEmpty) {
      updateWithOp(opStack.pop(), outStack)
    }

    return outStack.top
  }
}
