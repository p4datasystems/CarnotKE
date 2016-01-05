package org.python.ReL;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.python.core.PyObject;
import org.python.ReL.SPARQLDoer;
import org.python.ReL.PyRelConnection;

/**
 * SPARQLHelper is a set of utility functions that are useful
 * for generating sparql, and executing the sparql on a database.
 */
public class SPARQLHelper {
    PyRelConnection connection = null; 
    String schemaString = "SCHEMA";

    /**
     * SPARQLHelper constructor.
     *
     * Note:  The connection should not be closed within this class, as it will be closed
     * by its parent (invoker).
     *
     * @param connection
     */
    public SPARQLHelper(PyRelConnection conn) {
        this.connection = conn; 
    }
    
    public String getSchemaString() { return schemaString; }

    /**
     * Insert an RDF Quad into the RDF data store
     *
     * @param graph The subject of the statement  e.g. EMP
     * @param subject The subject of the statement  e.g. EMP
     * @param property The object-property  e.g. Name
     * @param object The object of the statement  e.g. 'Phil'
     * @throws SQLException
     */
    public void insertQuad(String graph, String subject, String predicate, String object, Boolean eva) throws SQLException {
        String connection_DB = connection.getConnectionDB();
        if(connection_DB.equals("OracleNoSQL")) {
/*
A0_C##CS329E_UTEID:<SCHEMA> <#dept> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   <http://www.w3.org/2000/01/rdf-schema#Class>
A0_C##CS329E_UTEID:<dept_SCHEMA>    <#deptno>   <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   <http://www.w3.org/2002/07/owl#DatatypeProperty>
A0_C##CS329E_UTEID:<dept_SCHEMA>    <#deptno>   <http://www.w3.org/2000/01/rdf-schema#domain>   <#dept>
A0_C##CS329E_UTEID:<dept_SCHEMA>    <#deptno>   <http://www.w3.org/2000/01/rdf-schema#range>    <http://www.w3.org/2001/XMLSchema#string>
A0_C##CS329E_UTEID:<dept_SCHEMA>    <#i15>  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   <#dept>

sim is: INSERT dept ( DEPTNO := 10 );

exec -> INSERT INTO A0_C##CS329E_UTEID_DATA VALUES ( A0_C##CS329E_UTEID_SQNC.nextval, SDO_RDF_TRIPLE_S('A0_C##CS329E_UTEID:<SCHEMA>', '#dept', 'rdf:type', 'rdfs:Class'))
exec -> INSERT INTO A0_C##CS329E_UTEID_DATA VALUES ( A0_C##CS329E_UTEID_SQNC.nextval, SDO_RDF_TRIPLE_S('A0_C##CS329E_UTEID:<dept_SCHEMA>', '#deptno', 'rdf:type', 'owl:DatatypeProperty'))
exec -> INSERT INTO A0_C##CS329E_UTEID_DATA VALUES ( A0_C##CS329E_UTEID_SQNC.nextval, SDO_RDF_TRIPLE_S('A0_C##CS329E_UTEID:<dept_SCHEMA>', '#deptno', 'rdfs:domain', '#dept'))
exec -> INSERT INTO A0_C##CS329E_UTEID_DATA VALUES ( A0_C##CS329E_UTEID_SQNC.nextval, SDO_RDF_TRIPLE_S('A0_C##CS329E_UTEID:<dept_SCHEMA>', '#deptno', 'rdfs:range', 'xsd:string'))
exec -> INSERT INTO A0_C##CS329E_UTEID_DATA VALUES ( A0_C##CS329E_UTEID_SQNC.nextval, SDO_RDF_TRIPLE_S('A0_C##CS329E_UTEID:<dept_SCHEMA>', '#i20', 'rdf:type', '#dept'))
exec -> INSERT INTO A0_C##CS329E_UTEID_DATA VALUES ( A0_C##CS329E_UTEID_SQNC.nextval, SDO_RDF_TRIPLE_S('A0_C##CS329E_UTEID:<dept>', '#i20', '#deptno', '#10'))
*/

            connection.OracleNoSQLAddQuad("SCHEMA", graph, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "rdfs:Class");
            if(eva) connection.OracleNoSQLAddQuad("SCHEMA", predicate, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "owl:FunctionalProperty");
            else connection.OracleNoSQLAddQuad("SCHEMA", predicate, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "owl:DatatypeProperty");
            connection.OracleNoSQLAddQuad("SCHEMA", predicate, "http://www.w3.org/2000/01/rdf-schema#domain", graph);
            connection.OracleNoSQLAddQuad("SCHEMA", predicate, "http://www.w3.org/2000/01/rdf-schema#range", "xsd:string");
            connection.OracleNoSQLAddQuad("SCHEMA", subject, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", graph);
            connection.OracleNoSQLAddQuad(graph, subject, predicate, object);
        }
        else if(connection_DB.equals("Oracle")) {
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
            if(  ! graph.equals("")) {
    			s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName +
    				"', '" + subject.replaceAll("'", "") + "', '" + predicate.replaceAll("'", "") + "', '" + object.replaceAll("'", "") + typeString + "'))";
    			connection.executeStatement(s);		
    			s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
    				"', '" + subject.replaceAll("'", "") + "', 'rdf:type', '" + connection.getNamespace() + graph + "'))";
    			connection.executeStatement(s);	
    			if(eva) s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
    				"', '" + predicate.replaceAll("'", "") + "', 'rdf:type', 'owl:FunctionalProperty'))";
    			else s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
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
    				
    //INSERT INTO F2014_C##CS347_PROF_DATA VALUES ( 4, SDO_RDF_TRIPLE_S('F2014_C##CS347_PROF:<MALE_SCHEMA>', '#i1529', 'rdf:type', '#MALE'));
    //INSERT INTO F2014_C##CS347_PROF_DATA VALUES ( 5, SDO_RDF_TRIPLE_S('F2014_C##CS347_PROF:<MALE_SCHEMA>', '#ID', 'rdf:type', 'owl:DatatypeProperty'));
    //INSERT INTO F2014_C##CS347_PROF_DATA VALUES ( 6, SDO_RDF_TRIPLE_S('F2014_C##CS347_PROF:<MALE_SCHEMA>', '#ID', 'rdfs:domain', '#MALE'));
    //INSERT INTO F2014_C##CS347_PROF_DATA VALUES ( 7, SDO_RDF_TRIPLE_S('F2014_C##CS347_PROF:<MALE_SCHEMA>', '#ID', 'rdfs:range', 'xsd:string'));
    		}
        }
    } // End insertQuad

    /**
     * Insert an RDF Schema Quad into the RDF data store
     *
     * @param graph The subject of the statement  e.g. EMP
     * @param subject The subject of the statement  e.g. EMP
     * @param property The object-property  e.g. rdf:type
     * @param object The object of the statement  e.g. rdfs:Class
     * @throws SQLException
     */
    public void insertSchemaQuad(String graph, String subject, String predicate, String object)  throws SQLException {

        String connection_DB = connection.getConnectionDB();
        if(connection_DB.equals("OracleNoSQL")) {
            connection.OracleNoSQLAddQuad(graph, subject, predicate, object);
        }
        else if(connection_DB.equals("Oracle")) {
            String graphName = connection.getModel() + ":<" + graph + "_" + schemaString +">";
            String graphName2 = connection.getModel() + ":<" + schemaString + ">";
            
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
            String s = "";
            if( ! object.equals("rdf:class") && ! graph.equals("")) {
    			s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName +
    				"', '" + subject + "', '" + predicate + "', '" + object + "'))";
    			connection.executeStatement(s);
    		} else {
    			s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
    				"', '" + subject + "', '" + predicate + "', '" + object + "'))";
    			connection.executeStatement(s);
    		}
        }
    } // End insertSchemaQuad

    /**
     * Get instances of a class, with datatype properties
     * that match the attr-value pairs provided
     * 
     * @param graph
     * @param className
     * @param Map of attrValues
     * @return class instances
     * @throws SQLException
     */
    public List<String> getInstancesWithObjectValue(String graph, String className, Map<String, Object> attrValues) throws SQLException{
		List<String> instances = new ArrayList<String>();
		String typeTriple = "";
		if (className != null) {
				typeTriple =  "         ?indiv rdf:type :" + className + " .\n";
		}
		String attrValuesQ = "";
		for (String attr : attrValues.keySet()) {
			Object val = attrValues.get(attr); 
			if(val instanceof String){
				attrValuesQ += "?indiv :" + attr + " :" + ((String)val).replaceAll("'", "") + " .\n";
			}
			else if(val instanceof Integer){
				attrValuesQ += "?indiv :" + attr + " " + attrValues.get(attr).toString() + " .\n";
			}
		}
		String q =
			"select indiv from table(sem_match(\n" +
			"  'select * where {\n" +
			typeTriple +
			attrValuesQ +
			"}',\n" +
			"SEM_MODELS('" 
			+ connection.getModel() +"'), null,\n" +
			"SEM_ALIASES( SEM_ALIAS('', '" 
			+ connection.getNamespace() + "')), null) )";
		if (connection.getDebug() == "debug") System.out.println("\ngetInstancesWithObjectValue: query=\n" + q);
		instances = SPARQLDoer.executeRdfSelect(connection, q);
		return instances;
    }  // End getInstancesWithObjectValue

    /**
     * Get subjects given a graph, predicate, and object.
     * 
     * @param graph
     * @param predicate
     * @param object
     * @return subjects
     * @throws SQLException
     */
    public List<String> getSubjects(String graph, String predicate, String object) throws SQLException{
		List<String> subjects = new ArrayList<String>();
		String q =
			"select distinct sub from table(sem_match(\n'select * where {\n" +
			"\tGRAPH <" + graph + "> {?sub " + predicate + " " + object + "}\n" +
			"}',\n" +
			"SEM_MODELS('" 
			+ connection.getModel() +"'), null,\n" +
			"SEM_ALIASES( SEM_ALIAS('', '" 
			+ connection.getNamespace() + "')), null) )";
		if (connection.getDebug() == "debug") System.out.println("\ngetSubjects: query=\n" + q);
			try {
				subjects = SPARQLDoer.executeRdfSelect(connection, q);
			} catch (SQLException ex) {
				System.out.println(ex);
			}
		return subjects;
    }  // End getInstancesWithObjectValue
}