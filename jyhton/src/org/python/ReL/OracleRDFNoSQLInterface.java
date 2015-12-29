package org.python.ReL;

import java.sql.ResultSet;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import oracle.rdf.kv.client.jena.*;

/**
 * A class that is used to interface with the oracle database.
 * All statements and queries should go through here to communicate with oracle.
 */
 
public class OracleRDFNoSQLInterface extends DatabaseInterface {

    private OracleNoSqlConnection connection;
    private ResultSet rs = null;
    private String debug;
     
    public OracleRDFNoSQLInterface(String url, String uname, String passw, String conn_type, String debug) {
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
        this.debug = debug;
        if(conn_type != "none" ) {
            connection = OracleNoSqlConnection.createInstance("kvstore", "Phils-MacBook-Pro.local", "5000");
        }
    }

    public OracleNoSqlConnection getConnection() { return connection; }

    @Override
    public void executeStatement(String stmt)
    {
        System.out.println("In dummy executeStatement");
    }

    @Override
    public ResultSet executeQuery(String query)
    {
        System.out.println("In dummy executeQuery");
        return rs;
    }
}
