package org.python.ReL;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;
import org.cyphersim.CypherSimTranslator;
import org.python.core.PyObject;
import org.python.core.PyType;
import wdb.metadata.*;
import wdb.parser.Node;
import wdb.parser.QueryParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class ProcessLanguages {
    PyRelConnection conn;
	Adapter adapter;
    SPARQLHelper sparqlHelper;
    DatabaseInterface connDatabase;
    String schemaString;
	String NoSQLNameSpacePrefix = "";
    static Boolean parserInitialized = false;
	static QueryParser parser;
	String where = "";

    /**
     * Process a language statement such as SQL or SIM.
     *
     * Note:  The connection should not be closed within this class, as it will be closed
     * by its parent (invoker).
     *
     * @param conn
     */
    public ProcessLanguages(PyRelConnection conn) {
        this.conn = conn;
        sparqlHelper = new SPARQLHelper(conn);
        schemaString = sparqlHelper.getSchemaString();
        connDatabase  = conn.getDatabase();
		this.adapter = connDatabase.adapter;
		NoSQLNameSpacePrefix = connDatabase.getNameSpacePrefix();
    }

// ------------------------------------- SIM -------------------------
    public synchronized String processSIM(String ReLstmt) throws SQLException {

    	String Save_ReLstmt = ReLstmt;
		ReLstmt += ";";
		InputStream is = new ByteArrayInputStream(ReLstmt.getBytes());
		Query q = null;
        String sparql = null;
		if( ! parserInitialized) {
			parser =  new QueryParser(is);
			parserInitialized = true;
		} else { parser.ReInit(is); }
		try { q = parser.getNextQuery(); }
		catch(Exception e1) { System.out.println(e1.getMessage()); }
		// --------------------------------------------------------------------------------- SIM Insert
	    // E.g., SQL: INSERT INTO onto_DATA VALUES ( 1, SDO_RDF_TRIPLE_S('onto', '#PERSON', 'rdf:type', 'rdfs:Class'));
	    // E.g., SIM: INSERT dept ( DEPTNO := 10 , DNAME := "ACCOUNTING" , LOC := "NEW YORK" );
		if(q instanceof InsertQuery) {
			InsertQuery iq = (InsertQuery)q;
			String instanceID = "";
			if(conn.getConnectionDB() == "OracleNoSQL") instanceID = String.valueOf(UUID.randomUUID());
			for (int i = 0; i < iq.numberOfAssignments(); i++) {
				DvaAssignment dvaAssignment = (DvaAssignment)iq.getAssignment(i);
				if(conn.getConnectionDB() == "OracleNoSQL") {
					if(iq.getAssignment(i)instanceof EvaAssignment) { 
						// This isn't implemented yet because currently EVAs are constructed in the SIM MODIFY statement.
					} else if(iq.getAssignment(i)instanceof DvaAssignment) {
			            connDatabase.OracleNoSQLAddQuad(schemaString, iq.className, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2000/01/rdf-schema#Class", true);
			            connDatabase.OracleNoSQLAddQuad(iq.className + "_" + schemaString, dvaAssignment.AttributeName, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#DatatypeProperty", true);
			            connDatabase.OracleNoSQLAddQuad(iq.className + "_" + schemaString, dvaAssignment.AttributeName, "http://www.w3.org/2000/01/rdf-schema#domain", iq.className, true);
			            connDatabase.OracleNoSQLAddQuad(iq.className + "_" + schemaString, dvaAssignment.AttributeName, "http://www.w3.org/2000/01/rdf-schema#range", "http://www.w3.org/2001/XMLSchema#string", true);
			            connDatabase.OracleNoSQLAddQuad(iq.className + "_" + schemaString, instanceID, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", iq.className, true);
			            connDatabase.OracleNoSQLAddQuad(iq.className, instanceID, dvaAssignment.AttributeName, (String)dvaAssignment.Value.toString(), false);
					}
				} else {
					if(iq.getAssignment(i)instanceof EvaAssignment) { 
						// This isn't implemented yet because currently EVAs are constructed in the SIM MODIFY statement.
					} else if(iq.getAssignment(i)instanceof DvaAssignment) sparqlHelper.insertQuad(iq.className, instanceID, dvaAssignment.AttributeName, (String)dvaAssignment.Value.toString(), false);
				}
			}
		// --------------------------------------------------------------------------------- SIM Query
		} else if(q instanceof RetrieveQuery) {	
			RetrieveQuery rq = (RetrieveQuery)q;
			String className = rq.className;
			List<String> dvaAttribs = new ArrayList<String>();
			List<String> evaAttribs = new ArrayList<String>();
			Map<String, String> whereAttrValues = new HashMap<String, String>();
			List<String> columns;
			for(int j = 0; j < rq.numAttributePaths(); j++)
			{
				if(rq.getAttributePath(j).attribute == "*")
				{
					columns = sparqlHelper.getSubjects(className + "_" + sparqlHelper.getSchemaString(), "rdf:type", "owl:DatatypeProperty");
					if(rq.getAttributePath(j).levelsOfIndirection() == 0) {
						for(int i = 0; i < columns.size(); i++) {
							dvaAttribs.add(columns.get(i));
						}
					} else {
					
					}
				}
				else if(rq.getAttributePath(j).levelsOfIndirection() == 0)
				{
					dvaAttribs.add(rq.getAttributePath(j).attribute);
				}
				else {
					String evaPath = "";
					for(int k = rq.getAttributePath(j).levelsOfIndirection() - 1; k >= 0; k--)
					{
						if (conn.getDebug() == "debug") System.out.println("rq.getAttributePath(j).getIndirection(k): " + rq.getAttributePath(j).getIndirection(k));
						if(k > 0) evaPath = " OF " + rq.getAttributePath(j).getIndirection(k) + evaPath;
						else evaPath = rq.getAttributePath(j).getIndirection(k) + evaPath;
					}
					evaPath = rq.getAttributePath(j).attribute + " OF " + evaPath;
					evaAttribs.add(evaPath);
				}
			}
			if (conn.getDebug() == "debug") System.out.println("className: " + className);
			if (conn.getDebug() == "debug") System.out.println("dvaAttribs: " + dvaAttribs);
			if (conn.getDebug() == "debug") System.out.println("evaAttribs: " + evaAttribs);
			if (rq.expression != null) {
				traverseWhereInorder(rq.expression);
				where = where.replaceAll("= ", "= :").replaceAll("And", "&&").replaceAll("Or", "||");
			}
			if (conn.getDebug() == "debug") System.out.println("where: " + where);
			// The following is temporary until filter is used for the where clause
			String whereTmp = "";
			if(where != "") {
				whereTmp = where.replaceAll(" = :", " ").replaceAll("&&", " ").replaceAll("\\|\\|", " ").replaceAll("  *", " ").replaceAll("^  *", "").replaceAll("  *$", ""); //temporary
				if (conn.getDebug() == "debug") System.out.println(whereTmp); //temporary
				String [] whereTmpArray = whereTmp.split(" "); //temporary
				for( int i = 0; i <= whereTmpArray.length - 1; i+=2) //temporary
				{
					whereAttrValues.put(whereTmpArray[i], whereTmpArray[i+1]);
				}
			}
			if (conn.getDebug() == "debug") System.out.println("whereAttrValues: " + whereAttrValues);
			SIMHelper simhelper = new SIMHelper(conn);
			try {
					sparql = simhelper.executeFrom(className, dvaAttribs, evaAttribs, whereAttrValues);
				} catch (Exception e) {
					System.out.println(e);
			}
			if (conn.getDebug() == "debug") System.out.println("In ProcessesLangauges,sparql is: " + sparql);
		// --------------------------------------------------------------------------------- SIM Modify
		} else if(q instanceof ModifyQuery) {
// E.g., SIM is: MODIFY LIMIT = ALL emp (dept_members := dept WITH (deptno = 20)) WHERE deptno = 20;
//                                   ^       ^             ^        ^^^^^^^^^^^         ^^^^^^^^^^^
//                               className eva_name      eva_class      where1             where
			ModifyQuery mq = (ModifyQuery)q;
			String className = mq.className;
			String eva_name = "";
			String eva_class  = "";
			int limit = 1000000;		
	        ArrayList<PyObject> rows = new ArrayList<PyObject>();
			List<String> subjects = new ArrayList<String>();
			List<String> eva_subjects = new ArrayList<String>();
 
			sparql = "select ?indiv where { ";
			for(int i = 0; i < mq.assignmentList.size(); i++) {
				EvaAssignment evaAssignment = (EvaAssignment)mq.assignmentList.get(i);
				eva_name = evaAssignment.getAttributeName();
				eva_class = evaAssignment.targetClass;
				traverseWhereInorder(evaAssignment.expression.jjtGetChild(i)); // This sets the where variable to e.g., deptno = 20
				String where1 = where.trim();
				sparql += "GRAPH " + NoSQLNameSpacePrefix + ":" + eva_class + " { ?indiv " + NoSQLNameSpacePrefix + ":" + where1.replaceAll(" *= *", " \"") + "\"^^xsd:string }";
			}
			sparql += " }";
            if (conn.getDebug().equals("debug")) System.out.println("\nProcessLanguages SIM Modify, sparql is: \n" + sparql + "\n");
            rows = conn.getDatabase().OracleNoSQLRunSPARQL(sparql);
            for (int i = 1; i < rows.size(); i++) {
                eva_subjects.add(String.format("%s", rows.get(i)).replaceAll("[()]", "").replaceAll("'", "").replaceAll(",", "").replaceAll(conn.getDatabase().getNameSpace(), ""));
            }

			// Process WHERE clause
			if (mq.expression != null) {
				where = "";
				traverseWhereInorder(mq.expression);
				where = where.replaceAll("  *", " ").replaceAll("^ ", "").replaceAll(" $", "");
			}
			sparql = "select ?indiv where { GRAPH " + NoSQLNameSpacePrefix + ":" + className + " { ?indiv " + NoSQLNameSpacePrefix + ":" + where.replaceAll(" *= *", " \"") + "\"^^xsd:string } }";
            if (conn.getDebug() == "debug") System.out.println("\nProcessLanguages SIM Modify, sparql is: \n" + sparql + "\n");
            rows = conn.getDatabase().OracleNoSQLRunSPARQL(sparql);
            for (int i = 1; i < rows.size(); i++) {
                subjects.add(String.format("%s", rows.get(i)).replaceAll("[()]", "").replaceAll("'", "").replaceAll(",", "").replaceAll(conn.getDatabase().getNameSpace(), ""));
            }
			for (String subject: subjects) {
				for (String entity: eva_subjects) {
		            //connDatabase.OracleNoSQLAddQuad(className + "_" + schemaString, eva_name, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#FunctionalProperty", true);
		            //connDatabase.OracleNoSQLAddQuad(className + "_" + schemaString, eva_name, "http://www.w3.org/2000/01/rdf-schema#domain", className, true);
		            //connDatabase.OracleNoSQLAddQuad(className + "_" + schemaString, eva_name, "http://www.w3.org/2000/01/rdf-schema#range", eva_class, true);
		            connDatabase.OracleNoSQLAddQuad(className, subject, eva_name, entity, true);
				}
			}
			sparql = null;
			if(conn.getConnectionDB() == "OracleNoSQL") {
		    }
		}
		return sparql;
	}

	public synchronized void processNativeSIM(String ReLstmt) throws Exception {

		boolean DBG = true;
		ReLstmt = ReLstmt.replaceAll("\\_\\^\\_", ";");

		if (DBG) {
			if (ReLstmt.equalsIgnoreCase("clear database")) {
                OracleNoSQLDatabase db = (OracleNoSQLDatabase) connDatabase;
                db.clearDatabase();
                return;
			}
			if (ReLstmt.equalsIgnoreCase("stop database")) {
				OracleNoSQLDatabase db = (OracleNoSQLDatabase) connDatabase;
				db.ultimateCleanUp("Cleaning up from test.");
				return;
			}
		}
		InputStream is = new ByteArrayInputStream(ReLstmt.getBytes());
		Query q = null;
		if( ! parserInitialized) {
			parser =  new QueryParser(is);
			parserInitialized = true;
		} else { parser.ReInit(is); }
		try { q = parser.getNextQuery(); }
		catch(Exception e1) { System.out.println(e1.getMessage()); }

		if (DBG)
			System.out.println("Statement executed: " + ReLstmt);

		/************* BEGIN WDB CODE DUMP ************************/
//		if(q.getClass() == ClassDef.class || q.getClass() == SubclassDef.class)
		if(q instanceof ClassDef)
		{
			ClassDef cd = (ClassDef)q;
            try
            {
                adapter.getClass(cd.name);
                //That class already exists;
                throw new Exception("Class \"" + cd.name + "\" already exists");
            }
            catch(ClassNotFoundException cnfe)
            {
                if(cd.getClass() == SubclassDef.class)
                {
                    ClassDef baseClass = null;
                    for(int i = 0; i < ((SubclassDef)cd).numberOfSuperClasses(); i++)
                    {
                        //Cycles are implicitly checked since getClass will fail for the current defining class
                        ClassDef superClass = adapter.getClass(((SubclassDef)cd).getSuperClass(i));
                        if(baseClass == null)
                        {
                            baseClass = superClass.getBaseClass(adapter);
                        }
                        else if(!baseClass.name.equals(superClass.getBaseClass(adapter).name))
                        {
                            throw new Exception("Super classes of class \"" + cd.name + "\" do not share the same base class");
                        }
                    }
                }

                adapter.putClass(cd);
                adapter.commit();
            }
            catch(Exception e)
            {
                System.out.println("This class already exists: " + cd.name);
                adapter.abort();
                return;
            }
		}

		if(q.getClass() == ModifyQuery.class)
		{
			ModifyQuery mq = (ModifyQuery)q;
            try
            {
                ClassDef targetClass = adapter.getClass(mq.className);
                WDBObject[] targetClassObjs = targetClass.search(mq.expression, adapter);
                if(mq.limit > -1 && targetClassObjs.length > mq.limit)
                {
                    throw new Exception("Matching entities exceeds limit of " + mq.limit.toString());
                }
                for(int i = 0; i < targetClassObjs.length; i++)
                {
                    setValues(mq.assignmentList, targetClassObjs[i], adapter);
                }
                adapter.commit();
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
                adapter.abort();
            }
		}

		if(q instanceof InsertQuery)
		{
			InsertQuery iq = (InsertQuery)q;
            try
            {
                ClassDef targetClass = adapter.getClass(iq.className);
                WDBObject newObject = null;

                if(iq.fromClassName != null)
                {
                    //Inserting from an entity of a superclass...
                    if(targetClass.getClass() == SubclassDef.class)
                    {
                        SubclassDef targetSubClass = (SubclassDef)targetClass;
                        ClassDef fromClass = adapter.getClass(iq.fromClassName);
                        if(targetSubClass.isSubclassOf(fromClass.name, adapter))
                        {
                            WDBObject[] fromObjects = fromClass.search(iq.expression, adapter);
                            if(fromObjects.length <= 0)
                            {
                                throw new IllegalStateException("Can't find any entities from class \"" + fromClass.name + "\" to extend");
                            }
                            for(int i = 0; i < fromObjects.length; i++)
                            {
                                newObject = targetSubClass.newInstance(fromObjects[i].getBaseObject(adapter), adapter);
                                setValues(iq.assignmentList, newObject, adapter);
                            }
                        }
                        else
                        {
                            throw new IllegalStateException("Inserted class \"" + targetClass.name + "\" is not a subclass of the from class \"" + iq.fromClassName);
                        }
                    }
                    else
                    {
                        throw new IllegalStateException("Can't extend base class \"" + targetClass.name + "\" from class \"" + iq.fromClassName);
                    }
                }
                else
                {
                    //Just inserting a new entity
                    newObject = targetClass.newInstance(null, adapter);
                    setDefaultValues(targetClass, newObject, adapter);
                    setValues(iq.assignmentList, newObject, adapter);
                    checkRequiredValues(targetClass, newObject, adapter);
                }

                if(newObject != null)
                {
                    newObject.commit(adapter);
                }
                adapter.commit();
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
                adapter.abort();
            }
		}

		if(q instanceof IndexDef)
		{
			IndexDef indexQ = (IndexDef)q;
            try
            {
                ClassDef classDef = adapter.getClass(indexQ.className);
                classDef.addIndex(indexQ, adapter);

                adapter.commit();
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
                adapter.abort();
            }
		}

		if(q instanceof RetrieveQuery)
		{
			//Ok, its a retrieve...
			RetrieveQuery rq = (RetrieveQuery)q;
            try
            {
                ClassDef targetClass = adapter.getClass(rq.className);
                WDBObject[] targetClassObjs = targetClass.search(rq.expression, adapter);
                int i, j;
                String[][] table;
                String[][] newtable;

                PrintNode node = new PrintNode(0,0);
                for(j = 0; j < rq.numAttributePaths(); j++)
                {
                    targetClass.printAttributeName(node, rq.getAttributePath(j), adapter);
                }
                table = node.printRow();
                for(i = 0; i < targetClassObjs.length; i++)
                {
                    node = new PrintNode(0,0);
                    for(j = 0; j < rq.numAttributePaths(); j++)
                    {
                        targetClassObjs[i].PrintAttribute(node, rq.getAttributePath(j), adapter);
                    }
                    newtable = joinRows(table, node.printRow());
                    table = newtable;
                }

                adapter.commit();

                Integer[] columnWidths= new Integer[table[0].length];

                for(i = 0; i < columnWidths.length; i++)
                {
                    columnWidths[i] = 0;
                    for(j = 0; j < table.length; j++)
                    {
                        if(i < table[j].length && table[j][i] != null && table[j][i].length() > columnWidths[i])
                        {
                            columnWidths[i] = table[j][i].length();
                        }
                    }
                }

                for(i = 0; i < table.length; i++)
                {
                    for(j = 0; j < table[0].length; j++)
                    {
                        if(j >= table[i].length || table[i][j] == null)
                        {
                            System.out.format("| %"+columnWidths[j].toString()+"s ", "");
                        }
                        else
                        {
                            System.out.format("| %"+columnWidths[j].toString()+"s ", table[i][j]);
                        }
                    }
                    System.out.format("|%n");
                }
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
                adapter.abort();
            }
		}
		/******************* END WDB CODE DUMP *****************/
	}


	public synchronized PyObject[] processSQLRdfMode(Statement statement,
													 PyRelConnection conn,
													 ArrayList<PyType> relQueryInstancesType,
													 ArrayList<String> relQueryInstancesTypeNames)
	{
		if (statement instanceof CreateTable) {
			try {
				CreateTable caststmt = (CreateTable)statement;
				SQLVisitor visitor = new SQLVisitor(conn);
				visitor.doCreateTable(caststmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (statement instanceof Insert) {
			try {
				Insert caststmt = (Insert)statement;
				SQLVisitor visitor = new SQLVisitor(conn);
				visitor.doInsert(caststmt, conn.getConnectionType());//visitor.doInsert takes 2 arguments now instead of 1 argument
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (statement instanceof Select) {
			try {

				Select caststmt = (Select)statement;
				SQLVisitor visitor = new SQLVisitor(conn);
				String sparql = "";
				if (relQueryInstancesTypeNames.size() > 0)
				{
					sparql = visitor.getSelect(caststmt, relQueryInstancesTypeNames, conn.getConnectionType());
				}
				else {
					sparql = visitor.getSelect(caststmt, null, conn.getConnectionType());
				}
				// an oo query forces the session to be committed first.
				conn.commit_oorel_session();
				ArrayList<PyObject> rowResults;
				if(conn.getConnectionDB().equals("OracleNoSQL")) {
					rowResults = conn.getDatabase().OracleNoSQLRunSPARQL(sparql);
					//a lot of conversion going on here. . .
					return rowResults.toArray(new PyObject[rowResults.size()]);
				} else {
					ProcessOracleEESQL processOracleEESQL = new ProcessOracleEESQL(conn, relQueryInstancesType, relQueryInstancesTypeNames);
					rowResults = processOracleEESQL.processSQL(sparql);
					//a lot of conversion going on here. . .
					return rowResults.toArray(new PyObject[rowResults.size()]);
				}
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		} else if (statement instanceof Delete)
		{
			Delete caststmt = (Delete) statement;
			String sqlstmt = "";

			BinaryExpression expression = ((BinaryExpression)caststmt.getWhere());
			Expression left = expression.getLeftExpression();
			Expression right = expression.getRightExpression();
			String left_exp = left + "";
			String right_exp = right + "";
			if(right.toString().charAt(0) == '\'') {
				right_exp = right_exp.substring(1, right_exp.length()-1);
			}

			String table = conn.getTable();
			String this_value = right_exp;
			String this_column = left_exp;
			String rvalue = "";
			String lvalue = "";
			try
			{
				Double.parseDouble(this_value);
				try {
					Integer.parseInt(this_value);
					rvalue  = "a.triple.get_obj_value() = '\"" + this_value + "\"^^<http://www.w3.org/2001/XMLSchema#integer>'";
				} catch(NumberFormatException e) {
					rvalue  = "a.triple.get_obj_value() = '\"" + this_value + "\"^^<http://www.w3.org/2001/XMLSchema#float>'";
				}
			} catch(NumberFormatException e)
			{
				rvalue  = "a.triple.get_obj_value() = '\"" + this_value + "\"^^<http://www.w3.org/2001/XMLSchema#string>'";
			}
			try
			{
				Double.parseDouble(this_column);
				try {
					Integer.parseInt(this_column);
					lvalue  = "a.triple.get_obj_value() = '\"" + this_column + "\"^^<http://www.w3.org/2001/XMLSchema#integer>'";
				} catch(NumberFormatException e) {
					lvalue  = "a.triple.get_obj_value() = '\"" + this_column + "\"^^<http://www.w3.org/2001/XMLSchema#float>'";
				}
			} catch(NumberFormatException e)
			{
				lvalue  = "a.triple.get_obj_value() = '\"" + this_column + "\"^^<http://www.w3.org/2001/XMLSchema#string>'";
			}

			String lcolumn = "a.triple.get_property() = '<" + conn.getNamespace() + this_column + ">'";
			String rcolumn = "a.triple.get_property() = '<" + conn.getNamespace() + this_value + ">'";
			String subject = "a.triple.get_subject() IN (SELECT distinct b.triple.GET_SUBJECT()  from " + conn.getTable() +
					" b where b.triple.get_property() = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>' and b.triple.get_obj_value() = '<" +
					conn.getNamespace() + caststmt.getTable() + ">')";

            /* Example:
            DELETE FROM RDF_CS347_PROF_DATA a
            WHERE ((a.triple.get_property() = '<http://www.example.org/people.owl#VAL1>' AND a.triple.get_obj_value() = '"one"^^<http://www.w3.org/2001/XMLSchema#string>')
            OR (a.triple.get_property() = '<http://www.example.org/people.owl#one>' AND a.triple.get_obj_value() = '"VAL1"^^<http://www.w3.org/2001/XMLSchema#string>'))
            AND a.triple.get_subject() IN
            (SELECT distinct b.triple.GET_SUBJECT()
               from RDF_CS347_PROF_DATA b
               where b.triple.get_property() = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>' and b.triple.get_obj_value() = '<http://www.example.org/people.owl#TEST_DELETE>')
            */

			sqlstmt = "DELETE FROM " + table + " a WHERE ((" + lcolumn + " AND " + rvalue + ") OR (" + rcolumn + " AND " + lvalue + ")) AND " + subject;
			if (conn.getDebug() == "debug") {
				System.out.println();
				System.out.println(sqlstmt);
				System.out.println();
			}
			try{ conn.executeStatement(sqlstmt);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		} else if (statement instanceof Update) {
			Update caststmt = (Update) statement;
			String sqlstmt = "";

			String table = conn.getTable();
			String subject = "a.triple.get_subject() IN (SELECT distinct b.triple.GET_SUBJECT()  from " + conn.getTable() +
					" b where b.triple.get_obj_value() = '<" +
					conn.getNamespace() + ">')";
			System.out.println("In Update\ntable is : " + table);
			System.out.println("subject is : " + subject);
		} else if (statement instanceof Drop) {
			Drop caststmt = (net.sf.jsqlparser.statement.drop.Drop) statement;
			String sqlstmt = "";

			String table = conn.getTable();
			String subject = "a.triple.get_subject() IN (SELECT distinct b.triple.GET_SUBJECT()  from " + conn.getTable() +
					" b where b.triple.get_obj_value() = '<" +
					conn.getNamespace() + caststmt.getName() + ">')";

			sqlstmt = "DELETE FROM " + table + " a WHERE " + subject;
			if (conn.getDebug() == "debug") {
				System.out.println();
				System.out.println(sqlstmt);
				System.out.println();
			}
			try{ conn.executeStatement(sqlstmt);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
		return null;
	}

	private static void setDefaultValues(ClassDef targetClass, WDBObject targetObject, Adapter adapter) throws Exception
	{
		for(int j = 0; j < targetClass.numberOfAttributes(); j++)
		{
			if(targetClass.getAttribute(j) instanceof DVA)
			{
				DVA dva = (DVA)targetClass.getAttribute(j);
				if(dva.initialValue != null)
				{
					targetObject.setDvaValue(dva.name, dva.initialValue, adapter);
				}
			}
		}
	}
	private static void checkRequiredValues(ClassDef targetClass, WDBObject targetObject, Adapter adapter) throws Exception
	{
		for(int j = 0; j < targetClass.numberOfAttributes(); j++)
		{
			Attribute attribute = (Attribute)targetClass.getAttribute(j);
			if(attribute.required != null && attribute.required && targetObject.getDvaValue(attribute.name, adapter) == null)
			{
				throw new Exception("Attribute \"" + targetClass.getAttribute(j).name + "\" is required");
			}
		}
	}
	private static void setValues(ArrayList assignmentList, WDBObject targetObject, Adapter adapter) throws Exception
	{
		for(int j = 0; j < assignmentList.size(); j++)
		{
			if(assignmentList.get(j) instanceof DvaAssignment)
			{
				DvaAssignment dvaAssignment = (DvaAssignment)assignmentList.get(j);
				targetObject.setDvaValue(dvaAssignment.AttributeName, dvaAssignment.Value, adapter);
			}

			else if(assignmentList.get(j) instanceof EvaAssignment)
			{
				EvaAssignment evaAssignment = (EvaAssignment)assignmentList.get(j);
				if(evaAssignment.mode == EvaAssignment.REPLACE_MODE)
				{
					WDBObject[] currentObjects = targetObject.getEvaObjects(evaAssignment.AttributeName, adapter);
					targetObject.removeEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, currentObjects, adapter);
					targetObject.addEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, evaAssignment.expression, adapter);
				}
				else if(evaAssignment.mode == EvaAssignment.EXCLUDE_MODE)
				{
					targetObject.removeEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, evaAssignment.expression, adapter);
				}
				else if(evaAssignment.mode == EvaAssignment.INCLUDE_MODE)
				{
					targetObject.addEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, evaAssignment.expression, adapter);
				}
				else
				{
					throw new Exception("Unsupported multivalue EVA insert/modify mode");
				}
			}
		}
	}
	private static String[][] joinRows(String[][] row1, String[][] row2)
	{
		if(row1.length <= 0)
		{
			return row2;
		}
		else
		{
			String[][] newRow = new String[row1.length+row2.length][row1[0].length];
			int i, j;
			for(i = 0; i < row1.length; i++)
			{
				newRow[i] = row1[i];
			}
			for(j = i; j < row2.length + i; j++)
			{
				newRow[j] = row2[j-i];
			}

			return newRow;
		}
	}


 public void traverseWhereInorder(Node node) {
	if (node != null)
	{
		if(node.jjtGetNumChildren() > 0) traverseWhereInorder(node.jjtGetChild(0));
		if(node.toString() != "Root") where += " " + node.toString();
		if(node.jjtGetNumChildren() > 1) traverseWhereInorder(node.jjtGetChild(1));
	}
 }
	
//                                                                  ------------------------------------- Neo4j -------------------------
    public String processNeo4j(String ReLstmt) {
		
    	CypherSimTranslator t = new CypherSimTranslator();
    	if (conn.getDebug() == "debug") System.out.println("SIM is: " + t.translate(ReLstmt));
		return t.translate(ReLstmt);
	}

//Libraries for AllegroGraph added by Joojay
//import com.hp.hpl.jena.query.ResultSet; //conflicts with java.sql.ResultSet

	public class SQLVisitor extends SelectDeParser implements SelectVisitor, FromItemVisitor, ExpressionVisitor, ItemsListVisitor, SelectItemVisitor, OrderByVisitor {

		private List<String> filters;
		private List<String> matches;
		private HashMap<String, String> tablesAliases;
		private HashMap<String, String> tables2alias;
		private LinkedHashMap<String, String> columnsAs;
		private LinkedHashMap<String, String> aggrColumnsAs;
		private List<String> joinColumns;
		private  String temp;
		private String ownException;
		private Boolean wasEquals;
		private Boolean subselect;
		private int subDepth;
		private  int joinInc = 1; // for each join this will be incremented and used in a variable name.

		HashMap<String, String> colVarNames = new HashMap<String, String>();
		ArrayList<String> allCols = new ArrayList<String>();
		private String[] aggregates = {"avg", "count", "max", "min", "sum"}; // Input as lower case.
		private String[] inequalities = {" = ", " > ", " < ", " >= ", " <= ", " != "};
		ArrayList<String> plainAliases = new ArrayList<String>();

		private String tablenameFrom = "";
		public String url = "";
		public String uname = "";
		public String pword = "";
		public OracleDataSource ods;
		public PyRelConnection connection;
		public com.hp.hpl.jena.rdf.model.Statement stmt;

		public CCJSqlParserManager parserManager = new CCJSqlParserManager();

		 private HashMap<String, String> map  = new HashMap<String, String>();

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
				for (ColumnDefinition col : stmt.getColumnDefinitions()) {
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
			//Creating data structures to hold valuesF to build Oracle SQL statement.
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
							if (Objects.equals(columnName, ""))
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
								if (Objects.equals(columnName, ""))
									columnName = aggregateElement[1];
								columnName = aggregateElement[0].toUpperCase() + "(" + columnName + ")";
								aggrColumnsAs.put("?v" + pos,
										"\"" + aliasName.replace("\"", "") + "\"");
							}
							else{
								String tcolName = columnName;
								columnName = resolveColumnName(columnNames, columnName);
								if (Objects.equals(columnName, ""))
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
				for (Join join : plainSelect.getJoins()) {
					if (join.getOnExpression() != null) {
						join.getOnExpression().accept(this);
						String s = join.toString().substring(join.toString().lastIndexOf("ON (") + 4);
						String[] split = s.substring(0, s.length() - 1).split(" = ");
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

				String[] orderbyElems = orderByStmnt.split(" |,");

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
					for (String inequality : inequalities) {
						if (inequality.replace(" ", "").equals(havingElems[i])) {
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


			if (connection.getDebug().equals("debug")) {
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
				if (connection.getDebug().equals("debug")) System.out.println("\t - columnsAs: " + columnsAs + "\naggrColumnsAs: " + aggrColumnsAs);
			}
			int aggrPos = 0;

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
				} else if (connectionType.equals("rdf_mode")) {
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
		private Stack<Boolean> sq = new Stack<>();

		/* Store queries */
		private Stack<String> subq = new Stack<>();

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
				if (connection.getDebug().equals("debug")) System.out.println("SQLVisitor-getColumns, entry: " + entry);
				if (connection.getDebug().equals("debug")) System.out.println("SQLVisitor-getColumns, colname(entry): " + colname(entry));
				if (connection.getDebug().equals("debug")) System.out.println("SQLVisitor-getColumns, tablename(entry): " + tablename(entry));
				if (connection.getDebug().equals("debug")) System.out.println("SQLVisitor-getColumns, columnsAs.get(entry): " + columnsAs.get(entry));
				if (connection.getDebug().equals("debug")) System.out.println("SQLVisitor-getColumns, colname(columnsAs.get(entry)): " + colname(columnsAs.get(entry)));
				// So if we are returning instances. We need a way to determine which instance the data belongs too.
				// To do this i append the tablename_ as part of the column's "as" statement, so the columns returned will start
				// with the name of the instance for which they belong too.
				if (returns_instances_of != null)
                	s += "v" + var++ + " " + tablename(entry)+"_"+colname(columnsAs.get(entry)) + ", ";
				else
					s += "v" + var++ + " " + colname(columnsAs.get(entry)) + ", ";
			}
			if (s.length() < 1)
				return s;
			s = s.substring(0,s.length()-2);
			s += " from table(\n\tSEM_MATCH('SELECT * WHERE {\n\t";
			return s;
		}



		/**
		 *
		 */
		 public String tablename(String item) {
			if(item.indexOf('.')>0)
				return item.substring(0,item.indexOf('.'));
			return "tbl";
		}

		/**
		 *
		 */
		 public String getFilterTableName(String str) {
			return (str.substring(0, str.indexOf(" "))).substring(str.indexOf("_") + 1);
		}


		/**
		 *
		 */
		 public String colname(String item) {
			if(item.indexOf('.')>0)
				return item.substring(item.indexOf('.')+1);
			return item;
		}

		/**
		 *
		 */
		 public String filter(String item, String comparison, String value) {
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
		 public boolean isNumeric(String str){
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


}