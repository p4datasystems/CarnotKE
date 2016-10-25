/*
 * Created on Feb 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.python.ReL.WDB.database.wdb.metadata;


import java.rmi.server.UID;
import java.util.*;


/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class SubclassDef extends ClassDef {
	private ArrayList<String> superClasses;

	public SubclassDef()
	{
		super();
		superClasses = new ArrayList<>();
	}
	
	public SubclassDef(String _name, String _comment)
	{
		super(_name, _comment);
		superClasses = new ArrayList<>();
	}
	
	public SubclassDef(String _name, String _comment, Attribute[] _attributes)
	{
		super(_name, _comment, _attributes);
		superClasses = new ArrayList<>();
	}
	public ClassDef getBaseClass(ParserAdapter scda) throws Exception
	{
		//Assume all of our superclasses go back to the same base class alreadly
		ClassDef superClass;
		superClass = scda.getClass(((String)superClasses.get(0)));
		return superClass.getBaseClass(scda);
	}
	public void addSuperClass(String _parent)
	{
		superClasses.add(_parent);
	}
	public void removeSuperClass(String _parent)
	{
		superClasses.remove(_parent);
	}
	public void removeSuperClass(int index)
	{
		superClasses.remove(index);
	}
	public String getSuperClass(int index)
	{
		return superClasses.get(index);
	}
	public int numberOfSuperClasses()
	{
		return superClasses.size();
	}
	public boolean isSubclassOf(String superClassName, ParserAdapter scda) throws Exception
	{
		//See if its one of my immediate superclasses.
		if(superClasses.contains(superClassName))
		{
			return true;
		}
		//Not immediate superclass of me. Check my parents.
		ClassDef superClass;
		for(int i = 0; i < superClasses.size(); i++)
		{
			superClass = scda.getClass(((String)superClasses.get(i)));
			if(superClass.isSubclassOf(superClassName, scda))
			{
				return true;
			}
		}
		//Not any superClass of mine!
		return false;
	}
	
	public WDBObject newInstance(WDBObject baseParent, ParserAdapter scda) throws Exception
	{
		Integer newUid = new Integer(Math.abs((new UID()).hashCode()));
		WDBObject newObject = new WDBObject(new Hashtable<String, Integer>(), new Hashtable<String, Integer>(), new Hashtable<String, Object>(), new Hashtable<String, Object>(), this.name, newUid);
	
		for(int i = 0; i < this.superClasses.size(); i++)
		{
			ClassDef superClass = scda.getClass((String)this.superClasses.get(i));
			WDBObject parent;
			if(baseParent == null || !superClass.name.equals(baseParent.getClassName()))
			{
				if(superClass.getClass() == SubclassDef.class)
				{
					//If this super class is another subclass, then create a new instance of one of those with this object as the child and the parent we want to attach to
					parent = superClass.newInstance(baseParent, scda);
				}
				else
				{
					//If this super class is a base class, then create a new isntance but don't pass the parent we want to attach since its a base class
					parent = superClass.newInstance(null, scda);
				}
			}
			else
			{
				//The base parent is my immediate parent
				parent = baseParent;
			}
			
			WDBObject childObject = parent.getChildObject(this.name, scda);
			
			if(childObject != null)
			{
				//Parent alreadly extended to this class, just return the child instance
				return childObject;
			}
			
			parent.addChildObject(newObject);
			parent.commit(scda);
			newObject.addParentObject(parent);
		}
		
		this.addInstance(newUid);
		this.commit(scda);
		
		return newObject;
	}
	public void printAttributeName(PrintNode row, AttributePath attributePath, ParserAdapter scda) throws Exception
	{
		for(int j = 0; j < attributes.size(); j++)
		{	
			Attribute currentAttribute = (Attribute)attributes.get(j);
			int attributeLength = currentAttribute.name.length() + 2;
			
			if(currentAttribute.getClass() == DVA.class && attributePath.levelsOfIndirection() <= 0)
			{	
				//If the attribute we want is found or we just want everything, output
				if(attributePath.attribute.equals("*"))
				{
					PrintCell cell = new PrintCell();
					cell.setOutput(String.format("%s", currentAttribute.name));
					row.addCell(cell);
				}
				else if(attributePath.attribute.equals(currentAttribute.name))
				{
					PrintCell cell = new PrintCell();
					cell.setOutput(String.format("%s", currentAttribute.name));
					row.addCell(cell);
					return;
				}
			}
			if(currentAttribute.getClass() == EVA.class && attributePath.levelsOfIndirection() > 0)
			{
				String evaName = attributePath.getIndirection(attributePath.levelsOfIndirection() - 1);
				if(evaName.equals(currentAttribute.name))
				{
					ClassDef targetClass = scda.getClass(((EVA)currentAttribute).baseClassName);
					attributePath.removeIndirection(attributePath.levelsOfIndirection() - 1);
					targetClass.printAttributeName(row, attributePath, scda);
					attributePath.addIndirection(evaName);
					return;
				}
			}
		}
		
		//Got here if we didn't get what we need in this class
		int i = 0;
		while(i < superClasses.size())
		{
			try
			{
				ClassDef superClass = scda.getClass(((String)superClasses.get(i)));
				superClass.printAttributeName(row, attributePath, scda);
				i++;
			}
			catch (NoSuchFieldException nsfe)
			{
				if(i < superClasses.size())
				{
					i++;
				}
				else
				{
					throw nsfe;
				}
			}
		}
	}
}
