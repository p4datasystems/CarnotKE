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

package parallelscan;

import java.util.Arrays;
import java.util.List;

import oracle.kv.Direction;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;
import oracle.kv.KeyRange;
import oracle.kv.KeyValueVersion;
import oracle.kv.ParallelScanIterator;
import oracle.kv.StoreIteratorConfig;
import oracle.kv.Value;
import oracle.kv.stats.DetailedMetrics;

/**
 * This is a simple example that demonstrates the Parallel Scan feature of
 * KVStore.storeIterator(). It can load "user" records into a KVStore and then
 * retrieve them using Parallel Scan.
 * <p>
 * Record keys are formatted like this:
 * <code>
 * /user/userNNN
 * </code>
 * and all Values are a fixed zero-filled 1024 byte array.
 * <p>
 * There are command line options to load, scan with a key range, and
 * scan with a client-side user search string (mimicking a SQL "WHERE" clause).
 * <p>
 * Use the -storeIteratorThreads option to specify the number of Parallel Scan
 * threads to use. The default value for this option is 1, which indicates
 * non-Parallel Scan.
 * <p>
 * At the end of each retrieval operation, the number of matching records and
 * the per-shard DetailedMetrics (number of records and scan time for the
 * shard) are shown.
 * <p>
 * Example invocations:
 * <p>
 * To load 50000 records:
 *
 * <code>
 * java -cp ... parallelscan.ParallelScanExample \
 *              -store <store> -host <host> -port <port> \
 *              -load 50000
 * </code>
 * <p>
 * <code>
 * <p>
 * To specify a key range to scan only those users whose user id starts with
 * "1":
 *
 * <code>
 * java -cp ... parallelscan.ParallelScanExample \
 *              -store <store> -host <host> -port <port> \
 *              -startUser 1 -endUser 1
 * </code>
 * <p>
 * To add a client-side filter (similar to a SQL WHERE clause in a full table
 * scan) to find all records with keys containing "99" in the user id:
 *
 * <code>
 * java -cp ... parallelscan.ParallelScanExample \
 *              -store <store> -host <host> -port <port> \
 *              -where 99
 * </code>
 * <p>
 * <code>
 */
public class ParallelScanExample {

    private final KVStore store;

    private int nStoreIteratorThreads = 1;
    private String where = null;
    private int startUser = -1;
    private int endUser = -1;
    private int nToLoad = -1;

    public static void main(final String args[]) {
        try {
            ParallelScanExample runTest =
                new ParallelScanExample(args);
            runTest.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ParallelScanExample(final String[] argv) {

        String storeName = "";
        String hostName = "";
        String hostPort = "";

        final int nArgs = argv.length;
        int argc = 0;

        if (nArgs == 0) {
            usage(null);
        }

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
            } else if (thisArg.equals("-storeIteratorThreads")) {
                if (argc < nArgs) {
                    nStoreIteratorThreads = Integer.parseInt(argv[argc++]);
                } else {
                    usage("-storeIteratorThreads requires an argument");
                }
            } else if (thisArg.equals("-where")) {
                if (argc < nArgs) {
                    where = argv[argc++];
                } else {
                    usage("-where requires an argument");
                }
            } else if (thisArg.equals("-startUser")) {
                if (argc < nArgs) {
                    startUser = Integer.parseInt(argv[argc++]);
                } else {
                    usage("-startUser requires an argument");
                }
            } else if (thisArg.equals("-endUser")) {
                if (argc < nArgs) {
                    endUser = Integer.parseInt(argv[argc++]);
                } else {
                    usage("-endUser requires an argument");
                }
            } else if (thisArg.equals("-load")) {
                if (argc < nArgs) {
                    nToLoad = Integer.parseInt(argv[argc++]);
                } else {
                    usage("-load requires an argument");
                }
            } else {
                usage("Unknown argument: " + thisArg);
            }
        }

        store = KVStoreFactory.getStore
            (new KVStoreConfig(storeName, hostName + ":" + hostPort));
    }

    private void usage(final String message) {
        if (message != null) {
            System.err.println("\n" + message + "\n");
        }

        System.err.println("usage: " + getClass().getName());
        System.err.println
            ("\t-store <instance name>\n" +
             "\t-host <host name>\n" +
             "\t-port <port number>\n" +
	     "\t[-load <# records to load>]\n" +
             "\t[-storeIteratorThreads <number storeIterator threads>\n" +
             "\t\t(default: 1)]\n" +
             "\t[-where <string to search for in key>]\n" +
             "\t[-startUser <startUser#>]\n" +
             "\t[-endUser <endUser#>]\n");
        System.exit(1);
    }

    private void run() {
        try {
	    doRun();
        } finally {
            store.close();
        }
    }

    private void doRun() {
        try {
	    if (nToLoad < 0) {
		doStoreIteration();
	    } else {
		doLoad();
	    }
        } catch (Exception e) {
            System.err.println
                ("storeIteratorThread caught: " + e);
            e.printStackTrace();
        }
    }

    private void doLoad() {
        final Value dummyData = Value.createValue(new byte[1024]);
        for (int i = 0; i < nToLoad; i++) {
            final String uid = "user" + i;
            store.put(Key.createKey(Arrays.asList("user", uid)), dummyData);
        }
    }

    private void doStoreIteration() {
        final StoreIteratorConfig storeIteratorConfig =
            new StoreIteratorConfig().
            setMaxConcurrentRequests(nStoreIteratorThreads);

        Key useParent = null;
        KeyRange useSubRange = null;
        if (startUser > 0 || endUser > 0) {
            useParent = Key.createKey("user");
            useSubRange =
                new KeyRange((startUser > 0 ? ("user" + startUser) : null),
                             true,
                             (endUser > 0 ? ("user" + endUser) : null),
                             true);
        }

        final long start = System.currentTimeMillis();
        final ParallelScanIterator<KeyValueVersion> iter =
            store.storeIterator(Direction.UNORDERED, 0 /* batchSize */,
                                useParent,
                                useSubRange,
                                null, /* depth */
                                null, /* consistency */
                                0 /* timeout */,
                                null,
                                storeIteratorConfig);

        /* Key format: "/user/userNNN/-/ */
        int cnt = 0;
        try {
            while (iter.hasNext()) {
                final KeyValueVersion kvv = iter.next();
                final List<String> majorKeys = kvv.getKey().getMajorPath();
                final String userId = majorKeys.get(1);
                if (where == null) {
                    cnt++;
                } else {
                    if (userId.indexOf(where) > 0) {
                        cnt++;
                    }
                }
            }
        } finally {
            iter.close();
        }

        final long end = System.currentTimeMillis();

        System.out.println(cnt + " records found in " +
                           (end - start) + " milliseconds.");

        final List<DetailedMetrics> shardMetrics = iter.getShardMetrics();
        for (DetailedMetrics dmi : shardMetrics) {
            System.out.println(dmi);
        }
    }
}
