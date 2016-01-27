package org.python.ReL;

import net.sf.jsqlparser.*;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.*;
import java.io.StringReader; //hopefully this works
import net.sf.jsqlparser.schema.*;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.create.table.*;
import net.sf.jsqlparser.statement.delete.*;
import net.sf.jsqlparser.statement.drop.*;
import net.sf.jsqlparser.statement.insert.*;
import net.sf.jsqlparser.statement.replace.*;
import net.sf.jsqlparser.statement.select.*;

import net.sf.jsqlparser.statement.truncate.*;
import net.sf.jsqlparser.statement.update.*;
import net.sf.jsqlparser.util.deparser.SelectDeParser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.pool.OracleDataSource;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.lang.String;
import java.lang.Character;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;
import java.util.UUID; //added by Joojay

import org.python.core.PyObject;
import org.python.ReL.PyRelConnection;
import org.python.ReL.SPARQLDoer;
import org.python.ReL.SQLValidator;

//Libraries for AllegroGraph added by Joojay
import com.franz.agraph.jena.AGGraph;
import com.franz.agraph.jena.AGGraphMaker;
import com.franz.agraph.jena.AGInfModel;
import com.franz.agraph.jena.AGModel;
import com.franz.agraph.jena.AGQuery;
import com.franz.agraph.jena.AGQueryExecution;
import com.franz.agraph.jena.AGQueryExecutionFactory;
import com.franz.agraph.jena.AGQueryFactory;
import com.franz.agraph.jena.AGReasoner;
import com.franz.agraph.repository.AGCatalog;
import com.franz.agraph.repository.AGQueryLanguage;
import com.franz.agraph.repository.AGFreetextIndexConfig;
import com.franz.agraph.repository.AGRepository;
import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGServer;
import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
//import com.hp.hpl.jena.query.ResultSet; //conflicts with java.sql.ResultSet
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SQLVisitor extends SelectDeParser implements SelectVisitor, FromItemVisitor, ExpressionVisitor, ItemsListVisitor, SelectItemVisitor, OrderByVisitor {

    private List<String> filters;
	private List<String> matches;
	private HashMap<String, String> tablesAliases;
	private HashMap<String, String> tables2alias;
	private LinkedHashMap<String, String> columnsAs;
	private LinkedHashMap<String, String> aggrColumnsAs;
	private List<String> joinColumns;
	private static String temp;
	private String ownException;
	private Boolean wasEquals;
	private Boolean subselect;
	private int subDepth;
	private static int joinInc = 1; // for each join this will be incremented and used in a variable name.
	
	HashMap<String, String> colVarNames = new HashMap<String, String>();
	ArrayList<String> allCols = new ArrayList<String>(); 
	private static String[] aggregates = {"avg", "count", "max", "min", "sum"}; // Input as lower case.
    private static String[] inequalities = {" = ", " > ", " < ", " >= ", " <= ", " != "};
	ArrayList<String> plainAliases = new ArrayList<String>();

	private String tablenameFrom = "";
	public String url = "";
	public String uname = "";
	public String pword = "";
	public OracleDataSource ods;
	public PyRelConnection connection;
	public Statement stmt;

	public CCJSqlParserManager parserManager = new CCJSqlParserManager();
	
	static private HashMap<String, String> map  = new HashMap<String, String>();

    String NoSQLNameSpacePrefix = "";
	
	/**
	 *
	 */
	public SQLVisitor(PyObject conn) {
		this.connection = (PyRelConnection)conn;
        if(connection.getConnectionDB().equals("OracleNoSQL")) NoSQLNameSpacePrefix = connection.getDatabase().getNameSpacePrefix();
	}
	
	public void doDrop(Drop stmt, OracleConnection conn) {
		String tableToDrop = stmt.getName();
		String command = "";
    }

    //public void doInsert(Insert stmt) throws SQLException { // Old method signature
    public void doInsert(Insert stmt, String getConnectionType) throws SQLException {//New method signature
        if (stmt.getColumns() != null) {
        	this.connectionType = getConnectionType;
        	Iterator valsIt = ((ExpressionList)stmt.getItemsList()).getExpressions().iterator();
        	//String id = Integer.toString(SPARQLDoer.getNextGUID(connection));
        	//String subject = id;
        	// String attvalPairs = "DBUNIQUEID" + " := " + id + " ";
            String attvalPairs = "";

            String COMMA = "";
        	for (Iterator colsIt = stmt.getColumns().iterator(); colsIt.hasNext(); ) {
                // CAUTION - the following replaceAll statements remove all single and double quates and special characters for the column names and column values.
            	String attr = COMMA + ((Column)colsIt.next()).getColumnName().replaceAll("'", "").replaceAll("\"", "").replaceAll("[^ -~]+","");
            	Object attrValue = valsIt.next(); 
            	String valStr = (attrValue.toString().replaceAll("'", "")).replaceAll("\'", "").replaceAll("\"", "").replaceAll("[^ -~]+","");
                // CAUTION - the following replaceAll statement replaces all TO_DATE statements with the date as a string.
                if(valStr.toUpperCase().contains("TO_DATE")) {
                    valStr = valStr.toUpperCase().replaceAll(".*[(]", "").replaceAll(" .*", "");
                }
            	attvalPairs += attr + " := \"" + valStr + "\" ";
                COMMA = ",";
        	}
            	
			ProcessLanguages processLanguage = new ProcessLanguages(connection);
			processLanguage.processSIM("INSERT " + stmt.getTable().toString() + " ( " + attvalPairs + ")");
        }
    }
	
    public void doCreateTable(CreateTable stmt) throws SQLException {
        String modelName = connection.getModel();
        String tableName = stmt.getTable().getName();
        //String namedGraph = "<www.example.org/" + tableName + ">";
        String namedGraph = "";
        String attribute;
        String type;
        String xsdType;

        SPARQLDoer.createQuadStore(connection);
        SPARQLDoer.insertObjectPropQuad(connection, namedGraph, "rdf:type", "rdfs:Class");

        if (stmt.getColumnDefinitions() != null) {
            for (Iterator colsIt = stmt.getColumnDefinitions().iterator(); colsIt.hasNext(); ) {
                ColumnDefinition col = (ColumnDefinition)colsIt.next();
                attribute = col.getColumnName();
                type = col.getColDataType().getDataType();

                if (type.toLowerCase().equals("numeric") || type.toLowerCase().equals("decimal") ||
                    type.toLowerCase().equals("real")) {
                    xsdType = "xsd:decimal"; // was decimal
                } else if (type.toLowerCase().equals("varchar") || type.toLowerCase().equals("varchar2")) {
                    xsdType = "xsd:string"; // was string
                } else if (type.toLowerCase().equals("bit") || type.toLowerCase().equals("tinyint") ||
                           type.toLowerCase().equals("bigint")) {
                    xsdType = "xsd:integer"; // was integer
                } else if (type.toLowerCase().equals("date")) {
                    xsdType = "xsd:date"; // was date
                } else {
                    xsdType = "xsd:decimal"; // was decimal
                }

                SPARQLDoer.insertObjectPropQuad(connection, attribute, "rdf:type", "owl:DatatypeProperty");
                SPARQLDoer.insertObjectPropQuad(connection, attribute, "rdfs:domain", tableName);
                SPARQLDoer.insertObjectPropQuad(connection, attribute, "rdf:range", "rdfs:" + xsdType);
            }
        }
    }
	
    private Collection<String> returns_instances_of = null; 
	private String connectionType = null;
	/**
	 *
	 */	 
	public String getSelect(Select select, Collection<String> instance_type_names, String getConnectionType) throws SQLException, JSQLParserException, ownIllegalSQLException{
		this.returns_instances_of = instance_type_names;
		// Initialize Validator
		SQLValidator validator = new SQLValidator();
	    String SPARQL = "";
	    this.connectionType = getConnectionType;
		
		if (connection.getDebug() == "debug") System.out.println("SQL statement: |" + select + "|");	
		//Setting depth for subqueries, asumming subqueries on the where clause
		select.getSelectBody().accept(this);
		
/*		
		System.out.println(select.getWithItemsList() + "\n\n");
		try{
			if(!ownException.equals("")){
				throw new ownIllegalSQLException(ownException);
			}
		} catch (ownIllegalSQLException e){
			System.out.println(e);
			return e.toString();
		}
*/
		SPARQL += subq.pop() + endOfStmt;
		//Building SPARQL Statement
		while(!subq.isEmpty()){
    			SPARQL += subq.pop() + endOfStmt + ")";
		}
		
		if (connection.getDebug() == "debug") System.out.println("RDF conversion of select:\n |" + SPARQL + "| END");
		return SPARQL;
	}
	/* Current subquery */
	private String tempSub;
	
	/**
	 *
	 */
	public void visit(PlainSelect plainSelect) {
		//Creting data structures to hold valuesF to build Oracle SQL statement.
		//Done this way since expecting basic forms of subqueries
		List<String> filters = new ArrayList<String>();
		List<String> tables = new ArrayList<String>();
		List<String> orderby = new ArrayList<String>();
		List<String> groupby = new ArrayList<String>();
		List<String> having = new ArrayList<String>();
		List<String> subselects = new ArrayList<String>();
		HashMap<String,String> tablesAliases = new HashMap<String,String>();
		HashMap<String,String> tables2alias = new HashMap<String,String>();
		LinkedHashMap<String,String> columnsAs = new LinkedHashMap<String,String>();
		LinkedHashMap<String,String> aggrColumnsAs = new LinkedHashMap<String,String>();

		List<String> joinColumns = new ArrayList<String>();

		tempSub = visitSelect_buildSPARQL(plainSelect, filters, tables, orderby, groupby, having, tablesAliases, tables2alias, columnsAs, aggrColumnsAs, joinColumns);

		subq.add(tempSub);
	}

	/**
	 * Build the SPARQL for a SELECT statement.
	 */
	public String visitSelect_buildSPARQL(
        PlainSelect plainSelect, 
        List<String> filters,
        List<String> tables,
        List<String> orderby,
        List<String> groupby,
        List<String> having,
        HashMap<String, String> tablesAliases,
        HashMap<String, String> tables2alias,
        LinkedHashMap<String, String> columnsAs,
        LinkedHashMap<String, String> aggrColumnsAs,
        List<String> joinColumns)
	{ 
	
		// Visit the Select statement and build structures necessary to build the SPARQL statement.

		// Get all table names from the RDF data.
		List<String> RDFtables = null;
		List<String> RDFTableNames = new ArrayList<String>();
		SPARQLHelper sparqlHelper = new SPARQLHelper(connection);

		try {		// Get all of the classes (i.e., table names in this case) in the SCHEMA graph	
			RDFtables = sparqlHelper.getSubjects(sparqlHelper.getSchemaString(), "rdf:type", "rdfs:Class");
			for (String t : RDFtables) {
			   RDFTableNames.add(t);
			}
		} catch (SQLException ex) {
			System.out.println(ex);
		}
		// Check to see if there are any table names that differ only by case.
		for (String t1 : RDFTableNames) {
		   for (String t2 : RDFTableNames) {
			  if((! t1.equals(t2)) && t1.toUpperCase().equals(t2.toUpperCase()))
				  System.out.println("Table name " + t1 + " and table name " + t2 + " appear in the RDF data, this is probably an error.");
		   }
		}

// End getting all table names from the RDF data.

// Get table names and their aliases if any from the SQL statement.
		FromItem fromItem = plainSelect.getFromItem(); //Accepting the visitor
		fromItem.accept(this);

		String alias = null;
		if (fromItem.getAlias() != null)
			alias = fromItem.getAlias().getName().toUpperCase();
		String tableName = temp;
		String tmpTableName = "";
		for (String t : RDFTableNames) {
			if(tableName.toUpperCase().equals(t.toUpperCase()))
				tmpTableName = t;
		}
		if( ! tmpTableName.equals("")) {
		   tables.add(tmpTableName);
		   tablesAliases.put((alias == null ? tmpTableName : alias), tmpTableName);
		   tables2alias.put(tmpTableName, (alias == null ? tmpTableName : alias));
		}
		else System.out.println("Table name " + tableName + " does not exist in the RDS data.");

		if (plainSelect.getJoins() != null) {
			for (Iterator joinsIt = plainSelect.getJoins().iterator(); joinsIt.hasNext();) {
				Join join = (Join) joinsIt.next();
				fromItem = join.getRightItem();
				fromItem.accept(this);

				if (fromItem.getAlias() != null)
					alias = fromItem.getAlias().getName().toUpperCase();
				tableName = temp;
				tmpTableName = "";
				for (String t : RDFTableNames) {
				   if(tableName.toUpperCase().equals(t.toUpperCase())) tmpTableName = t;
				}
				if( ! tmpTableName.equals("")) {
					tables.add(tmpTableName);
					tablesAliases.put( (alias == null ? tmpTableName : alias), tmpTableName);
					tables2alias.put(tmpTableName, (alias == null ? tmpTableName : alias));
				}
				else System.out.println("Table name " + tableName + " does not exist in the RDS data.");
			}
		}
// End getting table names and their aliases if any.

// This map (tableSymbols) of table names to unique, short symbols will be used later in several places.
		HashMap<String,String> tableSymbols = new HashMap<String,String>();
	    int n = 1;
	    for (String s : tables) {
	       tableSymbols.put(s, "s" + n);
           n++;
	    }

// Get all column names from tables.
		List<String> columnNames = new ArrayList<String>();
		List<String> columns = null;  
		for (String table : tables) { 
			try {		// Get all of the column names for each of the tables.	
				columns = sparqlHelper.getSubjects(table + "_" +sparqlHelper.getSchemaString(), "rdfs:domain", NoSQLNameSpacePrefix + ":" + table);
				for (String column : columns) {
				   columnNames.add(tables2alias.get(table) + "." + column);
				}
			} catch (SQLException ex) {
				System.out.println(ex);
			}
		}

// End getting column names.

// GROUP BY processing
		if (plainSelect.getGroupByColumnReferences() != null){
			String groupbyStmnt = plainSelect.getGroupByColumnReferences().toString();
			String[] groupbyElems = groupbyStmnt.substring(1, groupbyStmnt.length() - 1).replace(" ", "").split(",");
			String withTableName = "";

			for (String gr: groupbyElems)
				gr.replace(",", "");

			int i = 0;
			for (String groupbyElem: groupbyElems){
				if (groupbyElem.equals(" "))
					continue;
				boolean success = false;
				groupbyElem = groupbyElem.toUpperCase();

				for (int j = 0; j < allCols.size(); j++){
					String s = allCols.get(j);
					if (s.substring(s.lastIndexOf(".") + 1).equals(groupbyElem) || s.equals(groupbyElem)){
						allCols.add(s);
						groupby.add("?v" + allCols.size());
						success = true;
						break;
					}
				}
				if (!success && !groupbyElem.equals("")){
					int pos = columnsAs.keySet().size() + 1;
					String resGroupbyElem = resolveColumnName(columnNames, groupbyElem);
					if (resGroupbyElem == "")
						resGroupbyElem = groupbyElem;
					allCols.add(resGroupbyElem);
					groupby.add("?v" + allCols.size());	
				}
				i++;
			}
		}

// Get column names to project.
        if(plainSelect.getSelectItems() != null) {
			//gets the columns that are asked of
			for(Iterator i = plainSelect.getSelectItems().iterator(); i.hasNext();) {
				//int cnt = 0;
				String columnName = "";
				SelectItem item = (SelectItem)i.next();
				item.accept(this);

				String aggregateElement[] = getAggregateSelect(item.toString().toLowerCase());
				int pos = allCols.size() + 1;

				// if selecting everything
				if(temp.equals("*")){
					columnsAs.put("*", "*");
				}
				else{
					// If aggregate (no alias)
					if(aggregateElement != null && !item.toString().contains(" AS ")){
						columnName = resolveColumnName(columnNames, aggregateElement[1]);
						if (columnName == "")
							columnName = aggregateElement[1];
						String aggrColumnName = aggregateElement[0].toUpperCase() + "(" + columnName + ")";
						allCols.add(aggrColumnName);
						aggrColumnsAs.put("?v" + pos, "\"" + aggrColumnName + "\"");
						columnsAs.put("?v" + pos, "\"" + columnName.substring(columnName.lastIndexOf(".") + 1) + "\"");
					} // Alias
					else if(item.toString().toLowerCase().contains(" as ")){
						String[] split = (item.toString().toLowerCase()).split(" as ");
						columnName = split[0];
						String aliasName = split[1];

						if (aggregateElement != null){
							columnName = resolveColumnName(columnNames, aggregateElement[1]);
							if (columnName == "")
								columnName = aggregateElement[1];
							columnName = aggregateElement[0].toUpperCase() + "(" + columnName + ")";
							aggrColumnsAs.put("?v" + pos, 
											  "\"" + aliasName.replace("\"", "") + "\"");
						}
						else{
							String tcolName = columnName;
							columnName = resolveColumnName(columnNames, columnName);
							if (columnName == "")
								columnName = tcolName;
						}
						columnsAs.put("?v" + pos, "\"" + aliasName.replace("\"", "") + "\"");
						allCols.add(columnName);
					}
					else { // Non alias, non aggregate
						columnName = item.toString();
						String tcolName;
						if ((tcolName = resolveColumnName(columnNames, columnName)) != "")
							columnName = tcolName;
						columnsAs.put("?v" + pos, "\"" + columnName.substring(columnName.lastIndexOf(".") + 1) + "\"");
						allCols.add(columnName);
					}
				}
			}
		}
		
// End getting column names to project.	

// Get join column names.	
		if (plainSelect.getJoins() != null) {
			for (Iterator joinsIt = plainSelect.getJoins().iterator(); joinsIt.hasNext();) {
				Join join = (Join) joinsIt.next
				();
				if(join.getOnExpression() != null) {
					join.getOnExpression().accept(this);
                    String s = join.toString().substring(join.toString().lastIndexOf("ON (") + 4);
					String[] split = s.substring(0,s.length()-1).split(" = ");
					String col1 = resolveColumnName(columnNames, split[0]);
					String col2 = resolveColumnName(columnNames, split[1]);
                    joinColumns.add(col1 + " = " + col2);
				}
			}
		}
	
// End getting join column names.

// Process WHERE statement if any.
        if (plainSelect.getWhere() != null) { //ie, there's a where clause
                String s = plainSelect.getWhere().toString().replace(" and ", " &&  and ");
                s = s.replace(" AND ", " &&  and ");
                s = s.replace(" or ", " ||  and ");
                s = s.replace(" OR ", " ||  and ");
			    String[] whereClauses = s.split(" and | AND | or | OR ");
			    String fColumns = "";
			    String filter = "\tFILTER(";
                n = 1;
				for (String c : whereClauses) {
					for (String ineq : inequalities) {						
	                    if (c.contains(ineq)) {
	                    	String left = "";
						    left = resolveColumnName(columnNames, c.split(ineq)[0]);
						    if(left.equals(""))
						    	filter += c.split(ineq)[0];
						    else {
								fColumns += "\t?" + tableSymbols.get(tablesAliases.get(left.split("\\.")[0])) + " :" + left.split("\\.")[1] + " ?f" + n + " .\n";
								filter += "?f" + n;
								n++;
						    }
						    filter += ineq;
						    String right = resolveColumnName(columnNames, c.split(ineq)[1]);
						    if(right.equals(""))
						    	filter += ":" + c.split(ineq)[1];
						    else {
								fColumns += "\t?" + tableSymbols.get(tablesAliases.get(right.split("\\.")[0])) + " :" + left.split("\\.")[1] + " ?f" + n + " .\n";
			                	    // So fColumns will be set = ?s1 :eventType ?f1 .
								filter += "?f" + n;
		                    	n++;
		                	}
		                }
					}
				}
				filters.add(fColumns + filter.replace("'", "") + ") ");
		}

// End processing WHERE statement.
		
// Process Order By statement
	
		if(plainSelect.getOrderByElements() != null) {
			String orderByStmnt = plainSelect.getOrderByElements().toString().toLowerCase();
			orderByStmnt = orderByStmnt.substring(1, orderByStmnt.length() - 1);
			String s = "ORDER BY ";

			String[] orderbyElems = orderByStmnt.split(" |\\,");

			for (int i = 0; i < orderbyElems.length; i++){
				if (orderbyElems[i].equals(""))
					continue;
				String resolvedName = resolveColumnName(columnNames, orderbyElems[i]);

				if (i > 0)
					s += " ";

				//If "ASC" or "DESC", handle here
				if ((i + 1 < orderbyElems.length) && (orderbyElems[i+1].equalsIgnoreCase("ASC") || orderbyElems[i+1].equalsIgnoreCase("DESC"))){
					s = s + orderbyElems[i+1].toUpperCase() + "(?v" + (allCols.size() + 1) + ") ";
					i++;
				}
				else
					s = s + "?v" + allCols.size();
			}
			orderby.add(s);
		}	


// Process "having" statement
		/*
		 		cases:	aggregate(col) > number
						groupedbycolumn > number (must be in groupby)
						aggregate(col) OR groupedbycolumn>number (no spaces)

		 */
		if (plainSelect.getHaving() != null){
			String s = plainSelect.getHaving().toString().replace(" and ", " &&  and ");
            s = s.replace(" AND ", " &&  and ");
            s = s.replace(" or ", " ||  and ");
            s = s.replace(" OR ", " ||  and ");
			String[] havingElems = s.split(" and | AND | or | OR | ");
			boolean success = false;
			String str = "HAVING(";

			for (int i = 0; i < havingElems.length; i++){
				if (havingElems[i].equals("")) continue;

				if (havingElems[i].equals("&&") || havingElems[i].equals("||")){
					str += havingElems[i] + " ";
					continue;
				}				

				try{
					Integer.parseInt(havingElems[i]);
					str += havingElems[i];
					if (i + 1 < havingElems.length) str += " ";
					continue;
				}
				catch (NumberFormatException ex){ success = false; }

				//add the inequality
				for (int j = 0; j < inequalities.length; j++){
					if (inequalities[j].replace(" ", "").equals(havingElems[i])){
						str += havingElems[i] + " ";
						success = true;
						break;
					}
				}
						
				if (success) continue;

				//get resolved name && check if there's something other than aggregates that use parentheses
				String resolvedName;
				String aggregateElement[] = getAggregateSelect(havingElems[i]);
				if (aggregateElement != null)
					resolvedName = resolveColumnName(columnNames, aggregateElement[1]);
				else 
					resolvedName = resolveColumnName(columnNames, havingElems[i]);
				int pos = groupby.size() - 1;
				for (String aggr: aggrColumnsAs.keySet()){
					pos++;
					if (columnsAs.get(aggr).equals(resolvedName))
						break;
				}
				if (aggregateElement != null)
					str += aggregateElement[0] + "(?v" + pos + ") ";
				else{
					allCols.add(resolvedName);
					str += "?v" + pos + " ";
				}
			}
			having.add(str + ")");
		}


		if (connection.getDebug() == "debug") {
			System.out.println("\nvisitSelect_buildSPARQL Structures necessary to build the SPARQL statement:");
			System.out.println("\t - plainSelect: " + plainSelect);
			System.out.println("\t - RDFTableNames: " + RDFTableNames);
			System.out.println("\t - tables: " + tables);
			System.out.println("\t - tablesAliases: " + tablesAliases);
			System.out.println("\t - tables2alias: " + tables2alias);
			System.out.println("\t - tableSymbols: " + tableSymbols);
			System.out.println("\t - columnNames: " + columnNames);
			System.out.println("\t - columnsAs: " + columnsAs);
			System.out.println("\t - aggrColumnsAs: " + aggrColumnsAs);
			System.out.println("\t - joinColumns: " + joinColumns);
			System.out.println("\t - filters: " + filters);
			System.out.println("\t - orderby: " + orderby);
			System.out.println("\t - groupby: " + groupby);	
			System.out.println("\t - having: " + having);
			System.out.println("\t - allCols: " + allCols);
		}
		
//Build SPARQL.
	    
        String SPARQL = "SELECT ";
        String tmpSparql = "";
        n = 0;
        // Add the columns to be projected to the SPARQL string. 
        boolean nonExistentColumns = false;
       
	   LinkedHashMap<String,String> tmpColumnsAs = new LinkedHashMap<String,String>();
       // If select * ...", replace * in columnsAs with all column names.
	   if(columnsAs.get("*") != null) {
		   if(columnsAs.get("*").equals("*")) {
			  columnsAs.remove("*");
			  String filter = "";
			  if(filters != null) if (filters.size() != 0) filter = filters.get(0);
			  for (String table : tables) {
		         columns = new ArrayList<String>();
	             try {
					columns = sparqlHelper.getSubjects(table + "_" +sparqlHelper.getSchemaString(), "rdfs:domain", NoSQLNameSpacePrefix + ":" + table);
			        for (String column : columns) {
                       if( ! column.equals("DBUNIQUEID")) {
						   columnsAs.put(tables2alias.get(table) + "." + column, "\"" + column + "\"");
						   allCols.add(tables2alias.get(table) + "." + column);
			           }
			        }
			     } catch (SQLException e) {
				    System.out.println(e);
			    } 
			  }      
		   }
		   if (connection.getDebug() == "debug") System.out.println("\t - columnsAs: " + columnsAs + "\naggrColumnsAs: " + aggrColumnsAs);
		}
        int aggrPos = 0;
		// for (String col: allCols){
		// 	int groupbySize;
		// 	n++;
		// 	if (groupby.size() == 0) groupbySize = 0;
		// 	else groupbySize = groupby.size() - 1;
		// 	String currentVar;
		// 	if (groupbySize == 0)
		// 		currentVar = "?v" + n;
		// 	else
		// 		currentVar = "?v" + (n + groupbySize - 1);
		// 	if (n <= columnsAs.keySet().size() + groupbySize && n >= groupby.size() - 1){		
		//         if (aggrColumnsAs.keySet().contains(currentVar)){
		// 			aggrPos++;
		//    			String v = aggrColumnsAs.get(currentVar).toUpperCase();
		//         	SPARQL += "n" + aggrPos + " " + v;
		//         }
		//     	else{
		//     		int n2 = n;
		//     		for (int j = 1; j < groupby.size(); j++){
		//     			if (allCols.get(j - 1).equals(col)){
		//     				n2 = j;
		//     				break;
		//     			}
		//     		}
		//        		SPARQL += "v" + n2 + " " + columnsAs.get(currentVar);
		//     	}
		// 		if (n > groupbySize - 1 && n < groupbySize + columnsAs.size())
		// 			SPARQL += ", ";
			// }


        // Prepend all group by columns to optional selects
        for (int groupbyVar = 0; groupbyVar < groupby.size(); groupbyVar++) {
            /* 
             * There should be an if block for Oracle RDF and AG
             * Each if block should create the correct SPARQL version for the specified connectionType,
             * similar to lines
        	 */
            String groupbyCol = allCols.get(groupbyVar);
			if (tablesAliases.get(groupbyCol.split("\\.")[0]) == null) {
				tmpSparql += "\tOPTIONAL { ?s1"  + " " + NoSQLNameSpacePrefix + ":" + groupbyCol.substring(groupbyCol.lastIndexOf(".") + 1) + " ?v" + (groupbyVar + 1) + " }\n";
				nonExistentColumns = true;
			}
			else {
				tmpSparql += "\tOPTIONAL { ?" + tableSymbols.get(tablesAliases.get(groupbyCol.split("\\.")[0]))
			              + " " + NoSQLNameSpacePrefix + ":" + groupbyCol.substring(groupbyCol.lastIndexOf(".") + 1) + " ?v" + (groupbyVar + 1) + " }\n";
			}
        }

        n += groupby.size();
        for (String col: columnsAs.keySet()) {
			n++;
			if (aggrColumnsAs.keySet().contains(col)) {
				aggrPos++;
				String v = aggrColumnsAs.get(col).toUpperCase();
                if( ! connection.getConnectionDB().equals("OracleNoSQL")) SPARQL += "n" + aggrPos + " AS " + v;
                else  SPARQL += "(?n" + aggrPos + " AS ?" + v.replaceAll("\"", "") + ")";
			} else {
				int n2 = n;
				for (int j = 1; j < allCols.size(); j++) {
					if (allCols.get(j - 1).equals(allCols.get(n-1))) {
						n2 = j;
						break;
					}
				}
		    	if( ! connection.getConnectionDB().equals("OracleNoSQL")) SPARQL += "v" + n2 + " " + columnsAs.get(col);
                else SPARQL += "(?v" + n2 + " AS ?" + columnsAs.get(col).replaceAll("\"", "") + ")";
			}	    	
			if (n != allCols.size())
                if( ! connection.getConnectionDB().equals("OracleNoSQL")) SPARQL += ", ";
                else SPARQL += " ";
			// Break the col into a key (if it is an aggregate, we must get rid of the function)
			String key;
			if (aggrColumnsAs.keySet().contains("?v" + n)) {
				key = aggrColumnsAs.get(col);
				key = key.substring(key.indexOf("(") + 1, key.indexOf(")"));
			}
			else key = allCols.get(n - 1);
            if (tablesAliases.get(key.split("\\.")[0]) == null) {
                if ( ! connection.getConnectionDB().equals("OracleNoSQL")) {
                    tmpSparql += "\tOPTIONAL { ?s1" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))
                           + " " + NoSQLNameSpacePrefix + ":" + key.substring(key.lastIndexOf(".") + 1) + " ?v" + n + " }\n";
                } else {
                    tmpSparql += " OPTIONAL { ?indiv" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))
                           + " " + NoSQLNameSpacePrefix + ":" + key.substring(key.lastIndexOf(".") + 1) + " ?v" + n + " } ";
                } 
				nonExistentColumns = true;
			}
			else {
                if ( ! connection.getConnectionDB().equals("OracleNoSQL")) {
                    tmpSparql += "\tOPTIONAL { ?" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))
                           + " " + NoSQLNameSpacePrefix + ":" + key.substring(key.lastIndexOf(".") + 1) + " ?v" + n + " }\n";
                } else {
                    tmpSparql += "OPTIONAL { GRAPH ?g" + n + " {?" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))
			               + " " + NoSQLNameSpacePrefix + ":" + key.substring(key.lastIndexOf(".") + 1) + " ?v" + n + " } } ";
                } 
			}
	    }		

