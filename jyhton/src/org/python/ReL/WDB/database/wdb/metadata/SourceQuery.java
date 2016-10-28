/*
 * Created on May 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.python.ReL.WDB.database.wdb.metadata;

import java.io.*;
import java.util.*;
/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SourceQuery implements Query, Serializable {
	public String filename;
	
	public SourceQuery()
	{
		super();
	}

	/**
	 * @return Returns the filename.
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename The filename to set.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public String getQueryName() {
		return null;
	}
}
