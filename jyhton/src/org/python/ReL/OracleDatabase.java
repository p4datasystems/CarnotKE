package org.python.ReL;


import org.python.ReL.WDB.database.wdb.metadata.*;
import org.python.core.PyObject;

import java.sql.*;
import java.util.*;

import static org.python.ReL.OracleNoSQLDatabase.DBG;
import static org.python.ReL.ProcessLanguages.debugMsg;

/**
 * A class that is used to interface with the oracle database.
 * All statements and queries should go through here to communicate with oracle.
 */

public class OracleDatabase extends DatabaseInterface implements ParserAdapter, NonDefaultParser {

    private Connection connection;
    private Statement callableStatement;
    private Statement createStatement;
    private ResultSet rs;
    private String debug;

    public OracleDatabase(PyRelConnection pyRelConnection, String url, String uname, String passw, String conn_type, String debug)
    {
        super(pyRelConnection);
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = null;
        callableStatement = null;
        createStatement = null;
        this.debug = debug;
        if (conn_type != "none") {
            try {
                // Connect to the database
                connection = DriverManager.getConnection(url, uname, passw);
            } catch (SQLException e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void executeStatement(String stmt)
    {
        try {
            if (callableStatement != null) {
                callableStatement.close();
            }
            if (debug == "debug") {
                System.out.println("exec -> " + stmt);
            }
            callableStatement = connection.createStatement();
            callableStatement.execute(stmt);
            callableStatement.close();

        } catch (java.sql.SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        // System.out.println(stmt);
    }

    @Override
    public ResultSet executeQuery(String query)
    {
        try {
            if (createStatement != null) {
                createStatement.close();
            }
            if (rs != null) {
                rs.close();
            }
            createStatement = connection.createStatement();
            rs = createStatement.executeQuery(query);
        } catch (java.sql.SQLException e) {
            System.out.println("exception: " + e.getMessage());
        }
        return rs;
    }

    public void insertQuad(String graph, String subject, String predicate, String object, Boolean eva) {
        String graphName = pyRelConn.getModel() + ":<" + graph + ">";
        String graphName2 = pyRelConn.getModel() + ":<" + graph + "_" + pyRelConn.getSchemaString() + ">";

        if (!subject.contains(":")) {
            // no specified pyRelConn.getNamespace():  use current default pyRelConn.getNamespace()
            subject = pyRelConn.getNamespace() + subject;
        }
        if (!predicate.contains(":")) {
            // no specified pyRelConn.getNamespace():  use current default pyRelConn.getNamespace()
            predicate = pyRelConn.getNamespace() + predicate;
        }
        if (!object.contains(":")) {
            // no specified pyRelConn.getNamespace():  use current default pyRelConn.getNamespace()
            object = pyRelConn.getNamespace() + object;
        }
        String typeString = "";
        String s = "";
        try {
            if (!graph.equals("")) {
                s = "INSERT INTO " + pyRelConn.getTable() + " VALUES ( " + pyRelConn.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName +
                        "', '" + subject.replaceAll("'", "") + "', '" + predicate.replaceAll("'", "") + "', '" + object.replaceAll("'", "") + typeString + "'))";
                pyRelConn.executeStatement(s);
                s = "INSERT INTO " + pyRelConn.getTable() + " VALUES ( " + pyRelConn.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
                        "', '" + subject.replaceAll("'", "") + "', 'rdf:type', '" + pyRelConn.getNamespace() + graph + "'))";
                pyRelConn.executeStatement(s);
                if (eva)
                    s = "INSERT INTO " + pyRelConn.getTable() + " VALUES ( " + pyRelConn.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
                            "', '" + predicate.replaceAll("'", "") + "', 'rdf:type', 'owl:FunctionalProperty'))";
                else
                    s = "INSERT INTO " + pyRelConn.getTable() + " VALUES ( " + pyRelConn.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
                            "', '" + predicate.replaceAll("'", "") + "', 'rdf:type', 'owl:DatatypeProperty'))";
                pyRelConn.executeStatement(s);
                s = "INSERT INTO " + pyRelConn.getTable() + " VALUES ( " + pyRelConn.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
                        "', '" + predicate.replaceAll("'", "") + "', 'rdfs:domain', '" + pyRelConn.getNamespace() + graph + "'))";
                pyRelConn.executeStatement(s);
                s = "INSERT INTO " + pyRelConn.getTable() + " VALUES ( " + pyRelConn.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
                        "', '" + predicate.replaceAll("'", "") + "', 'rdfs:range', 'xsd:string'))";
                pyRelConn.executeStatement(s);
            }
            else {
                s = "INSERT INTO " + pyRelConn.getTable() + " VALUES ( " + pyRelConn.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
                        "', '" + subject.replaceAll("'", "") + "', '" + predicate.replaceAll("'", "") + "', '" + object.replaceAll("'", "") + typeString + "'))";
                pyRelConn.executeStatement(s);
            }
        }
        catch (SQLException e) {
            // Ignore for now :)
        }
    }

    public List<String> getSubjects(String graph, String predicate, String object) {
        List<String> subjects = new ArrayList<>();
        String q =
                "select distinct sub from table(sem_match(\n'select * where {\n" +
                        "\tGRAPH <" + graph + "> {?sub " + predicate + " " + object + "}\n" +
                        "}',\n" +
                        "SEM_MODELS('"
                        + pyRelConn.getModel() + "'), null,\n" +
                        "SEM_ALIASES( SEM_ALIAS('', '"
                        + pyRelConn.getNamespace() + "')), null) )";
        if (DBG) System.out.println("\ngetSubjects: query=\n" + q);
        try {
            subjects = SPARQLDoer.executeRdfSelect(pyRelConn, q);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return subjects;
    }



    @Override
    public void putClass(Query classDefQuery) {

    }

    @Override
    public ClassDef getClass(Query query) throws ClassNotFoundException {
        return null;
    }

    @Override
    public ClassDef getClass(String className) throws ClassNotFoundException {
        return null;
    }

    @Override
    public void putObject(WDBObject wdbObject) {

    }

    @Override
    public WDBObject getObject(String className, Integer Uid) {
        return null;
    }

    @Override
    public ArrayList<WDBObject> getObjects(Query indexDefQuery, String key) {
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
        for (int i = 0; i < iq.numberOfAssignments(); i++) {
            DvaAssignment dvaAssignment = (DvaAssignment) iq.getAssignment(i);
            this.insertQuad(iq.className, instanceID, dvaAssignment.getAttributeName(),
                    dvaAssignment.Value.toString(), false);
        }
    }

    @Override
    public void modify(org.python.ReL.WDB.database.wdb.metadata.Query modifyQuery) {
        // TODO(jhurt): I am unsure about this class using OracleNoSQLRunSPARQL method. Is it supposed to?
        // If so, then simply take the implementation from OracleRDFNoSQLDatabase and put it here as well.
//        ModifyQuery mq = (ModifyQuery) modifyQuery;
//        String className = mq.className;
//        String eva_name = "";
//        String eva_class = "";
//        int limit = 1000000;
//        ArrayList<PyObject> rows = new ArrayList<>();
//        List<String> subjects = new ArrayList<>();
//        List<String> eva_subjects = new ArrayList<>();
//
//        String sparql = "select ?indiv where { ";
//        String where = "";
//        for (int i = 0; i < mq.assignmentList.size(); i++) {
//            EvaAssignment evaAssignment = (EvaAssignment) mq.assignmentList.get(i);
//            eva_name = evaAssignment.getAttributeName();
//            eva_class = evaAssignment.targetClass;
//            where = traverseWhereInorder(where, evaAssignment.expression.jjtGetChild(i)); // This sets the where variable to e.g., deptno = 20
//            String where1 = where.trim();
//            sparql += "GRAPH " + this.getNameSpacePrefix() + ":" + eva_class + " { ?indiv " + getNameSpacePrefix() + ":"
//                    + where1.replaceAll(" *= *", " \"") + "\"^^xsd:string }";
//        }
//        sparql += " }";
//        debugMsg(DBG, "\nProcessLanguages SIM Modify, sparql is: \n" + sparql + "\n");
//        rows = this.OracleNoSQLRunSPARQL(sparql);
//        for (int i = 1; i < rows.size(); i++) {
//            eva_subjects.add(String.format("%s", rows.get(i))
//                    .replaceAll("[()]", "")
//                    .replaceAll("'", "")
//                    .replaceAll(",", "")
//                    .replaceAll(this.getNameSpace(), ""));
//        }
//
//        // Process WHERE clause
//        if (mq.expression != null) {
//            where = "";
//            where = traverseWhereInorder(where, mq.expression);
//            where = where.replaceAll("  *", " ").replaceAll("^ ", "").replaceAll(" $", "");
//        }
//        sparql = "select ?indiv where { GRAPH " + this.getNameSpacePrefix() + ":" + className + " { ?indiv "
//                + this.getNameSpacePrefix() + ":" + where.replaceAll(" *= *", " \"") + "\"^^xsd:string } }";
//        debugMsg(DBG, "\nProcessLanguages SIM Modify, sparql is: \n" + sparql + "\n");
//        rows = this.OracleNoSQLRunSPARQL(sparql);
//        for (int i = 1; i < rows.size(); i++) {
//            subjects.add(String.format("%s", rows.get(i))
//                    .replaceAll("[()]", "")
//                    .replaceAll("'", "")
//                    .replaceAll(",", "")
//                    .replaceAll(this.getNameSpace(), ""));
//        }
//        for (String subject : subjects) {
//            for (String entity : eva_subjects)
//                this.OracleNoSQLAddQuad(className, subject, eva_name, entity, true);
//        }
//        sparql = null;
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
            // TODO(jhurt): Figure out how to pass in the necessary information to ProcessOracleEESQL elegantly
//            ProcessOracleEESQL eesql = new ProcessOracleEESQL(this.pyRelConn, this.pyRelConn.);
//            return eesql.processSQL(sparql);
            return null;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public String traverseWhereInorder(String where, org.python.ReL.WDB.parser.generated.wdb.parser.Node node)
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
