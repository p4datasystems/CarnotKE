package org.cyphersim;

import org.cyphersim.CypherParser.ReadContext;
import org.cyphersim.CypherParser.WriteedgeContext;
import org.cyphersim.CypherParser.WritenodeContext;


public class ClausesVisitor extends CypherBaseVisitor<String> {

	@Override
	public String visitRead(ReadContext ctx) {
		// TODO Auto-generated method stub
		return "read";
	}

	@Override
	public String visitWritenode(WritenodeContext ctx) {
		// TODO Auto-generated method stub
		return "writenode";
	}

	@Override
	public String visitWriteedge(WriteedgeContext ctx) {
		// TODO Auto-generated method stub
		return "writeedge";
	}

}
