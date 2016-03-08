/*-
 *
 *  This file is part of Oracle NoSQL Database
 *  Copyright (C) 2011, 2015 Oracle and/or its affiliates.  All rights reserved.
 *
 *  Oracle NoSQL Database is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, version 3.
 *
 *  Oracle NoSQL Database is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public
 *  License in the LICENSE file along with Oracle NoSQL Database.  If not,
 *  see <http://www.gnu.org/licenses/>.
 *
 *  An active Oracle commercial licensing agreement for this product
 *  supercedes this license.
 *
 *  For more information please contact:
 *
 *  Vice President Legal, Development
 *  Oracle America, Inc.
 *  5OP-10
 *  500 Oracle Parkway
 *  Redwood Shores, CA 94065
 *
 *  or
 *
 *  berkeleydb-info_us@oracle.com
 *
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  EOF
 *
 */

package table;

import java.util.List;

import oracle.kv.FaultException;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.table.ArrayValue;
import oracle.kv.table.Index;
import oracle.kv.table.IndexKey;
import oracle.kv.table.MapValue;
import oracle.kv.table.PrimaryKey;
import oracle.kv.table.ReadOptions;
import oracle.kv.table.RecordValue;
import oracle.kv.table.Row;
import oracle.kv.table.Table;
import oracle.kv.table.TableAPI;
import oracle.kv.table.TableIterator;

/**
 * This file includes a number of examples that demonstrate some of the
 * features of tables and secondary indexes in Oracle NoSQL Database.
 * <p>
 * See the {@link table package documentation} for instructions on how to build
 * and run this example. It requires a running Oracle NoSQL Database
 * instance. The usage is:
 * <pre>
 *   java -cp .:&lt;path-to-kvclient.jar&gt; table.TableAPIExample 
 *                            [-store &lt;instance name&gt;] \
 *                            [-host  &lt;host name&gt;]     \
 *                            [-port  &lt;port number&gt;]
 * </pre>
 * <p>
 * The example uses the default store, host, and port of <i>kvstore, localhost,
 * and 5000</i>.  These are the defaults for KVLite as well.
 * <h3>Example Cases</h3>
 * <ul>
 * <li>SimpleReadWrite: Creates a very simple table and does simple put and
 * get of a row.
 * <li>IndexReadWrite: Uses simple and composite indexes to demonstrate index
 * scans.
 * <li>ShardKeys: Uses a table that has a composite primary key and a defined
 * shard key.  This demonstrates the ability to ensure that rows with the same
 * shard key are stored in the same shard and are therefore accessible in an
 * atomic manner.  Such rows can also be accessed using the various "multi*"
 * API operations.
 * <li>UseChildTable: Uses a parent/child table relationship to demonstrate how
 * to put and get to/from a child table.  It also uses an index on the child
 * table to retrieve child rows via the index.
 * <li>ComplexFields: Uses a table with complex fields (Record, Array, Map) to
 * demonstrate input and output of rows in tables with such fields.
 * <li>ArraySecondaryIndex: Uses an index on the array from the complex field
 * example table to demonstrate use of an array index.
 * </ul>
 */
public class TableAPIExample {

    /*
     * Default values for accessing the cluster; conforms to defaults for
     * kvlite.
     */
    private String storeName = "kvstore";
    private String hostName = "localhost";
    private String hostPort = "5000";

    /* Handles onto the store */
    private KVStore store;
    private TableAPI tableAPI;

    /**
     * Run the TableAPIExample.
     */
    public static void main(String args[]) {
        TableAPIExample example = new TableAPIExample();
        try {
            example.parseArgs(args);
            example.runCases();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            example.closeStore();
        }
    }

    private void runCases() {
        /* Connect to the NoSQL store */
        store = KVStoreFactory.getStore
            (new KVStoreConfig(storeName, hostName + ":" + hostPort));

        tableAPI = store.getTableAPI();

        try {
            /* Create tables and indices */
            createTables();

            /* Execute each example */
            simpleReadWrite();
            indexReadWrite();
            shardKeys();
            useChildTable();
            complexFields();
            arraySecondaryIndex();
        } catch (IllegalArgumentException e) {
            System.err.println("Problem encountered: " + e);
        } catch (FaultException e) {
            System.err.println("Transient problem encountered: " + e +
                               ", consider retrying the example");
        } finally {
            dropTables();
        }
    }

