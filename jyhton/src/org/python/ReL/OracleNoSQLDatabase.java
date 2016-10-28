package org.python.ReL;

import oracle.kv.FaultException;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.table.PrimaryKey;
import oracle.kv.table.Row;
import oracle.kv.table.Table;
import oracle.kv.table.TableAPI;
import org.apache.commons.lang3.SerializationUtils;
import org.python.ReL.WDB.database.wdb.metadata.*;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Joshua Hurt
 */
public class OracleNoSQLDatabase extends DatabaseInterface implements ParserAdapter {
    public static boolean DBG;
    private File INSTALLATION_ROOT;
    private static final int MIN_PORT_NUMBER = 2000;
    private static final int MAX_PORT_NUMBER = 8000;

    /* Table Info */
    private static final String CLASSDEF_TABLE_NAME = "ClassDef";
    private static final String WDBOBJECT_TABLE_NAME = "WDBObject";

    /* Storage Node */
    private static String STORE_NAME;
    private static String HOST;
    private static String PORT;
    private static String ADMIN_PORT;
    private static String HARANGE;
    private String[] baseCommands;

    public static KVStore store;
    private static TableAPI tableH;
    private static Table classTable;
    private static Table objectTable;
    private static File storeRoot;
    private static Process storeProcess;
    private static File storageDir;


    /**
     * Connects to a KVStore on port 5000 if one exists, otherwise
     * creates a new KVStore.
     */
    public OracleNoSQLDatabase(PyRelConnection pyRelConn, String url, String uname, String passw,
                               String conn_type, String debug)
    {
        super(pyRelConn);
        validateInstallationRoot();
        baseCommands = new String[]{"java", "-jar", INSTALLATION_ROOT.getAbsolutePath() + "/dist/javalib/kvstore.jar"};
        validateConfigParameters(uname, passw);

        if (!reconnectToExistingStore()) {
            setupSNDirectories();
            setupEnvironmentAndCreateNodes();
            connectToStore();
            if (!DBG) {
                dropTable(classTable);
                dropTable(objectTable);
            }
            createTables();
        }
    }


    /*******************************************************
     * ************ STORE CONNECTION METHODS ***************************
     *******************************************************/

    public boolean setupEnvironmentAndCreateNodes()
    {
        // Initialize Storage Node
        kvStoreCommand("makebootconfig",
                "-root", storeRoot.getAbsolutePath(),
                "-port", PORT,
                "-admin", ADMIN_PORT,
                "-host", HOST,
                "-harange", HARANGE,
                "-capacity", "1",
                "-num_cpus", "1",
                "-memory_mb", "512",
                "-store-security", "none",
                "-storagedir", storageDir.getAbsolutePath());


        kvStoreCommand("start", "-root", storeRoot.getAbsolutePath());

        final File wdbStoreSetupScript = new File(INSTALLATION_ROOT,
                "setup_"+STORE_NAME+"_store.txt");
        if (!wdbStoreSetupScript.exists())
            ultimateCleanUp("Cannot find setup script for storage node.");

        // (Yes, I meant 'PORT' and not 'ADMIN_PORT' since we need to connect on the registry port)
        kvStoreCommand("runadmin",
                "-port", PORT,
                "-host", HOST,
                "load", "-file", wdbStoreSetupScript.getAbsolutePath());

        return true;
    }

    public void kvStoreCommand(String... commandWithArgs)
    {
        final String command = commandWithArgs[0];
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(baseCommands));
        arguments.addAll(Arrays.asList(commandWithArgs));
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(INSTALLATION_ROOT);
        pb.command(arguments);

        if (DBG)
            System.out.println(String.format("Running command: '%s'", pb.command().toString()));

