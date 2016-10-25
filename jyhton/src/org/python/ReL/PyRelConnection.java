package org.python.ReL;

import batch.json.JSONTransport;
import batch.tcp.TCPClient;
import batch.util.BatchTransport;
import org.python.core.*;
import org.python.expose.ExposedType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A class that is resposible for communicating with a database.
 * <p>
 * This class acts as an interface for database communication. Ideally we should
 * be able to implement database's other than oracle someday.
 * <p>
 * PyRelConnections are created when a RelConnection node is visited.
 * <p>
 * For now a Name node with the id = "RelConnection", can be used to place the PyRelConnection
 * on the frame.
 */

@ExposedType(name = "PyRelConnection", base = PyObject.class, doc = BuiltinDocs.tuple_doc)
public class PyRelConnection extends PyObject {

    // What type are we. 
    public static final PyType TYPE = PyType.fromClass(PyRelConnection.class);
    // A PyRelConnection provides access to a database via a DatabaseInterface
    private DatabaseInterface database;
    // Information about the connection
    private String url, username, password, connection_type, connection_DB;
    // the default namespace (or graph) within which full object URIs are resolved
    // private String namespace = "http://www.example.org/people.owl#";
    // private String namespace = "NA#";
    private String namespace = "#";

    private String schemaString = "SCHEMA";
    // the current model
    private String model;
    // the current table
    private String table;
    // the current debug
    private String debug;
    // the current graph
    private String graph;

    //private ArrayList<String> statements;

    // Keep internals about which instances persist in our session. 
    // An instance that is inserted exists in the session map, and 
    // is reinserted on commits.
    private ConcurrentHashMap<Integer, PyObject> instances_map = new ConcurrentHashMap<Integer, PyObject>();

    public PyRelConnection(String url, String uname, String pword, String conn_type, String user_model, String user_table, String debug)
    {
        super();
        this.url = url;
        this.username = uname.toUpperCase();
        this.password = pword;
        this.connection_type = conn_type;
        this.model = user_model.toUpperCase();
        this.table = user_table.toUpperCase();
        this.debug = debug;

        // For now we create a oracle database connection, but we could create any type of connection.
        /* 
         * There is a problem with this database connection. The OracleDatabase.java class inherits from
         * the DatabaseInterface.java abstract class, which is designed for connecting only to relational
         * databases. If DatabaseInterface.java is not re-implemented, than one can only make
         * connections to relational databases and not triple stores, such as AllegroGraph, through DatabaseInterface.java
         */

        if (url.contains("jdbc:oracle")) {
            database = new OracleDatabase(this, url, uname, pword, conn_type, debug);
            connection_DB = "Oracle";

            // make sure the quad store is setup. 
            /*if(conn_type == "rdf_mode" || conn_type == "ag_sql_rdf_mode") {
                SPARQLDoer.createQuadStore(this);
            }*/
            if (conn_type == "rdf_mode") {
                SPARQLDoer.createQuadStore(this);
            }
        }
        else if (url.contains("OracleNoSQL")) {
            if (conn_type.equalsIgnoreCase("native_mode")) {
                database = new OracleNoSQLDatabase(this, url, uname, pword, conn_type, debug);
                connection_DB = "Native_OracleNoSQL";
            }
            else {
                database = new OracleRDFNoSQLDatabase(this, url, uname, pword, conn_type, debug);
                connection_DB = "OracleNoSQL";
            }

        }
        else if (url.contains("TitanNoSQL")) {
            if (conn_type.equalsIgnoreCase("native_mode")) {
                database = new TitanNoSQLDatabase();
                connection_DB = "Native_OracleNoSQL";
            }
            else {
                database = new OracleRDFNoSQLDatabase(this, url, uname, pword, conn_type, debug);
                connection_DB = "OracleNoSQL";
            }
        }
    }

