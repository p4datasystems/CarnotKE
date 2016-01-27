package org.cyphersim;

import org.cyphersim.CypherParser.ReadContext;

public class WPVisitor extends CypherBaseVisitor<Expression> {
	@Override
	public Expression visitRead(ReadContext ctx) {
		// TODO Auto-generated method stub
		if (ctx.getChildCount() == 3) return visit(ctx.where());
		return null;
	}
	
	public Expression visitWhere(CypherParser.WhereContext ctx) {
		return visit(ctx.getChild(1));
	}
	
	public Expression visitExpression(CypherParser.ExpressionContext ctx) {
		Expression prop = visit(ctx.getChild(0));
		Expression comp = visit(ctx.getChild(1));
		Expression val = visit(ctx.getChild(2));
		return new Expression(prop.property, comp.comparison, val.value);
	}
	
	public Expression visitProperty(CypherParser.PropertyContext ctx) {
		Property newProp = new Property(ctx.ID(0).getText(), null);
		int numIds = ctx.ID().size();
		if (numIds > 1) newProp.prop = ctx.ID(1).getText();
		return new Expression(newProp, null, null);
	}
	
	public Expression visitComparison(CypherParser.ComparisonContext ctx) {
		return new Expression(null, ctx.getChild(0).getText(), null);
	}
	
	public Expression visitValue(CypherParser.ValueContext ctx) {
		return new Expression(null, null, ctx.getChild(0).getText());
	}
}
