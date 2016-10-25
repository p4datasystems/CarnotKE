package org.python.ReL;


import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import oracle.rdf.kv.client.jena.DatasetGraphNoSql;
import oracle.rdf.kv.client.jena.OracleGraphNoSql;
import oracle.rdf.kv.client.jena.OracleNoSqlConnection;
import org.python.ReL.WDB.database.wdb.metadata.*;
import org.python.core.*;

import java.sql.ResultSet;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.python.ReL.OracleNoSQLDatabase.DBG;
import static org.python.ReL.ProcessLanguages.debugMsg;

/**
 * A class that is used to interface with the oracle database.
 * All statements and queries should go through here to communicate with oracle.
 */

public class OracleRDFNoSQLDatabase extends DatabaseInterface implements ParserAdapter, NonDefaultParser{

    private OracleNoSqlConnection connection;
    DatasetGraphNoSql datasetGraph = null;
    private ResultSet rs = null;
    private String debug;
    private String nameSpace = "carnot:";
    private String nameSpacePrefix = "c";

    public OracleRDFNoSQLDatabase(PyRelConnection pyRelConnection, String url, String uname, String passw, String conn_type, String debug)
    {
        super(pyRelConnection);

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

    public void addTypedPyobject(ArrayList<PyObject> items, String item) {
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
    }

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

                                this.addTypedPyobject(items, item);
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

                                this.addTypedPyobject(items, item);
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

    public List<String> getSubjects(String graph, String predicate, String object) {
        ArrayList<PyObject> rows = new ArrayList<PyObject>();
        List<String> subjects = new ArrayList<String>();
        String sparql = "SELECT ?s WHERE { GRAPH " + "c:" + graph + " { ?s " + predicate + " " + object + " } } ";
        if (DBG) System.out.println("\ngetSujects, sparql is: \n" + sparql);
        rows = this.OracleNoSQLRunSPARQL(sparql);
        for (int i = 1; i < rows.size(); i++) {
            subjects.add(String.format("%s", rows.get(i))
                    .replaceAll("[()]", "")
                    .replaceAll("'", "")
                    .replaceAll(",", "")
                    .replaceAll(this.getNameSpace(), ""));
        }
        return subjects;
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

    @Override
    public void putClass(org.python.ReL.WDB.database.wdb.metadata.Query classDefQuery) {
//        SIMHelper simhelper = new SIMHelper(this.pyRelConn);
//        simhelper.executeInstance(classDefQuery.getQueryName(), classDefQuery.getQueryName());
//        final String instanceID = String.valueOf(UUID.randomUUID());
//        final byte[] data = SerializationUtils.serialize((ClassDef)classDefQuery);
//        final String dataStr = Arrays.toString(data);
//        try {
//            SQLVisitor.insertQuad(this.pyRelConn, classDefQuery.getQueryName(), dataStr, classDefQuery.getQueryName(), Integer.toString(((ClassDef)classDefQuery).getUid()), false);
//        }
//        catch (SQLException e) {
//            // Ignore for now :)
//        }
    }

    @Override
    public ClassDef getClass(org.python.ReL.WDB.database.wdb.metadata.Query query) throws ClassNotFoundException {
        // Predicate: carnot:QUERY_NAME
        // Object: query.getUid() as a String
//        String predicate = "carnot:" + query.getQueryName();
//        String object = Integer.toString(((ClassDef)query).getUid());
//        String sparqlQuery = "select ?x where { ?x " + predicate + " " + object + " }";
//        try {
//            ArrayList<PyObject> stuff = this.OracleNoSQLRunSPARQL(sparqlQuery);
//        }
//        catch (QueryParseException e) {
//            throw new ClassNotFoundException();
//        }
        throw new ClassNotFoundException();
    }

    @Override
    public ClassDef getClass(String className) throws ClassNotFoundException {
        throw new ClassNotFoundException();
    }

    @Override
    public void putObject(WDBObject wdbObject) {
//        final byte[] data = SerializationUtils.serialize(wdbObject);
//        final String dataStr = Arrays.toString(data);
//        try {
//            SQLVisitor.insertQuad(this.pyRelConn, wdbObject.getClassName(), wdbObject.getUid().toString(), wdbObject.getClassName(),
//                    dataStr, false);
//        }
//        catch (SQLException e) {
//            // Ignore for now :)
//        }
    }

    @Override
    public WDBObject getObject(String className, Integer Uid) {
        return null;
    }

    @Override
    public ArrayList<WDBObject> getObjects(org.python.ReL.WDB.database.wdb.metadata.Query indexDefQuery, String key) {
        return null;
    }

    @Override
    public void commit() {

    }

    @Override
    public void abort() {

    }

    // NonDefaultParser implementation
    @Override
    public void insert(org.python.ReL.WDB.database.wdb.metadata.Query insertQuery) {
        InsertQuery iq = (InsertQuery) insertQuery;
        final String instanceID = String.valueOf(UUID.randomUUID());
        String graph, subject, schemaString, predicate, object;
        for (int i = 0; i < iq.numberOfAssignments(); i++) {
            DvaAssignment dvaAssignment = (DvaAssignment) iq.getAssignment(i);
            schemaString = pyRelConn.getSchemaString();
            graph = iq.className;
            subject = instanceID;
            predicate = dvaAssignment.getAttributeName();
            object = dvaAssignment.Value.toString();
            this.OracleNoSQLAddQuad(schemaString, graph, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2000/01/rdf-schema#Class", true);
            // Unimplemented as of now
            // if(eva)
            this.OracleNoSQLAddQuad(graph + "_" + schemaString, predicate, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#DatatypeProperty", true);
            this.OracleNoSQLAddQuad(graph + "_" + schemaString, predicate, "http://www.w3.org/2000/01/rdf-schema#domain", graph, true);
            this.OracleNoSQLAddQuad(graph + "_" + schemaString, predicate, "http://www.w3.org/2000/01/rdf-schema#range", "http://www.w3.org/2001/XMLSchema#string", true);
            this.OracleNoSQLAddQuad(graph + "_" + schemaString, subject, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", graph, true);
            this.OracleNoSQLAddQuad(graph, subject, predicate, object, true);
            this.OracleNoSQLAddQuad(iq.className, instanceID, dvaAssignment.getAttributeName(),
                    dvaAssignment.Value.toString(), false);
        }
    }

    @Override
    public void modify(org.python.ReL.WDB.database.wdb.metadata.Query modifyQuery) {
        ModifyQuery mq = (ModifyQuery) modifyQuery;
        String className = mq.className;
        String eva_name = "";
        String eva_class = "";
        int limit = 1000000;
        ArrayList<PyObject> rows = new ArrayList<>();
        List<String> subjects = new ArrayList<>();
        List<String> eva_subjects = new ArrayList<>();

        String sparql = "select ?indiv where { ";
        String where = "";
        for (int i = 0; i < mq.assignmentList.size(); i++) {
            EvaAssignment evaAssignment = (EvaAssignment) mq.assignmentList.get(i);
            eva_name = evaAssignment.getAttributeName();
            eva_class = evaAssignment.targetClass;
            where = traverseWhereInorder(where, evaAssignment.expression.jjtGetChild(i)); // This sets the where variable to e.g., deptno = 20
            String where1 = where.trim();
            sparql += "GRAPH " + this.getNameSpacePrefix() + ":" + eva_class + " { ?indiv " + getNameSpacePrefix() + ":"
                    + where1.replaceAll(" *= *", " \"") + "\"^^xsd:string }";
        }
        sparql += " }";
        debugMsg(DBG, "\nProcessLanguages SIM Modify, sparql is: \n" + sparql + "\n");
        rows = this.OracleNoSQLRunSPARQL(sparql);
        for (int i = 1; i < rows.size(); i++) {
            eva_subjects.add(String.format("%s", rows.get(i))
                    .replaceAll("[()]", "")
                    .replaceAll("'", "")
                    .replaceAll(",", "")
                    .replaceAll(this.getNameSpace(), ""));
        }

        // Process WHERE clause
        if (mq.expression != null) {
            where = "";
            where = traverseWhereInorder(where, mq.expression);
            where = where.replaceAll("  *", " ").replaceAll("^ ", "").replaceAll(" $", "");
        }
        sparql = "select ?indiv where { GRAPH " + this.getNameSpacePrefix() + ":" + className + " { ?indiv "
                + this.getNameSpacePrefix() + ":" + where.replaceAll(" *= *", " \"") + "\"^^xsd:string } }";
        debugMsg(DBG, "\nProcessLanguages SIM Modify, sparql is: \n" + sparql + "\n");
        rows = this.OracleNoSQLRunSPARQL(sparql);
        for (int i = 1; i < rows.size(); i++) {
            subjects.add(String.format("%s", rows.get(i))
                    .replaceAll("[()]", "")
                    .replaceAll("'", "")
                    .replaceAll(",", "")
                    .replaceAll(this.getNameSpace(), ""));
        }
        for (String subject : subjects) {
            for (String entity : eva_subjects)
                this.OracleNoSQLAddQuad(className, subject, eva_name, entity, true);
        }
    }

    @Override
    public ArrayList<PyObject> retrieve(org.python.ReL.WDB.database.wdb.metadata.Query retrieveQuery) {
        String where = "";
        RetrieveQuery rq = (RetrieveQuery) retrieveQuery;
        String className = rq.className;
        List<String> dvaAttribs = new ArrayList<String>();
        List<String> evaAttribs = new ArrayList<String>();
        Map<String, String> whereAttrValues = new HashMap<String, String>();
        List<String> columns;
        for (int j = 0; j < rq.numAttributePaths(); j++) {
            if (rq.getAttributePath(j).attribute == "*") {
                columns = this.getSubjects(className + "_" + this.pyRelConn.getSchemaString(), "rdf:type", "owl:DatatypeProperty");
                if (rq.getAttributePath(j).levelsOfIndirection() == 0) {
                    for (int i = 0; i < columns.size(); i++)
                        dvaAttribs.add(columns.get(i));
                }
            }
            else if (rq.getAttributePath(j).levelsOfIndirection() == 0)
                dvaAttribs.add(rq.getAttributePath(j).attribute);
            else {
                String evaPath = "";
                for (int k = rq.getAttributePath(j).levelsOfIndirection() - 1; k >= 0; k--) {
                    debugMsg(DBG, "rq.getAttributePath(j).getIndirection(k): " + rq.getAttributePath(j).getIndirection(k));
                    if (k > 0)
                        evaPath = " OF " + rq.getAttributePath(j).getIndirection(k) + evaPath;
                    else
                        evaPath = rq.getAttributePath(j).getIndirection(k) + evaPath;
                }
                evaPath = rq.getAttributePath(j).attribute + " OF " + evaPath;
                evaAttribs.add(evaPath);
            }
        }
        if (DBG) {
            System.out.println("className: " + className);
            System.out.println("dvaAttribs: " + dvaAttribs);
            System.out.println("evaAttribs: " + evaAttribs);
        }
        if (rq.expression != null) {
            where = traverseWhereInorder(where, rq.expression);
            where = where.replaceAll("= ", "= :").replaceAll("And", "&&").replaceAll("Or", "||");
        }
        debugMsg(DBG, "where: "+where);
        // The following is temporary until filter is used for the where clause
        String whereTmp = "";
        if (where != "") {
            whereTmp = where.replaceAll(" = :", " ")
                    .replaceAll("&&", " ")
                    .replaceAll("\\|\\|", " ")
                    .replaceAll("  *", " ")
                    .replaceAll("^  *", "")
                    .replaceAll("  *$", ""); //temporary
            debugMsg(DBG, whereTmp);
            String[] whereTmpArray = whereTmp.split(" "); //temporary
            for (int i = 0; i <= whereTmpArray.length - 1; i += 2) //temporary
                whereAttrValues.put(whereTmpArray[i], whereTmpArray[i + 1]);
        }
        debugMsg(DBG, "whereAttrValues: "+whereAttrValues);
        SIMHelper simhelper = new SIMHelper(this.pyRelConn);
        try {
            String sparql = simhelper.executeFrom(className, dvaAttribs, evaAttribs, whereAttrValues);
            return this.OracleNoSQLRunSPARQL(sparql);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    private String traverseWhereInorder(String where, org.python.ReL.WDB.parser.generated.wdb.parser.Node node)
    {
        if (node != null) {
            if (node.jjtGetNumChildren() > 0)
                where += traverseWhereInorder(where, node.jjtGetChild(0));
            if (node.toString() != "Root")
                where += " " + node.toString();
            if (node.jjtGetNumChildren() > 1)
                where += traverseWhereInorder(where, node.jjtGetChild(1));
        }
        return where;
    }
}