    // We need to provide access to the following to generate some queries i suppose.
    public String getUrl()
    {
        return url;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getConnectionType()
    {
        return connection_type;
    }

    public String getConnectionDB()
    {
        return connection_DB;
    }

    public DatabaseInterface getDatabase()
    {
        return database;
    }

    // Accessor methods for the model, graph and namespace that this connection
    // should be writing too. 
    public String getModel()
    {
        return model;
    }

    public String getTable()
    {
        return table;
    }

    public String getGraph()
    {
        return graph;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public String getSchemaString()
    {
        return schemaString;
    }

    public String getDebug()
    {
        return debug;
    }

    public void batchExecuteStatement(ArrayList<String> stmts) throws SQLException, UnknownHostException
    {
        InetAddress address = InetAddress.getByName("");
        BatchTransport transport = new JSONTransport();
        //Reader sql_stmts = new Reader();
        //transport.read(sql_stmts);
        TCPClient test = new TCPClient(address, 80, transport);		
    /*batch (String stmt: stmts) {
	     database.executeStatement(stmt);
	}*/
    }

    // Provide access to our database dependent methods
    // This will just execute the statement. 
    public void executeStatement(String stmt) throws SQLException
    {
        database.executeStatement(stmt);
    }

    // This will execute a query. 
    public ResultSet executeQuery(String query) throws SQLException
    {
        return database.executeQuery(query);
    }

    // The rest of this code is to provide access to our OORel session information.
    // We return the unique id this instace has in our connection. 
    // Each connection could have different instances in the sessions. 
    public int addToSession(PyObject obj)
    {
        //First add the next DBUNIQUEID attribute to the instance. 
        //Grab a pointer to the dictionary, within the instance's type's dictionary to set a values
        ConcurrentMap<Object, PyObject> inst_dict;
        inst_dict = ((PyStringMap) obj.fastGetDict()).getMap();
        int prev_id = getDBUNIQUEID(obj);
        if (prev_id > -1) {
            // If this instance already exists in our session. Then do not add it again. 
            if (!instances_map.containsKey(prev_id)) {
                instances_map.put(prev_id, obj);
            }
            return prev_id;
        }
        else {
            int id = SPARQLDoer.getNextGUID(this);
            instances_map.put(id, obj);
            inst_dict.put("DBUNIQUEID", new PyInteger(id));
            return id;
        }
    }

    // Commit the session state for this connection.
    // This means that every instance that was inserted to the session should 
    // persist in the database with the same values at the time of the commit. 
    public void commit_oorel_session() throws SQLException
    {
        SIMHelper simHelper = new SIMHelper(this);
        for (PyObject obj : instances_map.values()) {
            //We delete the object if it was already in the database. 
            TreeMap<String, Object> key_vals = new TreeMap<String, Object>();
            key_vals.put("DBUNIQUEID", getDBUNIQUEID(obj));
            //Do the delete on all objects with this DBUNIQUEID
            SPARQLDoer.deleteSubjectsWithAttrValues(this, obj.getType().getName(), key_vals);
            //Then we repopulate the instance in the database. 
            insertInstance((PyObjectDerived) obj, simHelper);
        }
    }

    /**
     * Return the DBUNIQUEID of an instance, or -1 if there was no DBUNIQUEID found on that
     * instance.
     *
     * @param instance
     */
    private static int getDBUNIQUEID(PyObject instance)
    {
        ConcurrentMap<Object, PyObject> inst_dict =
                ((PyStringMap) instance.fastGetDict()).getMap();
        Object val = inst_dict.get("DBUNIQUEID");
        if (val != null && val instanceof PyInteger) {
            return ((PyInteger) val).getValue();
        }
        else {
            //We failed to find a DBUNIQUEID
            return -1;
        }
    }

    // Return an instance that already exists in the session, 
    // or return null if no instance with that DBUNIQUEID is in the session. 
    public PyObject getInstance(int DBUNIQUEID)
    {
        return instances_map.get(new Integer(DBUNIQUEID));
    }

    /**
     * Insert a class instance into the database.
     */
    private void insertInstance(PyObjectDerived instance, SIMHelper simHelper)
    {
        //So what is going on here...
        //Jython keeps two dictionaries of member data, one dictionary is defined in the 
        //PyObjects objtype, this is the initial values of all the data.
        //When an object is used
        //it is then moved over to the PyObject's dictionary itself. 
        //So we need to iterate over the PyObject's dictionary, and remember
        //what variables we've used.
        //Then go through and fill the database with variables that are defined but unused. 
        TreeSet<String> used_variables = new TreeSet<String>();

        simHelper.executeInstance(((PyStringMap) instance.getDict()).getMap()
                .get("DBUNIQUEID")
                .toString(), instance.getType().getName());
        //Add the member data. 
        //We first add any live data members, i.e data members that
        //have been moved to the instance's 
        //dictionary, we then go through and add any remaining data
        //members that may have been missed
        //to looking at the types dictionary. 
        PyStringMap variableMap = (PyStringMap) instance.getDict();
        addMemberDataHelper(instance, variableMap, used_variables, simHelper);
        variableMap = (PyStringMap) (instance.getType().fastGetDict());
        addMemberDataHelper(instance, variableMap, used_variables, simHelper);

    }

    /**
     * A helper method for adding instances to the database.
     * XXX This method properly populates the datastructure necessary to
     * populate the database.
     */
    private void addMemberDataHelper(PyObject instance, PyStringMap variableMap, TreeSet<String> used_vars, SIMHelper simHelper)
    {
        ConcurrentMap<Object, PyObject> data = variableMap.getMap();
        for (Map.Entry<Object, PyObject> entry : data.entrySet()) {
            Object key = entry.getKey();
            PyObject value = entry.getValue();
            String value_type = value.getType().getName();
            //keys that start with _ are considered invalid characters in the database. 
            if (key.toString().startsWith("_")) {
                continue;
            }
            //Only do the logic required for adding member data, if we have not already added the data. 
            if (!used_vars.contains(key.toString())) {

                //Filter out types we don't want, as well as variables that start with _
                if (!value.getType().getName().equals("function") && !value_type.equals("NoneType") && (value.toString()
                        .length() > 0 && !value.toString().substring(0, 1).equals("_"))) {
                    if (value instanceof PyTuple) {
                        throw new IllegalStateException("Only multi-valued attributes can be of type Tuple.");
                    }
                    simHelper.executeMemberData(((PyStringMap) instance.getDict()).getMap()
                            .get("DBUNIQUEID")
                            .toString(), value_type, key.toString(), value.toString());

                }
                used_vars.add(key.toString());
            }
        }
    }
}