//         for (Map.Entry<String, String> entry : columnsAs.entrySet()) {
//            // For "select e.domain AS d from ..." ==> key is: e.domain, value is: d
//            String key = entry.getKey();
//            String value = entry.getValue();
           
//            String v = "";
//            // This if statement should never compute true
//            if(value.contains(".")) v = "\"" + value.substring(value.lastIndexOf(".") + 1);
//            else v = value;

//            System.out.println("\n\n" + key + " SIZE OF columnsAs   " + columnsAs + " SIZE OF aggrColumnsAs " 
//            	+ aggrColumnsAs + "\ncolvarnames " + colVarNames + "  allCols: " + allCols);

// /*
// revisit the plainselect (columns selected segment), 
// if aggregate, take column name out, resolve, put back in and use to get value from aggrColumnAs
// */

//            // This is where the columns to be projected are added to the SPARQL string.
//            if (!doNotSelect.contains(colVarNames.get(key))){
// 		       if (aggrColumnsAs.keySet().contains(key)){
// 		   		   v = "\"" + aggrColumnsAs.get(key).toUpperCase() + "(" + v.substring(1, v.length() - 1) + ")\"";    	   
// 		           if(n == 1) SPARQL += "n" + n + " " + v;
// 		           else SPARQL += ", n" + n + " " + v;
// 		       }
// 		        else
// 		       	   if(n == 1) SPARQL += "v" + n + " " + v;
// 		           else SPARQL += ", v" + n + " " + v;
// 	       }
           
