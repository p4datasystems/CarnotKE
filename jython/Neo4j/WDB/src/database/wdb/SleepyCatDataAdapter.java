/*
 * Created on Feb 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package wdb;

import wdb.metadata.*;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;

import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.SecondaryCursor;

import java.util.*;
/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SleepyCatDataAdapter {

	private Transaction txn;
	private SleepyCatDataBase scdb;
	
	public SleepyCatDataAdapter(SleepyCatDataBase scdb, Transaction txn)
	{
		this.scdb = scdb;
		this.txn = txn;
	}

	public void commit() throws Exception
	{
		this.txn.commit();
	}
	
	public void abort() throws Exception
	{
		this.txn.abort();
	}
	public void putClass(ClassDef classDef) throws Exception
	{
		EntryBinding keyBinding = new SerialBinding(this.scdb.getClassCatalog(), String.class);
		EntryBinding dataBinding = new SerialBinding(this.scdb.getClassCatalog(), ClassDef.class);
		
		DatabaseEntry theKey = new DatabaseEntry();
		keyBinding.objectToEntry(makeClassKey(classDef.name), theKey);
		
		DatabaseEntry theData = new DatabaseEntry();
		dataBinding.objectToEntry(classDef, theData);
		
		this.scdb.getClassDb().put(this.txn, theKey, theData);
	}
	
	public ClassDef getClass(String className) throws Exception
	{
		EntryBinding keyBinding = new SerialBinding(this.scdb.getClassCatalog(), String.class);
		EntryBinding dataBinding = new SerialBinding(this.scdb.getClassCatalog(), ClassDef.class);
		
		DatabaseEntry theKey = new DatabaseEntry();
		keyBinding.objectToEntry(makeClassKey(className), theKey);
		
	    DatabaseEntry theData = new DatabaseEntry();
	    
	    OperationStatus status;
	    status = this.scdb.getClassDb().get(this.txn, theKey, theData, LockMode.DEFAULT);
	    
	    if(status == OperationStatus.NOTFOUND)
	    {
	    	throw new ClassNotFoundException("Class \"" + className + "\" is not defined");
	    }
	    	
	    return (ClassDef)dataBinding.entryToObject(theData);
	}
	
	public void putObject(WDBObject object) throws Exception
	{
		EntryBinding keyBinding = new SerialBinding(this.scdb.getClassCatalog(), String.class);
		EntryBinding dataBinding = new SerialBinding(this.scdb.getClassCatalog(), WDBObject.class);
		
		DatabaseEntry theKey = new DatabaseEntry();
		keyBinding.objectToEntry(makeObjectKey(object.getClassName(), object.getUid()), theKey);
		
		DatabaseEntry theData = new DatabaseEntry();
		dataBinding.objectToEntry(object, theData);
		
		this.scdb.getObjectDb().put(null, theKey, theData);
	}
	
	public ArrayList<WDBObject> getObjects(IndexDef index, String key) throws Exception
	{
		EntryBinding keyBinding = new SerialBinding(this.scdb.getClassCatalog(), String.class);
		EntryBinding dataBinding = new SerialBinding(this.scdb.getClassCatalog(), WDBObject.class);
		
		DatabaseEntry theKey = new DatabaseEntry();
		keyBinding.objectToEntry(key, theKey);
		
	    DatabaseEntry theData = new DatabaseEntry();
	    
	    SecondaryCursor cursor = this.scdb.getSecDb(index).openSecondaryCursor(this.txn, null);
	    
	    OperationStatus status;
	    status = cursor.getSearchKey(theKey, theData, LockMode.DEFAULT);
	    
	    ArrayList<WDBObject> foundObjectsList = new ArrayList<WDBObject>();
	    while(status == OperationStatus.SUCCESS)
	    {
	    	foundObjectsList.add((WDBObject)dataBinding.entryToObject(theData));
	    	status = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
	    }
	    
	    cursor.close();
	    return foundObjectsList;
	}
	
	public WDBObject getObject(String className, Integer Uid) throws Exception
	{
		EntryBinding keyBinding = new SerialBinding(this.scdb.getClassCatalog(), String.class);
		EntryBinding dataBinding = new SerialBinding(this.scdb.getClassCatalog(), WDBObject.class);
		
		DatabaseEntry theKey = new DatabaseEntry();
		keyBinding.objectToEntry(makeObjectKey(className, Uid), theKey);
		
	    DatabaseEntry theData = new DatabaseEntry();
	    
	    OperationStatus status;
	    status = this.scdb.getObjectDb().get(null, theKey, theData, LockMode.DEFAULT);
	    
	    if(status == OperationStatus.NOTFOUND)
	    {
	    	throw new Exception("Object with UID " + Uid.toString() + " of class \"" + className + "\" does not exist");
	    }
	    	
	    return (WDBObject)dataBinding.entryToObject(theData);
	}
	
	private String makeClassKey(String className)
	{
		return this.scdb.getClassKeyPrefix()+":"+className;
	}
	
	private String makeObjectKey(String className, Integer Uid)
	{
		return this.scdb.getObjectKeyPrefix()+":"+Uid.toString();
	}
	/*
	public void NewClassDef(ClassDef classDef) throws Exception
	{
		int i;
		ArrayList superClasses = null;
		ClassDef superClass = null;
		
		//See if this class is alrealdy defined
		Integer oid = (Integer)GetMasterClassList().get(classDef.name);
		
		if(oid != null)
		{
			throw new Exception ("Class " + classDef.name + " is alreadly defined");
		}
		
		//If this is a subclass definition, we need to make sure all superclasses exists
		//and set their subclass array to include this class.
		//Also need to put all the parent's attributes in this hashtable so I can find it
		//easier
		if(classDef.getClass() == SubclassDef.class)
		{
			superClasses = new ArrayList();
			
			for(i = 0; i < ((SubclassDef)classDef).numberOfSuperClasses(); i++)
			{
				superClass = GetClassDef(((SubclassDef)classDef).getSuperClass(i));
				if(superClass != null)
				{
					superClass.addsubClass(classDef.name);
					superClasses.add(superClass);
				}
				else
				{
					throw new Exception("Superclass " + ((SubclassDef)classDef).getSuperClass(i) + " not defined");
				}
			}
		}
		
		//Compile this class
		classDef.setJavaClass(loader.loadClass("tmp" + classDef.getOid().toString(), classDef.generateJavaClass(), true));
	
			
		Key k = new Key(Key.metaDataType, classDef.getOid());
			
		EntryBinding keyBinding = new SerialBinding(dbEnv.getClassCatalog(), Key.class);
		EntryBinding dataBinding = new SerialBinding(dbEnv.getClassCatalog(), classDef.getClass());
	    
		DatabaseEntry theKey = new DatabaseEntry();
		keyBinding.objectToEntry(k, theKey);
			
	    DatabaseEntry theData = new DatabaseEntry();
	    dataBinding.objectToEntry(classDef, theData);

	    dbEnv.getDatabase().put(null, theKey, theData);
	    	
	    Hashtable newMcl = GetMasterClassList();
	    newMcl.put(classDef.name, classDef.getOid());
	    UpdateMasterClassList(newMcl);
	    	
	    if(superClasses != null)
	    {
	   		for(i = 0; i < superClasses.size(); i++)
	    	{
	    		UpdateClassDef((ClassDef)superClasses.get(i));
	    	}
	   	}
	}
	
	public ClassDef GetClassDef(String name) throws Exception
	{
		Integer oid = (Integer)GetMasterClassList().get(name);
		
		if(oid == null)
		{
			throw new Exception("Class " + name + " is not defined!");
		}
		
		Key k = new Key(Key.metaDataType, oid);
		
		EntryBinding keyBinding = new SerialBinding(dbEnv.getClassCatalog(), Key.class);
		EntryBinding dataBinding = new SerialBinding(dbEnv.getClassCatalog(), ClassDef.class);
    
		DatabaseEntry theKey = new DatabaseEntry();
		keyBinding.objectToEntry(k, theKey);
		
	    DatabaseEntry theData = new DatabaseEntry();
	    OperationStatus status;
	    status = dbEnv.getDatabase().get(null, theKey, theData, LockMode.DEFAULT);
	    	
	    if(status == OperationStatus.NOTFOUND)
	   	{
	   		throw new Exception("Class " + name + " can not be found");
	   	}
	    else
	    {
	    	ClassDef classDef;
	    	classDef = (ClassDef)dataBinding.entryToObject(theData);
	    	return classDef;
	    }
	}
	
	public boolean CheckSubclassMembership(ClassDef subClass, ClassDef superClass) throws Exception
	{
		if(subClass.name.equals(superClass.name))
		{
			return true;
		}
		else if(subClass.getClass() != SubclassDef.class)
		{
			return false;
		}
		else
		{
			SubclassDef sd = (SubclassDef)subClass;
			if(sd.isSubclassOf(superClass.name))
			{
				return true;
			}
			else
			{
				for(int i = 0; i < sd.numberOfSuperClasses(); i++)
				{
					ClassDef cd = GetClassDef(sd.getSuperClass(i));
					if(CheckSubclassMembership(cd, superClass))
					{
						return true;
					}
				}
				
				return false;
			}
		}
		
	}
	public Attribute GetAttribute(String attributeName, String className) throws Exception
	{
		ClassDef classDef = GetClassDef(className);
		Attribute attribute;
		int i = 0;
		
		attribute = classDef.getAttribute(attributeName);
		if(attribute == null)
		{
			if(classDef.getClass() == SubclassDef.class)
			{
				SubclassDef subclassDef = (SubclassDef)classDef;
				
				while(attribute == null && i < subclassDef.numberOfSuperClasses())
				{
					attribute = GetAttribute(attributeName, subclassDef.getSuperClass(i));
					i++;
				}
			}
			
		}
		return attribute;
	}
	
	public WDBObject GetWDBObject(Integer oid, String className) throws Exception
	{
		Key k = new Key(Key.userDataType, oid);
		
		Class javaClass;
		ClassDef classDef = GetClassDef(className);
		
		javaClass = classDef.getJavaClass();
		
		EntryBinding keyBinding = new SerialBinding(dbEnv.getClassCatalog(), Key.class);
		EntryBinding dataBinding = new SerialBinding(dbEnv.getClassCatalog(), javaClass);
    
		DatabaseEntry theKey = new DatabaseEntry();
		keyBinding.objectToEntry(k, theKey);
		
	    DatabaseEntry theData = new DatabaseEntry();
	    
    	OperationStatus status;
    	status = dbEnv.getDatabase().get(null, theKey, theData, LockMode.DEFAULT);
    	
    	if(status == OperationStatus.NOTFOUND)
    	{
    		return null;
    	}
    	else
    	{
    		return new WDBObject(classDef, dataBinding.entryToObject(theData), javaClass, new Hashtable(), new Hashtable(), false, this); 
    	}

	}
	
	/*
	public void UpdateWDBObject(WDBObject wdbO)
	{
		if(wdbO.dirty)
		{
			Key k = new Key(Key.userDataType, wdbO.getOid());
			
			EntryBinding keyBinding = new SerialBinding(dbEnv.getClassCatalog(), Key.class);
			EntryBinding dataBinding = new SerialBinding(dbEnv.getClassCatalog(), wdbO.GetJavaClass());
	    
			DatabaseEntry theKey = new DatabaseEntry();
			keyBinding.objectToEntry(k, theKey);
			
		    DatabaseEntry theData = new DatabaseEntry();
		    dataBinding.objectToEntry(wdbO.GetJavaObject(), theData);
	
		    try
		    {
		    	dbEnv.getDatabase().delete(null, theKey);
		    	dbEnv.getDatabase().put(null, theKey, theData);
		    	
		    	wdbO.dirty = false;
		    }
	    	catch(Exception e)
	    	{
	    		e.toString();
	    	}
		}
		
		Enumeration e;
		e = wdbO.dirtyParents.elements();
		while(e.hasMoreElements())
		{
			UpdateWDBObject((WDBObject)e.nextElement());
		}
		e = wdbO.dirtyPartners.elements();
		while(e.hasMoreElements())
		{
			UpdateWDBObject((WDBObject)e.nextElement());
		}
	}*/
	/*
	public void PutWDBObject(WDBObject wdbO) throws Exception
	{
		ClassDef classDef = GetClassDef(wdbO.getClassName());
		
		Key k = new Key(Key.userDataType, wdbO.getOid());
		
		EntryBinding keyBinding = new SerialBinding(dbEnv.getClassCatalog(), Key.class);
		EntryBinding dataBinding = new SerialBinding(dbEnv.getClassCatalog(), wdbO.GetJavaClass());
    
		DatabaseEntry theKey = new DatabaseEntry();
		keyBinding.objectToEntry(k, theKey);
		
	    DatabaseEntry theData = new DatabaseEntry();
	    dataBinding.objectToEntry(wdbO.GetJavaObject(), theData);
	    
	    DatabaseEntry theTempData = new DatabaseEntry();

    	OperationStatus status;
    	status = dbEnv.getDatabase().get(null, theKey, theTempData, LockMode.DEFAULT);
    	
    	if(status == OperationStatus.NOTFOUND)
    	{
			//Add this instance oid to the instance list for this class only if its not alreadly in DB
			classDef.addInstance(wdbO.getOid());
			//Update the class definition which now has the newly added instance oid
			UpdateClassDef(classDef);
    	}
    	else if(status == OperationStatus.SUCCESS)
    	{
    		//If it alreadly exists, delete the existing object before putting hte updated one.
    		dbEnv.getDatabase().delete(null, theKey);
    	}
    	
    	dbEnv.getDatabase().put(null, theKey, theData);
		
    	wdbO.dirty = false;
    	
		Enumeration e;
		e = wdbO.dirtyParents.elements();
		while(e.hasMoreElements())
		{
			PutWDBObject((WDBObject)e.nextElement());
		}
		e = wdbO.dirtyPartners.elements();
		while(e.hasMoreElements())
		{
			PutWDBObject((WDBObject)e.nextElement());
		}
	}
	
	public void UpdateClassDef(ClassDef classDef) throws Exception
	{
		Integer oid = (Integer)GetMasterClassList().get(classDef.name);
		
		if(oid.intValue() != classDef.getOid().intValue())
		{
			throw new Exception("Class " + classDef.name + " conflicts with another defined class!");
		}
		
		Key k = new Key(Key.metaDataType, classDef.getOid());
		
		EntryBinding keyBinding = new SerialBinding(dbEnv.getClassCatalog(), Key.class);
		EntryBinding dataBinding = new SerialBinding(dbEnv.getClassCatalog(), classDef.getClass());
    
		DatabaseEntry theKey = new DatabaseEntry();
		keyBinding.objectToEntry(k, theKey);
		
	    DatabaseEntry theData = new DatabaseEntry();
	    dataBinding.objectToEntry(classDef, theData);

	    dbEnv.getDatabase().delete(null, theKey);
	    dbEnv.getDatabase().put(null, theKey, theData);
	}
	
	public void Insert(String className, InsertQuery insertQuery) throws Exception
	{
		int i = 0;
		DvaAssignment dvaAssignment;
		EvaAssignment evaAssignment;
		WDBObject wdbO;
		
		if(insertQuery.fromClassName != null)
		{
			WDBObject parent = SearchWDBObject(insertQuery.getExpression(), insertQuery.fromClassName)[0];
			wdbO = NewInstance(className, null, parent);
		}
		else
		{
			wdbO = NewInstance(className, null, null);
		}

		for(i = 0; i < insertQuery.numberOfAssignments(); i++)
		{
			if(insertQuery.getAssignment(i).getClass() == DvaAssignment.class)
			{
				dvaAssignment = (DvaAssignment)insertQuery.getAssignment(i);
				if(!wdbO.SetDVAValue(dvaAssignment.AttributeName, dvaAssignment.Value, dvaAssignment.Index))
				{
					throw new Exception(dvaAssignment.AttributeName + " is not a valid attribute!");
				}

			}
			else if(insertQuery.getAssignment(i).getClass() == EvaAssignment.class)
			{
				evaAssignment = (EvaAssignment)insertQuery.getAssignment(i);
				if(!wdbO.SetEVARef_to(evaAssignment.AttributeName, evaAssignment.targetClass, evaAssignment.expression))
				{
					throw new Exception(evaAssignment.AttributeName + " is not a valid attribute!");
				}
			}
		}
		
		PutWDBObject(wdbO);
	}
	
	public WDBObject NewInstance(String className, Integer child, WDBObject parent) throws Exception
	{
		//Get the class definition
		ClassDef classDef = GetClassDef(className);
		int i = 0;
		int j = 0;
		Hashtable parents = new Hashtable();
		WDBObject wdbO = null;

		//System.out.println("Number of instances alreadly in db: " + classDef.numberOfInstances());
		//Get the java class and make a new instance of this object
		Class c = classDef.getJavaClass();
		Object o = c.newInstance();
		//Generate a new oid and set it to the new object and set it to the previous child's oid
		Integer oid = new Integer((new UID()).hashCode());
		c.getField("oid").set(o, oid);
		c.getField("child").set(o, child);
	
		//If this class was a subclass, recursively call newinstance for every parent and 
		//add the returned parent oids to the parent hashtable for this object.
		if(classDef.getClass() == SubclassDef.class)
		{
			if(parent != null)
			{
				if(((SubclassDef)classDef).isSubclassOf(parent.getClassName()))
				{
					if(parent.GetDVAValue("child") == null)
					{
						parent.dirty = true;
						parent.SetDVAValue("child", oid);
						((Hashtable)(c.getField("parents").get(o))).put(parent.getClassName(), parent.getOid());
						parents.put(parent.getOid(), parent);
					}
					else
					{
						throw new Exception(parent.getClassName() + " alreadly extended to " + className);
					}
				}
				else
				{
					throw new Exception(parent.getClassName() + " is not a superclass of " + className);
				}
				
			}
				
			for(i = 0; i < ((SubclassDef)classDef).numberOfSuperClasses(); i++)
			{
				if(parent == null || !((SubclassDef)classDef).getSuperClass(i).equalsIgnoreCase(parent.getClassName()))
				{
					parent = NewInstance(((SubclassDef)classDef).getSuperClass(i), oid, null);
					((Hashtable)(c.getField("parents").get(o))).put(((SubclassDef)classDef).getSuperClass(i), parent.getOid());
					parents.put(parent.getOid(), parent);
				}
			}
		}
	
		//Once thats all done, wrap the WDBObject wrapper around the new object
		wdbO = new WDBObject(classDef, o, c, parents, new Hashtable(), true, this);
	
		//Finally return the oid so the caller function can continue on its work
		return wdbO;
	}
	
	public WDBObject[] SearchWDBObject(SimpleNode expression, String className) throws Exception
	{
		ClassDef classDef = GetClassDef(className);
		ArrayList matches = new ArrayList();
		WDBObject[] matchesArray = new WDBObject[1];
		WDBObject wdbO = null;

		//Get all the instances of the desired class and loop through them
		for(int i = 0; i < classDef.numberOfInstances(); i++)
		{
			wdbO = GetWDBObject(classDef.getInstance(i), classDef.name);
			//If we got the object and it evals true from the WHERE clause...
			if(wdbO != null && expression.eval(this, wdbO))
			{
				matches.add(wdbO);
			}
		}
		
		return (WDBObject[])matches.toArray(matchesArray);
	}
*/
}
