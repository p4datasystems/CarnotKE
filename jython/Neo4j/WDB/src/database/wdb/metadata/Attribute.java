/*
 * Created on Feb 1, 2005
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
public class Attribute implements Serializable  {
	public String name;
	public String comment;
	public Boolean required;
	
	public Attribute()
	{
	}
	
	public Attribute(String _name, String _comment,Boolean _required)
	{
		name = _name;
		comment = _comment;
		required = _required;
	}
}
