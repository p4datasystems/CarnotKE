package org.python.ReL;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.python.antlr.base.expr;

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
    String schemaString = null;
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
		// --------------------------------------------------------------------------------- Insert
		if(q instanceof InsertQuery) {
			InsertQuery iq = (InsertQuery)q;
			String instanceID = SPARQLDoer.getNextAnonNodeForInd(conn);
			//INSERT INTO onto_DATA VALUES ( 1, SDO_RDF_TRIPLE_S('onto', '#PERSON', 'rdf:type', 'rdfs:Class'));
			sparqlHelper.insertSchemaQuad("", iq.className, "rdf:type", "rdfs:Class");
			for (int i = 0; i < iq.numberOfAssignments(); i++) {
				if(iq.getAssignment(i)instanceof EvaAssignment) {
				/*
					Map<String, Object> attrValues = new HashMap<String, Object>();
					EvaAssignment evaAssignment = (EvaAssignment)iq.getAssignment(i);
					String evaValue = (String)evaAssignment.expression.jjtGetChild(0).toString();
					List<String> members = sparqlHelper.getInstancesWithObjectValue("dept", "dept", attrValues);
					for (String member : members) {
						sparqlHelper.insertQuad(iq.className, instanceID, evaAssignment.AttributeName, member);
						// sparqlHelper.insertQuad(withClass, member, inverse, instanceID);
					}
					// The following statement puts the data value into the quad store so that SQL joins will work.
					// sparqlHelper.insertQuad(iq.className, instanceID, attrName, withParts[4]);	
				*/
				} else if(iq.getAssignment(i)instanceof DvaAssignment) {
					DvaAssignment dvaAssignment = (DvaAssignment)iq.getAssignment(i);
					sparqlHelper.insertQuad(iq.className, instanceID, dvaAssignment.AttributeName, (String)dvaAssignment.Value.toString(), false);
				}
			}
		// --------------------------------------------------------------------------------- Query
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
					columns = sparqlHelper.getSubjects(className + "_" + sparqlHelper.getSchemaString(), "rdf:type", "owl:" + "DatatypeProperty");
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
						System.out.println("rq.getAttributePath(j).getIndirection(k): " + rq.getAttributePath(j).getIndirection(k));
						if(k > 0) evaPath = " OF " + rq.getAttributePath(j).getIndirection(k) + evaPath;
						else evaPath = rq.getAttributePath(j).getIndirection(k) + evaPath;
					}
					evaPath = rq.getAttributePath(j).attribute + " OF " + evaPath;
					evaAttribs.add(evaPath);
				}
			}
System.out.println("className: " + className);
System.out.println("dvaAttribs: " + dvaAttribs);
System.out.println("evaAttribs: " + evaAttribs);
			if (rq.expression != null) {
				traverseWhereInorder(rq.expression);
				where = where.replaceAll("= ", "= :").replaceAll("And", "&&").replaceAll("Or", "||");
			}
System.out.println("where: " + where);
			// The following is temporary until filter is used for the where clause
			String whereTmp = "";
			if(where != "") {
				whereTmp = where.replaceAll(" = :", " ").replaceAll("&&", " ").replaceAll("\\|\\|", " ").replaceAll("  *", " ").replaceAll("^  *", "").replaceAll("  *$", ""); //temporary
				System.out.println(whereTmp); //temporary
				String [] whereTmpArray = whereTmp.split(" "); //temporary
				for( int i = 0; i <= whereTmpArray.length - 1; i+=2) //temporary
				{
					whereAttrValues.put(whereTmpArray[i], whereTmpArray[i+1]);
				}
			}
System.out.println("whereAttrValues: " + whereAttrValues);
			SIMHelper simhelper = new SIMHelper(conn);
			try {
					sparql = simhelper.executeFrom(className, dvaAttribs, evaAttribs, whereAttrValues);
				} catch (Exception e) {
					System.out.println(e);
			}
			System.out.println(sparql);
		// --------------------------------------------------------------------------------- Modify
		} else if(q instanceof ModifyQuery) {
			ModifyQuery mq = (ModifyQuery)q;
			String className = mq.className;
			Map<String, String> attributeValues = new HashMap<String, String>();
			Map<String, Object> whereAttrValues = new HashMap<String, Object>();
			// Process EVA assignments.
			for(int i = 0; i < mq.assignmentList.size(); i++) {
				EvaAssignment evaAssignment = (EvaAssignment)mq.assignmentList.get(i);
				traverseWhereInorder(evaAssignment.expression.jjtGetChild(i));
				String evaValue = (evaAssignment.targetClass + " WITH " + where).replaceAll("=", " ").replaceAll("And", " ").replaceAll("  *", " ");
				attributeValues.put(evaAssignment.getAttributeName(), evaValue);
			}
			// Process WHERE clause.
			if (mq.expression != null) {
				where = "";
				traverseWhereInorder(mq.expression);
				where = where.replaceAll("  *", " ").replaceAll("^ ", "").replaceAll(" $", "");
			}
			String [] whereTmpArray = where.split(" And "); //temporary
			for( int j = 0; j < whereTmpArray.length; j++) //temporary
			{
				String [] whereTmpArray2 = whereTmpArray[j].split(" = "); //temporary
				whereAttrValues.put(whereTmpArray2[0], whereTmpArray2[1]);
			}
			// executeModify.
			SIMHelper simhelper = new SIMHelper(conn);
			try {
					simhelper.executeModify(className, attributeValues, whereAttrValues, 1000000);
				} catch (Exception e) {
					System.out.println(e);
			}
			System.out.println(sparql);
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
    	System.out.println("SIM is: " + t.translate(ReLstmt));
		return t.translate(ReLstmt);
	}
}