package org.python.ReL;

import wdb.metadata.*;

import java.sql.*;
import java.util.ArrayList;

/**
 * A class that is used to interface with the oracle database.
 * All statements and queries should go through here to communicate with oracle.
 */

public class OracleDatabase extends DatabaseInterface {

    private Connection connection;
    private Statement callableStatement;
    private Statement createStatement;
    private ResultSet rs;
    private String debug;

    public OracleDatabase(String url, String uname, String passw, String conn_type, String debug)
    {
        super();
        this.adapter = new OracleDatabaseAdapter(this);
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

    private class OracleDatabaseAdapter implements Adapter {
        private OracleDatabase db;

        private OracleDatabaseAdapter(OracleDatabase db)
        {
            this.db = db;
        }


        @Override
        public void putClass(ClassDef classDef)
        {

        }

        @Override
        public ClassDef getClass(Query query) throws ClassNotFoundException
        {
            return null;
        }

        @Override
        public ClassDef getClass(String s) throws ClassNotFoundException
        {
            ClassDef classDef = new ClassDef(s, "Created on the fly via RDF");
            return classDef;
        }

        @Override
        public void putObject(WDBObject wdbObject)
        {
//            final String graph = wdbObject.getClassName();
//            String subject = String.valueOf(UUID.randomUUID());
//            final String schemaString = "SCHEMA";
//            final String nameSpace = "#";)
//            String predicate = "";
//            String object = "";
//                String graphName = connection.getModel() + ":<" + graph + ">";
//                String graphName2 = connection.getModel() + ":<" + graph + "_" + schemaString + ">";
//
//                if (subject.indexOf(":") < 0) {
//                    // no specified connection.getNamespace():  use current default connection.getNamespace()
//                    subject = nameSpace + subject;
//                }
//                if (predicate.indexOf(":") < 0) {
//                    // no specified connection.getNamespace():  use current default connection.getNamespace()
//                    predicate = nameSpace + predicate;
//                }
//                if (object.indexOf(":") < 0) {
//                    // no specified connection.getNamespace():  use current default connection.getNamespace()
//                    object = nameSpace + object;
//                }
//                String typeString = "";
//                String s = "";
//                if(  ! graph.equals("")) {
//                    s = "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName +
//                        "', '" + subject.replaceAll("'", "") + "', '" + predicate.replaceAll("'", "") + "', '" + object.replaceAll("'", "") + typeString + "'))";
//                    db.executeStatement(s);
//                    s = "INSERT INTO " + db.getTable() + " VALUES ( " + db.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
//                        "', '" + subject.replaceAll("'", "") + "', 'rdf:type', '" + db.getNamespace() + graph + "'))";
//                    db.executeStatement(s);
//                    s = "INSERT INTO " + db.getTable() + " VALUES ( " + db.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
//                        "', '" + predicate.replaceAll("'", "") + "', 'rdf:type', 'owl:DatatypeProperty'))";
//                    db.executeStatement(s);
//                    s = "INSERT INTO " + db.getTable() + " VALUES ( " + db.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
//                        "', '" + predicate.replaceAll("'", "") + "', 'rdfs:domain', '" + db.getNamespace() + graph + "'))";
//                    db.executeStatement(s);
//                    s = "INSERT INTO " + db.getTable() + " VALUES ( " + db.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
//                        "', '" + predicate.replaceAll("'", "") + "', 'rdfs:range', 'xsd:string'))";
//                    db.executeStatement(s);
//                }
//                else {
//                    s = "INSERT INTO " + db.getTable() + " VALUES ( " + db.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + graphName2 +
//                        "', '" + subject.replaceAll("'", "") + "', '" + predicate.replaceAll("'", "") + "', '" + object.replaceAll("'", "") + typeString + "'))";
//                    db.executeStatement(s);
//                }
//
//            sparqlHelper.insertQuad(iq.className, instanceID, dvaAssignment.AttributeName,
//                    (String)dvaAssignment.Value.toString(), false);

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
