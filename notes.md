### CTL syntax
Let AP be a set of atomic propositions. For p∈AP, the set of CTL formulas is defined as:
```
φ ::= p | ¬φ | φ ∨ φ | (A|E)(X|F|G)φ | (A|E)[φ U φ]
```

### Converter
The rules for the other operators can be computed as follows:
```
AXφ ≡ ¬EX ¬φ
EFφ ≡ E[true U φ]
AFφ ≡ A[true U φ]
EGφ ≡ ¬AF¬φ
AGφ ≡ ¬EF¬φ
```
The other boolean operators are defined as usual:
```
φ ∧ ψ ≡ ¬(¬φ ∨ ¬ψ)
φ ⇒ ψ ≡ ¬φ ∨ ψ
φ ⇔ ψ ≡ (φ ⇒ ψ) ∧ (ψ ⇒ φ)
```
### Solver
```
Function Sat(φ, M)
	Case φ of
		true, then Sat(φ, M) ← S
		false, then Sat(φ, M) ← ∅
		p∈AP, then Sat(φ, M) ← {s∈S|p∈L(s)}
		¬ψ, then Sat(φ, M) ← S−Sat(ψ, M)
		ψ∨η, then Sat(φ, M) ← Sat(ψ, M)∪Sat(η, M)
		EXψ, then Sat(φ, M) ← {s∈S|∃s′∈δ(s). s′∈Sat(ψ, M)}
		E[ψUη], then Sat(φ, M) ← SatEU(ψ, η, M)
		A[ψUη], then Sat(φ, M) ← SatAU(ψ, η, M)
	End{Case}
End{Function}
```
```
FunctionSatEU(ψ,η,M)
{
    Pre:M= (S,δ,I,L)−Kripke structure overAP, δ−total relationψ,η−CTL formulas
    Post:SatEU(ψ,η,M) ={s∈S|M,s|=E[ψUη]}
}
	Q←Sat(η,M);
	Q′←∅;
	While Q != Q′do
		Q′←Q;
		Q←Q∪(Sat(ψ,M)∩{s∈S|∃s′∈δ(s)∩Q})
	End{While};
	SatEU(ψ,η,M)←Q
End{Function}
```
### Solver - Example problem
```
Given the Kripke structure M = (S, δ, I, L) over AP = {a, b, c},
with S = {p, q, r, t}, 
δ = {(p, q),(q, r), (q, t), (r, r), (r, t), (t, r)},
I = {p},
L(p) = {a}, L(q) = {b}, L(r) = {b, c}, L(t) = {c, a}
and the CTL formula: φ = A [a U b] ˄  EG b, indicate the states s such that M, s╞ φ.
```

### Internals - expression
```
Expression (abstraction for storing data)
	|-> UnaryOperation like ~, EX, etc
	|-> BinaryOperation like v, ^
	|-> Atom
		  |-> True
		  |-> False
		  |-> NamedAtom(label) p∈AP
```

### Parser
Parse the string repr. of the CTL Formula and convert it to an expression that can be processed
```
input 1: A[a U b] ^ EG b
input 2: E[true U ~E[true U ~a]]
```

### Parser - Internals

opStack -> Stack<String> -- holds the operators (v, ^, ~, AX, etc)  
outStack -> Stack<Expression> -- holds and constructs the AST  
depthStack -> Stack<Pair<String, Int>> -- holds the linear-time operator (e.g. U, R, W)  
and the indentation level at which it appeared


Notes:  
The unary ops have only unary meaning (e.g. ~a)  
For the (A|E)U operations we keep the depthStack, and have the following equation holding true:  
depth(A) = depth(U) + 1 (because the op is expressed as: A[expr U expr])
