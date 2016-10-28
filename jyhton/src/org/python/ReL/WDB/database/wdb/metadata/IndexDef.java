/*
 * Created on Feb 7, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.python.ReL.WDB.database.wdb.metadata;

import java.io.Serializable;
import java.util.*;
/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IndexDef implements Query, Serializable {

	@Override
	public String getQueryName() {
		return this.className;
	}
	public String name;
	public String className;
	public String comment;
	public Boolean unique;
	
	private ArrayList dvas;
	
	public IndexDef()
	{
		super();
		this.dvas = new ArrayList();
		this.unique = false;
	}
	
	public void addDva(String dva)
	{
		this.dvas.add(dva);
	}
	
	public ArrayList getDvas()
	{
		return this.dvas;
	}
	
	public void removeDva(String dva)
	{
		this.dvas.remove(dva);
	}
}