    /**
     * Closes the store.
     */
    private void closeStore() {
        if (store != null) {
            store.close();
        }
    }

    /**
     * Create the tables needed by the example.
     */
    private void createTables() {
        System.out.println("Creating table simpleUsers");
        store.executeSync
            ("CREATE TABLE IF NOT EXISTS simpleUsers  " +
             "(firstName STRING, " +
             " lastName STRING, " +
             " userID INTEGER, " +
             " PRIMARY KEY (userID))");

        System.out.println("Creating index simpleIndex");
        store.executeSync
            ("CREATE INDEX IF NOT EXISTS simpleIndex ON " +
             "simpleUsers(firstName)");

        System.out.println("Creating index compoundIndex");
        store.executeSync
            ("CREATE INDEX IF NOT EXISTS compoundIndex ON " +
             "simpleUsers(lastName, firstName)");

        System.out.println("Creating table shardUsers");
        store.executeSync
            ("CREATE TABLE IF NOT EXISTS shardUsers  " +
             "(firstName STRING, " +
             " lastName STRING, " +
             " email STRING, " +
             " PRIMARY KEY (shard(lastName), firstName))");

        System.out.println("Creating child table shardUsers.address");
        store.executeSync
            ("CREATE TABLE IF NOT EXISTS shardUsers.address  " +
             "(street STRING, " +
             " state STRING, " +
             " zip INTEGER, " +
             " addressName STRING, " +
             " addressID INTEGER, " +
             " PRIMARY KEY (addressID))");

        System.out.println("Creating index on shardUsers.address");
        store.executeSync
            ("CREATE INDEX IF NOT EXISTS addressIndex ON " +
             "shardUsers.address(addressName)");

        System.out.println("Creating table complexUsers");
        store.executeSync
            ("CREATE TABLE IF NOT EXISTS complexUsers " +
             "(name RECORD (firstName STRING, lastName STRING), " +
             " likes ARRAY(STRING), " +
             " optionalInformation MAP(STRING), " +
             " userID INTEGER, " +
             " PRIMARY KEY (userID))");

        System.out.println("Creating index on complexUsers.likes");
        store.executeSync
            ("CREATE INDEX IF NOT EXISTS arrayIndex ON complexUsers(likes)");
    }

    /**
     * Drop the tables and indices created by the example.
     */
    private void dropTables() {
        System.out.println("Dropping index simpleIndex");
        store.executeSync("DROP INDEX IF EXISTS simpleIndex ON simpleUsers");

        System.out.println("Dropping index compoundIndex");
        store.executeSync
            ("DROP INDEX IF EXISTS compoundIndex ON simpleUsers");

        System.out.println("Dropping table simpleUsers");
        store.executeSync("DROP TABLE IF EXISTS simpleUsers");

        System.out.println("Dropping index on shardUsers.address");
        store.executeSync
            ("DROP INDEX IF EXISTS addressIndex ON shardUsers.address");

        System.out.println("Dropping child table shardUsers.address");
        store.executeSync ("DROP TABLE IF EXISTS shardUsers.address");
        
        System.out.println("Dropping table shardUsers");
        store.executeSync("DROP TABLE IF EXISTS shardUsers");

        System.out.println("Dropping index on complexUsers.likes");
        store.executeSync("DROP INDEX IF EXISTS arrayIndex ON complexUsers");

        System.out.println("Dropping table complexUsers");
        store.executeSync ("DROP TABLE IF EXISTS complexUsers ");
    }

