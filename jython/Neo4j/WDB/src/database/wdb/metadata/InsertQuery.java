/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package wdb.metadata;

import java.util.ArrayList;

/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InsertQuery extends UpdateQuery {
	public String fromClassName;
	
	public InsertQuery()
	{
		super();
	}
	
	public InsertQuery(String className)
	{
		super(className);
	}
	/**
	 * @param className
	 * @param assignmentList
	 */
	public InsertQuery(String className, ArrayList<Assignment> assignmentList)
	{
		super(className, assignmentList);
	}
	
	public String getFromClassName()
	{
		return fromClassName;
	}
	
	public void setFromClassName(String name)
	{
		this.fromClassName = name;
	}
}
