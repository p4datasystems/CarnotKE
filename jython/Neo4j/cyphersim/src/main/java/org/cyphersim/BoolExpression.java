package org.cyphersim;

public class BoolExpression {
	public String op;
	public Expression exp;
	
	public BoolExpression() {
		op = null;
		exp = null;
	}
	
	public BoolExpression(String o, Expression e) {
		op = o;
		exp = e;
	}
	
	public String nodeID() {
		return exp.property.nodeId;
	}
	
	public String toString() {
		if (op != null) return " " + op + " " + exp.toString();
		return exp.toString();
	}
}