    /**
     * Example: Simple read and write
     *
     * This example shows a simple use of tables, it uses a simple table
     * holding an id field as a primary key and 2 fields -- name and surname --
     * as Strings.  It does basic put and get.
     */
    private void simpleReadWrite() {
        System.out.println("\nExample: Simple Read and Write");

        Table table = getTable("simpleUsers");

        /* Insert row */
        Row row = table.createRow();
        row.put("userID", 1);
        row.put("firstName", "Alex");
        row.put("lastName", "Robertson");
        tableAPI.put(row, null, null);

        /* Insert row, if it is not already in the table */
        row = table.createRow();
        row.put("userID", 2);
        row.put("firstName", "John");
        row.put("lastName", "Johnson");
        tableAPI.putIfAbsent(row, null, null);

        /* Insert row only if it is already in the table */
        row = table.createRow();
        row.put("userID", 2);
        row.put("firstName", "John");
        row.put("lastName", "Jameson");
        tableAPI.putIfPresent(row, null, null);

        /*
         * Read a row from table using the primary key
         */

        /* Create a primary key and assign the field value */
        PrimaryKey key = table.createPrimaryKey();
        key.put("userID", 1);

        /* Get the matching row */
        row = tableAPI.get(key, new ReadOptions(null, 0, null));

        /* Print the full row as JSON */
        System.out.println(row.toJsonString(true));

        /* Access a specific field */
        System.out.println("firstName field as JSON: " +
                           row.get("firstName").toJsonString(false));
    }

    /**
     * Example 2: Secondary Index
     *
     * This example uses 2 indexes on the table "simpleUsers."  One is a
     * simple, single-field index on "firstName" and the other is a
     * composite index on "lastName, firstName".
     */
    private void indexReadWrite() {
        System.out.println("\nExample: index based read and write");
        Table table = getTable("simpleUsers");

        /* Insert new rows */
        Row row = table.createRow();
        row.put("userID", 3);
        row.put("firstName", "Joel");
        row.put("lastName", "Robertson");
        tableAPI.putIfAbsent(row, null, null);

        row = table.createRow();
        row.put("userID", 4);
        row.put("firstName", "Bob");
        row.put("lastName", "Jameson");
        tableAPI.putIfAbsent(row, null, null);

        row = table.createRow();
        row.put("userID", 5);
        row.put("firstName", "Jane");
        row.put("lastName", "Jameson");
        tableAPI.putIfAbsent(row, null, null);

        row = table.createRow();
        row.put("userID", 6);
        row.put("firstName", "Joel");
        row.put("lastName", "Jones");
        tableAPI.putIfAbsent(row, null, null);

        TableIterator<Row> iter = null;
        try {

            /*
             * Use the simple index on firstName to retrieve all users with
             * the firstName of "Joel"
             */
            Index simple_index = table.getIndex("simpleIndex");

            /*
             * Create an IndexKey and assign the firstName value.
             * The IndexKey works similarly to a PrimaryKey and only allows
             * assignment of fields that are part of the index.
             */
            IndexKey simpleIndexKey = simple_index.createIndexKey();
            simpleIndexKey.put("firstName", "Joel");

            /* Get the matching rows */
            iter = tableAPI.tableIterator(simpleIndexKey, null, null);

            /* Print rows as JSON */
            System.out.println("\nUsers with firstName Joel");
            while(iter.hasNext()) {
                System.out.println(iter.next().toJsonString(true));
            }

            /*
             * TableIterator instances must be closed to release resources
             */
            iter.close();
            iter = null; 

            /*
             * Use the composite index to match both last and firstname.
             */
            Index compound_index = table.getIndex("compoundIndex");

            /*
             * Create and initialize the IndexKey
             */
            IndexKey compositeIndexKey = compound_index.createIndexKey();
            compositeIndexKey.put("firstName", "Bob");
            compositeIndexKey.put("lastName", "Jameson");

            /* Get the matching rows */
            iter = tableAPI.tableIterator(compositeIndexKey, null, null);

            /* Print rows as JSON */
            System.out.println("\nUsers with full name Bob Jameson");
            while(iter.hasNext()) {
                System.out.println(iter.next().toJsonString(true));
            }
            iter.close();
            iter = null;

            /*
             * Use the composite index to match all rows with
             * lastName "Jameson".
             */
            compositeIndexKey = compound_index.createIndexKey();
            compositeIndexKey.put("lastName", "Jameson");

            /* Get the matching rows */
            iter = tableAPI.tableIterator(compositeIndexKey, null, null);

            /* Print rows as JSON */
            System.out.println("\nAll users with last name Jameson");
            while(iter.hasNext()) {
                System.out.println(iter.next().toJsonString(true));
            }
        } finally {
            /*
             * Make sure that the TableIterator is closed.
             */
            if (iter != null) {
                iter.close();
            }
        }
    }

