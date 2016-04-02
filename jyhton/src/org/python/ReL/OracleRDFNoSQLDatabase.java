package org.python.ReL;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import oracle.rdf.kv.client.jena.DatasetGraphNoSql;
import oracle.rdf.kv.client.jena.OracleGraphNoSql;
import oracle.rdf.kv.client.jena.OracleNoSqlConnection;
import org.python.core.*;
import wdb.metadata.*;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that is used to interface with the oracle database.
 * All statements and queries should go through here to communicate with oracle.
 */

public class OracleRDFNoSQLDatabase extends DatabaseInterface {

    private OracleNoSqlConnection connection;
    DatasetGraphNoSql datasetGraph = null;
    private ResultSet rs = null;
    private String debug;
    private String nameSpace = "carnot:";
    private String nameSpacePrefix = "c";

    public OracleRDFNoSQLDatabase(String url, String uname, String passw, String conn_type, String debug)
    {
        super();

        connection = null;
        this.debug = debug;

        if (conn_type != "none") {
            //this.connection = OracleNoSqlConnection.createInstance("kvstore", "localhost", "5000");
            String[] s = passw.split(":");
            this.connection = OracleNoSqlConnection.createInstance(uname, s[0].trim(), s[1].trim());
            OracleGraphNoSql graph = new OracleGraphNoSql(connection);
            this.datasetGraph = DatasetGraphNoSql.createFrom(graph);

            // Close graph, as it is no longer needed
            graph.close();

            // Clear dataset
            if (debug.equals("debug")) {
                System.out.println("\nWARNING: the NoSQL database is being cleared in PyRelConnection!\n");
                datasetGraph.clearRepository();
            }
        }
    }

    public String getNameSpace()
    {
        return nameSpace;
    }

    public String getNameSpacePrefix()
    {
        return nameSpacePrefix;
    }

    @Override
    public void OracleNoSQLAddQuad(String graph, String subject, String predicate, String object, Boolean object_as_uri)
    {
        if (!graph.contains("http://")) graph = nameSpace + graph;
        if (!subject.contains("http://")) subject = nameSpace + subject;
        if (!predicate.contains("http://")) predicate = nameSpace + predicate;
        if (!object.contains("http://")) object = nameSpace + object;
        if (debug.equals("debug"))
            System.out.println("In addQuad, stmt is: " + graph + ", " + subject + ", " + predicate + ", " + object);
        if (object_as_uri)
            datasetGraph.add(Node.createURI(graph), Node.createURI(subject), Node.createURI(predicate), Node.createURI(object));
        else {
            if (object.contains("http://"))
                datasetGraph.add(Node.createURI(graph), Node.createURI(subject), Node.createURI(predicate), Node.createURI(object));
            else
                datasetGraph.add(Node.createURI(graph), Node.createURI(subject), Node.createURI(predicate), Node.createLiteral(object
                        .replaceAll(nameSpace, "")));
        }
    }

