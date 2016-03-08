package org.python.ReL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.*;
import java.sql.*;

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
}
