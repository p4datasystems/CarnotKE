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
    DatasetGraphNoSql datasetGraph = null;
    private ResultSet rs = null;
    private String debug;
     
    public OracleRDFNoSQLInterface(String url, String uname, String passw, String conn_type, String debug) {
        super();

        connection = null;

        this.debug = debug;

        if(conn_type != "none" ) {
            this.connection = OracleNoSqlConnection.createInstance("kvstore", "Phils-MacBook-Pro.local", "5000");
            OracleGraphNoSql graph = new OracleGraphNoSql(connection);
            this.datasetGraph = DatasetGraphNoSql.createFrom(graph);

            // Close graph, as it is no longer needed
            graph.close(); 
            
            // Clear dataset
            System.out.println("\nWRNING: the NoSQL database is being cleared in PyRelConnection!\n");
            datasetGraph.clearRepository();
        }
    }

    public OracleNoSqlConnection getConnection() { return connection; }

    @Override
    public void OracleNoSQLAddQuad(String graph, String subject, String predicate, String object)
    {
        System.out.println("In addQuad, stmt is: " + graph + ", " + subject + ", " + predicate + ", " + object);
        datasetGraph.add(Node.createURI(graph), Node.createURI(subject), Node.createURI(predicate), Node.createURI(object));
    }
}
