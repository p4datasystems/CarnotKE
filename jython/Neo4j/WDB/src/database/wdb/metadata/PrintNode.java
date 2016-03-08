package wdb.metadata;

import java.util.*;


public class PrintNode {
	private ArrayList<PrintCell> cells;
	private Hashtable<String, ArrayList<PrintNode>> children;
	private Integer maxRows;
	private Integer maxCols;
	
	public PrintNode(Integer startRow, Integer startCol) {
		this.cells = new ArrayList<PrintCell>();
		this.children = new Hashtable<String, ArrayList<PrintNode>>();
		this.maxRows = startRow;
		this.maxCols = startCol;
	}

	public ArrayList<PrintNode> newBranch(String evaName, Integer numNodes) throws Exception
	{
		if(this.children.containsKey(evaName))
		{
			throw new Exception("Branch " + evaName + " alreadly exists");
		}
		else
		{
			ArrayList<PrintNode> childNodes = new ArrayList<PrintNode>();
			for(int i = 0; i < numNodes; i++)
			{
				childNodes.add(new PrintNode(maxRows, maxCols));
			}
			this.children.put(evaName,childNodes);
			return childNodes;
		}
	}
	public ArrayList<PrintNode> getBranch(String evaName)
	{
		if(this.children.containsKey(evaName))
		{
			return this.children.get(evaName);
		}
		else
		{
			return null;
		}
	}
	public void updateBranchCols(String evaName)
	{
		ArrayList<PrintNode> branch = getBranch(evaName);
		if(branch != null && branch.size() > 0)
		{
			this.maxCols = branch.get(0).maxCols;
		}
	}
	public void addCell(PrintCell cell)
	{
		cell.setColumn(maxCols);
		this.cells.add(cell);
		this.maxCols++;
	}
	
	public Integer getMaxCols()
	{
		return this.maxCols;
	}
	public Integer getMaxRows()
	{
		if(this.children.size() <= 0)
		{
			return 1;
		}
		else
		{
			Enumeration<ArrayList<PrintNode>> e = this.children.elements();
			ArrayList<PrintNode> currentEva;
			Integer maxRows = 0;
			Integer childrenRows = 0;
			while(e.hasMoreElements())
			{
				childrenRows = 0;
				currentEva = e.nextElement();
				for(int i = 0; i < currentEva.size(); i++)
				{
					childrenRows += currentEva.get(i).getMaxRows();
				}
				if(childrenRows > maxRows)
				{
					maxRows = childrenRows;
				}
			}
			
			if(maxRows > 1)
			{
				return maxRows;
			}
			else
			{
				return 1;
			}
		}
	}
	public String[][] printRow()
	{
		String[][] table = new String[getMaxRows()][this.maxCols];
		this.printRow(0, table);
		return table;
	}
	private void printRow(Integer startRow, String[][] table)
	{
		int i;
		for(i = 0; i < this.cells.size(); i++)
		{
			this.cells.get(i).setRow(startRow);
			table[startRow][this.cells.get(i).getColumn()] = this.cells.get(i).getOutput();
		}
		
		Enumeration<ArrayList<PrintNode>> e = this.children.elements();
		ArrayList<PrintNode> currentEva;
		Integer currentRow;
		while(e.hasMoreElements())
		{
			currentRow = startRow;
			currentEva = e.nextElement();
			for(i = 0; i < currentEva.size(); i++)
			{
				currentEva.get(i).printRow(currentRow, table);
				currentRow += currentEva.get(i).getMaxRows();
			}
		}
	}
}
