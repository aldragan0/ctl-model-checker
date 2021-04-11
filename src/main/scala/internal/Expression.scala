package internal

sealed trait Expression

sealed trait UnaryOp extends Expression {
  def expr: Expression
}

case class Not(val expr: Expression) extends UnaryOp
case class AX(val expr: Expression) extends UnaryOp
case class EX(val expr: Expression) extends UnaryOp
case class AF(val expr: Expression) extends UnaryOp
case class EF(val expr: Expression) extends UnaryOp
case class AG(val expr: Expression) extends UnaryOp
case class EG(val expr: Expression) extends UnaryOp

sealed trait BinaryOp extends Expression {
  def lhs: Expression
  def rhs: Expression
}

case class And(val lhs: Expression, val rhs: Expression) extends BinaryOp
case class Or(val lhs: Expression, val rhs: Expression) extends BinaryOp
case class Implies(val lhs: Expression, val rhs: Expression) extends BinaryOp
case class Equiv(val lhs: Expression, val rhs: Expression) extends BinaryOp
case class AU(val lhs: Expression, val rhs: Expression) extends BinaryOp
case class EU(val lhs: Expression, val rhs: Expression) extends BinaryOp

sealed abstract class Atom extends Expression

case class NamedAtom(val value: String) extends Atom
case class True() extends Atom
case class False() extends Atom
