package org.python.ReL.WDB.database.wdb.metadata;

import org.python.ReL.WDB.parser.generated.wdb.parser.SimpleNode;

import java.io.*;
import java.util.*;
import java.rmi.server.*;


/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClassDef implements Query, Serializable {
	public String name;
	public String comment;
	public boolean sl;
	protected ArrayList<Attribute> attributes;
	protected ArrayList<Integer> instances;
	protected ArrayList<IndexDef> indexes;
	protected int uid;
	
	public ClassDef()
	{
		super();
		sl = false;
		attributes = new ArrayList<Attribute>();
		instances = new ArrayList<Integer>();
		indexes = new ArrayList<IndexDef>();
		uid = new Integer(Math.abs((new UID()).hashCode()));
	}
	
	public ClassDef(String _name, String _comment)
	{
		super();
		sl = false;
		attributes = new ArrayList<Attribute>();
		instances = new ArrayList<Integer>();
		indexes = new ArrayList<IndexDef>();
		name = _name;
		comment = _comment;
		uid = new Integer(Math.abs((new UID()).hashCode()));
	}
	
	public ClassDef(String _name, String _comment, Attribute[] _attributes)
	{
		super();
		sl = false;
		attributes = new ArrayList<Attribute>();
		instances = new ArrayList<Integer>();
		indexes = new ArrayList<IndexDef>();
		name = _name;
		comment = _comment;
		uid = new Integer(Math.abs((new UID()).hashCode()));

		for(int i = 0; i < _attributes.length; i++)
		{
			attributes.add(_attributes[i]);
		}
	}
	public int getUid() {
		return this.uid;
	}
	
	public IndexDef[] getIndexes()
	{
		IndexDef[] indexes = new IndexDef[0];
		
		return (IndexDef[])this.indexes.toArray(indexes);
	}
	public void addIndex(IndexDef index, ParserAdapter scda) throws Exception
	{
		indexes.add(index);
		this.commit(scda);
	}
	protected void addInstance(Integer uid)
	{
		instances.add(uid);
	}
	
	protected void removeInstance(Integer uid)
	{
		instances.remove(uid);
	}
	
	public WDBObject getInstance(int index, ParserAdapter da) throws Exception
	{
		return da.getObject(this.name, (Integer)instances.get(index));
	}
	
	public int numberOfInstances()
	{
		return instances.size();
	}
	
	public void addAttribute(Attribute _attribute)
	{
		attributes.add(_attribute);
	}
	
	public void removeAttribute(Attribute _attribute)
	{
		attributes.remove(_attribute);
	}
	
	public void removeAttribute(int index)
	{
		attributes.remove(index);
	}
	
	public Attribute getAttribute(int index)
	{
		return (Attribute)attributes.get(index);
	}
	
	public Attribute getAttribute(String _attribute)
	{
		if(_attribute != null)
		{
			String name;
			for(int i = 0; i < attributes.size(); i++)
			{
				name = ((Attribute)attributes.get(i)).name;
				if(name.equals(_attribute))
				{
					return ((Attribute)attributes.get(i));
				}
			}
		}

		return null;
	}
	public int numberOfAttributes()
	{
		return attributes.size();
	}
	
	public boolean isSubclassOf(String _parent, ParserAdapter scda) throws Exception
	{
		return false;
	}
	public ClassDef getBaseClass(ParserAdapter scda) throws Exception
	{
		return this;
	}
	public WDBObject newInstance(WDBObject parent, ParserAdapter scda) throws Exception
	{
		if(parent != null)
		{
			throw new IllegalArgumentException("Failed to add parent object of class \"" + parent.getClassName() + "\" to base class " + this.name);
		}
		Integer newUid = new Integer(Math.abs((new UID()).hashCode()));
		WDBObject newObject = new WDBObject(new Hashtable<String, Integer>(), new Hashtable<String, Integer>(), new Hashtable<String, Object>(), new Hashtable<String, Object>(), this.name, newUid);
		this.addInstance(newUid);
		this.commit(scda);
		return newObject;
	}

	public WDBObject newInstance(WDBObject parent) throws Exception {
		if (parent != null) {
			throw new IllegalArgumentException("Failed to add parent object of class \"" + parent.getClassName() + "\" to base class " + this.name);
		}
		Integer newUid = new Integer(Math.abs((new UID()).hashCode()));
		WDBObject newObject = new WDBObject(new Hashtable<String, Integer>(), new Hashtable<String, Integer>(), new Hashtable<String, Object>(), new Hashtable<String, Object>(), this.name, newUid);
		this.addInstance(newUid);
		return newObject;
	}

	public void commit(ParserAdapter scda) throws Exception
	{
		scda.putClass(this);
	}
	public WDBObject[] search(SimpleNode expression, ParserAdapter scda) throws Exception
	{
        boolean hasWhereClause = (expression != null);
		WDBObject[] matchesArray = new WDBObject[0];
		ArrayList<WDBObject> matchesList = new ArrayList<WDBObject>();
		WDBObject[] indexFilteredArray;
        if (hasWhereClause) {
		    indexFilteredArray = expression.filterObjectsWithIndexes(scda, this.indexes).getFilteredResults(scda, this.indexes);
        }
        else {
            indexFilteredArray = null;
        }
		if(indexFilteredArray == null)
		{
			//Simple for loop for search
			WDBObject object;
			for(int i = 0; i < this.instances.size(); i++)
			{
				object = this.getInstance(i, scda);
				if( !hasWhereClause 
                    || (hasWhereClause && expression.eval(scda, object)) )
				{
					matchesList.add(object);
				}
			}
		}
		else
		{
			//Simple for loop for search
			for(int i = 0; i < indexFilteredArray.length; i++)
			{
				if(!hasWhereClause
                    || (hasWhereClause && expression.eval(scda, indexFilteredArray[i])))
				{
					matchesList.add(indexFilteredArray[i]);
				}
			}
		}

		return (WDBObject[])matchesList.toArray(matchesArray);
	}
	public void padAttribute(PrintNode row, AttributePath attributePath, ParserAdapter scda) throws Exception
	{
		for(int j = 0; j < attributes.size(); j++)
		{	
			Attribute currentAttribute = (Attribute)attributes.get(j);
			int attributeLength = currentAttribute.name.length();
			
			if(currentAttribute.getClass() == DVA.class && attributePath.levelsOfIndirection() <= 0)
			{	
				//If the attribute we want is found or we just want everything, output
				if(attributePath.attribute.equals("*"))
				{
					PrintCell cell = new PrintCell();
					cell.setOutput(String.format(""));
					row.addCell(cell);
				}
				else if(attributePath.attribute.equals(currentAttribute.name))
				{
					PrintCell cell = new PrintCell();
					cell.setOutput(String.format(""));
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
					targetClass.padAttribute(row, attributePath, scda);
					attributePath.addIndirection(evaName);
					return;
				}
			}
		}
		
		if(!(attributePath.attribute.equals("*") && attributePath.levelsOfIndirection() <= 0))
		{
			//If we got here and we weren't trying to output all the attributes, we didn't find the requested attribute.
			
			if(this.getClass() == SubclassDef.class)
			{
				for(int i = 0; i < ((SubclassDef)this).numberOfSuperClasses(); i++)
				{
					ClassDef parentClass = scda.getClass(((SubclassDef)this).getSuperClass(i));
					parentClass.padAttribute(row, attributePath, scda);
				}
			}
			else
			{
				throw new NoSuchFieldException("Attribute \"" + attributePath.attribute + "\" is not a valid DVA");
			}
		}
	}
	public void printAttributeName(PrintNode row, AttributePath attributePath, ParserAdapter scda) throws Exception
	{
		for(int j = 0; j < attributes.size(); j++)
		{	
			Attribute currentAttribute = (Attribute)attributes.get(j);
			int attributeLength = currentAttribute.name.length();
			
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
		
		if(!(attributePath.attribute.equals("*") && attributePath.levelsOfIndirection() <= 0))
		{
			//If we got here and we weren't trying to output all the attributes, we didn't find the requested attribute. 
			throw new NoSuchFieldException("Attribute \"" + attributePath.attribute + "\" is not a valid DVA");
		}
	}

	@Override
	public String getQueryName() {
		return this.name;
	}
}
