/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package org.python.ReL.WDB.database.wdb.metadata;

import org.python.ReL.WDB.parser.generated.wdb.parser.SimpleNode;

import java.io.Serializable;
import java.util.*;

/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WDBObject implements Serializable {
	private String classDefName;
	private Integer Uid;

	private Hashtable<String, Integer> parents;
	private Hashtable<String, Integer> children;

	private Hashtable<String, Object> evaObjects;
	private Hashtable<String, Object> dvaValues;

	public WDBObject(Hashtable<String, Integer> parents, Hashtable<String, Integer> children, Hashtable<String, Object> evaObjects, Hashtable<String, Object> dvaValues, String classDefName, Integer Uid)
	{
		this.parents = parents;
		this.children = children;
		this.evaObjects = evaObjects;
		this.dvaValues = dvaValues;
		this.classDefName = classDefName;
		this.Uid = Uid;
	}

	public boolean equals(Object o)
	{
		if(o.getClass() == this.getClass() && ((WDBObject)o).Uid.equals(this.Uid))
		{
			return true;
		}

		return false;
	}
	public WDBObject getBaseObject(ParserAdapter scda) throws Exception
	{
		ClassDef myClass = this.getClassDef(scda);
		//See if its parent of this class
		if(myClass.getClass() == SubclassDef.class)
		{
			//Check my parents.
			Enumeration e = parents.keys();
			while(e.hasMoreElements())
			{
				String parentClass = (String)e.nextElement();
				Integer parentUid = (Integer)parents.get(parentClass);
				WDBObject parent = scda.getObject(parentClass, parentUid);
				WDBObject grandparent = parent.getBaseObject(scda);
				if(grandparent != null)
				{
					return grandparent;
				}
			}
			//Not any superClass of mine!
			return null;
		}
		else if(myClass.getClass() == ClassDef.class)
		{
			return this;
		}
		else
		{
			return null;
		}
	}
	public WDBObject getParentObject(String superClassName, ParserAdapter scda) throws Exception
	{
		ClassDef myClass = this.getClassDef(scda);
		//See if its parent of this class
		if(myClass.getClass() == SubclassDef.class)
		{
			//See if its one of my immediate superclasses.
			if(parents.containsKey(superClassName))
			{
				return scda.getObject(superClassName, ((Integer)parents.get(superClassName)));
			}
			//Not immediate superclass of me. Check my parents.
			Enumeration e = parents.keys();
			while(e.hasMoreElements())
			{
				String parentClass = (String)e.nextElement();
				Integer parentUid = (Integer)parents.get(parentClass);
				WDBObject parent = scda.getObject(parentClass, parentUid);
				WDBObject grandparent = parent.getParentObject(superClassName, scda);
				if(grandparent != null)
				{
					return grandparent;
				}
			}
			//Not any superClass of mine!
			return null;
		}
		else
		{
			return null;
		}
	}

	public void addParentObject(WDBObject parent) throws Exception
	{
		this.parents.put(parent.getClassName(), parent.getUid());
	}

	public void removeParentObject(WDBObject parent) throws Exception
	{
		this.parents.remove(parent.getClassName());
	}

	public void removeParentObject(String superclassName) throws Exception
	{
		this.parents.remove(superclassName);
	}

	public void addChildObject(WDBObject child) throws Exception
	{
		if(!children.containsKey(child.getClassName()))
		{
			this.children.put(child.getClassName(), child.getUid());
		}
		else
		{
			throw new ClassCastException("This entity of class \"" + this.classDefName + "\" has alreadly been extended to class \"" + child.getClassName() + "\"");
		}
	}

	public WDBObject getChildObject(String subClassName, ParserAdapter scda) throws Exception
	{
		//See if its one of my immediate subclasses.
		if(children.containsKey(subClassName))
		{
			return scda.getObject(subClassName, ((Integer)children.get(subClassName)));
		}
		else
		{
			return null;
		}
	}

	public boolean hasChildObjectOfClass(String className)
	{
		return children.containsKey(className);
	}

	public void removeChildObject(WDBObject child) throws Exception
	{
		this.children.remove(child.getClassName());
	}

	public void removeChildObject(String subclassName) throws Exception
	{
		this.children.remove(subclassName);
	}
	public void commit(ParserAdapter scda) throws Exception
	{
		scda.putObject(this);
	}
	public ClassDef getClassDef(ParserAdapter scda) throws Exception
	{
		return scda.getClass(this.classDefName);
	}

	public Object getImmDvaValue(String dvaName)
	{
		return this.dvaValues.get(dvaName);
	}
	public Object getDvaValue(String dvaName, ParserAdapter scda) throws Exception
	{
		Object value = null;

		//See if its immediate in this class
		ClassDef myClass = this.getClassDef(scda);
		Attribute myAttribute = myClass.getAttribute(dvaName);
		if(myAttribute != null && myAttribute.getClass() == DVA.class)
		{
			value = this.dvaValues.get(dvaName);
		}
		//Not immediate, go check parents if I'm an object of a subclass
		else if(myClass.getClass() == SubclassDef.class)
		{
			Enumeration e = parents.keys();
			while(true)
			{
				try
				{
					String parentClass = (String)e.nextElement();
					Integer parentUid = (Integer)parents.get(parentClass);
					WDBObject parent = scda.getObject(parentClass, parentUid);

					value = parent.getDvaValue(dvaName, scda);
					//If we reached here, we got our value. Get out of here
					break;
				}
				catch(NoSuchFieldException nsfe)
				{
					//Can't find DVA going up that parent's heirarchy. Continue on to another parent
					if(!e.hasMoreElements())
					{
						//No more parents to try. We are here if we exausted our parent's list and
						//still can't find the DVA. Throw exception
						throw nsfe;
					}
				}
			}
		}
		//value can't be found and I'm not a subclass. It doesn't exist in this heirarchy
		else
		{
			throw new NoSuchFieldException("Attribute \"" + dvaName + "\" is not a valid DVA");
		}

		return value;
	}

	public ArrayList<String> getDvaNames()
	{
		ArrayList<String> dvaNames = new ArrayList<String>();
		for (String name : dvaValues.keySet())
		{
			dvaNames.add(name);
		}
		return dvaNames;

	}

	public ArrayList<String> getDvaValues()
	{
		ArrayList<String> dvaValsAsStrings = new ArrayList<String>();
		for (Object dvaVal : dvaValues.values())
		{
			dvaValsAsStrings.add(dvaVal.toString());
		}
		return dvaValsAsStrings;
	}

	public void setDvaValue(String dvaName, Object value, ParserAdapter scda) throws Exception
	{
		//See if its immediate in this class
        ClassDef myClass = this.getClassDef(scda);
		Attribute myAttribute = myClass.getAttribute(dvaName);
		if(myAttribute != null && myAttribute.getClass() == DVA.class)
		{
			if(value instanceof String)
			{
				this.dvaValues.put(dvaName, value);
			}
			else if(value instanceof Integer)
			{
				this.dvaValues.put(dvaName, value);
			}
			else if(value instanceof Boolean)
			{
				this.dvaValues.put(dvaName, value);
			}
			else
			{
				throw new ClassCastException("Type verification failed: Attribute \"" + dvaName + "\" type: " + ((DVA)myAttribute).type.toString() + ", Value type: " + value.getClass().toString());
			}
		}
		//Not immediate, go check parents if I'm an object of a subclass
		else if(myClass.getClass() == SubclassDef.class)
		{
			Enumeration e = parents.keys();
			while(true)
			{
                if(parents.keySet().isEmpty()) {
                    break;
                }
				try
				{
					String parentClass = (String)e.nextElement();
					Integer parentUid = (Integer)parents.get(parentClass);
					WDBObject parent = scda.getObject(parentClass, parentUid);
					parent.setDvaValue(dvaName, value, scda);
					//If we reached here, we set our value. Get out of here
					break;
				}
				catch(NoSuchFieldException nsfe)
				{
					//Can't find DVA going up that parent's heirarchy. Continue on to another parent
					if(!e.hasMoreElements())
					{
						//No more parents to try. We are here if we exausted our parent's list and
						//still can't find the DVA. Throw exception
						throw nsfe;
					}
				}
			}
		}
		//value can't be found and I'm not a subclass. It doesn't exist in this heirarchy
		else
		{
			throw new NoSuchFieldException("Attribute \"" + dvaName + "\" is not a valid DVA");
		}


		this.commit(scda);
	}

	public void addEvaObjects(String evaName, String targetClass, SimpleNode expression, ParserAdapter scda) throws Exception
	{
		ClassDef myClass = this.getClassDef(scda);
		Attribute myAttribute = myClass.getAttribute(evaName);
		if(myAttribute != null && myAttribute.getClass() == EVA.class)
		{
			ClassDef targetClassDef = scda.getClass(targetClass);
			if(targetClassDef.name.equals(((EVA)myAttribute).baseClassName) ||
					targetClassDef.isSubclassOf((((EVA)myAttribute).baseClassName), scda))
			{
				ClassDef baseClassDef = scda.getClass(((EVA)myAttribute).baseClassName);
				WDBObject baseObject;
				WDBObject[] matchingObjs = targetClassDef.search(expression, scda);
				for(int i = 0; i < matchingObjs.length; i++)
				{
					this.addEvaObject((EVA)myAttribute, matchingObjs[i], scda);
					//Enforce referential integrity by adding a reference to myself
					//in the other object
					//Get the object that belongs to the base class of the EVA
					if((((EVA)myAttribute).baseClassName).equals(targetClassDef.name))
					{
						baseObject = matchingObjs[i];
					}
					else
					{
						baseObject = matchingObjs[i].getParentObject((((EVA)myAttribute).baseClassName), scda);
					}
					//Get the inverse EVA, if there is one
					Attribute inverseAttribute = baseClassDef.getAttribute(((EVA)myAttribute).inverseEVA);
					//Put myself as a reference in the target's EVA
					if(inverseAttribute != null && inverseAttribute.getClass() == EVA.class)
					{
						baseObject.addEvaObject((EVA)inverseAttribute, this, scda);
					}
					else
					{
						throw new Exception("Implication of target class inverse EVAs is not implemented");
					}
					//Update the object
					baseObject.commit(scda);

				}
			}
			else
			{
				throw new ClassCastException("The class of referenced object \"" + targetClass + "\" is not a subclass of " + ((EVA)myAttribute).baseClassName);
			}
		}
		//Not immediate, go check parents if I'm an object of a subclass
		else if(myClass.getClass() == SubclassDef.class)
		{
			Enumeration e = parents.keys();
			while(true)
			{
				try
				{
					String parentClass = (String)e.nextElement();
					Integer parentUid = (Integer)parents.get(parentClass);
					WDBObject parent = scda.getObject(parentClass, parentUid);

					parent.addEvaObjects(evaName, targetClass, expression, scda);
					//If we reached here, we set our value. Get out of here
					break;
				}
				catch(NoSuchFieldException nsfe)
				{
					//Can't find DVA going up that parent's heirarchy. Continue on to another parent
					if(!e.hasMoreElements())
					{
						//No more parents to try. We are here if we exausted our parent's list and
						//still can't find the DVA. Throw exception
						throw nsfe;
					}
				}
			}
		}
		//value can't be found and I'm not a subclass. It doesn't exist in this heirarchy
		else
		{
			throw new NoSuchFieldException("Attribute \"" + evaName + "\" is not a valid EVA");
		}

		this.commit(scda);
	}

	public void removeEvaObjects(String evaName, String targetClass, SimpleNode expression, ParserAdapter scda) throws Exception
	{
		ClassDef targetClassDef = scda.getClass(targetClass);
		WDBObject[] matchingObjs = targetClassDef.search(expression, scda);
		removeEvaObjects(evaName, targetClass, matchingObjs, scda);
	}

	public void removeEvaObjects(String evaName, String targetClass, WDBObject[] targetObjects, ParserAdapter scda) throws Exception
	{
		//Don't do anything if the target objects to remove is null
		if(targetObjects != null)
		{
			ClassDef myClass = this.getClassDef(scda);
			Attribute myAttribute = myClass.getAttribute(evaName);
			if(myAttribute != null && myAttribute.getClass() == EVA.class)
			{
				ClassDef targetClassDef = scda.getClass(targetClass);
				if(targetClassDef.name.equals(((EVA)myAttribute).baseClassName) ||
						targetClassDef.isSubclassOf((((EVA)myAttribute).baseClassName), scda))
				{
					ClassDef baseClassDef = scda.getClass(((EVA)myAttribute).baseClassName);
					WDBObject baseObject;
					for(int i = 0; i < targetObjects.length; i++)
					{
						//See if we can remove it from our end first
						if(this.removeEvaObject((EVA)myAttribute, targetObjects[i], scda))
						{
							//Get the object that belongs to the base class of the EVA
							if((((EVA)myAttribute).baseClassName).equals(targetClassDef.name))
							{
								baseObject = targetObjects[i];
							}
							else
							{
								baseObject = targetObjects[i].getParentObject((((EVA)myAttribute).baseClassName), scda);
							}
							//Get the inverse EVA, if there is one
							Attribute inverseAttribute = baseClassDef.getAttribute(((EVA)myAttribute).inverseEVA);
							//Put myself as a reference in the target's EVA
							if(inverseAttribute != null && inverseAttribute.getClass() == EVA.class)
							{
								baseObject.removeEvaObject((EVA)inverseAttribute, this, scda);
							}
							else
							{
								throw new Exception("Implication of target class inverse EVAs is not implemented");
							}
							//Update the object
							baseObject.commit(scda);
						}
					}
				}
			}
			//Not immediate, go check parents if I'm an object of a subclass
			else if(myClass.getClass() == SubclassDef.class)
			{
				Enumeration e = parents.keys();
				while(true)
				{
					try
					{
						String parentClass = (String)e.nextElement();
						Integer parentUid = (Integer)parents.get(parentClass);
						WDBObject parent = scda.getObject(parentClass, parentUid);

						parent.removeEvaObjects(evaName, targetClass, targetObjects, scda);
						//If we reached here, we set our value. Get out of here
						break;
					}
					catch(NoSuchFieldException nsfe)
					{
						//Can't find DVA going up that parent's heirarchy. Continue on to another parent
						if(!e.hasMoreElements())
						{
							//No more parents to try. We are here if we exausted our parent's list and 
							//still can't find the DVA. Throw exception
							throw nsfe;
						}
					}
				}
			}
			//value can't be found and I'm not a subclass. It doesn't exist in this heirarchy
			else
			{
				throw new NoSuchFieldException("Attribute \"" + evaName + "\" is not a valid EVA");
			}

			this.commit(scda);
		}
	}

	private void addEvaObject(EVA targetEva, WDBObject targetEvaObject, ParserAdapter scda) throws Exception
	{
		//TODO: Make sure we update the objects that we removed when replacing with new values
		if(targetEva.cardinality.equals(EVA.MULTIVALUED))
		{
			ArrayList<String> targetObjectList = ((ArrayList)this.evaObjects.get(targetEva.name));
			if(targetObjectList == null)
			{
				targetObjectList = new ArrayList<String>();
			}
			targetObjectList.add(new String(targetEvaObject.classDefName + ":" + targetEvaObject.getUid()));
			this.evaObjects.put(targetEva.name, targetObjectList);
		}
		else if(targetEva.cardinality.equals(EVA.SINGLEVALUED))
		{
			//For singlevalued EVAs, just put the UID as the value;
			this.evaObjects.put(targetEva.name, new String(targetEvaObject.classDefName + ":" + targetEvaObject.getUid()));
		}
		else
		{
			throw new NoSuchFieldException("Attribute \"" + targetEva.name + "\" uses a invalid cardinality");
		}
	}

	private Boolean removeEvaObject(EVA targetEva, WDBObject targetEvaObject, ParserAdapter scda) throws Exception
	{
		if(targetEva.cardinality.equals(EVA.MULTIVALUED))
		{
			ArrayList targetObjectList = ((ArrayList)this.evaObjects.get(targetEva.name));
			if(targetObjectList == null)
			{
				return false;
			}
			Boolean result = targetObjectList.remove(new String(targetEvaObject.classDefName + ":" + targetEvaObject.getUid()));
			this.evaObjects.put(targetEva.name, targetObjectList);
			return result;
		}
		else if(targetEva.cardinality.equals(EVA.SINGLEVALUED))
		{
			//For singlevalued EVAs, just put the UID as the value;
			Boolean result = (this.evaObjects.remove(targetEva.name)) != null;
			return result;
		}
		else
		{
			throw new NoSuchFieldException("Attribute \"" + targetEva + "\" uses a invalid cardinality");
		}
	}
	/*
        private void addEvaInverse(EVA ownerEva, ClassDef ownerEvaClass, WDBObject ownerEvaObject, ParserAdapter scda) throws Exception
        {
            //Try to see if other class's inverse EVA is declared on my side
            ClassDef myClass = this.getClassDef(scda);
            Attribute myAttribute = myClass.getAttribute(ownerEva.inverseEVA);
            if(myAttribute != null && myAttribute.getClass() == EVA.class)
            {
                //Oh it is, good. Use the declared settings
                if(((EVA)myAttribute).cardinality == EVA.MULTIVALUED)
                {
                    ArrayList targetObjectList = ((ArrayList)this.evaObjects.get(myAttribute.name));
                    if(targetObjectList == null)
                    {
                        targetObjectList = new ArrayList();
                    }
                    targetObjectList.add(ownerEvaObject.getUid());
                    this.evaObjects.put(myAttribute.name, targetObjectList);
                }
                else if(((EVA)myAttribute).cardinality == EVA.SINGLEVALUED)
                {
                    //For singlevalued EVAs, just put the UID as the value;
                    this.evaObjects.put(myAttribute.name, ownerEvaObject.getUid());
                }
                else
                {
                    throw new NoSuchFieldException("Attribute \"" + myAttribute.name + "\" uses a invalid cardinality");
                }
            }
            else
            {
                throw new Exception("Implication of target class inverse EVAs is not implemented");

                //The inverse EVA is not defined in this class or there is no inverse EVA specified
                //We have to infer the cardinality of our inverse based on the cardinality EVA from the owner's class
                if(ownerEva.cardinality == EVA.SINGLEVALUED)
                {
                    //Imply MV UNIQUE
                    if(myAttribute != null)
                    {
                        ArrayList targetObjectList = ((ArrayList)this.evaObjects.get(myAttribute.name));
                        if(targetObjectList == null)
                        {
                            targetObjectList = new ArrayList();
                        }
                        targetObjectList.add(ownerEvaObject.getUid());
                    }
                }

            }

            this.commit(scda);
        }
        */
	public WDBObject[] getEvaObjects(String evaName, ParserAdapter scda) throws Exception
	{
		ClassDef myClass = this.getClassDef(scda);
		Attribute myAttribute = myClass.getAttribute(evaName);

		WDBObject[] evaObjects = new WDBObject[0];
		if(myAttribute != null && myAttribute.getClass() == EVA.class)
		{
			if(((EVA)myAttribute).cardinality.equals(EVA.MULTIVALUED))
			{
				ArrayList<Object> targetObjectList = ((ArrayList)this.evaObjects.get(evaName));
				ArrayList<WDBObject> targetObjects = new ArrayList<WDBObject>();
				if(targetObjectList != null)
				{
					for(int i = 0; i < targetObjectList.size(); i++)
					{
						String[] targetKey = ((String)targetObjectList.get(i)).split(":");
						String targetClass = targetKey[0];
						Integer targetUid = Integer.valueOf(targetKey[1]);
						WDBObject targetObject = scda.getObject(targetClass, targetUid);
						targetObjects.add(targetObject);
					}
					evaObjects = ((WDBObject[])targetObjects.toArray(evaObjects));
				}
			}
			else if(((EVA)myAttribute).cardinality.equals(EVA.SINGLEVALUED))
			{
				if(this.evaObjects.get(evaName) != null)
				{
					String[] targetKey = ((String)this.evaObjects.get(evaName)).split(":");
					String targetClass = targetKey[0];
					Integer targetUid = Integer.valueOf(targetKey[1]);
					WDBObject targetObject = scda.getObject(targetClass, targetUid);
					if(targetObject != null)
					{
						WDBObject[] targetObjs = {((WDBObject)targetObject)};
						evaObjects = targetObjs;
					}
				}
			}
			else
			{
				throw new NoSuchFieldException("Attribute \"" + evaName + "\" uses a invalid cardinality");
			}

		}
		else if(myClass.getClass() == SubclassDef.class)
		{
			Enumeration e = parents.keys();
			while(true)
			{
				try
				{
					String parentClass = (String)e.nextElement();
					Integer parentUid = (Integer)parents.get(parentClass);
					WDBObject parent = scda.getObject(parentClass, parentUid);

					return parent.getEvaObjects(evaName, scda);
					//If we reached here, we get our value. Get out of here
					//break;
				}
				catch(NoSuchFieldException nsfe)
				{
					//Can't find DVA going up that parent's heirarchy. Continue on to another parent
					if(!e.hasMoreElements())
					{
						//No more parents to try. We are here if we exausted our parent's list and 
						//still can't find the DVA. Throw exception
						throw nsfe;
					}
				}
			}
		}
		//value can't be found and I'm not a subclass. It doesn't exist in this heirarchy
		else
		{
			throw new NoSuchFieldException("Attribute \"" + evaName + "\" is not a valid EVA");
		}

		return evaObjects;
	}
	
	/*
	public String GetEVATarget_class(String name)
	{
		String target_class = null;
		
		//See if its in this class
		try
		{
			java.lang.reflect.Field field = javaClass.getField(name + "_target_class");
			target_class = (String)field.get(javaObject);
			if(target_class == null)
			{
				return "null";
			}
		}
		catch(Exception e)
		{
			if(e.getClass() == NoSuchFieldException.class && classDef.getClass() == SubclassDef.class)
			{
				SubclassDef subclassDef = (SubclassDef)classDef;
				WDBObject parent;
				Hashtable parents;
				int i = 0;
				
				try
				{
					parents = (Hashtable)javaClass.getField("parents").get(javaObject);
				
					while(target_class == null && i < subclassDef.numberOfSuperClasses())
					{
						//First check my dirtyparents list
						if(dirtyParents.contains(parents.get(subclassDef.getSuperClass(i))))
						{
							parent = (WDBObject)dirtyParents.get(parents.get(subclassDef.getSuperClass(i)));
							target_class = parent.GetEVATarget_class(name);
						}
						//Its not in my dirty list. So load it
						else
						{
							parent = scda.GetWDBObject((Integer)parents.get(subclassDef.getSuperClass(i)), subclassDef.getSuperClass(i));
							target_class = parent.GetEVATarget_class(name);
						}
						
						i++;
					}
				
				}
				catch(Exception ee)
				{
					System.out.println(ee.toString());
				}
			}
		}
		
		return target_class;
	}
	public Integer[] GetEVARef_all(String name)
	{
			Integer[] ref_to = GetEVARef_to(name);
			Integer[] ref_from = GetEVARef_from(name);
			int i;
			
			if(ref_to == null || ref_from == null)
			{
				return null;
			}
			
			Integer[] ref_all = new Integer[ref_to.length + ref_from.length];
			
			for(i = 0; i < ref_to.length; i++)
			{
					ref_all[i] = ref_to[i];
			}
			for(; i < ref_all.length; i++)
			{
				ref_all[i] = ref_from[i];
			}
			
			return ref_all;
	}
	public Integer[] GetEVARef_to(String name)
	{
		Integer[] ref_to = null;
		
		//See if its in this class
		try
		{
			java.lang.reflect.Field field = javaClass.getField(name + "_ref_to");
			ref_to = (Integer[])field.get(javaObject);
			if(ref_to == null)
			{
				return new Integer[0];
			}
		}
		catch(Exception e)
		{
			if(e.getClass() == NoSuchFieldException.class && classDef.getClass() == SubclassDef.class)
			{
				SubclassDef subclassDef = (SubclassDef)classDef;
				WDBObject parent;
				Hashtable parents;
				int i = 0;
				
				try
				{
					parents = (Hashtable)javaClass.getField("parents").get(javaObject);
				
					while(ref_to == null && i < subclassDef.numberOfSuperClasses())
					{
						//First check my dirtyparents list
						if(dirtyParents.contains(parents.get(subclassDef.getSuperClass(i))))
						{
							parent = (WDBObject)dirtyParents.get(parents.get(subclassDef.getSuperClass(i)));
							ref_to = parent.GetEVARef_to(name);
						}
						//Its not in my dirty list. So load it
						else
						{
							parent = scda.GetWDBObject((Integer)parents.get(subclassDef.getSuperClass(i)), subclassDef.getSuperClass(i));
							ref_to = parent.GetEVARef_to(name);
						}
						
						i++;
					}
				
				}
				catch(Exception ee)
				{
					System.out.println(ee.toString());
				}
			}
		}
		
		return ref_to;
	}
	public Integer[] GetEVARef_from(String name)
	{
		Integer[] ref_from = null;
		
		//See if its in this class
		try
		{
			java.lang.reflect.Field field = javaClass.getField(name + "_ref_from");
			ref_from = (Integer[])field.get(javaObject);
			if(ref_from == null)
			{
				return new Integer[0];
			}
		}
		catch(Exception e)
		{
			if(e.getClass() == NoSuchFieldException.class && classDef.getClass() == SubclassDef.class)
			{
				SubclassDef subclassDef = (SubclassDef)classDef;
				WDBObject parent;
				Hashtable parents;
				int i = 0;
				
				try
				{
					parents = (Hashtable)javaClass.getField("parents").get(javaObject);
				
					while(ref_from == null && i < subclassDef.numberOfSuperClasses())
					{
						//First check my dirtyparents list
						if(dirtyParents.contains(parents.get(subclassDef.getSuperClass(i))))
						{
							parent = (WDBObject)dirtyParents.get(parents.get(subclassDef.getSuperClass(i)));
							ref_from = parent.GetEVARef_from(name);
						}
						//Its not in my dirty list. So load it
						else
						{
							parent = scda.GetWDBObject((Integer)parents.get(subclassDef.getSuperClass(i)), subclassDef.getSuperClass(i));
							ref_from = parent.GetEVARef_from(name);
						}
						
						i++;
					}
				
				}
				catch(Exception ee)
				{
					System.out.println(ee.toString());
				}
			}
		}
		
		return ref_from;
	}
	public boolean AddEVARef_from(String name, Integer oid)
	{
		boolean success = false;
		
		try
		{
			java.lang.reflect.Field field = javaClass.getField(name + "_ref_from");
			Integer[] ref_from = (Integer[])field.get(javaObject);
			
			if(ref_from == null)
			{
				ref_from = new Integer[0];
			}
			int i = ref_from.length;
			Integer ref_from_new[] = new Integer[i + 1];
		    System.arraycopy(ref_from, 0, ref_from_new, 0, i);

		    ref_from_new[i] = oid;
		    
		    field.set(javaObject, ref_from_new);
		    
			success = true;
			dirty = true;
		}
		catch(Exception e)
		{			
			if(e.getClass() == NoSuchFieldException.class && classDef.getClass() == SubclassDef.class)
			{
				SubclassDef subclassDef = (SubclassDef)classDef;
				WDBObject parent;
				Hashtable parents;
				int i = 0;
				
				try
				{
					parents = (Hashtable)javaClass.getField("parents").get(javaObject);
				
					while(success == false && i < subclassDef.numberOfSuperClasses())
					{
						//First check my dirtyparents list
						if(dirtyParents.containsKey(parents.get(subclassDef.getSuperClass(i))))
						{
							parent = (WDBObject)dirtyParents.get(parents.get(subclassDef.getSuperClass(i)));
							if(parent.AddEVARef_from(name, oid))
							{
								success = true;
							}
						}
						//Its not in my dirty list. So load it
						else
						{
							parent = scda.GetWDBObject((Integer)parents.get(subclassDef.getSuperClass(i)), subclassDef.getSuperClass(i));
							if(parent.AddEVARef_from(name, oid))
							{
								dirtyPartners.put(parents.get(subclassDef.getSuperClass(i)), parent);
								success = true;
							}
						}
						i++;
					}
				
				}
				catch(Exception ee)
				{
					System.out.println(ee.toString());
				}
			}
		}
		
		return success;
	}
	public boolean SetEVARef_to(String name, String targetClass, SimpleNode exp)
	{
		boolean success = false;
		
		//First make sure we have an immediate eva with the name
		Attribute attribute = classDef.getAttribute(name);
		if(attribute != null && attribute.getClass() == EVA.class)
		{
			EVA eva = (EVA)attribute;
			java.lang.reflect.Field expression;
			java.lang.reflect.Field target_class;
			java.lang.reflect.Field ref_to;
			java.lang.reflect.Field ref_from;
			try
			{
				expression = javaClass.getField(name + "_expression");
				target_class = javaClass.getField(name + "_target_class");
				ref_to = javaClass.getField(name + "_ref_to");
				ref_from = javaClass.getField(name + "_ref_from");
			
				//Make sure targetClass is a subclass of the class defined for the EVA
				ClassDef tc = scda.GetClassDef(targetClass);
				ClassDef cd = scda.GetClassDef(eva.baseClassName);
				
				if(tc == null)
				{
					System.out.println("Target class not defined!");
					return false;
				}
				if(cd == null)
				{
					System.out.println("EVA class not defined!");
					return false;
				}
				
				if(scda.CheckSubclassMembership(tc, cd))
				{
					//Passed all checks. Now set the expression in the java object.
					expression.set(javaObject, exp);
					target_class.set(javaObject,targetClass);
					ArrayList references = new ArrayList();
					WDBObject partner;

					for(int i = 0; i < tc.numberOfInstances(); i++)
					{
						partner = scda.GetWDBObject(tc.getInstance(i), tc.name);
						if(exp.eval(scda, partner))
						{
							//This object matches! add it.
							references.add(tc.getInstance(i));
							if(!partner.AddEVARef_from(eva.inverseEVA, getOid()))
							{
								System.out.println("InverseEVA is invalid!");
								return false;
							}
							dirtyPartners.put(tc.name, partner);
						}
					}
					
					ref_to.set(javaObject, references.toArray(new Integer[0]));
				}
				else
				{
					System.out.println("Target class is not a subclass of the EVA class!");
					return false;
				}
				success = true;
				dirty = true;
			}
			catch(Exception e)
			{
				System.out.println("ERROR!");
				return false;
			}
		}
		//If its not immediate, recurse though parents
		else if(classDef.getClass() == SubclassDef.class)
		{
			SubclassDef subclassDef = (SubclassDef)classDef;
			WDBObject parent;
			Hashtable parents;
			int i = 0;
			
			try
			{
				parents = (Hashtable)javaClass.getField("parents").get(javaObject);
			
				while(success == false && i < subclassDef.numberOfSuperClasses())
				{
					//First check my dirtyparents list
					if(dirtyParents.containsKey(parents.get(subclassDef.getSuperClass(i))))
					{
						parent = (WDBObject)dirtyParents.get(parents.get(subclassDef.getSuperClass(i)));
						if(parent.SetEVARef_to(name, targetClass, exp))
						{
							success = true;
						}
					}
					//Its not in my dirty list. So load it
					else
					{
						parent = scda.GetWDBObject((Integer)parents.get(subclassDef.getSuperClass(i)), subclassDef.getSuperClass(i));
						if(parent.SetEVARef_to(name, targetClass, exp))
						{
							dirtyParents.put(parents.get(subclassDef.getSuperClass(i)), parent);
							success = true;
						}
					}
					i++;
				}
			
			}
			catch(Exception ee)
			{
				System.out.println(ee.toString());
			}
		}
		
		return success;
	}
	*/

	public ArrayList<Object> getAttributeValue(AttributePath attributePath, ParserAdapter scda) throws Exception
	{
		ArrayList<Object> values = new ArrayList<Object>();

		if(attributePath.levelsOfIndirection() <= 0)
		{
			values.add(this.getDvaValue(attributePath.attribute, scda));
			return values;
		}
		else
		{
			String evaName = attributePath.getIndirection(attributePath.levelsOfIndirection() - 1);
			WDBObject[] objects = this.getEvaObjects(evaName, scda);

			if(objects != null && objects.length > 0)
			{
				attributePath.removeIndirection(attributePath.levelsOfIndirection() - 1);
				for(int i = 0; i < objects.length; i++)
				{
					values.addAll(objects[i].getAttributeValue(attributePath, scda));
				}
				attributePath.addIndirection(evaName);
			}
		}
		return values;
	}

	public void PrintAttribute(PrintNode row, AttributePath attributePath, ParserAdapter scda) throws Exception
	{
		ClassDef myClass = this.getClassDef(scda);
		if(attributePath.levelsOfIndirection() <= 0)
		{
			if(attributePath.attribute.equals("*"))
			{
				for(int j = 0; j < myClass.attributes.size(); j++)
				{
					Attribute currentAttribute = (Attribute)myClass.getAttribute(j);
					if(currentAttribute.getClass() == DVA.class)
					{
						Object dvaValue = dvaValues.get(currentAttribute.name);
						PrintCell cell = new PrintCell();
						cell.setOutput(String.format("%s", dvaValue));
						row.addCell(cell);
					}
				}
				if(myClass.getClass() == SubclassDef.class)
				{
					for(int i = 0; i < ((SubclassDef)myClass).numberOfSuperClasses(); i++)
					{
						String parentClass = (String)((SubclassDef)myClass).getSuperClass(i);
						Integer parentUid = (Integer)this.parents.get(parentClass);
						WDBObject parent = scda.getObject(parentClass, parentUid);
						parent.PrintAttribute(row, attributePath, scda);
					}
				}
			}
			else
			{
				Attribute currentAttribute = (Attribute)myClass.getAttribute(attributePath.attribute);
				Object dvaValue = getDvaValue(attributePath.attribute, scda);
				PrintCell cell = new PrintCell();
				cell.setOutput(String.format("%s", dvaValue));
				row.addCell(cell);
			}
		}
		else
		{
			String evaName = attributePath.getIndirection(attributePath.levelsOfIndirection() - 1);
			WDBObject[] objects = this.getEvaObjects(evaName, scda);
			ArrayList<PrintNode> branch = row.getBranch(evaName);

			if(objects != null && objects.length > 0)
			{
				if(branch == null)
				{
					branch = row.newBranch(evaName, objects.length);
				}
				attributePath.removeIndirection(attributePath.levelsOfIndirection() - 1);
				for(int i = 0; i < objects.length; i++)
				{
					objects[i].PrintAttribute(branch.get(i), attributePath, scda);
				}
				attributePath.addIndirection(evaName);
			}
			else
			{
				if(branch == null)
				{
					branch = row.newBranch(evaName, 1);
					myClass.padAttribute(branch.get(0), attributePath, scda);
				}
			}

			row.updateBranchCols(evaName);
		}
	}

	public Integer getUid()
	{
		return this.Uid;
	}

	public String getClassName()
	{
		return this.classDefName;
	}
}
