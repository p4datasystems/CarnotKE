/*
 * Created on Apr 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package wdb.metadata;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AttributePath implements Serializable{
	public ArrayList path;
	public String attribute;
	public String className;
	public Integer index;
	
	/**
	 * 
	 */
	public AttributePath() {
		super();
		path = new ArrayList();
	}
	public void addIndirection(String eva)
	{
		path.add(eva);
	}
	public void removeIndirection(int i)
	{
		path.remove(i);
	}
	public void removeIndirection(String eva)
	{
		path.remove(eva);
	}
	public String getIndirection(int i)
	{
		return (String)path.get(i);
	}
	public ArrayList getPath()
	{
		return path;
	}
	public int levelsOfIndirection()
	{
		return path.size();
	}
	/**
	 * @return Returns the attribute.
	 */
	public String getAttribute() {
		return attribute;
	}
	/**
	 * @param attribute The attribute to set.
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
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
}
