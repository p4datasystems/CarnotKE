package org.cyphersim;

public class Expression {
	public Property property;
	public String comparison;
	public String value;

	public Expression(Property p, String c, String v) {
		property = p;
		comparison = c;
		value = v;
	}
	
	public String toString() {
		return property.toString() + " " + comparison + " " + value;
	}
}
