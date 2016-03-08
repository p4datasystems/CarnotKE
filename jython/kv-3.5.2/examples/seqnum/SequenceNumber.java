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

package seqnum;

import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;
import oracle.kv.Value;
import oracle.kv.ValueVersion;
import oracle.kv.Version;

import java.lang.Long;
import java.nio.ByteBuffer;

/**
 * A simple implementation of a Sequence Number Generator. Numbers are
 * unique and monotonically incremented in the domain determined by key.
 *
 * This is a simple implementation intended to demonstrate the concept. It may
 * not scale well in the face of a large number of concurrent accesses.
 *
 * Use the KVStore instance name, host and port for running this program:
 *
 * <pre>
 * java seqnum.SequenceNumber -store &lt;instance name&gt; \
 *                            -host  &lt;host name&gt;     \
 *                            -port  &lt;port number&gt;
 * </pre>
 *
 * For all examples the default instance name is kvstore, the default host name
 * is localhost and the default port number is 5000.  These defaults match the
 * defaults for running kvlite, so the simplest way to run the examples along
 * with kvlite is to omit all parameters.
 */
public class SequenceNumber {
    private final KVStore store;
    private Key key;
    private int noOfRetries;
    private Version lastKnownVersion;
    private long sequenceNumber;

    /**
     * Creates an instance of the sequence number generator.
     *
     * @param store The store name.
     * @param key The key determining the domain of the sequence
     * @param noOfRetries The number of attempts to synchronize with the store
     * @param initialValue The starting value of the sequence (the first
     *                     value returned will be an increment of this one.
     */
    public SequenceNumber(KVStore store,
                          Key key,
                          int noOfRetries,
                          long initialValue) {
        this.store = store;
        this.key = key;
        this.noOfRetries = noOfRetries;

        /* Save the initialValue in the store. */
        lastKnownVersion = store.putIfAbsent(key, writeLong(initialValue));
        if (lastKnownVersion == null) {
            /* If value already present get the last sequence number. */
            ValueVersion valueVersion = store.get(key);
            sequenceNumber = readLong(valueVersion.getValue());
            lastKnownVersion = valueVersion.getVersion();
        }
    }

    /**
     * Returns the next number in the sequence after it synchronizes with the
     * store, making a maximum of noOfRetries atempts.<br/>
     *
     * There are three possible outcomes when calling this method:<br/>
     *  - the next number in the sequence is returned.<br/>
     *  - the maximum number of retries is reached for trying to
     *  synchronize with the store and a RuntimeException is thrown<br/>
     *  - other exception if thrown by the store <br/>
     * @return the next number in the sequence
     * @throws java.lang.RuntimeException, oracle.kv.DurabilityException
     *      oracle.kv.RequestTimeoutException oracle.kv.FaultException
     *      oracle.kv.ConsistencyException
     */
    public long incrementAndGet() {
        for (int i = 0; i < noOfRetries; i++) {
            sequenceNumber++;

            /* Try to put the next sequence number with the lastKnownVersion. */
            Version newVersion =
                store.putIfVersion(key, writeLong(sequenceNumber),
                                   lastKnownVersion);
            if (newVersion == null) {
                /* Put was unsuccessful get the one in the store. */
                ValueVersion valueVersion = store.get(key);
                sequenceNumber = readLong(valueVersion.getValue());
                lastKnownVersion = valueVersion.getVersion();
            } else {
                /* Put was successful. */
                lastKnownVersion = newVersion;
                return sequenceNumber;
            }
        }

        throw new RuntimeException("Reached maximum number of retries.");
    }

    private long readLong(Value value) {
        return ByteBuffer.wrap(value.getValue()).getLong();
    }

    private Value writeLong(long v) {
        return Value.createValue
            (ByteBuffer.allocate(Long.SIZE / 8).putLong(v).array());
    }

    /* Main entry point when running the example */
    public static void main(String[] args) {
        String storeName = "kvstore";
        String hostName = "localhost";
        String hostPort = "5000";

        final int nArgs = args.length;
        int argc = 0;

        while (argc < nArgs) {
            final String thisArg = args[argc++];

            if (thisArg.equals("-store")) {
                if (argc < nArgs) {
                    storeName = args[argc++];
                } else {
                    usage("-store requires an argument");
                }
            } else if (thisArg.equals("-host")) {
                if (argc < nArgs) {
                    hostName = args[argc++];
                } else {
                    usage("-host requires an argument");
                }
            } else if (thisArg.equals("-port")) {
                if (argc < nArgs) {
                    hostPort = args[argc++];
                } else {
                    usage("-port requires an argument");
                }
            } else {
                usage("Unknown argument: " + thisArg);
            }
        }

        /* Connect to the store. */
        KVStore store = KVStoreFactory.getStore
            (new KVStoreConfig(storeName, hostName + ":" + hostPort));

        /* Initialize the generator, use the key for the sequence namespace. */
        SequenceNumber sn =
            new SequenceNumber(store, Key.createKey
                ("sequenceNumbers", "example"), 5, 0);
        System.out.println("Create Sequence Number Generator");

        /* Get a new number. */
        long firstSequenceNumber = sn.incrementAndGet();
        System.out.println(firstSequenceNumber);

        /* ... and another one... */
        long secondSequenceNumber = sn.incrementAndGet();
        System.out.println(secondSequenceNumber);

        /* ... and a few more. */
        for (int i = 0; i < 8; i++) {
            long l = sn.incrementAndGet();
            System.out.println(l);
        }
    }

    private static void usage(String message) {
        System.out.println("\n" + message + "\n");
        System.out.println("usage: SequenceNumber");
        System.out.println("\t-store <instance name> (default: kvstore)\n" +
            "\t-host <host name> (default: localhost)\n" +
            "\t-port <port number> (default: 5000)\n");
        System.exit(1);
    }
}
