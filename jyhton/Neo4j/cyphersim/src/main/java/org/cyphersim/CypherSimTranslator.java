package org.cyphersim;

import java.util.LinkedList;
import java.util.List;
import java.lang.String;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;


public class CypherSimTranslator {
	private RCVisitor rcVisitor;
	private RLVisitor rlVisitor;
	private WPVisitor wpVisitor;
	private ClausesVisitor clausesVisitor;
	private LabelVisitor labelVisitor;
	private AssignVisitor assignVisitor;
	private EVAModifiedVisitor evaModifiedVisitor;
	
	public CypherSimTranslator()
	{
		clausesVisitor = new ClausesVisitor();
		rcVisitor = new RCVisitor(); // RelationshipChain
		rlVisitor = new RLVisitor(); // ReturnList
		wpVisitor = new WPVisitor(); // WherePart
		labelVisitor = new LabelVisitor(); // Used in Create Statements
		assignVisitor = new AssignVisitor(); // Used in Create Statements
		evaModifiedVisitor = new EVAModifiedVisitor();  // Used in Create Edge Statements
		
	}

	public String translate(String query)
	{
		ParseTree tree = getParseTree(query);
		String mode = clausesVisitor.visit(tree);
		if (mode.equals("read")) { // Match Statements
		/* E.g., 
		MATCH (e:emp)-[:DEPTNO]->(d:dept)-[:ORGNUM]->(o:org) // 3 RCNodes with (e:emp)-[:DEPTNO] as the head
		WHERE e.empno = 111 // wherePart
		RETURN e.empno, e.ename, e.sal, d.dname, o.oname // return Part i.e., list with 5 Properties
		*/
			RCNode relationshipChain = rcVisitor.visit(tree);
			List<Property> returnList = rlVisitor.visit(tree);
			Expression wherePart = wpVisitor.visit(tree);
			return buildSIM(relationshipChain, returnList, wherePart);
		}
		else if (mode.equals("writenode")) { // Create Node Statements
			String insertClass = labelVisitor.visit(tree);
			List<Assign> assignList = assignVisitor.visit(tree);
			return buildSIM(insertClass, assignList);
		}
		else if (mode.equals("writeedge")) { // Create Edge Statements
			Entity entity = evaModifiedVisitor.visit(tree);
			return entity.translatedSIM();
		}
		return null;
	}

	private ParseTree getParseTree(String query)
	{
		ANTLRInputStream input = new ANTLRInputStream(query);
		CypherLexer lexer = new CypherLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CypherParser parser = new CypherParser(tokens);
		return parser.clauses();
	}
	
	private String buildSIM(RCNode relationshipChain,
			List<Property> returnList, Expression wherePart) {
		StringBuilder ret = new StringBuilder("FROM ");
		ret.append(getClass(relationshipChain));
		ret.append(" RETRIEVE ");
		ret.append(getAttributes(returnList, relationshipChain));
		if (wherePart != null) ret.append(getWhereClause(wherePart, relationshipChain));
		ret.append(";");
		return ret.toString(); 
	}
	
	private String buildSIM(String insertClass, List<Assign> assignList) {
		StringBuilder ret = new StringBuilder("INSERT ");
		ret.append(insertClass);
		ret.append(" ( ");
		for (Assign a : assignList) {
			a.value = a.value.replace('\'', '\"');
			ret.append(a.property + " := " + a.value + " , ");
		}
		ret.delete(ret.length() - 2, ret.length());
		ret.append(");");
		return ret.toString();
	}	
	
	private String getWhereClause(Expression wherePart, RCNode relationshipChain) {
		StringBuilder ret = new StringBuilder(" WHERE ");
		wherePart.value = wherePart.value.replace('\'','\"');
		LinkedList<Property> propList = new LinkedList<Property>();
		propList.add(wherePart.property);
		ret.append(getAttributes(propList, relationshipChain));
		ret.append(" ");
		ret.append(wherePart.comparison);
		ret.append(" ");
		ret.append(wherePart.value);
		return ret.toString();
		
	}

	private String getAttributes(List<Property> returnList,
			RCNode relationshipChain) {
		StringBuilder ret = new StringBuilder();
		String classId = relationshipChain.getIdentifier();
		if (classId != null){
			for (Property p : returnList) {
				if (p.nodeId.equals(classId)) {
					if (p.prop != null) {
						ret.append(p.prop.toUpperCase());
					}
					else {
						ret.append("*");
					}
				}
				else {
					if (p.prop != null) {
						ret.append(getAttributeChain(p, relationshipChain));
					}
					else {
						ret.append(getAttributeChain(new Property(p.nodeId, "*"), relationshipChain));
					}
				}
				ret.append(", ");
			}
			ret.delete(ret.length() - 2, ret.length());
		}
		return ret.toString();
	}
		

	private String getAttributeChain(Property p, RCNode relationshipChain) {
		StringBuilder ret = new StringBuilder(p.prop.toUpperCase());
		RCNode curr = relationshipChain;
		while (!curr.getIdentifier().equals(p.nodeId)) curr = curr.getNext();
		while (curr.getPrev() != null) {
			curr = curr.getPrev();
			ret.append(" OF ");
			ret.append(curr.getRelName());
		}
		return ret.toString();
	}

	private String getClass(RCNode relationshipChain) {
		if (relationshipChain.getLabel() != null) return relationshipChain.getLabel().toUpperCase();
		return "*no label given*";
	}
}
