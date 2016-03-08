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

import java.util.Random;

import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.table.FieldRange;
import oracle.kv.table.Index;
import oracle.kv.table.IndexKey;
import oracle.kv.table.KeyPair;
import oracle.kv.table.MapValue;
import oracle.kv.table.Row;
import oracle.kv.table.Table;
import oracle.kv.table.TableAPI;
import oracle.kv.table.TableIterator;

/**
 * This is an example of using map indexes.  It creates a simple table with a
 * map of properties field.  An index is created on that map.  The example
 * demonstrates how to create and use such an index.  The index is on the
 * map key plus the map values.  This 2-component index allows the following
 * index scan operations:
 * <ol>
 * <li>Specific key plus specific value
 * <li>Specific key plus range of values
 * <li>Range of keys
 * </ol>
 * It is not possible to search a range of values without a specific key
 * because of the way multi-component indexes are stored.
 *<p>
 * The map information used is artificial and comprises a small set of
 * keys that are known.  Using a map allows addition or removal of a new
 * key string without schema evolution, which is an advantage over using a
 * fixed record instead.
 *<p>
 * To build (from the examples/table directory):
 *<pre>
 *   javac -d . -cp .:&lt;path-to-kvclient.jar&gt; MapIndexExample.java
 *</pre>
 *<p>
 * To run (a KVLite or other store instance must be running on the
 * specified host and port, under the store name.  All parameters are required.
 *<pre>
 *   java -cp .:&lt;path-to-kvclient.jar&gt; table.MapIndexExample \
 *     -host &lt;hostName&gt; \
 *     -port &lt;port&gt; \
 *     -store &lt;storeName&gt;
 *     [-clean]
 *</pre>
 * If -clean is specified the example will remove any existing table (and data)
 * before and after it is run.
 *
 */
public class MapIndexExample {

    private final KVStore store;
    private final TableAPI tableAPI;
    private final boolean clean;
    private Table table = null;

    /*
     * DDL statement to create the table named Properties
     */
    private static final String CREATE_DDL = "CREATE TABLE IF NOT EXISTS " +
        "Properties(id INTEGER, PRIMARY KEY(id), properties MAP(DOUBLE))";

    /*
     * DDL statement to clean data
     */
    private static final String CLEAN_DDL = "DROP TABLE IF EXISTS Properties";

    /*
     * DDL statement to create the index PropertyIndex on table, Properties
     */
    private static final String INDEX_DDL = "CREATE INDEX IF NOT EXISTS " +
        "PropertyIndex ON Properties(KEYOF(properties), ELEMENTOF(properties))";

    /*
     * The number of rows to use in the example.  A small number is used to
     * limit the output.
     */
    private static final int NUM_ROWS = 50;

    /*
     * A static array of key strings for the properties.  If a set of
     * strings is truly fixed it is better to use a record than a map, as
     * it is more type-safe and storage is more efficient.
     * The values for each key are generated randomly.
     */
    private static final String keys[] = {
        "height", "width", "length", "volume", "area",
        "circumference", "radius", "diameter", "weight"
    };

    private MapIndexExample(String hostName, int port, String storeName,
                            boolean clean) {
        this.clean = clean;

        String hostPort = hostName + ":" + port;

        /* Connect to the NoSQL store and get a TableAPI instance */
        store = KVStoreFactory.getStore
            (new KVStoreConfig(storeName, hostPort));
        tableAPI = store.getTableAPI();
        if (clean) {
            cleanTables();
        }
    }

    /**
     * Closes the store.
     */
    private void close() {
        if (clean) {
            cleanTables();
        }
        store.close();
    }

    private void run() {
        initialize();
        populate();
        query();
        close();
    }

    private void cleanTables() {
        System.out.println("Removing tables and data...");
        store.executeSync(CLEAN_DDL);
    }

