package org.cyphersim;

import org.cyphersim.CypherParser.ReadContext;

public class RCVisitor extends CypherBaseVisitor<RCNode> {
	@Override
	public RCNode visitRead(ReadContext ctx) {
		// TODO Auto-generated method stub
		return visit(ctx.match().relationshipChain()); 
	}
	
	public RCNode visitForwardChain(CypherParser.ForwardChainContext ctx) {
		int childCount = ctx.getChildCount();
		if (childCount > 0) {
			int i = 0;
			RCNode head = visit(ctx.getChild(i));
			RCNode curr = head;
			while (i < childCount - 1) {
				i++;
				RCNode prev = curr;
				curr = visit(ctx.getChild(i));
				prev.setNext(curr);
				curr.setPrev(prev);
			}
			return head;
		}
		return null;
	}
	
	public RCNode visitBackwardChain(CypherParser.BackwardChainContext ctx) {
		int childCount = ctx.getChildCount();
		if (childCount > 0) {
			int i = childCount - 1;
			RCNode head = visit(ctx.getChild(i));
			RCNode curr = head;
			while (i > 0) {
				i--;
				RCNode prev = curr;
				curr = visit(ctx.getChild(i));
				prev.setNext(curr);
				curr.setPrev(prev);
			}
			return head;
		}
		return null;
	}
	
	public RCNode visitForwardNode(CypherParser.ForwardNodeContext ctx) {
		RCNode ret = visit(ctx.getChild(0));
		ret.setRelName(visit(ctx.getChild(1)).getRelName());
		return ret;
	}
	
	public RCNode visitBackwardNode(CypherParser.BackwardNodeContext ctx) {
		RCNode ret = visit(ctx.getChild(1));
		ret.setRelName(visit(ctx.getChild(0)).getRelName());
		return ret;
	}
	
	public RCNode visitLabelNode(CypherParser.LabelNodeContext ctx) {
		RCNode ret = new RCNode();
		ret.setIdentifier(ctx.ID(0).getText());
		ret.setLabel(ctx.ID(1).getText());
		return ret;
	}
	
	public RCNode visitIdentifierNode(CypherParser.IdentifierNodeContext ctx) {
		RCNode ret = new RCNode();
		ret.setIdentifier(ctx.ID().getText());
		return ret;
	}
	
	public RCNode visitFLabelEdge(CypherParser.FLabelEdgeContext ctx) {
		RCNode ret = new RCNode();
		int labelIDindex = ctx.ID().size() - 1;
		ret.setRelName(ctx.ID(labelIDindex).getText());
		return ret;
	}
	
	public RCNode visitBLabelEdge(CypherParser.BLabelEdgeContext ctx) {
		RCNode ret = new RCNode();
		int labelIDindex = ctx.ID().size() - 1;
		ret.setRelName(ctx.ID(labelIDindex).getText());
		return ret;
	}
}
