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

package coherence;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.BinaryEntryStore;

import java.util.Arrays;

import oracle.kv.Key;
import oracle.kv.coherence.NoSQLBinaryStore;

/**
 * This is a simple Oracle NoSQL Database client application that demonstrates
 * an integration of an existing Oracle Coherence application with Oracle NoSQL
 * Database. This example uses the {@link NoSQLBinaryStore} module, which is a
 * pre-built implementation of the Oracle Coherence {@link BinaryEntryStore}
 * interface to manage interaction with an Oracle NoSQL Database instance.
 * <p>
 * Objects that are manipulated by an Oracle Coherence application need to be
 * serialized when moving them between the application space and the Oracle
 * Coherence cache, as well as between the Oracle Coherence Cache and a
 * {@link BinaryEntryStore} implementation. Oracle Coherence supports a variety
 * of mechanisms for doing this, including multiple native Java Serialization
 * techniques, multiple Oacle Coherence Portable Object Format (POF) techniques,
 * and the ability to use user-provided serialization implementations. This
 * example assumes that an application exists with a working Serialization
 * scheme in place. This example provides POF serialization for the
 * {@link Person} implementation. Although we encourage users to migrate to an
 * Avro schema for object storage format, the BinaryEntryStore implementation
 * will leverage the existing application object serialization to allow for the
 * integration to be implemented with minimal impact to the Oracle Coherence
 * application code.
 * <p>
 * Because this example is using the {@link NoSQLBinaryStore} module to interact
 * with the Oracle NoSQL Database, instance it does not need to directly use
 * Oracle NoSQL Database classes in order allow the Oracle NoSQL Database to
 * provide persistent backing for an Oracle Coherence cache. The
 * {@link NoSQLBinaryStore} module relies on application-controlled
 * serialization.
 * <p>
 * The cache-config.xml file in this directory includes a definition for the
 * {@code BinaryCache} cache. It is assumed that the reader has some level of
 * familiarity with Oracle Coherence cache configuration. The
 * {@code BinaryCache} has a {@code cache-mapping} that references a
 * {@code distributed-scheme} named {@code BinaryCacheScheme}. Note that
 * although Oracle Coherence allows for a variety of different cache schemes,
 * {@link BinaryEntryStore} is specifically designed to work within distributed
 * cache schemes.
 * <p>
 * Please note that this example is structured, for simplicity, to assume that
 * the example program is the only member of the cache cluster. In a real
 * environment it will be necessary to have all cache cluster members using the
 * same cache configuration file.
 * <p>
 * The distributed scheme contains a serializer definition that tells Oracle
 * Coherence to use the Oracle Coherence Portable Object Format when serializing
 * and de-serializing objects in association with this cache. Its configuration
 * relies on a POF configuration file to control the type mapping for the
 * application.
 * <p>
 * The {@code distributed-scheme} also contains the definition of a
 * {@code cachestore-scheme} within a {@code read-write-backing-map-scheme}.
 * Here, the definition specifies {@code store-name} and {@code helper-hosts}
 * parameters to allow access to the Oracle NoSQL Database instance, in
 * association with this cache. Its configuration includes {@code <kv:XXX>}
 * style definitions, where {@code XXX} are:
 * <ul>
 * <li>
 * {@code store-name} - identifies the Oracle NoSQL Database instance name that
 * will be backing the cache.</li>
 * <li>
 * {@code helper-hosts} - contains a comma-separated list of host:port
 * identifiers for hosts to contact in order to set up a connection to the
 * Oracle NoSQL Database instance.</li>
 * </ul>
 * <p>
 * In addition to the kvclient.jar file, this application also requires that
 * kvcoherence.jar and coherence.jar be included in the classpath.
 * kvcoherence.jar is included in the same directory as kvclient.jar for Oracle
 * NoSQL Database EE distributions. The kvcoherence.jar file internally
 * references kvclient.jar so it does not need to be explicitly referenced in
 * the classpath. The coherence.jar file should be downloaded and installed
 * separately.
 * <p>
 * To build this example in the examples/coherence directory:
 * 
 * <pre>
 *   cd KVHOME/examples/coherence
 *   javac -cp KVHOME/lib/kvcoherence.jar:COHERENCE_HOME/lib/coherence.jar \
 *         -d classes *.java
 * </pre>
 * <p>
 * Before running this example program, start an Oracle NoSQL Database instance.
 * The simplest way to do that is to run kvlite as described in the Quickstart
 * document.
 * <p>
 * Before running the example, modify the coherence-cache.xml file, if
 * necessary, and update the definitions of the storeName and helperHosts
 * parameters with the appropriate values based on your kvstore instance. For
 * all examples the default instance name is kvstore, the default host name is
 * localhost and the default port number is 5000. Then, run this program, as
 * follows:
 * 
 * <pre>
 * java -cp classes:KVHOME/lib/kvcoherence.jar:COHERENCE_HOME/lib/coherence.jar \
 *         -Dtangosol.pof.config=pof-config.xml \
 *         -Dtangosol.coherence.cacheconfig=cache-config.xml \
 *         coherence.BinaryStoreExample -cache BinaryCache
 * </pre>
 * <p>
 * In this example a single key is used for storing a kv pair, where the value
 * is an object serialized using Portable Object Format encoding. The first time
 * the example is run it inserts the kv pair, and for subsequent executions, it
 * reads and updates the kv pair, incrementing the "age" field.
 */
