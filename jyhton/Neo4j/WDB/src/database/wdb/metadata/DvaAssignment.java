/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package wdb.metadata;

/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DvaAssignment extends Assignment {
	public Integer Index;
	public Object Value;
	
	public DvaAssignment() {
		super();
	}
	/**
	 * @param attributeName
	 * @param value
	 */
	public DvaAssignment(String attributeName, String value, Integer index) {
		super(attributeName);
		Value = value;
		Index = index;
	}
}