    /**
     * Example 3: Shard keys
     *
     * This example uses a table that has a composite primary key and
     * a defined shard key.  This demonstrates the ability to ensure that
     * rows with the same shard key are stored in the same shard and are
     * therefore accessible in an atomic manner.  Such rows can also be
     * accessed using the various "multi*" API operations.
     */
    private void shardKeys() {

        System.out.println("\nExample: shard key based read");
        Table table = getTable("shardUsers");

        /* 
         * Insert rows into table 
         * The primary key is (lastName, firstName) and the shard key
         * is lastName.
         */
        Row row = table.createRow();
        row.put("firstName", "Alex");
        row.put("lastName", "Robertson");
        row.put("email", "alero@email.com");
        tableAPI.put(row, null, null);

        /*
         * Insert a second row with lastName Robertson.
         * Since the previous row is inserted with the same shard key, this row
         * and the previous are guaranteed to be stored on the same shard.
         */
        row = table.createRow();
        row.put("firstName", "Beatrix");
        row.put("lastName", "Robertson");
        row.put("email", "bero@email.com");
        tableAPI.put(row, null, null);

        /*
         * Insert row with lastName Swanson.
         * Since this row has a different shard key the row may be stored in a
         * different shard.
         */
        row = table.createRow();
        row.put("firstName", "Bob");
        row.put("lastName", "Swanson");
        row.put("email", "bob.swanson@email.com");
        tableAPI.put(row, null, null);

        /* Use a complete shard key to allow use of multiGet() */
        PrimaryKey key = table.createPrimaryKey();

        /* shard key is "lastName" */
        key.put("lastName", "Robertson");

        /*
         * Use the multiget function, to retrieve all the rows with the same
         * shard key.  The tableIterator() API call will also work but may not
         * be atomic.
         */
        List<Row> rows = tableAPI.multiGet(key, null, null);

        /* Print the rows as JSON */
        System.out.println("\nRows with lastName Robertson via multiGet()");
        for (Row r: rows) {
            System.out.println(r.toJsonString(true));
        }
    }

    /**
     * Example 4: Parent and Child tables
     *
     * This example demonstrates use of parent and child tables.  The parent
     * table is the shardUsers table used above and the child table is a
     * table of addresses for a given user, allowing definition of multiple
     * addresses for a user.
     */
    private void useChildTable() {

        System.out.println("\nExample: use of child tables");

        Table parentTable = getTable("shardUsers");
        Table childTable = parentTable.getChildTable("address");

        /*
         * Insert rows into the child table.  Create a parent table
         * record first.
         */
        /* Create a parent (user) row. */
        Row row = parentTable.createRow();
        row.put("firstName", "Robert");
        row.put("lastName", "Johnson");
        row.put("email", "bobbyswan@email.com");
        tableAPI.put(row, null, null);

        /*
         * Create multiple child rows for the same parent.  To do this
         * create a row using the child table but be sure to set the
         * inherited parent table primary key fields (firstName and
         * lastName).
         */
        row = childTable.createRow();

        /* Parent key fields */
        row.put("firstName", "Robert");
        row.put("lastName", "Johnson");
        /* Child key */
        row.put("addressID", 1);
        /* Child data fields */
        row.put("Street", "Street Rd 132");
        row.put("State", "California");
        row.put("ZIP", 90011);
        row.put("addressName", "home");
        tableAPI.putIfAbsent(row, null, null);

        /*
         * Reuse the Row to avoid repeating all fields.  This is safe.
         * This requires a new child key and data.
         */
        row.put("addressID", 2);
        row.put("Street", "Someplace Ave. 162");
        row.put("State", "California");
        row.put("ZIP", 90014);
        row.put("addressName", "work");
        tableAPI.putIfAbsent(row, null, null);

        /*
         * Retrieve rows from the child table.
         * The child table primary key is a concatenation of its parent table's
         * primary key and it's own defined fields.
         */
        PrimaryKey key = childTable.createPrimaryKey();
        key.put("firstName", "Robert");
        key.put("lastName", "Johnson");
        key.put("addressID", 1);
        row = tableAPI.get(key, null);

        System.out.println(row.toJsonString(true));

        /*
         * There is an index on the "addressName" field of the child table.
         * Use that to retrieve all "work" addresses.
         */
        Index index = childTable.getIndex("addressIndex");
        IndexKey indexKey = index.createIndexKey();
        indexKey.put("addressName", "work");

        TableIterator<Row> iter = null;
        try {
            iter =  tableAPI.tableIterator(indexKey, null, null);
            System.out.println("\nAll \"work\" addresses");

            while(iter.hasNext()) {
                row = iter.next();
                System.out.println(row.toJsonString(true));
            }
        } finally {
            if (iter != null) {
                iter.close();
            }
        }
    }

