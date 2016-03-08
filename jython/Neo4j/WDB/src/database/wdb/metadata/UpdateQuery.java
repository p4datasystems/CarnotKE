package wdb.metadata;

import java.util.ArrayList;

import wdb.parser.SimpleNode;

public class UpdateQuery extends Query {
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