//            // Create sparql statements in tmpSparql for the columns to be projected, e.g., ?s1 :domain ?v1 . This will be used later.
//            //                                         | This will get the symbol from tableSymbols for the e from e.domain |        | This will get domain from e.domain   |
//            if(key.contains(".")) tmpSparql += "\tOPTIONAL { ?" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))              + " :" + key.substring(key.lastIndexOf(".") + 1) + " ?v" + n + " }\n";
//                               // So tmpSparql will be appended with ?s1 :domain ?v1 .
//            else System.out.println("Column names without aliases are not yet supported. - " + key);
//            n++;
// 		}

		// unless as is specified, go with ?n1, ?n2, etc instead of ?v1, ?v2...		
		if (aggrColumnsAs.keySet().size() == 0 && !nonExistentColumns)
            if(connection.getConnectionDB().equals("OracleNoSQL")) SPARQL += " WHERE {";
            else SPARQL += "\n FROM TABLE(SEM_MATCH('SELECT * WHERE { ";
		else {
            if(connection.getConnectionDB().equals("OracleNoSQL")) SPARQL += " ";
            else SPARQL += "\n FROM TABLE(SEM_MATCH('SELECT ";
			int x = 1;
			for (String groupbyVars: groupby)
				SPARQL += groupbyVars + " ";
			for (int pull = groupby.size(); pull <= allCols.size(); pull++) {
				if (aggrColumnsAs.keySet().contains("?v" + pull)) {
					SPARQL += "(" + getAggregateSelect(allCols.get(pull - 1).toLowerCase())[0] 
						   + "(?v" + pull + ")" + " as ?n" + x + ") ";
					x++;
				}
				else {
					boolean alreadySelected = false;
		    		for (int j = 1; j <= groupby.size(); j++) {
		    			if (allCols.get(j - 1).equals(allCols.get(pull - 1)) && (j-1 != pull-1)) {
		    				alreadySelected = true;
		    				break;
		    			}
		    		}
		    		if (!alreadySelected) SPARQL += "?v" + pull + " ";
				}
			}if(connection.getConnectionDB().equals("OracleNoSQL")) SPARQL += " WHERE {";
            else SPARQL += "WHERE {\n";	
		}
		
		for (Map.Entry<String, String> entry : tableSymbols.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(connection.getConnectionDB().equals("OracleNoSQL")) {
                SPARQL += "GRAPH " + NoSQLNameSpacePrefix + ":" + key + "_SCHEMA" + " { ?" + value + " rdf:type " + NoSQLNameSpacePrefix + ":" + key + " } ";
            } else if (connectionType == "rdf_mode") {
                SPARQL += "\tGRAPH <" + key + "_" + sparqlHelper.getSchemaString() + "> { ?" + value + " rdf:type :" + key + " }\n";
            } else { //representing the default case 
                SPARQL += "\t?" + value + " rdf:type :" + key + " .\n";
            }
        }
        
        // Add tmpSparql from above to the SPARQL string.
        SPARQL += tmpSparql;
        
        // Add sparql statements to do joins to the SPARQL string.
	    n = 1;
	    for (String s : joinColumns) {
           // E.g., if s is e.n = d.n
           if( ! connection.getConnectionDB().equals("OracleNoSQL")) {
               // This will get the symbol from tableSymbols for the e from e.n and will get n from e.n
    	       SPARQL += "\t?" + tableSymbols.get(tablesAliases.get(s.split(" = ")[0].split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + s.split(" = ")[0].split("\\.")[1] + " ?j" + n + " .\n";
               // This will get the symbol from tableSymbols for the d from d.n and will get n from d.n
    	       SPARQL += "\t?" + tableSymbols.get(tablesAliases.get(s.split(" = ")[1].split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + s.split(" = ")[1].split("\\.")[1] + " ?j" + n + " .\n";
    	       // So SPARQL will be appended with:     
    	          // ?s1 :n ?j1 .
    	          // ?s2 :n ?j1 .
           } else {
               // This will get the symbol from tableSymbols for the e from e.n and will get n from e.n
               SPARQL += " GRAPH ?gj" + n + " { ?" + tableSymbols.get(tablesAliases.get(s.split(" = ")[0].split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + s.split(" = ")[0].split("\\.")[1] + " ?j" + n + " . } ";
               // This will get the symbol from tableSymbols for the d from d.n and will get n from d.n
               SPARQL += " GRAPH ?gj" + (n+1) + " { ?" + tableSymbols.get(tablesAliases.get(s.split(" = ")[1].split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + s.split(" = ")[1].split("\\.")[1] + " ?j" + n + " .  } ";
               // So SPARQL will be appended with:     
                  // ?s1 :n ?j1 .
                  // ?s2 :n ?j1 .
           }
           n++;
	    }
	    
	    if(filters != null) if (filters.size() != 0) SPARQL += filters.get(0);
        
        n = 0;
		String orderbyStr = orderby.toString().substring(1 , orderby.toString().length() - 1);
		String havingStr = having.toString().substring(1 , having.toString().length() - 1);

        SPARQL += "}";
        if (groupby.size() > 0) {
        	SPARQL += "\nGROUPBY ";
	        for (String groupbyElem: groupby) {
	        	if (n++ > 0) SPARQL += " ";
	        	SPARQL += groupbyElem;
	        }
        }
        if (havingStr.length() > 0)	SPARQL += "\n" + havingStr;
        if (orderbyStr.length() > 0) SPARQL += "\n" + orderbyStr;
        if(connection.getConnectionDB().equals("OracleNoSQL")) SPARQL += "";
        else SPARQL += "\n" + "' ,\nSEM_MODELS('" + connection.getModel() + "'), null,\nSEM_ALIASES( SEM_ALIAS('', '" + connection.getNamespace() + "')), null) )";
		return SPARQL;		

/* Test python statements:
global_conn = connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'rdf_mode' 'EVENTS'
SQL on global_conn """select * from Events e where EventType='AUTHENTICATION_FAILED_EVENT'"""

emp_conn = connectTo 'jdbc:oracle:thin:@rising-sun.microlab.cs.utexas.edu:1521:orcl' 'CS347_prof' 'orcl_prof' 'rdf_mode' 'ReLEMP'
SQL on emp_conn """select e.EMPNO, e.ENAME from EMP e"""
SQL on emp_conn """select * from EMP"""
SQL on emp_conn """select * from EMP e where e.EMPNO = 7934"""
SQL on emp_conn """select * from emp e join dept d on (e.deptno = d.deptno) where e.EMPNO = 7934"""
*/
		
/* Here's an example of this process:
		
>>> SQL on global_conn """select * from EVENTS where EventType='AUTHENTICATION_FAILED_EVENT'"""
ReLstmt is: select * from EVENTS where EventType='AUTHENTICATION_FAILED_EVENT'
jsqlstmt is: SELECT * FROM EVENTS WHERE EventType = 'AUTHENTICATION_FAILED_EVENT'
SQL statement: |SELECT * FROM EVENTS WHERE EventType = 'AUTHENTICATION_FAILED_EVENT'|
In executeRdfSelect, selectStmt is: select distinct col from table(sem_match(
'select * where {
	?col rdfs:domain :EVENTS .
	?s1 ?col ?v .
}' ,
SEM_MODELS('EVENTS_CS347_PROF'), null,
SEM_ALIASES( SEM_ALIAS('', 'http://www.example.org/people.owl#')), null) )
Column name EventType is ambiguously defined, using EVENTS.eventType

visitSelect_buildSPARQL Structures necessary to build the SPARQL statement:
	 - plainSelect: SELECT * FROM EVENTS WHERE EventType = 'AUTHENTICATION_FAILED_EVENT'
	 - tables: [EVENTS]
	 - tablesAliases: {EVENTS=EVENTS}
	 - tables2alias: {EVENTS=EVENTS}
	 - tableSymbols: {EVENTS=s1}
	 - columnNames: [EVENTS.userID, EVENTS.eventDate, EVENTS.sentDelayedToLocalSelf, EVENTS.requestInfo_platform, EVENTS.sentToLocalSelf, EVENTS.groupName, EVENTS.inviterID, EVENTS.DBUNIQUEID, EVENTS.requestInfo_userAgent, EVENTS.sizeInBytes, EVENTS.creationDate, EVENTS.serialNumber, EVENTS.extension, EVENTS.IP, EVENTS.displayName, EVENTS.sentToRemoteSelf, EVENTS.guest, EVENTS.osInfo, EVENTS.sentDelayedToLocal, EVENTS.ID, EVENTS.errorReasonCode, EVENTS.fromGroupID, EVENTS.who, EVENTS.groupID, EVENTS.source, EVENTS.requestInfo_serialNumber, EVENTS.email, EVENTS.requestInfo_IP, EVENTS.requestInfo_pairingToken, EVENTS.eventBody, EVENTS.port, EVENTS.version, EVENTS.reason, EVENTS.fromUserAccountID, EVENTS.sentToRemote, EVENTS.loginRefuseRule, EVENTS.authenticationID, EVENTS.userAccountID, EVENTS.devicePairingID, EVENTS.sentToLocal, EVENTS.inviterUserID, EVENTS.productID, EVENTS.enabled, EVENTS.language, EVENTS.usageType, EVENTS.eventType, EVENTS.domain, EVENTS.eventID, EVENTS.toUserAccountID, EVENTS.invitationToken, EVENTS.requestInfo_version, EVENTS.errorCode, EVENTS.TCAcceptanceDate, EVENTS.requestInfo_userLogin, EVENTS.adminAccountID, EVENTS.platform, EVENTS.toGroupID]
	 - columnsAs: {*=*}
	 - joinColumns: []
	 - filters: [	?s1 :eventType ?f1 .
	FILTER(?f1 = "AUTHENTICATION_FAILED_EVENT") ]
	 - orderby: []
In executeRdfSelect, selectStmt is: select distinct col from table(sem_match(
'select * where {
	?col rdfs:domain :EVENTS .
	?s1 ?col ?v .
	?s1 :eventType ?f1 .
	FILTER(?f1 = "AUTHENTICATION_FAILED_EVENT") }' ,
SEM_MODELS('EVENTS_CS347_PROF'), null,
SEM_ALIASES( SEM_ALIAS('', 'http://www.example.org/people.owl#')), null) )
	 - columnsAs: {EVENTS.eventDate=eventDate, EVENTS.requestInfo_platform=requestInfo_platform, EVENTS.DBUNIQUEID=DBUNIQUEID, EVENTS.requestInfo_userAgent=requestInfo_userAgent, EVENTS.guest=guest, EVENTS.ID=ID, EVENTS.errorReasonCode=errorReasonCode, EVENTS.groupID=groupID, EVENTS.source=source, EVENTS.requestInfo_serialNumber=requestInfo_serialNumber, EVENTS.requestInfo_IP=requestInfo_IP, EVENTS.requestInfo_pairingToken=requestInfo_pairingToken, EVENTS.eventBody=eventBody, EVENTS.version=version, EVENTS.userAccountID=userAccountID, EVENTS.eventType=eventType, EVENTS.domain=domain, EVENTS.eventID=eventID, EVENTS.requestInfo_version=requestInfo_version, EVENTS.errorCode=errorCode, EVENTS.requestInfo_userLogin=requestInfo_userLogin}
RDF conversion of select:
 |SELECT v1 eventDate, v2 requestInfo_platform, v3 DBUNIQUEID, v4 requestInfo_userAgent, v5 guest, v6 ID, v7 errorReasonCode, v8 groupID, v9 source, v10 requestInfo_serialNumber, v11 requestInfo_IP, v12 requestInfo_pairingToken, v13 eventBody, v14 version, v15 userAccountID, v16 eventType, v17 domain, v18 eventID, v19 requestInfo_version, v20 errorCode, v21 requestInfo_userLogin
 FROM TABLE(SEM_MATCH('SELECT * WHERE {
	?s1 rdf:type :EVENTS .
	OPTIONAL { ?s1 :eventDate ?v1 }
	OPTIONAL { ?s1 :requestInfo_platform ?v2 }
	OPTIONAL { ?s1 :DBUNIQUEID ?v3 }
	OPTIONAL { ?s1 :requestInfo_userAgent ?v4 }
	OPTIONAL { ?s1 :guest ?v5 }
	OPTIONAL { ?s1 :ID ?v6 }
	OPTIONAL { ?s1 :errorReasonCode ?v7 }
	OPTIONAL { ?s1 :groupID ?v8 }
	OPTIONAL { ?s1 :source ?v9 }
	OPTIONAL { ?s1 :requestInfo_serialNumber ?v10 }
	OPTIONAL { ?s1 :requestInfo_IP ?v11 }
	OPTIONAL { ?s1 :requestInfo_pairingToken ?v12 }
	OPTIONAL { ?s1 :eventBody ?v13 }
	OPTIONAL { ?s1 :version ?v14 }
	OPTIONAL { ?s1 :userAccountID ?v15 }
	OPTIONAL { ?s1 :eventType ?v16 }
	OPTIONAL { ?s1 :domain ?v17 }
	OPTIONAL { ?s1 :eventID ?v18 }
	OPTIONAL { ?s1 :requestInfo_version ?v19 }
	OPTIONAL { ?s1 :errorCode ?v20 }
	OPTIONAL { ?s1 :requestInfo_userLogin ?v21 }
	?s1 :eventType ?f1 .
	FILTER(?f1 = "AUTHENTICATION_FAILED_EVENT") }' ,
SEM_MODELS('EVENTS_CS347_PROF'), null,
SEM_ALIASES( SEM_ALIAS('', 'http://www.example.org/people.owl#')), null) )| END
(('EVENTDATE', 'REQUESTINFO_PLATFORM', 'DBUNIQUEID', 'REQUESTINFO_USERAGENT', 'GUEST', 'ID', 'ERRORREASONCODE', 'GROUPID', 'SOURCE', 'REQUESTINFO_SERIALNUMBER', 'REQUESTINFO_IP', 'REQUESTINFO_PAIRINGTOKEN', 'EVENTBODY', 'VERSION', 'USERACCOUNTID', 'EVENTTYPE', 'DOMAIN', 'EVENTID', 'REQUESTINFO_VERSION', 'ERRORCODE', 'REQUESTINFO_USERLOGIN'), ('java.lang.NullPointerException',))
>>> 

*/

	}
	
	private String resolveColumnName(List<String> columnNames,String columnName) {
	   String tmpColumnName = "";
	   int cnt = 0;
	   for (String column : columnNames) {
		  if(columnName.contains(".")) {
			  if(columnName.toUpperCase().equals(column.toUpperCase()))
				 tmpColumnName = column;
		  }
		  else {
			  if(columnName.toUpperCase().equals(column.substring(column.lastIndexOf(".") + 1).toUpperCase()))
				 tmpColumnName = column;
		         cnt++;
		  }
	   }
	   if(cnt > 1) {
		   System.out.println("Column name " + columnName + " is ambiguously defined, using " + tmpColumnName);
	   }
	   return tmpColumnName;
	}

	private void p(String s) {
		System.out.println(s);
	}
	private String endOfStmt = "";	
	/* Stack of boolean to indicate weather or not we are
	 * currently at a subquery
	 */
	private Stack<Boolean> sq = new Stack<Boolean>();
	
	/* Store queries */
	private Stack<String> subq = new Stack<String>();
	
	/**
	 *
	 */
	@Override
	public void visit(SubSelect subSelect) {	
		sq.push(true);
		subSelect.getSelectBody().accept(this);
		
		temp = temp.substring(0, temp.indexOf("IN") + 2);
	}
	
	/**
	*	
	*/
	private String getColumns(LinkedHashMap<String,String> columnsAs){
        //creating columns for ORACLE SQL from regular SQL statement,
        //current part to create, e.g. SELECT E.A AS CS345, G.B CS370 FROM D AS E, F AS G ...
        //will result in SELECT A_D CS345, B_F CS370 FROM TABLE( SEM_MATCH('SELECT * WHERE {
        //note aliases mapping for table names where done while "visiting"
        
        String s = "";
            
        // If we are returning instances, make sure that we need to return the DBUNIQUEID as part of the select. 
        if (this.returns_instances_of != null)
        {
            for (String return_type : returns_instances_of)
            {
                if (columnsAs.get(return_type+".DBUNIQUEID") == null)
                {
                    columnsAs.put(return_type+".DBUNIQUEID", return_type+".DBUNIQUEID"); 
                }
                
            }

        }
        
        int var = 1;
        for (String entry : columnsAs.keySet()) {
            if (connection.getDebug() == "debug") System.out.println("SQLVisitor-getColumns, entry: " + entry);
            if (connection.getDebug() == "debug") System.out.println("SQLVisitor-getColumns, colname(entry): " + colname(entry));
            if (connection.getDebug() == "debug") System.out.println("SQLVisitor-getColumns, tablename(entry): " + tablename(entry));
            if (connection.getDebug() == "debug") System.out.println("SQLVisitor-getColumns, columnsAs.get(entry): " + columnsAs.get(entry));
            if (connection.getDebug() == "debug") System.out.println("SQLVisitor-getColumns, colname(columnsAs.get(entry)): " + colname(columnsAs.get(entry)));
            // So if we are returning instances. We need a way to determine which instance the data belongs too.
            // To do this i append the tablename_ as part of the column's "as" statement, so the columns returned will start
            // with the name of the instance for which they belong too. 
            if (returns_instances_of != null)
            {
                // s += colname(entry) + "_" + tablename(entry) + " " + tablename(entry)+"_"+colname(columnsAs.get(entry)) + ", ";
                s += "v" + var++ + " " + tablename(entry)+"_"+colname(columnsAs.get(entry)) + ", ";
            }
            else
            {
                // s += colname(entry) + "_" + tablename(entry) + " " + colname(columnsAs.get(entry)) + ", ";
                s += "v" + var++ + " " + colname(columnsAs.get(entry)) + ", ";
            }
        }
        if(s.length() < 1) return s;
        s = s.substring(0,s.length()-2);
        s += " from table(\n\tSEM_MATCH('SELECT * WHERE {\n\t";
        return s;
	}
	
	/**
	 *
	 */
	public String getColumnsVar(LinkedHashMap<String,String> columnsAs){
			//Following example from above SPARQL query will looks like:
			//	?thisD rdf:type :D .
			//	?thisD :A ?A_D .
			//	?thisF rdf:type :F .
			//	?thisF :B :B_F
			String s = "";
			String currTable = "";
			int var = 1;
			for (String item : columnsAs.keySet()) {
				String tableName = tablename(item);
				String colName = colname(item);
				String tempTableName = tableName;
				int minlen = Math.min(tempTableName.length(), 6);
				if(!tempTableName.equals(currTable)){
					s += "?" + tempTableName.substring(0, minlen) + " rdf:type :" + tableName + " . \n\t";
					currTable = tempTableName;
				}
				// s += "?" + tempTableName.substring(0, minlen) + " :" + colName + " ?" + colName + "_" + tempTableName + " . \n\t";
				s += "?" + tempTableName.substring(0, minlen) + " :" + colName + " ?v" + var++ + " . \n\t";
			}
			return s;
	}
	
	/**
	 *
	 */
	public String getJoins(List<String> matches){
			//Adding JOINS, e.g.FROM D AS E JOIN F AS G ON E.STUDENID = G.STUDENTID
			//will result in:	?thisD :STUDENTID ?j1 . ?thisF :STUDENTID ?j1 .
			String s = "";
			for(Iterator fI=matches.iterator(); fI.hasNext();) {
				String item = (String)fI.next();
				s += item + "\n\t";
			}
			return s;
	}
	
	/**
	 *
	 */
	public String getFilters(List<String> filters, LinkedHashMap<String, String> columnsAs){
			// Adding the filters to SEM_MATCH, e.g WHERE D.GRADES > 80
			//results in: FILTER( GRADES_D > 80)
			String s = "";
			if ( !filters.isEmpty() ) {
				for(Iterator fI=filters.iterator(); fI.hasNext();) {
					String item = ((String)fI.next()).trim();
					//String item = "?PETID_PETS = 1001";
    				String t = item.split("\\s+")[0];
    				String colName = t.substring(0,t.lastIndexOf("_")).replace("?", "");
    				String tblName = t.substring(t.lastIndexOf("_") + 1);
    				int minlen = Math.min(tblName.length(), 6);	
    				
					// if(!columnsAs.containsKey(tblName + "." + colName)){      Phil commented this and the other commented line below as a hack to get the where clauses to work
    						s += "?" + tblName.substring(0, minlen) + " :" + colName + " " + t + " .\n\t";
					// }
					
				}

				s += "FILTER ( ";
				for(Iterator fI=filters.iterator(); fI.hasNext();) {
					String item = ((String)fI.next()).trim();
					String tblName = getFilterTableName(item);
					String dataValue = item.substring(item.lastIndexOf(" ") + 1);
					String tempTV = dataValue;
					dataValue = dataValue.replaceAll("^\"|\"$", "");
					
					//Numeric? needed for special format
					if(!isNumeric(dataValue)){
						item = item.replace(tempTV, dataValue);
						item = item.substring(0, item.lastIndexOf(" ") + 1) + "\"" + item.substring(item.lastIndexOf(" ") + 1) + "\"";
					}
					s += item + " ";
					if (fI.hasNext()) {
						s += " && ";
					}
				}
				s += " ) \n\t";
						}
			return s;
	}
	
	/**
	 *
	 */
	public String getEndOfStmt(List<String> internalColumns, List<String> orderby){
			String s = "";
			//INTERNAL COLUMNS
			for (String item : internalColumns) {
				String tblName = tablename(item);
				String colName = colname(item);
    					
				int minlen = Math.min(tblName.length(), 6);
				s += "?" + tblName.substring(0, minlen) + " :" + colName + " ?" + colName + "_" + tblName + " . \n\t";
			}
			//working on progress...
			s += "}";
		
			// Adding order by to SEM_MATCH
			if(orderby.size()>0) {
				s += "\n\t ORDER BY ";
				for(Iterator fI=orderby.iterator(); fI.hasNext();) {
					String item = (String)fI.next();
					s += item + " ";
				}
			}
			//Adding model and alias to ORACLE SQL statement
			s += "',\n\t SEM_MODELS('" + connection.getModel() + "'), ";
			s += "null,\n\t SEM_ALIASES( SEM_ALIAS('', 'http://www.example.org/people.owl#')), null) )";
			return s;
	}
	
	/**
	 *
	 */
	public void printAllLists(List<String> filters, List<String> tables, List<String> internalColumns, 
				  List<String> matches, List<String> orderby, LinkedHashMap<String,String> columnsAs){
		@SuppressWarnings("unchecked")
		List<String> colAs = new ArrayList<String>();
    		for(String s : columnsAs.keySet()){
    			colAs.add(s); 
    		}
		List<List<String>> lists = 
				  Arrays.asList(filters, tables, colAs, internalColumns, matches, orderby);
		String[] listsNames = {"Filters:", "Tables:", "Columns:", "Internal Columns:", 
				       "Matches (or Joins?):", "Order By:"};
		for(int i = 0; i < lists.size(); i++){
			printList(listsNames[i],lists.get(i));
		}
	}
	
	/**
	 *
	 */
	private void printList(String field, List<String> list){ 
		if (connection.getDebug() == "debug") System.out.println(field);
		if (connection.getDebug() == "debug") for(Iterator fI=list.iterator(); fI.hasNext();) {
			System.out.println("\t"+fI.next());
		}
	}
	
	/**
	 *
	 */
	static public String tablename(String item) {
		if(item.indexOf('.')>0)
			return item.substring(0,item.indexOf('.'));
		return "tbl";
	}
	
	/**
	 *
	 */
	static public String getFilterTableName(String str) {
        	return (str.substring(0, str.indexOf(" "))).substring(str.indexOf("_") + 1);
    	}
	
	/**
	 *
	 */
	static public String getFilterColumnName(String str) {
        	return str.substring(1, str.lastIndexOf("_"));
    	}
    
     /**
	 *
	 */
	static public String colname(String item) {
		if(item.indexOf('.')>0)
			return item.substring(item.indexOf('.')+1);
		return item;
	}
	
	/**
	 *
	 */
	static public String filter(String item, String comparison, String value) {
		String tempTableName = tablename(item);
		return "?" + colname(item) + "_" + tempTableName + " " + comparison + " "  + value;
	}
	/**
	 *
	 */
	public String match(String item, String value, boolean isValue) {
		String joinString = "";
		String tblName = tablename(item);
		if(isValue) {
			join = false;
			// this is the case of columName = someValue
			return " ?" + colname(item) + "_" + tblName + " = " + value;
		} else {
			join = true;
			// this is the case of joining two tables
			// first half of join 
    					
			int minlen = Math.min(tblName.length(), 6);
			joinString += "?" + tblName.substring(0, minlen) + " " + NoSQLNameSpacePrefix + ":" + colname(item) + " ?j" + joinInc + " . ";
			// second half of join
			tblName = tablename(value);
			minlen = Math.min(tblName.length(), 6);
			joinString += "?" + tblName.substring(0, minlen) + " " + NoSQLNameSpacePrefix + ":" + colname(value) + " ?j" + joinInc + " . ";
			joinInc++;

			return joinString;
		}
	}

	public String[] getAggregateSelect(String selectSection){
		String[] result = {"NOT FOUND", ""};
		for(String aggregate: aggregates)
			if(selectSection.matches(aggregate + " *[(]")){
				result[0] = aggregate;
				break;
			}

		if(result[0].equals("NOT FOUND"))
			return null;

		String restofStmnt = selectSection.substring(result[0].length(), selectSection.length());
		String aggrCol = selectSection.substring(selectSection.indexOf("("), selectSection.indexOf(")") + 1);
		// not invalid syntax; trim the parentheses off
		aggrCol = aggrCol.substring(aggrCol.indexOf("(") + 1, aggrCol.indexOf(")"));
		result[1] = aggrCol;
		
		return result;
	}
	
	/**
	 *
	 */
	static public List<String> getAllColsFromTbl(String tableName) throws SQLException, JSQLParserException{
		List<String> tblCols = new ArrayList<String>();
		/* Tmp Comment
		List<String> temp = sd.getAllColumns(connection,tableName);
		for(Iterator fI=temp.iterator(); fI.hasNext();) {
			String col = (String)fI.next();
			if(!col.equals("type"))
				tblCols.add(col);
		}
		*/
		//System.out.println(Arrays.toString(tCols.toArray()));
		return tblCols;
	}

	/**
	 *
	 */
	static public boolean isNumeric(String str){
		DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
		char minusSign = currentLocaleSymbols.getMinusSign();

		if (!Character.isDigit(str.charAt(0)) && str.charAt(0) != minusSign) 
			return false;

		boolean isDecimal = false;
		char decimalSeparator = currentLocaleSymbols.getDecimalSeparator();

		for (char chr : str.substring(1).toCharArray()){
			if(!Character.isDigit(chr)){
				if(chr == decimalSeparator && !isDecimal){
					isDecimal = true;
					continue;
				}
				return false;
			}
		}
		return true;
	}
	
	/**
	 *
	 */
	public String getKeyByValue(HashMap<String, String> map, String value) {
		for (String entry : map.keySet()) {
			if (map.get(entry).equals(value)) {
				return entry;
			}
		}
		return null;
	}
	
	/**
	 *
	 */
	public void testMethod(String sub) {
		try {
			PlainSelect plainSelect2 = (PlainSelect) ((Select) parserManager.parse(new StringReader(sub))).getSelectBody();        	
			//System.out.println("Plainselect2 : " + plainSelect2);
			visit(plainSelect2);
			}
		catch (JSQLParserException e) {
			System.out.println("Null");
			//return null;
		}
	}

	/**
	 *
	 */
	@Override
	public void visit(Table tableName) {
		//Changed this to getFullyQualifiedName from getWholeTable; jsqlParser 9.1
		temp = tableName.getFullyQualifiedName();
	}
	private boolean sub = false;
	

	@Override
	public void visit(Addition addition) {
		addition.getLeftExpression().accept(this);
		String t = "(" + temp;
		t += addition.getStringExpression();
		addition.getRightExpression().accept(this);
		t += temp + ") ";
		temp = t;
	}

	@Override
	public void visit(AndExpression andExpression) {
		wasEquals = false;	//Does the expression has the equals sign?
		andExpression.getLeftExpression().accept(this);
		if(wasEquals) {	//If it does add it to matches
			matches.add(temp);
		} else {
			//Need tablesAliases	
			
			String tableName = getFilterTableName(temp.trim());
			String dataValue = temp.substring(temp.lastIndexOf(" ") + 1);
			//Right now if is not a numeric value, then it will be consider a string
			if(!isNumeric(dataValue))
				temp =temp.substring(0, temp.lastIndexOf(" ") + 1) + "\"" + temp.substring(temp.lastIndexOf(" ") + 1) + "\"";
			//validate
			if(!tableName.equals("tbl")){
/*
				//validate table name
				if(!(ownException = validator.validateTable(tablesAliases, tableName, temp.substring(temp.indexOf("?")+1,temp.lastIndexOf("_")))).isEmpty()){
					return;
				}
*/
				
				//Add filter
				if(tablesAliases.containsKey(tableName))
					filters.add(temp.replace(tableName, tablesAliases.get(tableName)));
			}
			else{
				String ct = temp.trim().split("\\s+")[0];
				String colName = ct.substring(1, ct.lastIndexOf("_"));
/*	
				//validate column
				colName = validator.validateColumn(tablesColumns, colName);
				if(!validator.isValidColumn()){
					ownException = colName;
					return;
				}
*/

				//validate table name, it does not contain alias.
				tableName = tablename(colName);
				//validate column
					
				//Add filter
				filters.add(temp.replace("_tbl","_" + tableName));
			}

		}
		wasEquals = false;
		andExpression.getRightExpression().accept(this);
	}

	@Override
	public void visit(Between between) {
		between.getLeftExpression().accept(this);
		between.getBetweenExpressionStart().accept(this);
		between.getBetweenExpressionEnd().accept(this);
	}

	@Override
	public void visit(Column tableColumn) {
		// temp = tableColumn.getWholeColumnName();
	}

	@Override
	public void visit(Division division) {
		division.getLeftExpression().accept(this);
		String t = "(" + temp;
		t += division.getStringExpression();
		division.getRightExpression().accept(this);
		t += temp + ") ";
		temp = t;
	}

	@Override
	public void visit(DoubleValue doubleValue) {
		temp = Double.toString(doubleValue.getValue());
	}

	private boolean join;
	public void visit(EqualsTo equalsTo) {
		equalsTo.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "";
	
		equalsTo.getRightExpression().accept(this);
		if(equalsTo.getRightExpression() instanceof Column) {
			join = true; 
			temp = match(item, temp, false);
			wasEquals = true;
		} else {
			temp = match(item, temp, true);
		}
		//wasEquals = true;
	}

	@Override
	public void visit(Function function) {
	}

	
	@Override
	public void visit(GreaterThan greaterThan) {
		greaterThan.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "";
		if(greaterThan.isNot()) {
			comparison = "<=";
		} else {
			comparison = ">";
		}
		greaterThan.getRightExpression().accept(this);
		String value = temp;
		temp = filter(item, comparison, value);
	}

	@Override
	public void visit(GreaterThanEquals greaterThanEquals) {
		greaterThanEquals.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "";
		if(greaterThanEquals.isNot()) {
			comparison = "<";
		} else {
			comparison = ">=";
		}
		greaterThanEquals.getRightExpression().accept(this);
		String value = temp;
		temp = filter(item, comparison, value);
	}

    @Override
    public void visit(InExpression inExpression) {
        inExpression.getLeftExpression().accept(this);
        inExpression.getRightItemsList().accept(this);
    }

	//(java/net/sf/jsqlparser/statement/ExpressionDeParser.java)
	@Override
	public void visit(CastExpression cast){}

	@Override
	public void visit(Modulo modulo){}

	@Override
	public void visit(AnalyticExpression aexpr){}

	@Override
	public void visit(ExtractExpression eexpr){}

	@Override
	public void visit(IntervalExpression iexpr){}

	@Override
    public void visit(JdbcNamedParameter jdbcNamedParameter){}

	@Override
	public void visit(OracleHierarchicalExpression oexpr){}

	@Override
	public void visit(SignedExpression signedExpression){}

	@Override
	public void visit(RegExpMatchOperator rexpr){}
    
	@Override
    public void visit(JsonExpression jsonExpr){}

	@Override
	public void visit(RegExpMySQLOperator regExpMySQLOperator){}

	//net/sf/jsqlparser/expression/operators/relational/ItemsListVisitor

	@Override
	public void visit(MultiExpressionList multiExprList){}
	/*
	 * Done implementing ExpressionDeParser methods
	 */

	@Override
	public void visit(IsNullExpression isNullExpression) {
	}

	@Override
	public void visit(JdbcParameter jdbcParameter) {
	}

	@Override
	public void visit(LikeExpression likeExpression) {
		visitBinaryExpression(likeExpression);
	}

	@Override
	public void visit(ExistsExpression existsExpression) {
		existsExpression.getRightExpression().accept(this);
	}

	@Override
	public void visit(LongValue longValue) {
		temp = longValue.getStringValue();
	}

	@Override
	public void visit(MinorThan minorThan) {
		minorThan.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "";
		if(minorThan.isNot()) {
			comparison = ">=";
		} else {
			comparison = "<";
		}
		minorThan.getRightExpression().accept(this);
		String value = temp;
		temp = filter(item,comparison, value);
	}

	@Override
	public void visit(MinorThanEquals minorThanEquals) {
		minorThanEquals.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "";
		if(minorThanEquals.isNot()) {
			comparison = ">";
		} else {
			comparison = "<=";
		}
		minorThanEquals.getRightExpression().accept(this);
		String value = temp;
		temp = filter(item, comparison, value);
	}

	@Override
	public void visit(Multiplication multiplication) {
		multiplication.getLeftExpression().accept(this);
		String t = "(" + temp;
		t += multiplication.getStringExpression();
		multiplication.getRightExpression().accept(this);
		t += temp + ") ";
		temp = t;
	}

	@Override
	public void visit(NotEqualsTo notEqualsTo) {
		notEqualsTo.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "!=";
		notEqualsTo.getRightExpression().accept(this);
		String value = temp;
		/*
		if(notEqualsTo.getRightExpression() instanceof Column)
			value += "?"+tablename(temp)+colname(temp);
		else
			value += temp;
		*/
		temp = filter(item, comparison, value);
	}

	@Override
	public void visit(NullValue nullValue) {
		temp = nullValue.toString();
	}

	@Override
	public void visit(OrExpression orExpression) {
		orExpression.getLeftExpression().accept(this);
		String left = temp;
		orExpression.getRightExpression().accept(this);
		temp = "FILTER( "+left+" || "+temp+" )";
		wasEquals = false;
	}

	@Override
	public void visit(Parenthesis parenthesis) {
		String t = "";
		if(parenthesis.isNot())
			t += "!";
		parenthesis.getExpression().accept(this);
		t += "("+temp+")";
		temp = t;
	}

	@Override
	public void visit(StringValue stringValue) {
		temp = stringValue.getValue();
	}

	@Override
	public void visit(Subtraction subtraction) {
		subtraction.getLeftExpression().accept(this);
		String t = "(" + temp;
		t += subtraction.getStringExpression();
		subtraction.getRightExpression().accept(this);
		t += temp + ") ";
		temp = t;
	}

	public void visitBinaryExpression(BinaryExpression binaryExpression) {
		binaryExpression.getLeftExpression().accept(this);
		binaryExpression.getRightExpression().accept(this);
	}

	@Override
	public void visit(ExpressionList expressionList) {
		String t = "(";
		for (Iterator iter = expressionList.getExpressions().iterator(); iter.hasNext();) {
			Expression expression = (Expression) iter.next();
			expression.accept(this);
			t += temp + ", ";
		}
		t = t.substring(0, t.length()-2) + ")";
		temp = t;

	}

	@Override
	public void visit(DateValue dateValue) {
		temp = dateValue.getValue().toString();
	}
	
	@Override
	public void visit(TimestampValue timestampValue) {
		temp = timestampValue.getValue().toString();
	}
	
	@Override
	public void visit(TimeValue timeValue) {
		temp = timeValue.getValue().toString();
	}

	@Override
	public void visit(CaseExpression caseExpression) {
	}

	@Override
	public void visit(WhenClause whenClause) {
	}
	
	@Override
	public void visit(BitwiseXor bitwiseXor) {
	}
	
	@Override
	public void visit(BitwiseOr bitwiseOr) {
	}
	
	@Override
	public void visit(BitwiseAnd bitwiseAnd) {
	}
	
	@Override
	public void visit(Matches matches) {
	}
	
	@Override
	public void visit(Concat concat) {
	}

	@Override
	public void visit(AllComparisonExpression allComparisonExpression) {
		allComparisonExpression.getSubSelect().getSelectBody().accept(this);
	}

	@Override
	public void visit(AnyComparisonExpression anyComparisonExpression) {
		anyComparisonExpression.getSubSelect().getSelectBody().accept(this);
	}

	@Override
	public void visit(SubJoin subjoin) {
		subjoin.getLeft().accept(this);
		subjoin.getJoin().getRightItem().accept(this);
	}
	
	//SELECT ItemS
	@Override
	public void visit(AllColumns columns) {
		temp = "*";
	}
	
	@Override
	public void visit(AllTableColumns columns) {
		temp = "*";
	}
	
	@Override
	public void visit(SelectExpressionItem item) {
		item.getExpression().accept(this);
	}
	
	//Order by visitor Jesse: Changed this method to work. Check SelectDeParser in src/main/java/net/sf/jsqlparser/util/deparser
	@Override
	public void visit(OrderByElement order) {
		order.getExpression().accept(this);
		if(order.isAsc()) {
			temp = " ASC( ?" + colname(temp) + "_" + tablename(temp) + " )";
		} else {
			temp = " DESC( ?" + colname(temp) + "_" + tablename(temp) + " )";
		}
	}

	private class ownIllegalSQLException extends IllegalArgumentException
	{
	public ownIllegalSQLException( String message )
	{
	  super( message );
	}
	}

}