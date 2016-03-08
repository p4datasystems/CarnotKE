/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package wdb.metadata;

import java.io.Serializable;

/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Assignment implements Serializable {
	public String AttributeName;
	public boolean assigned;

	public Assignment() {
		assigned = false;
	}

	/**
	 * @param attributeName
	 */
	public Assignment(String attributeName) {
		AttributeName = attributeName;
		assigned = false;
	}
	
	/**
	 * @return Returns the attributeName.
	 */
	public String getAttributeName() {
		return AttributeName;
	}
	/**
	 * @param attributeName The attributeName to set.
	 */
	public void setAttributeName(String attributeName) {
		AttributeName = attributeName;
	}
}
