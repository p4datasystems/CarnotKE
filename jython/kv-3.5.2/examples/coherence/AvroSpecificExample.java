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
import com.tangosol.net.cache.CacheStore;

import java.util.Arrays;

import oracle.kv.Key;
import oracle.kv.coherence.NoSQLAvroCacheStore;
import oracle.kv.coherence.NoSQLAvroSerializer;

import org.apache.avro.specific.SpecificRecord;

/**
 * This is a simple Oracle NoSQL Database client application that demonstrates
 * an integration of an Avro Schema-based Oracle NoSQL Database application with
 * Oracle Coherence. This example uses the {@link NoSQLAvroCacheStore} module,
 * which is a pre-built implementation of the Oracle Coherence
 * {@link CacheStore} interface to manage interaction with an Oracle NoSQL
 * Database instance.
 * <p>
 * Objects that are manipulated by an Oracle Coherence application need to be
 * serialized when moving them between the application space and the Oracle
 * Coherence cache, as well as between the Oracle Coherence cache and a
 * {@link CacheStore} implementation. Oracle Coherence supports a variety of
 * mechanisms for doing this, including multiple native Java Serialization
 * techniques, multiple Oracle Coherence Portable Object Format techniques, and
 * the ability to use user-provided serialization implementations. This example
 * demonstrates the use of the {@link NoSQLAvroSerializer} implementation for
 * object serialization. This mechanism uses the fast and compact Avro
 * serialization format to enable applications to be quickly moved to Oracle
 * Coherence.
 * <p>
 * Because this example assumes that an application is built using Avro schemas,
 * the reader should already be familiar with Avro Schemas. It is suggested that
 * you review the examples in the avro example directory if you are not yet
 * familiar with the use of Avro schemas.
 * <p>
 * As with the SpecificExample program from the avro example directory, this
 * examples uses an Ant build file to generate application classes from the
 * application's Avro schema. In this case, we use person.avsc to define the
 * content of a Person class.
 * <p>
 * An Ant build file was used to generate the specific class source file:
 * Person.java. The generate-specific.xml file can be found in the avro example
 * directory, along with directions for use.
 * <p>
 * Because this example is using the {@link NoSQLAvroCacheStore} module to
 * interact with the Oracle NoSQL Database, it does not need to directly use
 * SpecificAvroBinding objects. Instead, the application deals only with the
 * generated Person class. SpecificAvroBinding objects are used under the covers
 * by the {@link NoSQLAvroCacheStore} and {@link NoSQLAvroSerializer} classes to
 * perform the object serialization on behalf of the user.
 * <p>
 * The cache-config.xml file in this directory includes a definition for the
 * AvroCache cache. It is assumed that the reader has some level of familiarity
 * with Oracle Coherence cache configuration. The AvroCache has a cache-mapping
 * that references a distributed-scheme named AvroCacheScheme. Note that,
 * although Oracle Coherence allows for a variety of different cache schemes,
 * {@link CacheStore} is specifically designed to work within distributed cache
 * schemes.
 * <p>
 * Please note that this example is structured, for simplicity, to assume that
 * the example program is the only member of the cache cluster. In a real
 * environment it will be necessary to have all cache cluster members using the
 * same cache configuration file.
 * <p>
 * The distributed scheme contains a serializer definition that tells Oracle
 * Coherence to use the NoSQLAvroSerializer when serializing and de-serializing
 * objects in association with this cache. Its configuration includes init-param
 * definitions for:
 * <ul>
 * <li>
 * {@code storeName} - identifies the Oracle NoSQL Database instance name which
 * which the serializer is interacting.</li>
 * <li>
 * {@code helperHosts} - contains a comma-separated list of host:port
 * identifiers for hosts to contact in order to set up a connection to the
 * Oracle NoSQL Database instance.</li>
 * <li>
 * {@code schemaFiles} - contains a comma-separated list of schema file
 * resources that may be needed. This is needed for a cache application that may
 * operate on Json or Generic format object representations. Because this
 * example is using Specific representation, it is not actually needed, but is
 * included here in order to allow conversion to an alternate format.</li>
 * </ul>
 * The distributed-scheme also contains the definition of a cachestore-scheme
 * within a read-write-backing-map-scheme. Here, the definition again specifies
 * store-name and helper-hosts parameters to allow access to the KVStore
 * instance, but also defines an avroFormat parameter with the value
 * {@code SPECIFIC}. This setting tells the {@link NoSQLAvroCacheStore} module
 * that when an Avro-based object is loaded from the database, it should be
 * rendered in {@link SpecificRecord} format. This means that the classpath must
 * contain a compiled class for each such Avro type that will be accessed by the
 * cache application.
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
 *   mkdir classes
 *   javac -cp KVHOME/lib/kvcoherence.jar:COHERENCE_HOME/lib/coherence.jar \
 *       -d classes *.java
 * </pre>
 * <p>
 * The Person.java class in this directory was generated using the
 * generate-specific.xml ant build script found in the avro example directory.
 * It is useful to look at these files to understand how they are used, but they
 * should not be modified directly.
 * <p>
 * Before running this example program, start a KVStore instance. The simplest
 * way to do that is to run kvlite as described in the Quickstart document.
 * <p>
 * After starting the KVStore instance, the Avro schema used by the example must
 * be added to the store using the administration command line interface (CLI).
 * First start the admin CLI as described in the Oracle NoSQL Database
 * Administrator's Guide. Then enter the following command to add the example
 * schema:
 * 
 * <pre>
 * ddl add-schema -file person-schema.avsc
 * </pre>
 * 
 * After adding the schema, modify the coherence-cache.xml file, if necessary,
 * and update the defintions of the storeName and helperHosts parameters with
 * the appropriate values based on your kvstore instance. For all examples the
 * default instance name is kvstore, the default host name is localhost and the
 * default port number is 5000. Then, run this program, as follows:
 * 
 * <pre>
 * java -cp classes:KVHOME/lib/kvcoherence.jar:COHERENCE_HOME/lib/coherence.jar:.. \
 *       -Dtangosol.pof.config=pof-config.xml \
 *       -Dtangosol.coherence.cacheconfig=cache-config.xml \
 *       coherence.AvroSpecificExample -cache AvroCache
 * </pre>
 * 
 * <p>
 * In this example a single key is used for storing a kv pair, where the value
 * is an object serialized as Avro binary data. The first time the example is
 * run it inserts the kv pair, and subsequent times that it is run it reads and
 * updates the kv pair, incrementing the "age" field.
 */
public class AvroSpecificExample {

    private final NamedCache cache;

    /**
     * Runs the AvroSpecificExample command line program.
     */
    public static void main(String args[]) {
        try {
            AvroSpecificExample example = new AvroSpecificExample(args);
            example.runExample();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the command line args, opens the cache.
     */
    AvroSpecificExample(String[] argv) {

        String cacheName = "AvroCache";

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
        System.out.println("\t-cache <cache name> (default: AvroCache)");
        System.exit(1);
    }

    /**
     * Insert a kv pair if it doesn't exist, or read/update it if it does.
     */
    void runExample() {

        /* Use key "/p/avro/0000000001" to store the person object. */
        final Key key = Key.createKey(Arrays.asList("p", "avro", "0000000001"));

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
