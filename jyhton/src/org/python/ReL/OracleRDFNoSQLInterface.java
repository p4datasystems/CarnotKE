package org.python.ReL;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.lang.reflect.Array;

import org.python.core.*;
import org.python.antlr.base.expr;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import oracle.rdf.kv.client.jena.*;
import com.hp.hpl.jena.sparql.core.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that is used to interface with the oracle database.
 * All statements and queries should go through here to communicate with oracle.
 */
 
public class OracleRDFNoSQLInterface extends DatabaseInterface {

    private OracleNoSqlConnection connection;
    DatasetGraphNoSql datasetGraph = null;
    private ResultSet rs = null;
    private String debug;
     
    public OracleRDFNoSQLInterface(String url, String uname, String passw, String conn_type, String debug) {
        super();

        connection = null;

        this.debug = debug;

        if(conn_type != "none" ) {
            this.connection = OracleNoSqlConnection.createInstance("kvstore", "Phils-MacBook-Pro.local", "5000");
            OracleGraphNoSql graph = new OracleGraphNoSql(connection);
            this.datasetGraph = DatasetGraphNoSql.createFrom(graph);

            // Close graph, as it is no longer needed
            graph.close(); 
            
            // Clear dataset
            System.out.println("\nWRNING: the NoSQL database is being cleared in PyRelConnection!\n");
            datasetGraph.clearRepository();
        }
    }

    public OracleNoSqlConnection getConnection() { return connection; }

    @Override
    public void OracleNoSQLAddQuad(String graph, String subject, String predicate, String object)
    {
        System.out.println("In addQuad, stmt is: " + graph + ", " + subject + ", " + predicate + ", " + object);

        /*

           xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
           xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
           xmlns:owl="http://www.w3.org/2002/07/owl#"
           xmlns:foaf="http://xmlns.com/foaf/0.1/"
        */
        if(graph.contains("rdf:") || graph.contains("http://"))         {} else graph     = "#" + graph;
        if(subject.contains("rdf:") || subject.contains("http://"))     {} else subject   = "#" + subject;
        if(predicate.contains("rdf:") || predicate.contains("http://")) {} else predicate = "#" + predicate;
        if(object.contains("rdf:") || object.contains("http://"))       {} else object    = "#" + object;

        datasetGraph.add(Node.createURI(graph), Node.createURI(subject), Node.createURI(predicate), Node.createURI(object));
    }

    @Override
    public ArrayList<PyObject> OracleNoSQLRunSPARQL(String sparql, Boolean debug)
    {
        Dataset ds = DatasetImpl.wrap(datasetGraph);

        Query query = QueryFactory.create(sparql);
        QueryExecution qexec = QueryExecutionFactory.create(query, ds);
        ArrayList<String> attrs = new ArrayList<String>();
        ArrayList<PyObject> rows = new ArrayList<PyObject>();
        ArrayList<PyObject> items = new ArrayList<PyObject>();
        PyObject[] temp;

        try {
            com.hp.hpl.jena.query.ResultSet queryResults = qexec.execSelect();
            if(queryResults != null) {
                  String xmlstr = "Initial string";
                  xmlstr = ResultSetFormatter.asXMLString(queryResults); // For documentation, see http://grepcode.com/file/repo1.maven.org/maven2/com.hp.hpl.  
                  if(debug) System.out.println("xmlstr is: " + xmlstr);
                  Matcher m = Pattern.compile("variable name=.*").matcher(xmlstr);
                  while (m.find()) {
                    String item = m.group().replaceAll("variable name=.", "").replaceAll("./>", "");
                    attrs.add(item);
                    items.add(new PyString(item)); 
                  }
                  temp = listtoarray(items);
                  rows.add(new PyTuple(temp)); 
                  items = new ArrayList<PyObject>();

                  m = Pattern.compile("<binding name=.*|<uri>:?.*|<literal datatype=.*|</result>").matcher(xmlstr);
                  String attrName = "";
                  int num = 0;
                  while (m.find()) {
                      for(int i = 0; i <= attrs.size(); i++) {
                          if(m.group().contains("<uri>")) {
                              if(attrName.equals(attrs.get(num))) {
                                  String item = m.group().replaceAll("<uri>:?", "").replaceAll("</uri>", ""); 

                                  try  { 
                                      Double.parseDouble(item); 
                                      try { 
                                        Integer.parseInt(item);
                                        items.add(new PyInteger(Integer.parseInt(item))); 
                                      } catch(NumberFormatException e) { 
                                           items.add(new PyFloat(Float.parseFloat(item))); 
                                      }
                                  }  
                                      catch(NumberFormatException e)  
                                  {   
                                      items.add(new PyString(item));  
                                  }
                                  num++;
                                  break;
                              }
                              else {
                                  items.add(new PyString("null"));
                                  num++;
                              }
                          }
                          else if(m.group().contains("<literal datatype=")) {
                              if(attrName.equals(attrs.get(num))) {
                                  String item = m.group().replaceAll("<literal datatype=", "").replaceAll("</literal>", "");  
                                  // Literals:                                 
                                  // For numberic data types, see http://www.w3schools.com/xml/schema_dtypes_numeric.asp
                                  // For string data types, see http://www.w3schools.com/xml/schema_dtypes_string.asp
                                  // For date data types, see http://www.w3schools.com/xml/schema_dtypes_date.asp
                                  // For misc. data types, see http://www.w3schools.com/xml/schema_dtypes_misc.asp

                                  items.add(new PyString(item));  
                                  num++;
                                  break;
                              }
                              else {
                                  items.add(new PyString("null"));
                                  num++;
                              }
                          }
                          else if(m.group().contains("</result>")) {
                              temp = listtoarray(items);
                              rows.add(new PyTuple(temp));
                              items = new ArrayList<PyObject>();
                              num = 0;
                              break;
                          }
                          else if(m.group().contains("<binding name=")) {
                              attrName = m.group().replaceAll("<binding name=.", "").replaceAll(".>", "");
                          }
                      }
                  }
            }
        }
        finally {
            ds.close();
          qexec.close();
        }
        return(rows);
    }

    //helper to convert lists to arrays

    private PyObject[] listtoarray(ArrayList<PyObject> a) {
        PyObject[] results = new PyObject[a.size()];
        int iter = 0;
        for (PyObject pt : a) {
            results[iter] = pt;
            iter++;
        }
        return results;
    }
}
