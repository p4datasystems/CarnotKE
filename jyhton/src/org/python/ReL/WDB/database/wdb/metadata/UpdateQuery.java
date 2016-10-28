package org.python.ReL.WDB.database.wdb.metadata;


import org.python.ReL.WDB.parser.generated.wdb.parser.SimpleNode;

import java.util.ArrayList;


public class UpdateQuery implements Query {
	@Override
	public String getQueryName() {
		return this.className;
	}
	public String className;
	public SimpleNode expression;
	public ArrayList<Assignment> assignmentList;
	
	public UpdateQuery() {
		super();
		this.assignmentList = new ArrayList<Assignment>();
	}

	public UpdateQuery(String className)
	{
		super();
		this.className = className;
		this.assignmentList = new ArrayList<Assignment>();
	}
	
	public UpdateQuery(String className, ArrayList<Assignment> assignmentList)
	{
		super();
		this.className = className;
		this.assignmentList = assignmentList;
	}
	
	public SimpleNode getExpression() {
		return expression;
	}
	/**
	 * @param expression The expression that qualifies the objects from the FROM class.
	 */
	public void setExpression(SimpleNode expression) {
		this.expression = expression;
	}
	
	public void addAssignment(Assignment assignment)
	{
		this.assignmentList.add(assignment);
	}
	
	public void removeAssignment(Assignment assignment)
	{
		this.assignmentList.remove(assignment);
	}
	
	public void removeAssignment(int index)
	{
		this.assignmentList.remove(index);
	}
	
	public int numberOfAssignments()
	{
		return assignmentList.size();
	}
	
	public Assignment getAssignment(int index)
	{
		return (Assignment)assignmentList.get(index);
	}

}
