package org.python.ReL;

import com.hp.hpl.jena.rdf.model.Statement;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import oracle.jdbc.OracleConnection;
import org.python.core.PyObject;

import java.io.StringReader;
import java.sql.SQLException;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class SQLVisitor extends SelectDeParser implements SelectVisitor, FromItemVisitor, ExpressionVisitor, ItemsListVisitor, SelectItemVisitor, OrderByVisitor {

	private static String temp;
	private int n = 1;
	private Boolean wasEquals;

	/* Store queries */
	private Stack<String> subq = new Stack<String>();

	/* Boolean to indicate weather or not we are
     * currently at a subquery
     */
	private boolean subFromBefore =  false;
	private boolean subFromAfter = false;
	private boolean subWhereBefore = false;
	private boolean subWhereAfter = false;
	private boolean subWhereLeft = false;
	private boolean subWhereRight = false;

	private boolean subJoinBefore = false;
	private boolean subJoinAfter = false;
	private boolean outerLeft = false;
	private List<String> subTables;
	private HashMap<String, String> subTablesAliases;
	private HashMap<String, String> subTables2alias;
	private LinkedHashMap<String,String> subColumnsAs;
	private LinkedHashMap<String, String> subAggrColumnsAs;
	private HashMap<String,String> subTableSymbols;
	private List<String> subColumnNames = new ArrayList<String>();
	private List<String> subColumns = new ArrayList<String>();

	private static int joinInc = 1; // for each join this will be incremented and used in a variable name.

	private ArrayList<String> allCols = new ArrayList<String>();
	private ArrayList<String> subAllCols = new ArrayList<String>();
	private static String[] aggregates = {"avg", "count", "max", "min", "sum"}; // Input as lower case.
	private static String[] inequalities = {" = ", " > ", " < ", " >= ", " <= ", " != "};

	public String url = "";
	public String uname = "";
	public PyRelConnection connection;
	public Statement stmt;

	private CCJSqlParserManager parserManager = new CCJSqlParserManager();

	private List<String> matches;
	private HashMap<String, String> tablesAliases;
	private List<String> filters;

	String NoSQLNameSpacePrefix = "";

	public SQLVisitor(PyObject conn)
	{
		this.connection = (PyRelConnection) conn;
		if (connection.getConnectionDB().equals("OracleNoSQL"))
			NoSQLNameSpacePrefix = connection.getDatabase().getNameSpacePrefix();
	}

	public void doDrop(Drop stmt, OracleConnection conn)
	{
		String tableToDrop = stmt.getName();
		String command = "";
	}

	public void doInsert(Insert stmt, String getConnectionType) throws SQLException
	{//New method signature
		if (stmt.getColumns() != null) {
			this.connectionType = getConnectionType;
			Iterator valsIt = ((ExpressionList) stmt.getItemsList()).getExpressions().iterator();
			String attvalPairs = "";

			String COMMA = "";
			for (Iterator colsIt = stmt.getColumns().iterator(); colsIt.hasNext(); ) {
				// CAUTION - the following replaceAll statements remove all single and double quates and special characters for the column names and column values.
				String attr = COMMA + ((Column) colsIt.next()).getColumnName()
						.replaceAll("'", "")
						.replaceAll("\"", "")
						.replaceAll("[^ -~]+", "");
				Object attrValue = valsIt.next();
				String valStr = (attrValue.toString().replaceAll("'", "")).replaceAll("\'", "")
						.replaceAll("\"", "")
						.replaceAll("[^ -~]+", "");
				// CAUTION - the following replaceAll statement replaces all TO_DATE statements with the date as a string.
				if (valStr.toUpperCase().contains("TO_DATE")) {
					valStr = valStr.toUpperCase().replaceAll(".*[(]", "").replaceAll(" .*", "");
				}
				attvalPairs += attr + " := \"" + valStr + "\" ";
				COMMA = ",";
			}

			ProcessLanguages processLanguage = new ProcessLanguages(connection);
//			processLanguage.processSIM("INSERT " + stmt.getTable().toString() + " ( " + attvalPairs + ")");
		}
	}

	public void doCreateTable(CreateTable stmt) throws SQLException
	{
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
				ColumnDefinition col = (ColumnDefinition) colsIt.next();
				attribute = col.getColumnName();
				type = col.getColDataType().getDataType();

				if (type.toLowerCase().equals("numeric") || type.toLowerCase().equals("decimal") ||
						type.toLowerCase().equals("real")) {
					xsdType = "xsd:decimal"; // was decimal
				}
				else if (type.toLowerCase().equals("varchar") || type.toLowerCase().equals("varchar2")) {
					xsdType = "xsd:string"; // was string
				}
				else if (type.toLowerCase().equals("bit") || type.toLowerCase().equals("tinyint") ||
						type.toLowerCase().equals("bigint")) {
					xsdType = "xsd:integer"; // was integer
				}
				else if (type.toLowerCase().equals("date")) {
					xsdType = "xsd:date"; // was date
				}
				else {
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

	public String getSelect(Select select, Collection<String> instance_type_names, String getConnectionType) throws SQLException, JSQLParserException, ownIllegalSQLException
	{
		this.returns_instances_of = instance_type_names;
		// Initialize Validator
		SQLValidator validator = new SQLValidator();
		String SPARQL = "";
		this.connectionType = getConnectionType;

		if (connection.getDebug() == "debug") System.out.println("SQL statement: |" + select + "|");

		String test = select.toString().toUpperCase();

		if (test.contains("JOIN")) {
			if (test.contains("LEFT")) {
				outerLeft = true;
			}
			if (test.lastIndexOf(("SELECT")) > test.lastIndexOf(("JOIN"))) {
				subJoinBefore = true;
			}
		}

		select.getSelectBody().accept(this);

		SPARQL += subq.pop();

		System.out.println(" | " + SPARQL + "| END");

		if (connection.getDebug() == "debug") System.out.println("RDF conversion of select:\n | " + SPARQL + "| END");
		return SPARQL;
	}

	/* Current subquery */
	private String tempSub;

	public void visit(PlainSelect plainSelect) {

		tempSub = visitSelect_buildSPARQL(plainSelect);

		subq.add(tempSub);
	}

	/**
	 * Build the SPARQL for a SELECT statement.
	 */
	public String visitSelect_buildSPARQL(PlainSelect plainSelect) {
		List<String> filters = new ArrayList<String>();
		List<String> tables = new ArrayList<String>();
		List<String> orderby = new ArrayList<String>();
		List<String> groupby = new ArrayList<String>();
		List<String> having = new ArrayList<String>();
		HashMap<String,String> tablesAliases = new HashMap<String,String>();
		HashMap<String,String> tables2alias = new HashMap<String,String>();
		LinkedHashMap<String,String> columnsAs = new LinkedHashMap<String,String>();
		LinkedHashMap<String,String> aggrColumnsAs = new LinkedHashMap<String,String>();
		List<String> joinColumns = new ArrayList<String>();
		HashMap<String,String> tableSymbols = new HashMap<String,String>();
		List<String> RDFtables = null;
		List<String> RDFTableNames = new ArrayList<String>();
		List<String> columnNames = new ArrayList<String>();
		List<String> columns = null;
		String subqStr = "";

		// Get all table names from the RDF data.
		try {		// Get all of the classes (i.e., table names in this case) in the SCHEMA graph
			RDFtables = getSubjects(connection, connection.getSchemaString(), "rdf:type", "rdfs:Class");
			for (String t : RDFtables) {
				RDFTableNames.add(t);
			}
		} catch (SQLException ex) {
			System.out.println(ex);
		}
		// Check to see if there are any table names that differ only by case.
		for (String t1 : RDFTableNames) {
			for (String t2 : RDFTableNames) {
				if((!t1.equals(t2)) && t1.toUpperCase().equals(t2.toUpperCase()))
					System.out.println("Table name " + t1 + " and table name " + t2 + " appear in the RDF data, this is probably an error.");
			}
		}

		if (subFromAfter || subJoinAfter) {
			getSubStructures(tables, tablesAliases, tables2alias, columnsAs, aggrColumnsAs, tableSymbols);
		}

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

		if(!tmpTableName.equals("")) {
			tables.add(tmpTableName);
			tablesAliases.put((alias == null ? tmpTableName : alias), tmpTableName);
			tables2alias.put(tmpTableName, (alias == null ? tmpTableName : alias));
		}
		//else System.out.println("Table name " + tableName + " does not exist in the RDS data.");

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
				if( !tmpTableName.equals("")) {
					tables.add(tmpTableName);
					tablesAliases.put( (alias == null ? tmpTableName : alias), tmpTableName);
					tables2alias.put(tmpTableName, (alias == null ? tmpTableName : alias));
				}
				else System.out.println("Table name " + tableName + " does not exist in the RDS data.");
			}
		}
		// End getting table names and their aliases if any.

		if (!subWhereBefore) n = 1;

		if (subJoinAfter) {
			tables.addAll(subTables);
			tables2alias.putAll(subTables2alias);
			tablesAliases.putAll(subTablesAliases);
		} else {
			if (subColumnNames != null) columnNames = subColumnNames;
		}

		// This map (tableSymbols) of table names to unique, short symbols will be used later in several places.
		for (String table : tables) {
			tableSymbols.put(table, "s" + n);
			++n;
		}

		// Get all column names from tables.
		for (String table : tables) {
			try {		// Get all of the column names for each of the tables.
				columns = getSubjects(connection, table + "_" + connection.getSchemaString(), "rdfs:domain", NoSQLNameSpacePrefix + ":" + table);
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
		boolean b = false;

		if (subJoinAfter) allCols.clear();

		// Get column names to project.
		if(plainSelect.getSelectItems() != null) {
			int pos = 1;
			//gets the columns that are asked of
			for(Iterator i = plainSelect.getSelectItems().iterator(); i.hasNext();) {
				String columnName = "";
				SelectItem item = (SelectItem)i.next();
				item.accept(this);

				String aggregateElement[] = getAggregateSelect(item.toString().toLowerCase());
				pos = allCols.size() + 1;

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
						if (!allCols.contains(columnName)) allCols.add(columnName);
					}
				}
				if (subFromAfter || subJoinAfter) {
					for (Map.Entry<String, String> entry: columnsAs.entrySet()) {
						//System.out.println("The Key is: " + entry.getKey() + " " + entry.getValue());
						if (columnsAs.containsValue("*")) {
							//System.out.println("The Key1 is: " + entry.getKey() + " " + entry.getValue());
							b = true;
							columnsAs.clear();
							columnsAs = subColumnsAs;
							break;
						}
					}
					LinkedHashMap<String,String> ta = new LinkedHashMap<String,String>();
					for (Map.Entry<String, String> entry: subColumnsAs.entrySet()) {
						if (columnsAs.containsValue(entry.getValue())) {
							ta.put(entry.getKey(), entry.getValue());
						} else {
							if (subJoinAfter) ta.put(entry.getKey(), entry.getValue());
						}
					}
					if (!b) {
						if (ta.size() > 0) {
							columnsAs.clear();
							columnsAs.putAll(ta);
						} else {
							System.out.println("The columns are not part of the subquery");
							columnsAs.clear();
						}
					}
				}
			}
		}
		if (subJoinAfter) {
			allCols.addAll(subAllCols);
			columnsAs.clear();
			int i = 1;
			for (String col: allCols) {
				if (columnsAs == null || !columnsAs.containsValue(col.split("\\.")[1])) {
					columnsAs.put("?v" + i, col.split("\\.")[1]);
					++i;
				}
			}
		}

		// End getting column names to project.

		// Get join column names.
		if (plainSelect.getJoins() != null) {
			for (Iterator joinsIt = plainSelect.getJoins().iterator(); joinsIt.hasNext();) {
				Join join = (Join) joinsIt.next();
				if(join.getOnExpression() != null) {
					join.getOnExpression().accept(this);
					String joinStr = join.toString();
					String s = joinStr.substring(joinStr.lastIndexOf("ON (") + 4);
					String[] split = s.substring(0,s.length()-1).split(" = ");
					String col1 = resolveColumnName(columnNames, split[0]);
					String col2 = resolveColumnName(columnNames, split[1]);
					if (joinStr.contains("SELECT")) {

						//getSubStructures(tables, tablesAliases, tables2alias, columnsAs, aggrColumnsAs, tableSymbols);

						String subsq = joinStr.substring(joinStr.split("ON")[0].indexOf("(") + 1, joinStr.split("ON")[0].indexOf(")"));
						if (col1.equals("")) col1 = col2;
						String cols = subsq.split("FROM ")[0];
						cols = cols.split("SELECT ")[1].split(" ")[0];
						if (cols.indexOf(col1.split("\\.")[1]) < 0) System.out.println("This is not a valid Join subquery");
						else {
							col2 = subsq.split("FROM ")[subsq.split("FROM ").length - 1];
							col2 = col2.split(" ")[1].toUpperCase() + ".";
							col2 = col2 + col1.split("\\.")[1];
						}
					}
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
			String filter = (subWhereBefore ? "\t\t\t" : "\t");
			filter += "FILTER(";
			int m = (subWhereBefore) ? subColumnsAs.size() + columnsAs.size() + 1: 1;
			n = 1;
			String innerSubquery = "";
			for (String c : whereClauses) {
				for (String ineq : inequalities) {
					if (c.contains(ineq)) {
						String left = "";
						left = resolveColumnName(columnNames, c.split(ineq)[0]);
						if (subWhereBefore) fColumns += "\n\t\t\t";
						else fColumns += "\n\t";
						if(left.equals("")) {
							if (c.split(ineq)[0].contains("SELECT")) subWhereLeft = true;
							else filter += c.split(ineq)[0];
						} else {
							if (!connection.getConnectionDB().equals("OracleNoSQL")) {
								fColumns += "OPTIONAL { ?" + tableSymbols.get(tablesAliases.get(left.split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + left.split("\\.")[1] + " ?f" + n + "}} .\n";
								filter += "?f" + n;
								m++;
								n++;
							} else {
								fColumns += "OPTIONAL { GRAPH ?g" + m + " {?" + tableSymbols.get(tablesAliases.get(left.split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + left.split("\\.")[1] + " ?f" + n + "}} .\n";
								filter += "?f" + n;
								m++;
								n++;
							}
						}
						filter += ineq;
						String right = resolveColumnName(columnNames, c.split(ineq)[1]);
						if(right.equals("")) {
							if (c.split(ineq)[1].contains("SELECT")) subWhereRight = true;
							else filter += c.split(ineq)[1];
						} else {
							if (left.contains(".")) {
								if (!connection.getConnectionDB().equals("OracleNoSQL")) {
									fColumns += "OPTIONAL { ?" + tableSymbols.get(tablesAliases.get(left.split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + left.split("\\.")[1] + " ?f" + n + "}} .\n";
									filter += "?f" + n;
									m++;
									n++;
								} else {
									fColumns += "OPTIONAL { GRAPH ?g" + m + " {?" + tableSymbols.get(tablesAliases.get(left.split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + left.split("\\.")[1] + " ?f" + n + "}} .\n";
									filter += "?f" + n;
									m++;
									n++;
								}
							}
						}
						if (!subFromAfter) {
							if (subWhereRight || subWhereLeft) {
								subWhereBefore = true;
								try {
									String subsq = c.substring(c.indexOf("(") + 1, c.indexOf(")"));
									if (subWhereLeft)
										subsq += " WHERE " + subsq.split(" ")[1] + ineq + c.split(ineq)[1];
									if (subWhereRight)
										subsq += " WHERE " + subsq.split(" ")[1] + ineq + c.split(ineq)[0];
									subColumnsAs = columnsAs;
									subWhereLeft = false;
									subWhereRight = false;
									PlainSelect plainSelect2 = (PlainSelect) ((Select) parserManager.parse(new StringReader(subsq))).getSelectBody();
									visit(plainSelect2);

									innerSubquery = subq.pop();
									innerSubquery = "\n\t\t{" + innerSubquery + " }";
									subWhereAfter = true;
								}catch(Exception e){
									System.out.println("We exploded " + e.getMessage());
								}
							}
						}
					}
				}
			}
			if (subWhereAfter) {
				filters.add(innerSubquery);
			} else {
				filters.add(fColumns + filter.replace("'", "") + ") ");
			}
		}

		// End processing WHERE statement.

		// Process Order By statement

		if(plainSelect.getOrderByElements() != null) {
			String orderByStmnt = plainSelect.getOrderByElements().toString().toLowerCase();
			orderByStmnt = orderByStmnt.substring(1, orderByStmnt.length() - 1);
			String s = "   ORDER BY ";
			String orderbySparqlElem = "";

			String[] orderbyElems = orderByStmnt.split(" |\\,");

			for (int i = 0; i < orderbyElems.length; i++){
				if (orderbyElems[i].equals(""))
					continue;
				String resolvedName = resolveColumnName(columnNames, orderbyElems[i]);

				String t = "\"" + orderbyElems[i] + "\"";
				if (columnsAs.containsValue(t)) {
					for (final String entry : columnsAs.keySet()) {
						if (columnsAs.get(entry).equals(t)) {
							orderbySparqlElem = entry;
						}
					}
				}

				if (i > 0)
					s += " ";

				//If "ASC" or "DESC", handle here
				if ((i + 1 < orderbyElems.length) && (orderbyElems[i+1].equalsIgnoreCase("ASC") || orderbyElems[i+1].equalsIgnoreCase("DESC"))){
					s = s + orderbyElems[i+1].toUpperCase() + "(?v" + (allCols.size() + 1) + ") ";
					i++;
				}
				else
					s = s + orderbySparqlElem;
			}
			//s += "\n";
			orderby.add(s);
		}

		// Process "having" statement

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
			having.add(str + ")\n");
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
		if (!subWhereBefore) n = 0;
		// Add the columns to be projected to the SPARQL string.
		boolean nonExistentColumns = false;

		LinkedHashMap<String,String> tmpColumnsAs = new LinkedHashMap<String,String>();
		// If select * ...", replace * in columnsAs with all column names.
		if(columnsAs.get("*") != null) {
			columnsAs.remove("*");
			int k = 1;
			String filter = "";
			if (filters.size() != 0) filter = filters.get(0);
			List<String> tempTables = (subFromAfter || subJoinAfter || subWhereAfter) ? subTables : tables;
			for (String table : tempTables) {
				columns = new ArrayList<String>();
				try {
					columns = getSubjects(connection, table + "_" + connection.getSchemaString(), "rdfs:domain", NoSQLNameSpacePrefix + ":" + table);
					for (String column : columns) {
						if (!column.equals("DBUNIQUEID")) {
							//columnsAs.put(tables2alias.get(table) + "." + column, "\"" + column + "\"");
							columnsAs.put("?v" + k, "\"" + column + "\"");
							allCols.add(tables2alias.get(table) + "." + column);
							++k;
						}
					}
					if (subFromAfter) {
						int p = 1;
						for (Map.Entry<String, String> entry : columnsAs.entrySet()) {
							tmpColumnsAs.put("?v" + p, entry.getValue());
							++p;
						}
						columnsAs.clear();
						columnsAs = tmpColumnsAs;
					}
				} catch (SQLException e) {
					System.out.println(e);
				}
			}
			if (connection.getDebug() == "debug") System.out.println("\t - columnsAs: " + columnsAs + "\naggrColumnsAs: " + aggrColumnsAs);
		}
		int aggrPos = 0;

		// Prepend all group by columns to optional selects
		for (int groupbyVar = 0; groupbyVar < groupby.size(); groupbyVar++) {
			if (subWhereBefore) tmpSparql += "\n\t\t\t";
			else tmpSparql += "\n\t\t";
            /*
             * There should be an if block for Oracle RDF and AG
             * Each if block should create the correct SPARQL version for the specified connectionType,
             * similar to lines
        	 */
			String groupbyCol = allCols.get(groupbyVar);
			if (tablesAliases.get(groupbyCol.split("\\.")[0]) == null) {
				tmpSparql += "OPTIONAL { ?s1"  + " " + NoSQLNameSpacePrefix + ":" + groupbyCol.substring(groupbyCol.lastIndexOf(".") + 1) + " ?v" + (groupbyVar + 1) + " }";
				nonExistentColumns = true;
			}
			else {
				tmpSparql += "OPTIONAL { ?" + tableSymbols.get(tablesAliases.get(groupbyCol.split("\\.")[0]))
						+ " " + NoSQLNameSpacePrefix + ":" + groupbyCol.substring(groupbyCol.lastIndexOf(".") + 1) + " ?v" + (groupbyVar + 1) + " }";
			}
		}

		List<String> keys = new ArrayList<String>();
		n += groupby.size();
		for (String col: columnsAs.keySet()) {
			n++;
			if (aggrColumnsAs.keySet().contains(col)) {
				aggrPos++;
				String v = aggrColumnsAs.get(col).toUpperCase();
				if(!connection.getConnectionDB().equals("OracleNoSQL")) SPARQL += "n" + aggrPos + " AS " + v;
				else  SPARQL += "(?n" + aggrPos + " AS ?" + v.replaceAll("\"", "") + ")";
			} else {
				for (int j = 1; j < allCols.size(); j++) {
					if (allCols.get(j - 1).equals(allCols.get(n-1))) {
						break;
					}
				}
				if (!connection.getConnectionDB().equals("OracleNoSQL")) SPARQL += col + " " + columnsAs.get(col);
				else SPARQL += "(" + col + " AS ?" + columnsAs.get(col).replaceAll("\"", "") + ")";
			}

			if (n != allCols.size())
				if(!connection.getConnectionDB().equals("OracleNoSQL")) SPARQL += ", ";
				else SPARQL += " ";
			if (!subFromAfter) {
				if (subWhereBefore) tmpSparql += "\n\t\t\t";
				else tmpSparql += "\n\t\t";
				// Break the col into a key (if it is an aggregate, we must get rid of the function)
				String key;
				if (aggrColumnsAs.keySet().contains("?v" + n)) {
					key = aggrColumnsAs.get(col);
					key = key.substring(key.indexOf("(") + 1, key.indexOf(")"));
					if (subWhereBefore) key = allCols.get(allCols.size() - 1);
				} else {
					while (keys.contains(allCols.get(n - 1).substring(allCols.get(n - 1).lastIndexOf(".") + 1))) {
						++n;
					}
					keys.add(allCols.get(n - 1).substring(allCols.get(n - 1).lastIndexOf(".") + 1));

					key = allCols.get(n - 1);

					if (subWhereBefore) key = allCols.get(allCols.size() - 1);
				}

				if (tablesAliases.get(key.split("\\.")[0]) == null) {
					if (!connection.getConnectionDB().equals("OracleNoSQL")) {
						if (!outerLeft) {
							tmpSparql += "OPTIONAL { ?s1" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))
									+ " " + NoSQLNameSpacePrefix + ":" + key.substring(key.lastIndexOf(".") + 1) + " " + col + " }";
						} else {
							tmpSparql += "{ ?s1" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))
									+ " " + NoSQLNameSpacePrefix + ":" + key.substring(key.lastIndexOf(".") + 1) + " " + col + " }";
						}
					} else {
						tmpSparql += "{ ?indiv" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))
								+ " " + NoSQLNameSpacePrefix + ":" + key.substring(key.lastIndexOf(".") + 1) + " " + col + " } ";
					}
					nonExistentColumns = true;
				} else {
					if (!connection.getConnectionDB().equals("OracleNoSQL")) {
						if (!outerLeft) {
							tmpSparql += "OPTIONAL { ?" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))
									+ " " + NoSQLNameSpacePrefix + ":" + key.substring(key.lastIndexOf(".") + 1) + " " + col + " }";
						} else {
							tmpSparql += "{ ?" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))
									+ " " + NoSQLNameSpacePrefix + ":" + key.substring(key.lastIndexOf(".") + 1) + " " + col + " }";
						}
					} else {
						if (!outerLeft) {
							tmpSparql += "OPTIONAL { GRAPH ?g" + n + " {?" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))
									+ " " + NoSQLNameSpacePrefix + ":" + key.substring(key.lastIndexOf(".") + 1) + " " + col + " } }";
						} else {
							tmpSparql += "{ GRAPH ?g" + n + " {?" + tableSymbols.get(tablesAliases.get(key.split("\\.")[0]))
									+ " " + NoSQLNameSpacePrefix + ":" + key.substring(key.lastIndexOf(".") + 1) + " " + col + " } }";
						}
					}
				}
			}
		}

		// unless as is specified, go with ?n1, ?n2, etc instead of ?v1, ?v2...
		if (aggrColumnsAs.keySet().size() == 0 && !nonExistentColumns) {
			if (connection.getConnectionDB().equals("OracleNoSQL")) {
				if (!subFromAfter)
					if (subWhereBefore) SPARQL += "\n\t\tWHERE { ";
					else SPARQL += "\n\tWHERE { ";
			} else SPARQL += "\n FROM TABLE(SEM_MATCH('SELECT * WHERE { ";
		} else {
			if (connection.getConnectionDB().equals("OracleNoSQL")) SPARQL += " ";
			else SPARQL += "\n FROM TABLE(SEM_MATCH('SELECT ";
			int x = 1;
			for (String groupbyVars : groupby)
				SPARQL += groupbyVars + " ";
			for (int pull = groupby.size() + 1; pull <= allCols.size(); pull++) {
				if (aggrColumnsAs.keySet().contains("?v" + pull)) {
					SPARQL += "(" + getAggregateSelect(allCols.get(pull - 1).toLowerCase())[0]
							+ "(?v" + pull + ")" + " as ?n" + x + ") ";
					x++;
				} else {
					boolean alreadySelected = false;
					for (int j = 1; j <= groupby.size(); j++) {
						if (allCols.get(j - 1).equals(allCols.get(pull - 1)) && (j - 1 != pull - 1)) {
							alreadySelected = true;
							break;
						}
					}
					//if (!alreadySelected) SPARQL += "?v" + pull + " ";
				}
			}
			if (connection.getConnectionDB().equals("OracleNoSQL")) {
				if (!subFromAfter)
					if (subWhereBefore) SPARQL += "\n\t\tWHERE { ";
					else SPARQL += "\n\tWHERE { ";
			}
		}

		for (Map.Entry<String, String> entry : tableSymbols.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (connection.getConnectionDB().equals("OracleNoSQL")) {
				SPARQL += "GRAPH " + NoSQLNameSpacePrefix + ":" + key + "_SCHEMA" + " { ?" + value + " rdf:type " + NoSQLNameSpacePrefix + ":" + key + " } ";
			} else if (connectionType == "rdf_mode") {
				SPARQL += "\tGRAPH <" + key + "_" + connection.getSchemaString() + "> { ?" + value + " rdf:type :" + key + " } ";
			} else { //representing the default case
				SPARQL += "\t?" + value + " rdf:type :" + key + " .\n";
			}
		}
		if (subFromAfter && (!subq.isEmpty())) {
			subqStr = subq.pop();
			subqStr = subqStr.substring(subqStr.lastIndexOf(")") + 1, subqStr.length() - 1);
			tmpSparql += subqStr;
		}

		// Add tmpSparql from above to the SPARQL string.
		SPARQL += tmpSparql;

		// Add sparql statements to do joins to the SPARQL string.
		n = 1;
		for (String s : joinColumns) {
			if (!connection.getConnectionDB().equals("OracleNoSQL")) {
				// This will get the symbol from tableSymbols for the e from e.n and will get n from e.n
				SPARQL += "\t?" + tableSymbols.get(tablesAliases.get(s.split(" = ")[0].split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + s.split(" = ")[0].split("\\.")[1] + " ?j" + n + " .\n";
				// This will get the symbol from tableSymbols for the d from d.n and will get n from d.n
				SPARQL += "\t?" + tableSymbols.get(tablesAliases.get(s.split(" = ")[1].split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + s.split(" = ")[1].split("\\.")[1] + " ?j" + n + " .\n";
			} else {
				if (outerLeft) {
					// This will get the symbol from tableSymbols for the e from e.n and will get n from e.n
					SPARQL += "\n\t\tOPTIONAL { GRAPH ?gj" + n + " { ?" + tableSymbols.get(tablesAliases.get(s.split(" = ")[0].split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + s.split(" = ")[0].split("\\.")[1] + " ?j" + n + " . } ";
					// This will get the symbol from tableSymbols for the d from d.n and will get n from d.n
					SPARQL += "\n\t\tGRAPH ?gj" + (n + 1) + " { ?" + tableSymbols.get(tablesAliases.get(s.split(" = ")[1].split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + s.split(" = ")[1].split("\\.")[1] + " ?j" + n + " .  } }";
				} else {
					// This will get the symbol from tableSymbols for the e from e.n and will get n from e.n
					SPARQL += "\n\t\tGRAPH ?gj" + n + " { ?" + tableSymbols.get(tablesAliases.get(s.split(" = ")[0].split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + s.split(" = ")[0].split("\\.")[1] + " ?j" + n + " . } ";
					// This will get the symbol from tableSymbols for the d from d.n and will get n from d.n
					SPARQL += "\n\t\tGRAPH ?gj" + (n + 1) + " { ?" + tableSymbols.get(tablesAliases.get(s.split(" = ")[1].split("\\.")[0])) + " " + NoSQLNameSpacePrefix + ":" + s.split(" = ")[1].split("\\.")[1] + " ?j" + n + " .  } ";
				}
			}
			n++;
		}

		if (filters.size() != 0) {
			if (filters.get(0).contains("SELECT")) {
				SPARQL += "\n\t\t" + filters.get(0).substring(filters.get(0).lastIndexOf("OPTIONAL"), filters.get(0).lastIndexOf("FILTER") - 1) + filters.get(0).substring(filters.get(0).indexOf("FILTER"), filters.get(0).lastIndexOf(")") + 1);
			} else SPARQL += filters.get(0);
		}


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

		temp = "";
		if (!subFromBefore) SPARQL += "\n ";

		subWhereBefore = false;
		subFromAfter = false;
		subJoinAfter = false;

		setSubStructures(tables, tablesAliases, tables2alias, columnsAs, aggrColumnsAs, tableSymbols, columns, columnNames);

		return SPARQL;
	}

	private void setSubStructures (
			List<String> tables,
			HashMap<String, String> tablesAliases,
			HashMap<String, String> tables2alias,
			LinkedHashMap<String, String> columnsAs,
			LinkedHashMap<String, String> aggrColumnsAs,
			HashMap<String,String> tableSymbols,
			List<String> columns,
			List<String> columnNames) {

		subTables = tables;
		subTablesAliases = tablesAliases;
		subTables2alias = tables2alias;
		subColumnsAs = columnsAs;
		subAggrColumnsAs = aggrColumnsAs;
		subTableSymbols = tableSymbols;
		subColumns = columns;
		subColumnNames = columnNames;
		subAllCols.addAll(allCols);
	}

	private void getSubStructures (
			List<String> tables,
			HashMap<String, String> tablesAliases,
			HashMap<String, String> tables2alias,
			LinkedHashMap<String, String> columnsAs,
			LinkedHashMap<String, String> aggrColumnsAs,
			HashMap<String,String> tableSymbols) {

		for (String key:subTables) tables.add(key);

		for (String key:subTablesAliases.keySet()) tablesAliases.put(key, subTablesAliases.get(key));

		for (String key:subTables2alias.keySet()) tables2alias.put(key, subTables2alias.get(key));

		LinkedHashMap<String, String> tempColumnsAs = new LinkedHashMap<String, String>();
		int varCount = 1;
		for (String key:columnsAs.keySet()) {
			String var = "?v" + varCount;
			tempColumnsAs.put(var, columnsAs.get(key));
			++varCount;
		}
		for (String key:subColumnsAs.keySet()) {
			String var = "?v" + varCount;
			tempColumnsAs.put(var, subColumnsAs.get(key));
			++varCount;
		}
		columnsAs = tempColumnsAs;

		for (String key:subAggrColumnsAs.keySet()) aggrColumnsAs.put(key, subAggrColumnsAs.get(key));

		int symCount = tableSymbols.size() + 1;
		for (String key:subTableSymbols.keySet()) {
			String sym = "s" + symCount;
			tableSymbols.put(key, sym);
			++symCount;
		}
	}

	private String resolveColumnName(List<String> columnNames, String columnName)
	{
		String tmpColumnName = "";
		for (String column : columnNames) {
			if (columnName.contains(".")) {
				if (columnName.toUpperCase().equals(column.toUpperCase()))
					tmpColumnName = column;
			}
			else {
				if (columnName.toUpperCase().equals(column.substring(column.lastIndexOf(".") + 1).toUpperCase()))
					tmpColumnName = column;
			}
		}
		return tmpColumnName;
	}

	@Override
	public void visit(SubSelect subSelect)
	{
		subFromAfter = false;
		subJoinAfter = false;
		if (!subJoinBefore) subFromBefore = true;

		subSelect.getSelectBody().accept(this);

		if (!subJoinBefore) subFromAfter = true;
		if (!subFromAfter) subJoinAfter = true;
		subFromBefore = false;
		subJoinBefore = false;

		//temp = temp.substring(0, temp.indexOf("IN") + 2);
	}

	private String getColumns(LinkedHashMap<String, String> columnsAs)
	{

		String s = "";

		// If we are returning instances, make sure that we need to return the DBUNIQUEID as part of the select.
		if (this.returns_instances_of != null) {
			for (String return_type : returns_instances_of) {
				if (columnsAs.get(return_type + ".DBUNIQUEID") == null) {
					columnsAs.put(return_type + ".DBUNIQUEID", return_type + ".DBUNIQUEID");
				}
			}
		}

		int var = 1;
		for (String entry : columnsAs.keySet()) {
			if (connection.getDebug() == "debug") System.out.println("SQLVisitor-getColumns, entry: " + entry);
			if (connection.getDebug() == "debug")
				System.out.println("SQLVisitor-getColumns, colname(entry): " + colname(entry));
			if (connection.getDebug() == "debug")
				System.out.println("SQLVisitor-getColumns, tablename(entry): " + tablename(entry));
			if (connection.getDebug() == "debug")
				System.out.println("SQLVisitor-getColumns, columnsAs.get(entry): " + columnsAs.get(entry));
			if (connection.getDebug() == "debug")
				System.out.println("SQLVisitor-getColumns, colname(columnsAs.get(entry)): " + colname(columnsAs.get(entry)));
			// So if we are returning instances. We need a way to determine which instance the data belongs too.
			// To do this i append the tablename_ as part of the column's "as" statement, so the columns returned will start
			// with the name of the instance for which they belong too.
			if (returns_instances_of != null) {
				// s += colname(entry) + "_" + tablename(entry) + " " + tablename(entry)+"_"+colname(columnsAs.get(entry)) + ", ";
				s += "v" + var++ + " " + tablename(entry) + "_" + colname(columnsAs.get(entry)) + ", ";
			}
			else {
				// s += colname(entry) + "_" + tablename(entry) + " " + colname(columnsAs.get(entry)) + ", ";
				s += "v" + var++ + " " + colname(columnsAs.get(entry)) + ", ";
			}
		}
		if (s.length() < 1) return s;
		s = s.substring(0, s.length() - 2);
		s += " from table(\n\tSEM_MATCH('SELECT * WHERE {\n\t";
		return s;
	}

	/**
	 *
	 */
	public String getColumnsVar(LinkedHashMap<String, String> columnsAs)
	{
		//Following example from above SPARQL query will looks like:
		String s = "";
		String currTable = "";
		int var = 1;
		for (String item : columnsAs.keySet()) {
			String tableName = tablename(item);
			String colName = colname(item);
			String tempTableName = tableName;
			int minlen = Math.min(tempTableName.length(), 6);
			if (!tempTableName.equals(currTable)) {
				s += "?" + tempTableName.substring(0, minlen) + " rdf:type :" + tableName + " . \n\t";
				currTable = tempTableName;
			}
			s += "?" + tempTableName.substring(0, minlen) + " :" + colName + " ?v" + var++ + " . \n\t";
		}
		return s;
	}

	/**
	 *
	 */
	public String getJoins(List<String> matches)
	{
		//Adding JOINS, e.g.FROM D AS E JOIN F AS G ON E.STUDENID = G.STUDENTID
		//will result in:	?thisD :STUDENTID ?j1 . ?thisF :STUDENTID ?j1 .
		String s = "";
		for (Iterator fI = matches.iterator(); fI.hasNext(); ) {
			String item = (String) fI.next();
			s += item + "\n\t";
		}
		return s;
	}

	public String getFilters(List<String> filters, LinkedHashMap<String, String> columnsAs)
	{
		// Adding the filters to SEM_MATCH, e.g WHERE D.GRADES > 80
		//results in: FILTER( GRADES_D > 80)
		String s = "";
		if (!filters.isEmpty()) {
			for (Iterator fI = filters.iterator(); fI.hasNext(); ) {
				String item = ((String) fI.next()).trim();
				//String item = "?PETID_PETS = 1001";
				String t = item.split("\\s+")[0];
				String colName = t.substring(0, t.lastIndexOf("_")).replace("?", "");
				String tblName = t.substring(t.lastIndexOf("_") + 1);
				int minlen = Math.min(tblName.length(), 6);

				// if(!columnsAs.containsKey(tblName + "." + colName)){      Phil commented this and the other commented line below as a hack to get the where clauses to work
				s += "?" + tblName.substring(0, minlen) + " :" + colName + " " + t + " .\n\t";
			}

			s += "FILTER ( ";
			for (Iterator fI = filters.iterator(); fI.hasNext(); ) {
				String item = ((String) fI.next()).trim();
				String tblName = getFilterTableName(item);
				String dataValue = item.substring(item.lastIndexOf(" ") + 1);
				String tempTV = dataValue;
				dataValue = dataValue.replaceAll("^\"|\"$", "");

				//Numeric? needed for special format
				if (!isNumeric(dataValue)) {
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
	public String getEndOfStmt(List<String> internalColumns, List<String> orderby)
	{
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
		if (orderby.size() > 0) {
			s += "ORDER BY ";
			for (Iterator fI = orderby.iterator(); fI.hasNext(); ) {
				String item = (String) fI.next();
				s += item + " ";
			}
		}
		//Adding model and alias to ORACLE SQL statement
		s += "',\n\t SEM_MODELS('" + connection.getModel() + "'), ";
		s += "null,\n\t SEM_ALIASES( SEM_ALIAS('', 'http://www.example.org/people.owl#')), null) )";
		return s;
	}


	/**
	 * Get subjects given a graph, predicate, and object.
	 *
	 * @param graph
	 * @param predicate
	 * @param object
	 * @return subjects
	 * @throws SQLException
	 */
	public static List<String> getSubjects(PyRelConnection connection, String graph, String predicate, String object) throws SQLException
	{
		ArrayList<PyObject> rows = new ArrayList<PyObject>();
		List<String> subjects = new ArrayList<String>();
		String connection_DB = connection.getConnectionDB();
		if (connection_DB.equals("OracleNoSQL")) {
			String sparql = "SELECT ?s WHERE { GRAPH " + "c:" + graph + " { ?s " + predicate + " " + object + " } } ";
			if (connection.getDebug() == "debug")
				System.out.println("\ngetSujects, sparql is: \n" + sparql);
//			rows = connection.getDatabase().OracleNoSQLRunSPARQL(sparql);
			for (int i = 1; i < rows.size(); i++) {
				subjects.add(String.format("%s", rows.get(i))
						.replaceAll("[()]", "")
						.replaceAll("'", "")
						.replaceAll(",", "")
						.replaceAll(connection.getDatabase().getNameSpace(), ""));
			}
		}
		else {
			String q =
					"select distinct sub from table(sem_match(\n'select * where {\n" +
							"\tGRAPH <" + graph + "> {?sub " + predicate + " " + object + "}\n" +
							"}',\n" +
							"SEM_MODELS('"
							+ connection.getModel() + "'), null,\n" +
							"SEM_ALIASES( SEM_ALIAS('', '"
							+ connection.getNamespace() + "')), null) )";
			if (connection.getDebug() == "debug") System.out.println("\ngetSubjects: query=\n" + q);
			try {
				subjects = SPARQLDoer.executeRdfSelect(connection, q);
			} catch (SQLException ex) {
				System.out.println(ex);
			}
		}
		return subjects;
	}

	/**
	 * Insert an RDF Quad into the RDF data store
	 *
	 * @param graph     The subject of the statement  	e.g. EMP
	 * @param subject   The subject of the statement  e.g. EMP
	 * @param predicate The object-predicate  		e.g. Name
	 * @param object    The object of the statement  	e.g. 'Phil'
	 * @throws SQLException
	 */
	public static void insertQuad(PyRelConnection connection, String graph, String subject, String predicate, String object, Boolean eva)
			throws SQLException
	{
		final String connection_DB = connection.getConnectionDB();
		final String schemaString = connection.getSchemaString();
		if (connection_DB.equals("OracleNoSQL")) {
//			connection.OracleNoSQLAddQuad(schemaString, graph, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2000/01/rdf-schema#Class", true);
			// Unimplemented as of now
			// if(eva)
//			connection.OracleNoSQLAddQuad(graph + "_" + schemaString, predicate, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#DatatypeProperty", true);
//			connection.OracleNoSQLAddQuad(graph + "_" + schemaString, predicate, "http://www.w3.org/2000/01/rdf-schema#domain", graph, true);
//			connection.OracleNoSQLAddQuad(graph + "_" + schemaString, predicate, "http://www.w3.org/2000/01/rdf-schema#range", "http://www.w3.org/2001/XMLSchema#string", true);
//			connection.OracleNoSQLAddQuad(graph + "_" + schemaString, subject, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", graph, true);
//			connection.OracleNoSQLAddQuad(graph, subject, predicate, object, true);
		}
		else if (connection_DB.equals("Oracle")) {
			String graphName = connection.getModel() + ":<" + graph + ">";
			String graphName2 = connection.getModel() + ":<" + graph + "_" + schemaString + ">";

			if (subject.indexOf(":") < 0) {
				// no specified connection.getNamespace():  use current default connection.getNamespace()
				subject = connection.getNamespace() + subject;
			}
			if (predicate.indexOf(":") < 0) {
				// no specified connection.getNamespace():  use current default connection.getNamespace()
				predicate = connection.getNamespace() + predicate;
			}
			if (object.indexOf(":") < 0) {
				// no specified connection.getNamespace():  use current default connection.getNamespace()
				object = connection.getNamespace() + object;
			}
			String typeString = "";
			String s = "";
			if (!graph.equals("")) {
				s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName +
						"', '" + subject.replaceAll("'", "") + "', '" + predicate.replaceAll("'", "") + "', '" + object.replaceAll("'", "") + typeString + "'))";
				connection.executeStatement(s);
				s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
						"', '" + subject.replaceAll("'", "") + "', 'rdf:type', '" + connection.getNamespace() + graph + "'))";
				connection.executeStatement(s);
				if (eva)
					s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
							"', '" + predicate.replaceAll("'", "") + "', 'rdf:type', 'owl:FunctionalProperty'))";
				else
					s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
							"', '" + predicate.replaceAll("'", "") + "', 'rdf:type', 'owl:DatatypeProperty'))";
				connection.executeStatement(s);
				s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
						"', '" + predicate.replaceAll("'", "") + "', 'rdfs:domain', '" + connection.getNamespace() + graph + "'))";
				connection.executeStatement(s);
				s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
						"', '" + predicate.replaceAll("'", "") + "', 'rdfs:range', 'xsd:string'))";
				connection.executeStatement(s);
			}
			else {
				s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
						"', '" + subject.replaceAll("'", "") + "', '" + predicate.replaceAll("'", "") + "', '" + object.replaceAll("'", "") + typeString + "'))";
				connection.executeStatement(s);
			}
		}
	}


	public void printAllLists(List<String> filters, List<String> tables, List<String> internalColumns,
							  List<String> matches, List<String> orderby, LinkedHashMap<String, String> columnsAs)
	{
		@SuppressWarnings("unchecked")
		List<String> colAs = new ArrayList<String>();
		for (String s : columnsAs.keySet()) {
			colAs.add(s);
		}
		List<List<String>> lists =
				Arrays.asList(filters, tables, colAs, internalColumns, matches, orderby);
		String[] listsNames = {"Filters:", "Tables:", "Columns:", "Internal Columns:",
				"Matches (or Joins?):", "Order By:"};
		for (int i = 0; i < lists.size(); i++) {
			printList(listsNames[i], lists.get(i));
		}
	}

	private void printList(String field, List<String> list)
	{
		if (connection.getDebug() == "debug") System.out.println(field);
		if (connection.getDebug() == "debug") for (Iterator fI = list.iterator(); fI.hasNext(); ) {
			System.out.println("\t" + fI.next());
		}
	}

	static public String tablename(String item)
	{
		if (item.indexOf('.') > 0)
			return item.substring(0, item.indexOf('.'));
		return "tbl";
	}

	static public String getFilterTableName(String str)
	{
		return (str.substring(0, str.indexOf(" "))).substring(str.indexOf("_") + 1);
	}

	static public String getFilterColumnName(String str)
	{
		return str.substring(1, str.lastIndexOf("_"));
	}

	static public String colname(String item)
	{
		if (item.indexOf('.') > 0)
			return item.substring(item.indexOf('.') + 1);
		return item;
	}

	static public String filter(String item, String comparison, String value)
	{
		String tempTableName = tablename(item);
		return "?" + colname(item) + "_" + tempTableName + " " + comparison + " " + value;
	}

	public String match(String item, String value, boolean isValue)
	{
		String joinString = "";
		String tblName = tablename(item);
		if (isValue) {
			join = false;
			// this is the case of columName = someValue
			return " ?" + colname(item) + "_" + tblName + " = " + value;
		}
		else {
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

	public String[] getAggregateSelect(String selectSection)
	{
		String[] result = {"NOT FOUND", ""};
		for (String aggregate : aggregates)
			if (selectSection.matches(aggregate + " *[(]")) {
				result[0] = aggregate;
				break;
			}

		if (result[0].equals("NOT FOUND"))
			return null;

		String restofStmnt = selectSection.substring(result[0].length(), selectSection.length());
		String aggrCol = selectSection.substring(selectSection.indexOf("("), selectSection.indexOf(")") + 1);
		// not invalid syntax; trim the parentheses off
		aggrCol = aggrCol.substring(aggrCol.indexOf("(") + 1, aggrCol.indexOf(")"));
		result[1] = aggrCol;

		return result;
	}

	static public List<String> getAllColsFromTbl(String tableName) throws SQLException, JSQLParserException
	{
		List<String> tblCols = new ArrayList<String>();
		return tblCols;
	}

	static public boolean isNumeric(String str)
	{
		DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
		char minusSign = currentLocaleSymbols.getMinusSign();

		if (!Character.isDigit(str.charAt(0)) && str.charAt(0) != minusSign)
			return false;

		boolean isDecimal = false;
		char decimalSeparator = currentLocaleSymbols.getDecimalSeparator();

		for (char chr : str.substring(1).toCharArray()) {
			if (!Character.isDigit(chr)) {
				if (chr == decimalSeparator && !isDecimal) {
					isDecimal = true;
					continue;
				}
				return false;
			}
		}
		return true;
	}

	public String getKeyByValue(HashMap<String, String> map, String value)
	{
		for (String entry : map.keySet()) {
			if (map.get(entry).equals(value)) {
				return entry;
			}
		}
		return null;
	}

	public void testMethod(String sub)
	{
		try {
			PlainSelect plainSelect2 = (PlainSelect) ((Select) parserManager.parse(new StringReader(sub))).getSelectBody();
			visit(plainSelect2);
		} catch (JSQLParserException e) {
			System.out.println("Null");
		}
	}

	@Override
	public void visit(Table tableName)
	{
		//Changed this to getFullyQualifiedName from getWholeTable; jsqlParser 9.1
		temp = tableName.getFullyQualifiedName();
	}

	@Override
	public void visit(Addition addition)
	{
		addition.getLeftExpression().accept(this);
		String t = "(" + temp;
		t += addition.getStringExpression();
		addition.getRightExpression().accept(this);
		t += temp + ") ";
		temp = t;
	}

	@Override
	public void visit(AndExpression andExpression)
	{
		wasEquals = false;    //Does the expression has the equals sign?
		andExpression.getLeftExpression().accept(this);
		if (wasEquals) {    //If it does add it to matches
			matches.add(temp);
		}
		else {
			//Need tablesAliases

			String tableName = getFilterTableName(temp.trim());
			String dataValue = temp.substring(temp.lastIndexOf(" ") + 1);
			//Right now if is not a numeric value, then it will be consider a string
			if (!isNumeric(dataValue))
				temp = temp.substring(0, temp.lastIndexOf(" ") + 1) + "\"" + temp.substring(temp.lastIndexOf(" ") + 1) + "\"";
			//validate
			if (!tableName.equals("tbl")) {

				//Add filter
				if (tablesAliases.containsKey(tableName))
					filters.add(temp.replace(tableName, tablesAliases.get(tableName)));
			}
			else {
				String ct = temp.trim().split("\\s+")[0];
				String colName = ct.substring(1, ct.lastIndexOf("_"));

				//validate table name, it does not contain alias.
				tableName = tablename(colName);
				//validate column

				//Add filter
				filters.add(temp.replace("_tbl", "_" + tableName));
			}

		}
		wasEquals = false;
		andExpression.getRightExpression().accept(this);
	}

	@Override
	public void visit(Between between)
	{
		between.getLeftExpression().accept(this);
		between.getBetweenExpressionStart().accept(this);
		between.getBetweenExpressionEnd().accept(this);
	}

	@Override
	public void visit(Column tableColumn)
	{
		// temp = tableColumn.getWholeColumnName();
	}

	@Override
	public void visit(Division division)
	{
		division.getLeftExpression().accept(this);
		String t = "(" + temp;
		t += division.getStringExpression();
		division.getRightExpression().accept(this);
		t += temp + ") ";
		temp = t;
	}

	@Override
	public void visit(DoubleValue doubleValue)
	{
		temp = Double.toString(doubleValue.getValue());
	}

	private boolean join;

	public void visit(EqualsTo equalsTo)
	{
		equalsTo.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "";

		equalsTo.getRightExpression().accept(this);
		if (equalsTo.getRightExpression() instanceof Column) {
			join = true;
			temp = match(item, temp, false);
			wasEquals = true;
		}
		else {
			temp = match(item, temp, true);
		}
		//wasEquals = true;
	}

	@Override
	public void visit(Function function)
	{
	}

	@Override
	public void visit(GreaterThan greaterThan)
	{
		greaterThan.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "";
		if (greaterThan.isNot()) {
			comparison = "<=";
		}
		else {
			comparison = ">";
		}
		greaterThan.getRightExpression().accept(this);
		String value = temp;
		temp = filter(item, comparison, value);
	}

	@Override
	public void visit(GreaterThanEquals greaterThanEquals)
	{
		greaterThanEquals.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "";
		if (greaterThanEquals.isNot()) {
			comparison = "<";
		}
		else {
			comparison = ">=";
		}
		greaterThanEquals.getRightExpression().accept(this);
		String value = temp;
		temp = filter(item, comparison, value);
	}

	@Override
	public void visit(InExpression inExpression)
	{
		inExpression.getLeftExpression().accept(this);
		inExpression.getRightItemsList().accept(this);
	}

	//(java/net/sf/jsqlparser/statement/ExpressionDeParser.java)
	@Override
	public void visit(CastExpression cast)
	{
	}

	@Override
	public void visit(Modulo modulo)
	{
	}

	@Override
	public void visit(AnalyticExpression aexpr)
	{
	}

	@Override
	public void visit(ExtractExpression eexpr)
	{
	}

	@Override
	public void visit(IntervalExpression iexpr)
	{
	}

	@Override
	public void visit(JdbcNamedParameter jdbcNamedParameter)
	{
	}

	@Override
	public void visit(OracleHierarchicalExpression oexpr)
	{
	}

	@Override
	public void visit(SignedExpression signedExpression)
	{
	}

	@Override
	public void visit(RegExpMatchOperator rexpr)
	{
	}

	@Override
	public void visit(JsonExpression jsonExpr)
	{
	}

	@Override
	public void visit(RegExpMySQLOperator regExpMySQLOperator)
	{
	}

	@Override
	public void visit(MultiExpressionList multiExprList)
	{
	}
    /*
	 * Done implementing ExpressionDeParser methods
	 */

	@Override
	public void visit(IsNullExpression isNullExpression)
	{
	}

	@Override
	public void visit(JdbcParameter jdbcParameter)
	{
	}

	@Override
	public void visit(LikeExpression likeExpression)
	{
		visitBinaryExpression(likeExpression);
	}

	@Override
	public void visit(ExistsExpression existsExpression)
	{
		existsExpression.getRightExpression().accept(this);
	}

	@Override
	public void visit(LongValue longValue)
	{
		temp = longValue.getStringValue();
	}

	@Override
	public void visit(MinorThan minorThan)
	{
		minorThan.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "";
		if (minorThan.isNot()) {
			comparison = ">=";
		}
		else {
			comparison = "<";
		}
		minorThan.getRightExpression().accept(this);
		String value = temp;
		temp = filter(item, comparison, value);
	}

	@Override
	public void visit(MinorThanEquals minorThanEquals)
	{
		minorThanEquals.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "";
		if (minorThanEquals.isNot()) {
			comparison = ">";
		}
		else {
			comparison = "<=";
		}
		minorThanEquals.getRightExpression().accept(this);
		String value = temp;
		temp = filter(item, comparison, value);
	}

	@Override
	public void visit(Multiplication multiplication)
	{
		multiplication.getLeftExpression().accept(this);
		String t = "(" + temp;
		t += multiplication.getStringExpression();
		multiplication.getRightExpression().accept(this);
		t += temp + ") ";
		temp = t;
	}

	@Override
	public void visit(NotEqualsTo notEqualsTo)
	{
		notEqualsTo.getLeftExpression().accept(this);
		String item = temp;
		String comparison = "!=";
		notEqualsTo.getRightExpression().accept(this);
		String value = temp;
		temp = filter(item, comparison, value);
	}

	@Override
	public void visit(NullValue nullValue)
	{
		temp = nullValue.toString();
	}

	@Override
	public void visit(OrExpression orExpression)
	{
		orExpression.getLeftExpression().accept(this);
		String left = temp;
		orExpression.getRightExpression().accept(this);
		temp = "FILTER( " + left + " || " + temp + " )";
		wasEquals = false;
	}

	@Override
	public void visit(Parenthesis parenthesis)
	{
		String t = "";
		if (parenthesis.isNot())
			t += "!";
		parenthesis.getExpression().accept(this);
		t += "(" + temp + ")";
		temp = t;
	}

	@Override
	public void visit(StringValue stringValue)
	{
		temp = stringValue.getValue();
	}

	@Override
	public void visit(Subtraction subtraction)
	{
		subtraction.getLeftExpression().accept(this);
		String t = "(" + temp;
		t += subtraction.getStringExpression();
		subtraction.getRightExpression().accept(this);
		t += temp + ") ";
		temp = t;
	}

	public void visitBinaryExpression(BinaryExpression binaryExpression)
	{
		binaryExpression.getLeftExpression().accept(this);
		binaryExpression.getRightExpression().accept(this);
	}

	@Override
	public void visit(ExpressionList expressionList)
	{
		String t = "(";
		for (Iterator iter = expressionList.getExpressions().iterator(); iter.hasNext(); ) {
			Expression expression = (Expression) iter.next();
			expression.accept(this);
			t += temp + ", ";
		}
		t = t.substring(0, t.length() - 2) + ")";
		temp = t;
	}

	@Override
	public void visit(DateValue dateValue)
	{
		temp = dateValue.getValue().toString();
	}

	@Override
	public void visit(TimestampValue timestampValue)
	{
		temp = timestampValue.getValue().toString();
	}

	@Override
	public void visit(TimeValue timeValue)
	{
		temp = timeValue.getValue().toString();
	}

	@Override
	public void visit(CaseExpression caseExpression)
	{
	}

	@Override
	public void visit(WhenClause whenClause)
	{
	}

	@Override
	public void visit(BitwiseXor bitwiseXor)
	{
	}

	@Override
	public void visit(BitwiseOr bitwiseOr)
	{
	}

	@Override
	public void visit(BitwiseAnd bitwiseAnd)
	{
	}

	@Override
	public void visit(Matches matches)
	{
	}

	@Override
	public void visit(Concat concat)
	{
	}

	@Override
	public void visit(AllComparisonExpression allComparisonExpression)
	{
		allComparisonExpression.getSubSelect().getSelectBody().accept(this);
	}

	@Override
	public void visit(AnyComparisonExpression anyComparisonExpression)
	{
		anyComparisonExpression.getSubSelect().getSelectBody().accept(this);
	}

	@Override
	public void visit(SubJoin subjoin)
	{
		subjoin.getLeft().accept(this);
		subjoin.getJoin().getRightItem().accept(this);
	}

	//SELECT ItemS
	@Override
	public void visit(AllColumns columns)
	{
		temp = "*";
	}

	@Override
	public void visit(AllTableColumns columns)
	{
		temp = "*";
	}

	@Override
	public void visit(SelectExpressionItem item)
	{
		item.getExpression().accept(this);
	}

	//Order by visitor Jesse: Changed this method to work. Check SelectDeParser in src/main/java/net/sf/jsqlparser/util/deparser
	@Override
	public void visit(OrderByElement order)
	{
		order.getExpression().accept(this);
		if (order.isAsc()) {
			temp = " ASC( ?" + colname(temp) + "_" + tablename(temp) + " )";
		}
		else {
			temp = " DESC( ?" + colname(temp) + "_" + tablename(temp) + " )";
		}
	}

	private class ownIllegalSQLException extends IllegalArgumentException {
		public ownIllegalSQLException(String message)
		{
			super(message);
		}
	}

}