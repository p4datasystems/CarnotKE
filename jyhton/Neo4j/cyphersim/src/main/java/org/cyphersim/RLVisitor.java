package org.cyphersim;

import java.util.LinkedList;

import org.antlr.v4.runtime.tree.ParseTree;
import org.cyphersim.CypherParser.ReadContext;

public class RLVisitor extends CypherBaseVisitor<LinkedList<Property>> {
	@Override
	public LinkedList<Property> visitRead(ReadContext ctx) {
		// TODO Auto-generated method stub
		return visit(ctx.returnClause().returnList()); 
	}
	
	public LinkedList<Property> visitReturnList(CypherParser.ReturnListContext ctx) {
		LinkedList<Property> ret = new LinkedList<Property>();
		for (ParseTree child : ctx.children) {
			if (child.getChildCount() != 0) ret.addAll(visit(child));
		}
		return ret;
	}
	
	public LinkedList<Property> visitProperty(CypherParser.PropertyContext ctx) {
		Property newProp = new Property(ctx.ID(0).getText(), null);
		int numIds = ctx.ID().size();
		if (numIds > 1) newProp.prop = ctx.ID(1).getText();
		LinkedList<Property> ret = new LinkedList<Property>();
		ret.add(newProp);
		return ret;
	}
}
