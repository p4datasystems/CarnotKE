package org.python.ReL;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.UUID;

import org.python.antlr.base.expr;
import org.python.core.PyObject;

import org.python.ReL.PyRelConnection;
import org.python.ReL.SPARQLHelper;
import org.python.ReL.SIMHelper;

import wdb.metadata.*;
import wdb.parser.*;

import java.io.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.antlr.runtime.ANTLRStringStream;
import org.apache.commons.io.IOUtils;

import org.cyphersim.CypherSimTranslator;

public class ProcessLanguages {
    PyRelConnection conn = null;
    SPARQLHelper sparqlHelper = null; 
    DatabaseInterface connDatabase;
    String schemaString = null;
	String NoSQLNameSpacePrefix = "";
    static Boolean parserInitialized = false;
	static QueryParser parser = null;	
	String where = "";

    /**
     * Process a language statement such as SQL or SIM.
     *
     * Note:  The connection should not be closed within this class, as it will be closed
     * by its parent (invoker).
     *
     * @param connection
     */
    public ProcessLanguages(PyRelConnection conn) {
        this.conn = conn;  
        sparqlHelper = new SPARQLHelper(conn);
        schemaString = sparqlHelper.getSchemaString();
        connDatabase  = conn.getDatabase();
		NoSQLNameSpacePrefix = connDatabase.getNameSpacePrefix();
    }

//                                                                  ------------------------------------- SIM -------------------------
    public synchronized String processSIM(String ReLstmt) throws SQLException { 
    	String Save_ReLstmt = ReLstmt;
		ReLstmt += ";";
		InputStream is = new ByteArrayInputStream(ReLstmt.getBytes());
		Query q = null;
		if( ! parserInitialized) {
			parser =  new QueryParser(is);
			parserInitialized = true;
		} else { parser.ReInit(is); }
		try { q = parser.getNextQuery(); }
		catch(Exception e1) { System.out.println(e1.getMessage()); }
		String sparql = null;
		// --------------------------------------------------------------------------------- SIM Insert
	    // E.G., INSERT INTO onto_DATA VALUES ( 1, SDO_RDF_TRIPLE_S('onto', '#PERSON', 'rdf:type', 'rdfs:Class'));
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
			if (conn.getDebug() == "debug") System.out.println(sparql);
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
/*

		    sparql = null;
		    } else {
/*
// Process Modify on ! OracleRDFNoSQL.
				SIMHelper simhelper = new SIMHelper(conn);
				try {
						simhelper.executeModify(className, attributeValues, whereAttrValues, 1000000);
					} catch (Exception e) {
						System.out.println(e);
				}
*/
		    }
		}
		return sparql;
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