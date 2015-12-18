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
    public abstract void executeStatement(String stmt) throws SQLException;
    
    // This will execute a query. 
    public abstract ResultSet executeQuery(String query) throws SQLException; 
}