        try {
            Process utility = pb.start();
        /* The Storage Node Agent (SNA) should run in the background, so don't wait for it.
         * Save it so we can kill the process when we exit. */
            if (command.equalsIgnoreCase("start"))
                storeProcess = utility;
                // Don't want to wait for stop
            else if (command.equalsIgnoreCase("stop"))
                return;
            else {
                /* Need to setup the Node before we can use it
                 * so waitFor() makes us wait for the process to finish. */
                int status = utility.waitFor();

                if (status != 0) {
                    ultimateCleanUp("Process exited abnormally.");
                }
            }
        } catch (Exception e) {
            ultimateCleanUp(String.format("%s", e.getMessage()));
        }
    }

    public void validateInstallationRoot()
    {
        final String serverRoot = System.getenv("INSTALLATION_ROOT");
        if (serverRoot == null)
            ultimateCleanUp("Server not started. Please set INSTALLATION_ROOT environment " +
                    "variable to the directory that contains your setup_<name>_store.txt file");

        INSTALLATION_ROOT = new File(serverRoot);
        if (!INSTALLATION_ROOT.isDirectory()) {
            ultimateCleanUp(String.format(
                    "Invalid INSTALLATION_ROOT value. '%s' is not a directory!",
                    INSTALLATION_ROOT));
        }
    }

    public void validateConfigParameters(String uname, String passw)
    {
        String host = passw.split(":")[0];
        String port = passw.split(":")[1];
//        if (!is_port_available(Integer.parseInt(port))) {
//            ultimateCleanUp("Port "+port+" is already in use. Please choose another. " +
//                    "(Note: Don't forget to change the port in your " +
//                    "setup_<name>_store.txt)");
//        }

        STORE_NAME = uname;
        HOST = host;
        PORT = port;
        ADMIN_PORT = Integer.parseInt(PORT) + 1 + "";
        String harange_1 = Integer.parseInt(PORT) + 3 + "";
        String harange_2 = Integer.parseInt(PORT) + 6 + "";
        HARANGE = harange_1 + "," + harange_2;
        DBG = true;
    }


    public void setupSNDirectories()
    {
        storageDir = new File(INSTALLATION_ROOT, STORE_NAME+"_db/store");
        if (!storageDir.exists()) {
            if (storageDir.mkdirs())
                System.out.println(String.format(
                        "Creating storage directory at '%s'", storageDir.getAbsolutePath()));
        }

        storeRoot = new File(INSTALLATION_ROOT, STORE_NAME+"_STORE");
        if (storeRoot.mkdirs())
            System.out.println(String.format(
                    "Creating Storage Node directory at '%s'", storeRoot.getAbsolutePath()));
    }


    /*******************************************************
     * ************ TABLE METHODS ***************************
     *******************************************************/
    public void createTables()
    {
        createTable(classTable, CLASSDEF_TABLE_NAME);
        createTable(objectTable, WDBOBJECT_TABLE_NAME);
    }

    private void createTable(Table table, String tableName)
    {
//        if (!storeProcess.isAlive())
//            throw new IllegalThreadStateException("No connection to database!");
        tableH = store.getTableAPI();
        String statement;

        // If this succeeds then the tables have been already created
        // in a previous run.
        table = tableH.getTable(tableName);
        if (table == null) {
            try {
            /*
             * Add a table to the database.
             * Execute this statement asynchronously.
             */
                // works
                // lol just naming them 'key' and 'value'
                statement =
                        "CREATE TABLE " + tableName + " (" +
                                "key STRING," +
                                "value BINARY," +
                                "PRIMARY KEY (key))";

                store.executeSync(statement);
                table = tableH.getTable(tableName);

            } catch (IllegalArgumentException e) {
                ultimateCleanUp(String.format(
                        "Invalid statement: %s",
                        e.getMessage()));
            } catch (FaultException e) {
                ultimateCleanUp(String.format(
                        "Invalid statement: %s",
                        e.getMessage()));
            }
        }
        if (tableName.equalsIgnoreCase(CLASSDEF_TABLE_NAME))
            classTable = table;
        else if (tableName.equalsIgnoreCase(WDBOBJECT_TABLE_NAME))
            objectTable = table;
    }

    public void clearDatabase()
    {
        dropTable(classTable);
        dropTable(objectTable);
        createTables();

    }

    private void dropTable(Table table)
    {
        if (table == null)
            return;
        String statement =
                "DROP TABLE " +
                        table.getFullName();

        try {
            System.out.println(String.format("Dropping table %s ...", table.getFullName()));
            store.executeSync(statement);
            System.out.println("Success!");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid statement:\n" + e.getMessage());
            System.out.println("Well crap.");
        } catch (FaultException e) {
            System.out.println
                    ("Statement couldn't be executed, please retry: " + e);
            System.out.println("Well crap.");
        }
    }

    private void connectToStore()
    {
        // Obtain handles to the running Storage Node
        try {
            System.out.println("Trying to connect to the store ...");
            store = KVStoreFactory.getStore
                    (new KVStoreConfig(STORE_NAME, HOST + ":" + PORT));
        } catch (FaultException e) {
            ultimateCleanUp("Connection failed! Cleaning up...");
            if (DBG)
                System.out.println(String.
                        format("'%s' in connectToStore", e.getMessage()));
        }
        System.out.println("Connection successful!");
    }

    private boolean reconnectToExistingStore()
    {
        // Obtain handles to the running Storage Node
        try {
            System.out.println("Trying to connect to an already running store ...");
            store = KVStoreFactory.getStore
                    (new KVStoreConfig(STORE_NAME, HOST + ":" + PORT));
        } catch (FaultException e) {
            System.out.println("No existing connection found. Creating new one.");
            return false;
        }
        setupSNDirectories();
        createTables();
        System.out.println("Connection successful!");
        return true;
    }

    private void disconnectFromStore()
    {
        if (store != null)
            store.close();
    }

    /**
     * Checks to see if a specific port is available.
     *
     * @param port the port to check for availability
     */
    public boolean is_port_available(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                /* should not be thrown */
                }
            }
        }

        return false;
    }


    /**
     * Remove the directories created during initializing so we
     * can get a fresh start next try. Close connections to KVStores
     * and terminate background server processes.
     */
    public void ultimateCleanUp(String reason)
    {
        disconnectFromStore();
        // Explicitly run stop if server is running
        if (storeRoot != null)
            kvStoreCommand("stop", "-root", storeRoot.getAbsolutePath());

        // Below may not be needed if above works quickly
        if (storeProcess != null)
            storeProcess.destroy();
        System.out.println("=========" + reason + "=========");
        System.exit(-1);
    }

    public String getStorageDirPath()
    {
        return storageDir.getAbsolutePath();
    }

    public TableAPI getTableHandle()
    {
        return tableH;
    }

    public Table getClassTable()
    {
        return classTable;
    }

    public Table getObjectTable()
    {
        return objectTable;
    }



    /**
     * key: String class:(classDef.name)
     * value: ClassDef classDef
     */
    @Override
    public void putClass(Query query)
    {
        ClassDef classDef = (ClassDef) query;
        final String keyString = makeClassKey(classDef.name);
        final byte[] data = SerializationUtils.serialize(classDef);

        Row row = this.getClassTable().createRow();
        row.put("key", keyString);
        row.put("value", data);

        this.getTableHandle().put(row, null, null);
    }

    @Override
    public ClassDef getClass(Query query) throws ClassNotFoundException
    {
        return getClass(query.getQueryName());
    }

    /**
     * key: String class:(classDef.name)
     *
     * @return ClassDef or null if not found
     */
    @Override
    public ClassDef getClass(String className) throws ClassNotFoundException
    {
        PrimaryKey key = this.getClassTable().createPrimaryKey();
        final String keyString = makeClassKey(className);
        key.put("key", keyString);
        Row row = this.getTableHandle().get(key, null);

        if (row == null)
            throw new ClassNotFoundException(String.format(
                    "Key '%s' not present in table", keyString));
        byte[] data = row.get("value").asBinary().get();

        final ClassDef classDef = (ClassDef) SerializationUtils.deserialize(data);
        if (classDef == null) {
            this.ultimateCleanUp(String.format(
                    "Null value returned from ClassesDB lookup for class: %s",
                    keyString));
        }

        return classDef;
    }

    /**
     * key: String object:(Uid.toString())
     * value: WDBObject object
     *
     * @param wthisObject to serialize and store as value
     */
    @Override
    public void putObject(WDBObject wthisObject)
    {
        final String keyString = makeObjectKey(wthisObject.getUid());
        final byte[] data = SerializationUtils.serialize(wthisObject);

        Row row = this.getObjectTable().createRow();
        row.put("key", keyString);
        row.put("value", data);

        this.getTableHandle().put(row, null, null);
    }

    /**
     * key: String object:(Uid.toString())
     * value: WDBObject object
     *
     * @param className only used for MissingResourceException
     * @param Uid       is the key to retrieve the WDBObject
     * @return WDBObject or throws MissingResourceException
     */
    @Override
    public WDBObject getObject(String className, Integer Uid)
    {
        final String keyString = makeObjectKey(Uid);
        PrimaryKey key = this.getObjectTable().createPrimaryKey();
        key.put("key", keyString);
        Row row = this.getTableHandle().get(key, null);

        byte[] data = row.get("value").asBinary().get();

        final WDBObject wthisObject = (WDBObject) SerializationUtils.deserialize(data);
        if (wthisObject == null) {
            this.ultimateCleanUp(String.format(
                    "Null value returned from ClassesDB lookup for class: %s",
                    keyString));
        }

        return wthisObject;
    }

    @Override
    public ArrayList<WDBObject> getObjects(Query indexDefQuery, String key) {
        return null;
    }

    private String makeClassKey(String className)
    {
        return classKeyPrefix + ":" + className;
    }

    private String makeObjectKey(Integer Uid)
    {
        return objectKeyPrefix + ":" + Uid.toString();
    }

    @Override
    public void abort()
    {

    }

    @Override
    public void commit()
    {

    }
}
