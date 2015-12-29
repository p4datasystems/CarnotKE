package wdb.metadata;

import java.util.ArrayList;

public class ModifyQuery extends UpdateQuery {
	public Integer limit;
	
	public ModifyQuery()
	{
		super();
		this.limit = 1;
	}
	
	public ModifyQuery(String className)
	{
		super(className);
		this.limit = 1;
	}
	
	public ModifyQuery(String className, ArrayList<Assignment> assignmentList)
	{
		super(className, assignmentList);
		this.limit = 1;
	}
}
