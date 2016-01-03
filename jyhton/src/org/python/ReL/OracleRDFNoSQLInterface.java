package org.python.ReL;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import oracle.rdf.kv.client.jena.*;
import com.hp.hpl.jena.sparql.core.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        datasetGraph.add(Node.createURI("carnot:" + graph), Node.createURI("carnot:" + subject), Node.createURI("carnot:" + predicate), Node.createURI("carnot:" + object));

        Dataset ds = DatasetImpl.wrap(datasetGraph);

        String szQuery = " PREFIX c: <carnot:> "                    + 
                                                          // See URI discussion at https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
                                                          // For known non generic URI see https://gist.github.com/knu/744715/0143487cc3f3fffb0b6faf9b18b0da6f0642e7d5
               " SELECT ?g ?s ?p ?o"           +
               " WHERE "                       +
               " { "                           +
               "     GRAPH ?g { ?s ?p ?o }"    +
               " } ";

        Query query = QueryFactory.create(szQuery);
        QueryExecution qexec = QueryExecutionFactory.create(query, ds);

        try {
            com.hp.hpl.jena.query.ResultSet queryResults = qexec.execSelect();
                System.out.println("Made it to here 1.");
                if(queryResults != null) {
                    System.out.println("Made it to here 2. " + queryResults.toString());
                    //ResultSetFormatter.out(System.out, queryResults, query);
                    String xmlstr = "Initial string";
                    xmlstr = ResultSetFormatter.asXMLString(queryResults); // For documentation, see http://grepcode.com/file/repo1.maven.org/maven2/com.hp.hpl.  
                    System.out.println("xmlstr is: " + xmlstr);
                    System.out.println("Made it to here 3.");
                }
            }
        finally {
          qexec.close();
        }
        //ds.close();
    }
}