public class BinaryStoreExample {

    private final NamedCache cache;

    /**
     * Runs the BinaryStoreExample command line program.
     */
    public static void main(String args[]) {
        try {
            BinaryStoreExample example = new BinaryStoreExample(args);
            example.runExample();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the command line args, opens the cache.
     */
    BinaryStoreExample(String[] argv) {

        String cacheName = "BinaryCache";

        final int nArgs = argv.length;
        int argc = 0;

        while (argc < nArgs) {
            final String thisArg = argv[argc++];

            if (thisArg.equals("-cache")) {
                if (argc < nArgs) {
                    cacheName = argv[argc++];
                } else {
                    usage("-cache requires an argument");
                }
            } else {
                usage("Unknown argument: " + thisArg);
            }
        }

        /* Open the Cache. */
        CacheFactory.ensureCluster();
        cache = CacheFactory.getCache(cacheName);
    }

    private void usage(String message) {
        System.out.println("\n" + message + "\n");
        System.out.println("usage: " + getClass().getName());
        System.out.println("\t-cache <cache name> (default: BinaryCache)");
        System.exit(1);
    }

    /**
     * Insert a kv pair if it doesn't exist, or read/update it if it does.
     */
    void runExample() {

        /* Use key "/p/pof/0000000001" to store the person object. */
        final Key key = Key.createKey(Arrays.asList("p", "pof", "0000000001"));

        /* Read the value we previous stored, if any. */
        final Object value = cache.get(key);
        final Person person;
        final int age;
        if (value != null) {

            person = (Person) value;

            /* Print it as a JSON string. */
            System.out.println("INITIAL VALUE:\n" + person.toString());

            /* Increment age field. */
            final int oldAge = person.getAge();
            age = oldAge + 1;
            person.setAge(age);

        } else {
            System.out.println("NO INITIAL VALUE");

            /* Create a fresh Person object. */
            person = createPerson();
            age = person.getAge();
        }

        /* insert/update the Person object in the cache */
        cache.put(key, person);

        /* Read it again to confirm that it was stored. */
        final Person person2 = (Person) cache.get(key);

        /* Check for expected age. */
        final int age2 = person2.getAge();
        if (age2 != age) {
            throw new RuntimeException("Expected: " + age +
                                       " but got: " + age2);
        }

        /* Print object as a JSON string. */
        System.out.println("FINAL VALUE:\n" + person2.toString());

        CacheFactory.releaseCache(cache);
    }

    /**
     * Uses the generated classes to create a Person instance that conforms
     * to the Person schema.
     */
    private Person createPerson() {

        final Person person = new Person();
        person.setFirstname("Percival");
        person.setLastname("Lowell");
        person.setPhone("650-506-7000");
        person.setAge(36);

        return person;
    }
}
