package org.python.ReL;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.UUID;

import org.python.core.PyObject;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.cyphersim.CypherSimTranslator;
import wdb.metadata.*;
import wdb.parser.Node;
import wdb.parser.QueryParser;

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
            if (conn.getDebug() == "debug") System.out.println("\nProcessLanguages SIM Modify, sparql is: \n" + sparql + "\n");
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

	private List<String> trimAndSplitOnDelim(String s, String delim) {
        String r = s.trim();
        String[] parts = s.split(delim);
        List<String> parts2 = new ArrayList<String>();
        for (String part : parts) {
            part = part.trim();
            if (part.length() > 0) {
                parts2.add(part);
            }
        }
        return parts2;
    }
}