package internal

object Grammar {
  val test = "test"
  val terms = Map(
    "ATOM" -> raw"true|false|[a-ux-z]",
    "FUNCTION" -> raw"[AE][XFG]?",
    "BINARY_TEMPORAL_OP" -> "U",
    "BINARY_OP" -> raw"\^|v|=>|<=>",
    "UNARY_OP" -> "~",
    "LEFT_PARANTHESIS" -> raw"\[",
    "RIGHT_PARANTHESIS" -> raw"\]"
  )

  final val precedence = Map(
    "~" -> 5,
    "^" -> 4,
    "v" -> 4,
    "=>" -> 3,
    "<=>" -> 3
  )

  val after = raw"(.*)"
  val atom = s"(${terms("ATOM")})$after".r
  val function = s"(${terms("FUNCTION")})$after".r
  val binaryTemporalOp = s"(${terms("BINARY_TEMPORAL_OP")})$after".r
  val leftParanthesis = s"(${terms("LEFT_PARANTHESIS")})$after".r
  val rightParanthesis = s"(${terms("RIGHT_PARANTHESIS")})$after".r
  val operation = s"(${terms("BINARY_OP")}|${terms("UNARY_OP")})$after".r

  val unaryOp = s"${terms("UNARY_OP")}|${terms("FUNCTION")}".r
  val binaryOp = s"${terms("BINARY_OP")}|[AE]${terms("BINARY_TEMPORAL_OP")}".r

  def getAtom(value: String): Expression =
    value match {
      case "true"  => True()
      case "false" => False()
      case _       => NamedAtom(value)
    }

  def getUnaryOp(value: String, expr: Expression) =
    value match {
      case "~"  => Not(expr)
      case "AX" => AX(expr)
      case "EX" => EX(expr)
      case "AF" => AF(expr)
      case "EF" => EF(expr)
      case "AG" => AG(expr)
      case "EG" => EG(expr)
    }

  def getBinaryOp(value: String, lhs: Expression, rhs: Expression): Expression =
    value match {
      case "^"   => And(lhs, rhs)
      case "v"   => Or(lhs, rhs)
      case "=>"  => Implies(lhs, rhs)
      case "<=>" => Equiv(lhs, rhs)
      case "AU"  => AU(lhs, rhs)
      case "EU"  => EU(lhs, rhs)
    }
}