    /**
     * Example 5: Complex fields
     *
     * This example demonstrates how to create, populate and read complex
     * fields (Array, Map, Record) in a table.
     */
    private void complexFields() {

        System.out.println("\nExample: use of complex fields");
        Table table = getTable("complexUsers");

        /*
         * Insert data into complex fields.
         */
        Row row = table.createRow();

        /*
         * The putRecord function creates a RecordValue instance that
         * is then populated with its own fields.
         * The "name" field is a record with 2 fields --
         * firstName and lastName.
         */
        RecordValue recordValue = row.putRecord("name");
        recordValue.put("firstName", "Bob");
        recordValue.put("lastName", "Johnson");

        /*
         * The putArray function returns an ArrayValue instance which
         * is then populated.  In this case the array is an array of
         * String values.
         */
        ArrayValue arrayValue = row.putArray("likes");

        /* use the add() overload that takes an array as input */
        arrayValue.add(new String[]{"sports", "movies"});

        /*
         * The putMap function returns a MapValue instance which is then
         * populated.  In this table the map is a map of String values.
         */
        MapValue mapValue = row.putMap("optionalInformation");
        mapValue.put("email", "bob.johnson@email.com");
        mapValue.put("group", "work");

        /* Insert id */
        row.put("userID", 1);
        tableAPI.putIfAbsent(row, null, null);

        /*
         * Retrieve information from the table, examining the complex
         * fields.
         */
        /* Get row matching the requested primary key. */
        PrimaryKey key = table.createPrimaryKey();
        key.put("userID", 1);
        row = tableAPI.get(key, null);

        /*
         * Read the "name" record field.  Use asRecord() to cast.  If
         * this is done on a field that is not a record an exception is
         * thrown.
         */
        RecordValue record = row.get("name").asRecord();

        /* The RecordValue can be output as JSON */
        System.out.println("\nName record: " + record.toJsonString(false));

        /*
         * Read the "likes" array field.
         * Use the get function to return the field and cast it to
         * ArrayValue using the asArray function.
         */
        ArrayValue array = row.get("likes").asArray();

        /* The ArrayValue can be output as JSON */
        System.out.println("\nlikes array: " + array.toJsonString(false));

        /*
         * Read the map field.
         */
        MapValue map = row.get("optionalInformation").asMap();

        /* The MapValue can be output as JSON */
        System.out.println("\noptionalInformation map: " +
                           map.toJsonString(false));

        /* Print the entire row as JSON */
        System.out.println("\n The full row:\n" + row.toJsonString(true));
    }

