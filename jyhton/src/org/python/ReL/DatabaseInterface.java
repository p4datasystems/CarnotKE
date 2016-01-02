package org.python.ReL;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * A class that is resposible for communicating with a database.
 *
 * This class acts as an interface for database communication. Ideally we should
 * be able to implement database's other than oracle someday.
 */

public abstract class DatabaseInterface{

    // This will just execute the statement. 
    public void executeStatement(String stmt) throws SQLException {
        System.out.println("\nThis is the top level executeStatement statement in DatabaseInterface and probably should be overridden if it's being called, query is: " + stmt + "\n");
    }; 
    
    // This will execute a query. 
    public ResultSet executeQuery(String query) throws SQLException {
        System.out.println("\nThis is the top level executeQuery statement in DatabaseInterface and probably should be overridden if it's being called, query is: " + query + "\n");
        return null;
    }; 

    // This will execute OracleNoSQLAddQuad. 
    public void OracleNoSQLAddQuad(String graph, String subject, String predicate, String object) {
        System.out.println("\nThis is the top level OracleNoSQLAddQuad statement in DatabaseInterface and probably should be overridden if it's being called.\n");
    };
}