    @Override
    public ArrayList<PyObject> OracleNoSQLRunSPARQL(String sparql)
    {
        Dataset ds = DatasetImpl.wrap(datasetGraph);

        sparql = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                "PREFIX c: <carnot:> " +
                sparql;

        Query query = QueryFactory.create(sparql);
        QueryExecution qexec = QueryExecutionFactory.create(query, ds);
        ArrayList<String> attrs = new ArrayList<String>();
        ArrayList<PyObject> rows = new ArrayList<PyObject>();
        ArrayList<PyObject> items = new ArrayList<PyObject>();
        PyObject[] temp;

        try {
            com.hp.hpl.jena.query.ResultSet queryResults = qexec.execSelect();
            if (queryResults != null) {
                String xmlstr = "Initial string";
                xmlstr = ResultSetFormatter.asXMLString(queryResults); // For documentation, see http://grepcode.com/file/repo1.maven.org/maven2/com.hp.hpl.
                if (debug.equals("debug")) System.out.println("xmlstr is: " + xmlstr);
                Matcher m = Pattern.compile("variable name=.*").matcher(xmlstr);
                while (m.find()) {
                    String item = "";
                    if (debug.equals("debug")) item = m.group().replaceAll("variable name=.", "").replaceAll("./>", "");
                    else item = m.group()
                            .replaceAll("variable name=.", "")
                            .replaceAll("./>", "")
                            .replaceAll(nameSpace, "");
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
                    for (int i = 0; i <= attrs.size(); i++) {
                        if (m.group().contains("<uri>")) {
                            if (attrName.equals(attrs.get(num))) {
                                String item = "";
                                if (debug.equals("debug"))
                                    item = m.group().replaceAll("<uri>:?", "").replaceAll("</uri>", "");
                                else item = m.group()
                                        .replaceAll("<uri>:?", "")
                                        .replaceAll("</uri>", "")
                                        .replaceAll(nameSpace, "");

                                try {
                                    Double.parseDouble(item);
                                    try {
                                        Integer.parseInt(item);
                                        items.add(new PyInteger(Integer.parseInt(item)));
                                    } catch (NumberFormatException e) {
                                        items.add(new PyFloat(Float.parseFloat(item)));
                                    }
                                } catch (NumberFormatException e) {
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
                        else if (m.group().contains("<literal datatype=")) {
                            if (attrName.equals(attrs.get(num))) {
                                String item = "";
                                if (debug.equals("debug"))
                                    item = m.group().replaceAll("<literal datatype=", "").replaceAll("</literal>", "");
                                else item = m.group()
                                        .replaceAll("<literal datatype=", "")
                                        .replaceAll("</literal>", "")
                                        .replaceAll(nameSpace, "")
                                        .replaceAll("\"http://www.w3.org/2001/XMLSchema#.*\">", "");
                                // Literals:
                                // For numberic data types, see http://www.w3schools.com/xml/schema_dtypes_numeric.asp
                                // For string data types, see http://www.w3schools.com/xml/schema_dtypes_string.asp
                                // For date data types, see http://www.w3schools.com/xml/schema_dtypes_date.asp
                                // For misc. data types, see http://www.w3schools.com/xml/schema_dtypes_misc.asp

                                try {
                                    Double.parseDouble(item);
                                    try {
                                        Integer.parseInt(item);
                                        items.add(new PyInteger(Integer.parseInt(item)));
                                    } catch (NumberFormatException e) {
                                        items.add(new PyFloat(Float.parseFloat(item)));
                                    }
                                } catch (NumberFormatException e) {
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
                        else if (m.group().contains("</result>")) {
                            temp = listtoarray(items);
                            rows.add(new PyTuple(temp));
                            items = new ArrayList<PyObject>();
                            num = 0;
                            break;
                        }
                        else if (m.group().contains("<binding name=")) {
                            if (debug.equals("debug"))
                                attrName = m.group().replaceAll("<binding name=.", "").replaceAll(".>", "");
                            else attrName = m.group()
                                    .replaceAll("<binding name=.", "")
                                    .replaceAll(".>", "")
                                    .replaceAll(nameSpace, "");
                        }
                    }
                }
            }
        } finally {
            ds.close();
            qexec.close();
        }
        return (rows);
    }

    //helper to convert lists to arrays

    private PyObject[] listtoarray(ArrayList<PyObject> a)
    {
        PyObject[] results = new PyObject[a.size()];
        int iter = 0;
        for (PyObject pt : a) {
            results[iter] = pt;
            iter++;
        }
        return results;
    }


    private class OracleRDFNoSQLAdapter implements Adapter {
        private OracleRDFNoSQLDatabase db;

        private OracleRDFNoSQLAdapter(OracleRDFNoSQLDatabase db)
        {
            this.db = db;
        }

        @Override
        public void putClass(ClassDef classDef)
        {

        }

        @Override
        public ClassDef getClass(wdb.metadata.Query query) throws ClassNotFoundException
        {
            throw new ClassNotFoundException();
        }

        @Override
        public ClassDef getClass(String s) throws ClassNotFoundException
        {
            throw new ClassNotFoundException();
        }

        @Override
        public void putObject(WDBObject wdbObject)
        {
            final String instanceID = String.valueOf(UUID.randomUUID());
            ArrayList<String> dvaNames = wdbObject.getDvaNames();
            ArrayList<String> dvaVals = wdbObject.getDvaValues();
            String className = wdbObject.getClassName();
            for (int i = 0; i < dvaNames.size(); i++) {
                OracleNoSQLAddQuad("SCHEMA", className, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2000/01/rdf-schema#Class", true);
                OracleNoSQLAddQuad(className + "_" + "SCHEMA", dvaNames.get(i), "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#DatatypeProperty", true);
                OracleNoSQLAddQuad(className + "_" + "SCHEMA", dvaNames.get(i), "http://www.w3.org/2000/01/rdf-schema#domain", className, true);
                OracleNoSQLAddQuad(className + "_" + "SCHEMA", dvaNames.get(i), "http://www.w3.org/2000/01/rdf-schema#range", "http://www.w3.org/2001/XMLSchema#string", true);
                OracleNoSQLAddQuad(className + "_" + "SCHEMA", instanceID, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", className, true);
                OracleNoSQLAddQuad(className, instanceID, dvaNames.get(i), dvaVals.get(i), false);
            }
        }

        @Override
        public WDBObject getObject(String s, Integer integer)
        {
            return null;
        }

        @Override
        public ArrayList<WDBObject> getObjects(IndexDef indexDef, String s)
        {
            return null;
        }

        @Override
        public void commit()
        {

        }

        @Override
        public void abort()
        {

        }
    }
}