    /**
     * Example 6: Secondary index on array
     *
     * This example demonstrates use of a secondary index on an array.
     * An important thing to note about indexes on arrays is that a
     * independent index entry is generated for each value in the array.
     * This can lead to an explosion of entries as well as potential for
     * duplication in results (e.g. if the same array value repeats in
     * the same row).
     *
     * This example uses the same table as the complex field example.
     * There is an index on the "likes" array.
     */
    private void arraySecondaryIndex() {
        System.out.println("\nExample: Secondary index on array");
        final String tableName = "complexUsers";

        Table table = getTable(tableName);

        /*
         * Insert rows into table.
         */
        Row row = table.createRow();
        RecordValue recordValue = row.putRecord("name");
        recordValue.put("firstName", "Joseph");
        recordValue.put("lastName", "Johnson");
        ArrayValue arrayValue = row.putArray("likes");
        arrayValue.add(new String[]{"sports"});
        MapValue mapValue = row.putMap("optionalInformation");
        mapValue.put("email", "jjson@email.com");
        row.put("userID", 2);
        tableAPI.putIfAbsent(row, null, null);

        row = table.createRow();
        recordValue = row.putRecord("name");
        recordValue.put("firstName", "Burt");
        recordValue.put("lastName", "Nova");
        arrayValue = row.putArray("likes");
        arrayValue.add(new String[]{"sports", "movies", "technology"});
        mapValue = row.putMap("optionalInformation");
        row.put("userID", 3);
        tableAPI.putIfAbsent(row, null, null);

        /*
         * Retrieve information using the secondary index
         * Retrieve all the rows with the value "movies" in the "likes" array
         * field.
         */
        Index index = table.getIndex("arrayIndex");

        /*
         * Create an IndexKey to request the information.
         * The IndexKey works similarly to a PrimaryKey but when handling
         * secondary indexes on complex fields like an array use the putArray
         * function to retrieve the ArrayValue and set the value to look for.
         * When used as an index key only one value may be set in the array.
         * If used incorrectly an exception is thrown when the resulting
         * IndexKey is used.
         */
        IndexKey indexKey = index.createIndexKey();
        ArrayValue arrayIndex = indexKey.putArray("likes");
        arrayIndex.add("movies"); /* match "movies" */

        TableIterator<Row> iter = tableAPI.tableIterator(indexKey, null, null);
        System.out.println("\nUsers who \"like\" movies");
        try {
            while (iter.hasNext()) {
                row = iter.next();
                System.out.println(row.toJsonString(true));
            }
            iter.close();
            iter = null;

            /*
             * Do it again with "sports"
             */
            arrayIndex = indexKey.putArray("likes");
            arrayIndex.add("sports");
            iter = tableAPI.tableIterator(indexKey, null, null);
            System.out.println("\nUsers who \"like\" sports");
            while (iter.hasNext()) {
                row = iter.next();
                System.out.println(row.toJsonString(true));
            }
        } finally {
            if (iter != null) {
                iter.close();
            }
        }
    }

    /**
     * Gets the named table.  If it is not present an error is generated.
     */
    private Table getTable(String tableName) {
        Table table = tableAPI.getTable(tableName);
        return table;
    }

    /**
     * Determine cluster parameters
     */
    private void parseArgs(String argv[]) {
        final int nArgs = argv.length;
        int argc = 0;

        while (argc < nArgs) {
            final String thisArg = argv[argc++];

            if (thisArg.equals("-store")) {
                if (argc < nArgs) {
                    storeName = argv[argc++];
                } else {
                    usage("-store requires an argument");
                }
            } else if (thisArg.equals("-host")) {
                if (argc < nArgs) {
                    hostName = argv[argc++];
                } else {
                    usage("-host requires an argument");
                }
            } else if (thisArg.equals("-port")) {
                if (argc < nArgs) {
                    hostPort = argv[argc++];
                } else {
                    usage("-port requires an argument");
                }
            } else if (thisArg.equals("?") || thisArg.equals("help")) {
                usage(null);

            } else {
                usage("Unknown argument: " + thisArg);
            }
        }
    }

    private void usage(String message) {
        if (message != null) {
            System.out.println("\n" + message + "\n");
        }
        System.out.println("usage: " + getClass().getName());
        System.out.println("\ty[-store <instance name>] (default: kvstore)\n" +
                           "\t[-host <host name>] (default: localhost)\n" +
                           "\t[-port <port number>] (default: 5000)");
        System.exit(1);
    }
}
