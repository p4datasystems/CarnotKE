package org.python.ReL;

import oracle.kv.*;
import oracle.kv.table.Table;
import oracle.kv.table.TableAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Joshua Hurt
 */
public class Database extends DatabaseInterface {
    public static final boolean DBG = true;
    private File INSTALLATION_ROOT;

    /* Table Info */
    private static final String CLASSDEF_TABLE_NAME = "ClassDef";
    private static final String WDBOBJECT_TABLE_NAME = "WDBObject";

    /* Storage Node */
    private static final String STORE_NAME = "WDB";
    private static final String HOST = "localhost";
    private static final String PORT = "5000";
    private static final String ADMIN_PORT = "5001";
    private static final String HARANGE = "5010,5020";
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
    public Database() {
        validateInstallationRoot();
        baseCommands = new String[]{"java", "-jar", INSTALLATION_ROOT.getAbsolutePath() + "/extlibs/kvstore.jar"};

        if (! reconnectToExistingStore()) {
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
     ************* STORE CONNECTION METHODS ***************************
     *******************************************************/

    public boolean setupEnvironmentAndCreateNodes() {
        // Initialize Storage Node
        kvStoreCommand("makebootconfig",
                "-root", storeRoot.getAbsolutePath(),
                "-port", PORT,
                "-admin", ADMIN_PORT,
                "-host", HOST,
                "-harange", HARANGE,
                "-capacity", "1",
                "-num_cpus", "0",
                "-memory_mb", "0",
                "-store-security", "none",
                "-storagedir", storageDir.getAbsolutePath());


        kvStoreCommand("start", "-root", storeRoot.getAbsolutePath());

        final File wdbStoreSetupScript = new File(INSTALLATION_ROOT, "setup_WDB_store.txt");
        if (!wdbStoreSetupScript.exists())
            ultimateCleanUp("Cannot find setup script for storage node.");

        // (Yes, I meant 'PORT' and not 'ADMIN_PORT' since we need to connect on the registry port)
        kvStoreCommand("runadmin",
                "-port", PORT,
                "-host", HOST,
                "load", "-file", wdbStoreSetupScript.getAbsolutePath());

        return true;
    }

    public void kvStoreCommand(String... commandWithArgs) {
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
        }
        catch (Exception e) {
            ultimateCleanUp(String.format("%s",e.getMessage()));
        }
    }

    public void validateInstallationRoot() {
        final String serverRoot = System.getenv("INSTALLATION_ROOT");
        if (serverRoot == null)
            ultimateCleanUp("Server not started. Please set INSTALLATION_ROOT environment variable.");

        INSTALLATION_ROOT = new File(serverRoot);
        if (!INSTALLATION_ROOT.isDirectory()) {
            ultimateCleanUp(String.format(
                    "Invalid INSTALLATION_ROOT value. '%s' is not a directory!",
                    INSTALLATION_ROOT));
        }
    }

    public void setupSNDirectories() {
        storageDir = new File(INSTALLATION_ROOT, "db/store");
        if(!storageDir.exists())
        {
            if (storageDir.mkdirs())
                System.out.println(String.format(
                        "Creating storage directory at '%s'", storageDir.getAbsolutePath()));
        }

        storeRoot = new File(INSTALLATION_ROOT, "/STORE");
        if (storeRoot.mkdirs())
            System.out.println(String.format(
                    "Creating Storage Node directory at '%s'", storeRoot.getAbsolutePath()));
    }


    /*******************************************************
     ************* TABLE METHODS ***************************
     *******************************************************/
    public void createTables() {
        createTable(classTable, CLASSDEF_TABLE_NAME);
        createTable(objectTable, WDBOBJECT_TABLE_NAME);
    }


    private void createTable(Table table, String tableName) {
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


    public void clearDatabase() {
        dropTable(classTable);
        dropTable(objectTable);
        createTables();

    }
    private void dropTable(Table table) {
        if (table == null)
            return;
        String statement =
                "DROP TABLE " +
                        table.getFullName();

        try {
            System.out.println(String.format("Dropping table %s ...",table.getFullName()));
            store.executeSync(statement);
            System.out.println("Success!");
        }
        catch (IllegalArgumentException e) {
            System.out.println("Invalid statement:\n" + e.getMessage());
            System.out.println("Well crap.");
        }
        catch (FaultException e) {
            System.out.println
                    ("Statement couldn't be executed, please retry: " + e);
            System.out.println("Well crap.");
        }
    }


    private void connectToStore() {
        // Obtain handles to the running Storage Node
        try {
            System.out.println("Trying to connect to the store ...");
            store = KVStoreFactory.getStore
                    (new KVStoreConfig(STORE_NAME, HOST + ":" + PORT));
        }
        catch (FaultException e) {
            ultimateCleanUp("Connection failed! Cleaning up...");
            if (DBG)
                System.out.println(String.
                        format("'%s' in connectToStore", e.getMessage()));
        }
        System.out.println("Connection successful!");
    }

    private boolean reconnectToExistingStore() {
        // Obtain handles to the running Storage Node
        try {
            System.out.println("Trying to connect to an already running store ...");
            store = KVStoreFactory.getStore
                    (new KVStoreConfig(STORE_NAME, HOST + ":" + PORT));
        }
        catch (FaultException e) {
            System.out.println("No existing connection found. Creating new one.");
            return false;
        }
        setupSNDirectories();
        createTables();
        System.out.println("Connection successful!");
        return true;
    }

    private void disconnectFromStore() {
        if (store != null)
            store.close();
    }


    /**
     * Remove the directories created during initializing so we
     * can get a fresh start next try. Close connections to KVStores
     * and terminate background server processes.
     */
    public void ultimateCleanUp(String reason) {
        disconnectFromStore();
        // Explicitly run stop if server is running
        kvStoreCommand("stop", "-root", storeRoot.getAbsolutePath());

        // Below may not be needed if above works quickly
        if (storeProcess != null)
            storeProcess.destroy();
        System.out.println("========="+reason+"=========");
        System.exit(-1);
    }

    public String getStorageDirPath() {
        return storageDir.getAbsolutePath();
    }

    public TableAPI getTableHandle() {
        return tableH;
    }

    public Table getClassTable() {
        return classTable;
    }

    public Table getObjectTable() {
        return objectTable;
    }
}
