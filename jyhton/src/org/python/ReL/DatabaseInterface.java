package org.python.ReL;


import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class that is resposible for communicating with a database.
 * <p>
 * This class acts as an interface for database communication. Ideally we should
 * be able to implement database's other than oracle someday.
 */

public abstract class DatabaseInterface {
    private String nameSpace = "nameSpace should be overridden.";
    private String nameSpacePrefix = "nameSpacePrefix should be overridden.";
    protected PyRelConnection pyRelConn;

    DatabaseInterface(PyRelConnection pyRelConn) {
        this.pyRelConn = pyRelConn;
    }

    // This will just execute the statement. 
    public void executeStatement(String stmt) throws SQLException
    {
        System.out.println("\nThis is the top level executeStatement statement in DatabaseInterface and probably should be overridden if it's being called, query is: " + stmt + "\n");
    }

    ;

    // This will execute a query. 
    public ResultSet executeQuery(String query) throws SQLException
    {
        System.out.println("\nThis is the top level executeQuery statement in DatabaseInterface and probably should be overridden if it's being called, query is: " + query + "\n");
        return null;
    }

    ;


    public String getNameSpace()
    {
        return nameSpace;
    }

    public String getNameSpacePrefix()
    {
        return nameSpacePrefix;
    }
}
