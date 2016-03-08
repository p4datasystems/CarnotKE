package org.cyphersim;

import java.util.LinkedList;
import java.util.List;

public class Entity {
	public String modifiedID;
	public String insertedID;
	public String EVAName;
	public List<BoolExpression> whereClause;
	public List<LabelledNode> matchClause;
	
	public Entity(String mi, String ii, String e, List<BoolExpression> w, List<LabelledNode> m) {
		modifiedID = mi;
		insertedID = ii;
		EVAName = e;
		whereClause = w;
		matchClause = m;
	}
	
	public Entity() {
		modifiedID = null;
		insertedID = null;
		EVAName = null;
		whereClause = new LinkedList<BoolExpression>();
		matchClause = new LinkedList<LabelledNode>();
	}
	
	public String modifiedWherePart() {
		int i = 0;
		StringBuilder ret = new StringBuilder();
		while (i < whereClause.size()
				&& !whereClause.get(i).nodeID().equals(modifiedID)) i++;
		ret.append(whereClause.get(i).exp.toString());
		i++;
		while (i < whereClause.size()
				&& whereClause.get(i).nodeID().equals(modifiedID)) {
			ret.append(whereClause.get(i).toString());
			i++;
		}
		return ret.toString();
	}
	
	public String insertedWherePart() {
		int i = 0;
		StringBuilder ret = new StringBuilder();
		while (i < whereClause.size() 
				&& !whereClause.get(i).nodeID().equals(insertedID)) i++;
		ret.append(whereClause.get(i).exp.toString());
		i++;
		while ( i < whereClause.size() 
				&& whereClause.get(i).nodeID().equals(insertedID) ) {
			ret.append(whereClause.get(i).toString());
			i++;
		}
		return ret.toString();
	}
	
	public String modifiedClass() {
		if (matchClause.get(0).id.equals(modifiedID)) return matchClause.get(0).label;
		return matchClause.get(1).label;
	}
	
	public String insertedClass() {
		if (matchClause.get(0).id.equals(insertedID)) return matchClause.get(0).label;
		return matchClause.get(1).label;
	}
	
	public String translatedSIM() {
		String ret = "MODIFY LIMIT = ALL " 
				+ modifiedClass() 
				+ " ("
				+ EVAName
				+ " := "
				+ insertedClass()
				+ " WITH ("
				+ insertedWherePart()
				+ ")) WHERE "
				+ modifiedWherePart()
				+ ";";
		ret = ret.replace('\'', '\"');
		return ret;
	}
}