    /*
     * Do some index operations.
     */
    private void query() {
        Index index = table.getIndex("PropertyIndex");

        /*
         * Find a specify key + value in the index.  To do this,
         * 1. create an IndexKey
         * 2. put a map into the IndexKey to hold the values that will be used
         * for the search.
         * 3. put the desired key and/or value into the map.
         *
         * IMPORTANT: When specifying both a key and a value for a map index
         * that has both fields they MUST be specified individually using
         *   map.putNull(keyString) for the key and
         *   map.put(MapValue.ANONYMOUS, value) for the value.
         * You MUST NOT do this, which may be the obvious thing:
         *   map.put(keyString, value)
         * This will not throw an exception, but will only match based on the
         * keyString.
         */

        IndexKey ikey = index.createIndexKey();
        MapValue map = ikey.putMap("properties");
        /*
         * Use "height" as the key, and 1.0 as the value.  Given that the
         * values are generated randomly this will rarely yield a result.
         * Per the note above, these two fields are specified independently
         * in the IndexKey.
         */
        map.putNull("height");
        map.put(MapValue.ANONYMOUS, 1.0);

        /*
         * Iterate using default values.  A key iterator is used as these
         * are more efficient.  They do not have to read the Row value.  If
         * fields from the row that are not indexed or part of the primary
         * key are desired then a row iterator should be used.  In this example
         * there are no such fields.
         */
        TableIterator<KeyPair> iter =
            tableAPI.tableKeysIterator(ikey, null, null);
        displayResults(iter, "height",
                       "Results for key \"height\" and value 1.0");
        iter.close();

        /*
         * Now, match all rows that have some key "weight".  This will match
         * all rows because they all have this key string in the map.
         */
        map = ikey.putMap("properties");
        map.putNull("weight");
        iter = tableAPI.tableKeysIterator(ikey, null, null);
        displayResults(iter, "weight", "Results for key \"weight\"");
        iter.close();

        /*
         * This time, create a range of values for the key "width".  Depending
         * on the random generation multiple (or no) rows may match.
         * Use Index.createMapValueFieldRange() to create a range specific to
         * map values.
         */
        FieldRange range = index.createMapValueFieldRange("properties", null);
        range.setStart(10.0, true); /* inclusive */
        range.setEnd(100.0, true); /* inclusive */

        /*
         * Refresh the map to add just the key field.
         */
        map = ikey.putMap("properties");
        map.putNull("width");

        iter = tableAPI.tableKeysIterator(ikey, range.createMultiRowOptions(),
                                      null);
        displayResults(iter, "width",
                       "Results for key \"width\", value 10 - 100");
        iter.close();
    }

    /*
     * Displays a truncated version of each row in the result set.  Display
     * includes:
     * 1.  the row's id (primary key)
     * 2.  the value of the map entry from the index.  This will be found in
     * the IndexKey's map under the special key, MapValue.ANONYMOUS.
     *
     * Note that the values will be sorted based on the value, because index
     * scans are sorted by default.
     */
    private void displayResults(TableIterator<KeyPair> iter,
                                String key, String desc) {
        System.out.println(desc);
        if (!iter.hasNext()) {
            System.out.println("\tNo matching entries (this is expected)");
            return;
        }
        while (iter.hasNext()) {
            KeyPair pair = iter.next();
            String id = pair.getPrimaryKey().get("id").toString();
            String val = pair.getIndexKey().get("properties")
                .asMap().get(MapValue.ANONYMOUS).toString();
            System.out.println("\tid: " + id + ", " + key + " : " + val);
        }
    }

    /*
     * Populates the table with some data.  The use of putIfAbsent() will
     * cause this code to not overwrite existing rows with the same primary
     * key.
     */
    private void populate() {
        Random random = new Random();
        final double start = 0.0;
        final double end = 1000.0;
        for (int i = 0; i < NUM_ROWS; i++) {
            Row row = table.createRow();
            row.put("id", i); /* use the index as the primary key */
            MapValue map = row.putMap("properties");

            /* generate random double values for values */
            for (String key : keys) {
                double value = start + (random.nextDouble() * (end - start));
                map.put(key, value);
            }

            /*
             * Use default options, putIfAbsent.
             */
            tableAPI.putIfAbsent(row, null, null);
        }
    }

    /*
     * Creates the table and index if not already done.
     */
    private void initialize() {
        store.executeSync(CREATE_DDL);
        store.executeSync(INDEX_DDL);
        /* this call talks to a server so only do it once */
        table = tableAPI.getTable("Properties");
    }

    public static void main(String args[]) {
        String storeName = null;
        String hostName = null;
        int port = 0;
        final int nArgs = args.length;
        int argc = 0;
        boolean clean = false;

        while (argc < nArgs) {
            final String thisArg = args[argc++];
            if (thisArg.equals("-store")) {
                storeName = nextArg("-store", argc++, args);
            } else if (thisArg.equals("-host")) {
                hostName = nextArg("-host", argc++, args);
            } else if (thisArg.equals("-port")) {
                String portString = nextArg("-port", argc++, args);
                port = Integer.parseInt(portString);
            } else if (thisArg.equals("?") || thisArg.equals("help")) {
                usage(null);
            } else if (thisArg.equals("-clean")) {
                clean = true;
            } else {
                usage("Unknown argument: " + thisArg);
            }
        }

        if (storeName == null || hostName == null || port == 0) {
            usage("Missing required argument");
        }

        try {
            MapIndexExample example =
                new MapIndexExample(hostName, port, storeName, clean);
            example.run();
        } catch (RuntimeException re) {
            System.err.println("Exception running the example: " +
                               re.getMessage());
        }
    }


    private static String nextArg(String flag, int argc, String[] args) {
        if (argc >= args.length) {
            usage(flag + " requires an argument");
        }
        return args[argc];
    }

    private static void usage(String message) {
        if (message != null) {
            System.out.println("\n" + message + "\n");
        }
        System.out.println("Usage: MapIndexExample\n" +
                           "\t-host <hostName>\n" +
                           "\t-port <port>\n" +
                           "\t-store <storeName>\n" +
                           "\t[-clean]");
        System.exit(1);
    }
}
