package org.python.ReL;

import wdb.metadata.Adapter;
import wdb.metadata.ClassDef;
import wdb.metadata.IndexDef;
import wdb.metadata.WDBObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * A class that is used to interface with the oracle database.
 * All statements and queries should go through here to communicate with oracle.
 */
 
public class OracleInterface extends DatabaseInterface {

    private Connection connection;
    private Statement callableStatement; 
    private Statement createStatement; 
    private ResultSet rs;
    private String debug;

    public OracleInterface(String url, String uname, String passw, String conn_type, String debug) {
        super();
        this.adapter = new OracleInterfaceAdapter(this);
        try
        {
            Class.forName("oracle.jdbc.OracleDriver");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
            catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        connection = null;
        callableStatement = null;
        createStatement = null;
        this.debug = debug;
        if(conn_type != "none" ) {
        try
           {
              // Connect to the database
              connection = DriverManager.getConnection(url, uname, passw);
           } catch(SQLException e)
           {
                 System.out.println("Exception: " + e.getMessage());
           }
        }
    }

    @Override
    public void executeStatement(String stmt)
    {
        try
        {
            if(callableStatement != null) {
                callableStatement.close();
            }
            if (debug == "debug") 
            {
                System.out.println("exec -> " + stmt);
            }
            callableStatement = connection.createStatement();
            callableStatement.execute(stmt); 
            callableStatement.close();

        }   catch(java.sql.SQLException e)
        { 
            System.out.println("Exception: " + e.getMessage());
        }
        // System.out.println(stmt);
    }

    @Override
    public ResultSet executeQuery(String query)
    {
        try
        {
            if(createStatement != null) 
            {
                createStatement.close();
            }
            if(rs != null) 
            {
                rs.close();
            }
            createStatement = connection.createStatement();
            rs = createStatement.executeQuery(query); 
        }   catch(java.sql.SQLException e)
        {
            System.out.println("exception: " + e.getMessage());
        }
        return rs;
    }

    private class OracleInterfaceAdapter implements Adapter {
        private OracleInterface db;

        private OracleInterfaceAdapter(OracleInterface db) {
            this.db = db;
        }


        @Override
        public void putClass(ClassDef classDef) {

        }

        @Override
        public ClassDef getClass(String s) throws ClassNotFoundException {
            ClassDef classDef = new ClassDef(s, "Created on the fly via RDF");
            return classDef;
        }

        @Override
        public void putObject(WDBObject wdbObject) {

        }

        @Override
        public WDBObject getObject(String s, Integer integer) {
            return null;
        }

        @Override
        public ArrayList<WDBObject> getObjects(IndexDef indexDef, String s) {
            return null;
        }

        @Override
        public void commit() {

        }

        @Override
        public void abort() {

        }
    }
}
