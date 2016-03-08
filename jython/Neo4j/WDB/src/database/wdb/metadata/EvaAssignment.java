/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package wdb.metadata;

import wdb.parser.*;

/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EvaAssignment extends Assignment {
	public static Integer REPLACE_MODE = 0;
	public static Integer INCLUDE_MODE = 1;
	public static Integer EXCLUDE_MODE = 2;
	public Integer mode;
	public String targetClass;
	public SimpleNode expression;
	
	public EvaAssignment() {
		super();
		mode = EvaAssignment.REPLACE_MODE;
	}

	/**
	 * @param include
	 * @param targetClass
	 * @param expression
	 */
	public EvaAssignment(String AttributeName, int include, String targetClass, SimpleNode expression) {
		super(AttributeName);
		this.mode = include;
		this.targetClass = targetClass;
		this.expression = expression;
		mode = EvaAssignment.REPLACE_MODE;
	}
}