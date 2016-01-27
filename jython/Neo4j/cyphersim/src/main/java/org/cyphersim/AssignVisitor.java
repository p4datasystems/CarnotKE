package org.cyphersim;

import java.util.LinkedList;
import java.util.List;

import org.cyphersim.CypherParser.AssignContext;
import org.cyphersim.CypherParser.GenericNodeContext;
import org.cyphersim.CypherParser.WritenodeContext;

public class AssignVisitor extends CypherBaseVisitor<List<Assign>> {

	@Override
	public List<Assign> visitWritenode(WritenodeContext ctx) {
		// TODO Auto-generated method stub
		return visit(ctx.create().node());
	}

	@Override
	public List<Assign> visitGenericNode(GenericNodeContext ctx) {
		List<Assign> ret = new LinkedList<Assign>();
		for (AssignContext ac : ctx.attrList().assign()) {
			ret.add(new Assign(ac.ID().getText(), ac.value().getText()));
		}
		return ret;
	}
	
	

}
