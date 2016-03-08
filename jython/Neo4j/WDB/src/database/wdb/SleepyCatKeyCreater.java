/*
 * Created on Feb 8, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package wdb;

import wdb.metadata.WDBObject;
import wdb.metadata.IndexDef;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

import java.util.*;
/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SleepyCatKeyCreater implements SecondaryKeyCreator {
	private IndexDef index;
	private Database db;
	private StoredClassCatalog classCatalog;
	private EntryBinding dataBinding; 
	private EntryBinding keyBinding;
	private String objectKeyPrefix;
	
	public SleepyCatKeyCreater(IndexDef index, Database db, StoredClassCatalog classCatalog)
	{
		this.index = index;
		this.db = db;
		this.classCatalog = classCatalog;
		this.dataBinding = new SerialBinding(this.classCatalog, WDBObject.class);
		this.keyBinding = new SerialBinding(this.classCatalog, String.class);
		this.objectKeyPrefix = "object";
	}
	
	public boolean createSecondaryKey(SecondaryDatabase secDb,
			DatabaseEntry keyEntry, // From the primary
			DatabaseEntry dataEntry, // From the primary
			DatabaseEntry resultEntry) // set the key data on this.
			throws DatabaseException {
		
		String primaryKey = ((String)keyBinding.entryToObject(keyEntry));
		
		if(primaryKey != null && primaryKey.startsWith(this.objectKeyPrefix))
		{
			WDBObject object = ((WDBObject)dataBinding.entryToObject(dataEntry));
			if(object.getClassName().equals(index.className))
			{
				ArrayList dvas = index.getDvas();
				String key = null;
				for(int i = 0; i < dvas.size(); i++)
				{
					String value = object.getImmDvaValue(((String)dvas.get(i))).toString();
					if(key == null)
					{
						key = value;
					}
					else
					{
						key = key + ":" + value;
					}
				}
				
				this.keyBinding.objectToEntry(key, resultEntry);
				return true;
			}
		}
		return false;
	}
}
