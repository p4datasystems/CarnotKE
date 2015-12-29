package org.cyphersim;

import java.util.List;

import org.cyphersim.CypherParser.GenericNodeContext;
import org.cyphersim.CypherParser.LabelContext;
import org.cyphersim.CypherParser.WritenodeContext;

public class LabelVisitor extends CypherBaseVisitor<String> {

	@Override
	public String visitWritenode(WritenodeContext ctx) {
		// TODO Auto-generated method stub
		return visit(ctx.create().node());
	}


	@Override
	public String visitGenericNode(GenericNodeContext ctx) {
		// TODO Auto-generated method stub
		List<LabelContext> labelList = ctx.label();
		return labelList.get(labelList.size() - 1).ID().toString();
	}
}
