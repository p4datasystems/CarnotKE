/*
 * Created on Mar 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.python.ReL.WDB.database.wdb.metadata;


import org.python.ReL.WDB.parser.generated.wdb.parser.SimpleNode;

import java.util.*;
/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RetrieveQuery implements Query {

	@Override
	public String getQueryName() {
		return this.className;
	}
	public String className;
	public SimpleNode expression;
	public ArrayList<AttributePath> attributePaths;
	
	public RetrieveQuery() 
	{
		attributePaths = new ArrayList<AttributePath>();
	}
	/**
	 * @param className
	 * @param expression
	 */
	public RetrieveQuery(String className, SimpleNode expression) {
		super();
		this.className = className;
		this.expression = expression;
		attributePaths = new ArrayList<AttributePath>();
	}
	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className The className to set.
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @return Returns the expression.
	 */
	public SimpleNode getExpression() {
		return expression;
	}
	/**
	 * @param expression The expression to set.
	 */
	public void setExpression(SimpleNode expression) {
		this.expression = expression;
	}
	public void addAttributePath(AttributePath attributePath)
	{
		this.attributePaths.add(attributePath);
	}
	public int numAttributePaths()
	{
		return attributePaths.size();
	}
	public AttributePath getAttributePath(int i)
	{
//System.out.println("----------------- AttributePath: " + (AttributePath)attributePaths.get(i));
		return (AttributePath)attributePaths.get(i);
	}
	public ArrayList getAttributePaths()
	{
//System.out.println("----------------- AttributePaths: " + this.attributePaths);
		return this.attributePaths;
	}
}
