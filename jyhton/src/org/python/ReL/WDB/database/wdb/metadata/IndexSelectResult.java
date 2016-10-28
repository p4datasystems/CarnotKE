package org.python.ReL.WDB.database.wdb.metadata;

import java.util.Hashtable;
import java.util.ArrayList;

public class IndexSelectResult {
	
	//list of currently found objects using indexes. 
	//Null means can't filter. 
	private ArrayList<WDBObject> filteredList;
	//list of DVAs that are anded together collecting until we have to resolve it to a list from index(if possible)
	private Hashtable<String, Object> andDvaList;
	
	public IndexSelectResult()
	{
		this.andDvaList = new Hashtable<String, Object>();
	}
	public WDBObject[] getFilteredResults(ParserAdapter scda, ArrayList indexes) throws Exception
	{
		this.doDelayedAnd(scda, indexes);
		if(filteredList != null)
		{
			return (WDBObject[])filteredList.toArray(new WDBObject[0]);
		}
		
		return null;
	}
	public void clearResults()
	{
		this.filteredList = null;
	}
	public void addDva(String dva, Object value)
	{
		this.andDvaList.put(dva, value);
	}
	public IndexSelectResult and(IndexSelectResult isr)
	{
		this.filteredList = this.andFilteredList(isr.filteredList);
		this.andDvaList.putAll(isr.andDvaList);
		return this;
	}
	public IndexSelectResult or(IndexSelectResult isr)
	{
		this.filteredList = this.orFilteredList(isr.filteredList);
		return this;
	}
	private ArrayList<WDBObject> andFilteredList(ArrayList<WDBObject> objectsList)
	{
		if(this.filteredList == null && objectsList == null)
		{
			//Curent filter list is a full scan, so just use the objects we are anding with (everything AND something = something)
			return null;
		}
		else if(this.filteredList == null)
		{
			return objectsList;
		}
		else if(objectsList == null)
		{
			return this.filteredList;
		}
		else
		{
			int i;
			int j;
			ArrayList<WDBObject> newAndedList = new ArrayList<WDBObject>();
			for(i = 0; i < objectsList.size(); i++)
			{
				for(j = 0; j < this.filteredList.size(); j++)
				{
					if(((WDBObject)objectsList.get(i)).getUid().equals(((WDBObject)this.filteredList.get(j)).getUid()))
					{
						newAndedList.add(objectsList.get(i));
						break;
					}
				}
			}
			
			return newAndedList;
		}
	}
	private ArrayList<WDBObject> orFilteredList(ArrayList<WDBObject> objectsList)
	{
		if(this.filteredList == null || objectsList == null)
		{
			return null;
		}
		
		int i;
		int j;
		int objectsListSize = objectsList.size();
		for(i = 0; i < objectsListSize; i++)
		{
			Boolean found = false;
			for(j = 0; j < this.filteredList.size(); j++)
			{
				if(((WDBObject)objectsList.get(i)).getUid().equals(((WDBObject)this.filteredList.get(j)).getUid()))
				{
					found = true;
					break;
				}
			}
			if(!found)
			{
				this.filteredList.add(objectsList.get(i));
			}
		}

		return this.filteredList;
	}
	public void doDelayedAnd(ParserAdapter scda, ArrayList indexes) throws Exception
	{
		if(andDvaList.isEmpty())
		{
			//DVA list is empty, so no ands.. can't narrow down the filtered list
			return;
		}
		else 
		{
			IndexDef bestIndex = null;
			String bestIndexKey = null;
			IndexDef currentIndex = null;
			String currentIndexKey = null;
			String dvaName = null;
			int j;
			int i;
			//alright, find the best usuable index!
			for(i = 0; i < indexes.size(); i++)
			{
				currentIndex = ((IndexDef)indexes.get(i));
				for(j = 0; j < currentIndex.getDvas().size(); j++)
				{
					dvaName = ((String)((IndexDef)indexes.get(i)).getDvas().get(j));
					if(andDvaList.containsKey(dvaName))
					{
						if(currentIndexKey == null)
						{
							currentIndexKey = andDvaList.get(dvaName).toString();
						}
						else
						{
							currentIndexKey = currentIndexKey + ":" + andDvaList.get(dvaName).toString();
						}
					}
					else
					{
						break;
					}
				}
				if(j >= currentIndex.getDvas().size() && (bestIndex == null || currentIndex.getDvas().size() > bestIndex.getDvas().size()))
				{
					//All the required DVAs for that index were there
					//And this index has more DVAs so it will produce a smaller set of matches
					bestIndex = currentIndex;
					bestIndexKey = currentIndexKey;
				}
			}
			
			if(bestIndex != null)
			{
				//We found a usable index. Get the index we get from it
				this.filteredList = andFilteredList(scda.getObjects(bestIndex, bestIndexKey));
				
			}
			this.andDvaList.clear();
		}
	}
}
