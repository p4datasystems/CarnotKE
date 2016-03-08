package org.cyphersim;

import java.util.LinkedList;
import java.util.List;

import org.cyphersim.CypherParser.BLabelEdgeContext;
import org.cyphersim.CypherParser.BackCreateContext;
import org.cyphersim.CypherParser.BackwardNodeContext;
import org.cyphersim.CypherParser.BoolExpressionContext;
import org.cyphersim.CypherParser.EdgeMatchContext;
import org.cyphersim.CypherParser.ExpressionContext;
import org.cyphersim.CypherParser.FLabelEdgeContext;
import org.cyphersim.CypherParser.ForwardCreateContext;
import org.cyphersim.CypherParser.ForwardNodeContext;
import org.cyphersim.CypherParser.IdentifierNodeContext;
import org.cyphersim.CypherParser.LabelNodeContext;
import org.cyphersim.CypherParser.NodeContext;
import org.cyphersim.CypherParser.WriteedgeContext;

public class EVAModifiedVisitor extends CypherBaseVisitor<Entity> {

	@Override
	public Entity visitWriteedge(WriteedgeContext ctx) {
		Entity ret = new Entity();
		ret = visit(ctx.edgeCreate());
		ret.whereClause = visit(ctx.where().boolExpression()).whereClause;
		ret.matchClause = visit(ctx.edgeMatch()).matchClause;
		return ret;
	}
	
	@Override
	public Entity visitForwardCreate(ForwardCreateContext ctx) {
		Entity ret = new Entity();
		ret.modifiedID = visit(ctx.forwardNode()).modifiedID;
		ret.EVAName = visit(ctx.forwardNode()).EVAName;
		ret.insertedID = visit(ctx.node()).modifiedID;
		return ret;
	}

	@Override
	public Entity visitForwardNode(ForwardNodeContext ctx) {
		Entity ret = new Entity();
		ret.modifiedID = visit(ctx.node()).modifiedID;
		ret.EVAName = visit(ctx.forwardEdge()).EVAName;
		return ret;
	}

	@Override
	public Entity visitBackwardNode(BackwardNodeContext ctx) {
		Entity ret = new Entity();
		ret.modifiedID = visit(ctx.node()).modifiedID;
		ret.EVAName = visit(ctx.backwardEdge()).EVAName;
		return ret;
	}

	@Override
	public Entity visitFLabelEdge(FLabelEdgeContext ctx) {
		return new Entity(null, null, ctx.ID(0).getText(), null, null);
	}

	@Override
	public Entity visitBLabelEdge(BLabelEdgeContext ctx) {
		return new Entity(null, null, ctx.ID(0).getText(), null, null);
	}

	@Override
	public Entity visitBackCreate(BackCreateContext ctx) {
		Entity ret = new Entity();
		ret.modifiedID = visit(ctx.backwardNode()).modifiedID;
		ret.EVAName = visit(ctx.backwardNode()).EVAName;
		ret.insertedID = visit(ctx.node()).modifiedID;
		return ret;
	}

	@Override
	public Entity visitIdentifierNode(IdentifierNodeContext ctx) {
		return new Entity(ctx.ID().getText(), null, null, null, null);
	}

	@Override
	public Entity visitBoolExpression(BoolExpressionContext ctx) {
		Entity ret = new Entity();
		int numChildren = ctx.getChildCount();
		ExpressionContext e0 = (ExpressionContext) ctx.getChild(0);
		Property newProp0 = new Property(e0.property().ID(0).getText(), e0.property().ID(1).getText());
		Expression newExp0 = new Expression(newProp0, e0.comparison().getText(), e0.value().getText());
		ret.whereClause.add(new BoolExpression("", newExp0));
		if (numChildren > 1)
		{
			for (int i = 2; i < numChildren; i += 2)
			{
				ExpressionContext e = (ExpressionContext) ctx.getChild(i);
				Property newProp = new Property(e.property().ID(0).getText(), e.property().ID(1).getText());
				Expression newExp = new Expression(newProp, e.comparison().getText(), e.value().getText());
				ret.whereClause.add(new BoolExpression(ctx.getChild(i-1).getText(), newExp));
			}
		}
		return ret;
	}

	@Override
	public Entity visitEdgeMatch(EdgeMatchContext ctx) {
		Entity ret = new Entity();
		for (NodeContext n : ctx.node())
		{
			ret.matchClause.add(visit(n).matchClause.get(0));
		}
		return ret;
	}

	@Override
	public Entity visitLabelNode(LabelNodeContext ctx) {
		List<LabelledNode> newNode = new LinkedList<LabelledNode>();
		newNode.add(new LabelledNode(ctx.ID(0).getText(), ctx.ID(1).getText()));
		return new Entity(null, null, null, null, newNode);
	}

}
