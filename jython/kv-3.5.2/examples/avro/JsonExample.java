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

package avro;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;
import oracle.kv.ValueVersion;
import oracle.kv.avro.AvroCatalog;
import oracle.kv.avro.JsonAvroBinding;
import oracle.kv.avro.JsonRecord;

import org.apache.avro.Schema;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

/**
 * A simple KVStore client application that represents a {@code Value} as a
 * {@link JsonNode} object using the Jackson API, and serializes values using a
 * {@link JsonAvroBinding}.
 * <p>
 * When using an Avro binding, the Avro and Jackson jars must be in the
 * classpath, as well as the kvclient jar.  The Avro and Jackson jars are
 * included in the KVHOME/lib directory along with the kvclient jar:
 * <pre>
 *    kvclient.jar avro.jar jackson-core-asl.jar jackson-mapper-asl.jar
 * </pre>
 * As long as all four jars are in the same directory, only the kvclient jar
 * needs to be specified in the classpath, because the kvclient jar references
 * the other three jars.  If they are not in the same directory, all four
 * jars must be explicitly specified in the classpath.
 * <p>
 * To build this example in the examples/avro directory:
 * <pre>
 *   cd KVHOME/examples/avro
 *   mkdir classes
 *   javac -cp KVHOME/lib/kvclient.jar -d classes *.java
 * </pre>
 * <p>
 * Before running this example program, start a KVStore instance.  The simplest
 * way to do that is to run KV Lite as described in the Quickstart document.
 * <p>
 * After starting the KVStore instance, the Avro schema used by the example
 * must be added to the store using the administration command line interface
 * (CLI).  First start the admin CLI as described in the Oracle NoSQL Database
 * Administrator's Guide. Then enter the following command to add the example
 * schema:
 *  <pre>ddl add-schema -file member-schemas.avsc</pre>
 *
 * After adding the schema, use the KVStore instance name, host and port for
 * running this program, as follows:
 *
 * <pre>
 * java -cp classes:KVHOME/lib/kvclient.jar avro.JsonExample \
 *                       -store &lt;instance name&gt; \
 *                       -host  &lt;host name&gt;     \
 *                       -port  &lt;port number&gt;
 * </pre>
 *
 * For all examples the default instance name is kvstore, the default host name
 * is localhost and the default port number is 5000.  These defaults match the
 * defaults for running kvlite, so the simplest way to run the examples along
 * with kvlite is to omit all parameters.
 * <p>
 * In this example a single key is used for storing a kv pair, where the value
 * is an object serialized as Avro binary data.  The first time the example is
 * run it inserts the kv pair, and subsequent times that it is run it reads and
 * updates the kv pair, incrementing the "age" field.
 * <p>
 * This example may also be used to demonstrate simple schema evolution by
 * performing the following steps:
 * <ol>
 *   <li>
 *   Build and run the example as described above.
 *   </li>
 *   <li>
 *   Change the FullName schema in member-schemas.avsc to add the "middle"
 *   field, including a default value:
 *   <pre>
 *      "fields": [
 *          {"name": "first", "type": "string", "default": ""},
 *          {"name": "middle", "type": "string", "default": ""},
 *          {"name": "last", "type": "string", "default": ""}
 *      ]
 *   </pre>
 *   </li>
 *   <li>
 *   Uncomment the two lines in the example that set the middle name field.  In
 *   other words, change:
 *   <pre>
 *          //final ObjectNode name = (ObjectNode) member.get("name");
 *          //name.put("middle", "Lawrence");
 *   </pre>
 *   to:
 *   <pre>
 *          final ObjectNode name = (ObjectNode) member.get("name");
 *          name.put("middle", "Lawrence");
 *   </pre>
 *   </li>
 *   <li>
 *   Build and run the modified example as before.  The -evolve option must be
 *   used with the add-schema command as follows:
 *   <pre>ddl add-schema -file member-schemas.avsc -evolve</pre>
 *   </li>
 * </ol>
 * <p>
 * When the example is run after being changed, the initial value that is read
 * (which was stored with the old version of the schema) will be displayed with
 * a middle name field with an empty string value.  This is because, when Avro
 * deserializes a stored value that has no middle name field, it adds the
 * middle name field because it is present in the reader schema and it assigns
 * it the default value for that field.  A default value is required whenever a
 * new field is added.
 * <p>
 * The updated example will also set the middle name to "Lawrence" when it
 * increments the age field and stores the updated value.  So the final value
 * displayed will include this updated value for the middle name.
 * <p>
 * You can also reverse this process to demonstrate what happens when a client
 * using the old schema reads and writes a kv pair that was written earlier
 * using the new schema.  To try this, undo the two changes above -- remove the
 * middle name field from the schema and comment out the two lines that set the
 * middle name value -- and then build and run the program.
 * <p>
 * When the example is run after undoing these changes, the initial value that
 * is read (which was stored with the new version of the schema) will be
 * displayed without a middle name field.  This is because, when Avro
 * deserializes a stored value that has a middle name, it ignores it because no
 * middle name field is present in the reader schema.
 * <p>
 * Note that when this version of the example program updates the age field and
 * writes the modified record, no middle name is included in the stored data.
 * In other words, the middle name that was stored earlier is lost.
 * <p>
 * Also note that the same rules apply when a field is intentionally deleted
 * in a new version of the schema because it is no longer needed.  From the
 * Avro perspective, there is no difference between the two scenarios.
 */
public class JsonExample {

    private static final String SCHEMAS_FILE_NAME = "member-schemas.avsc";
    private static final String MEMBER_INFO_SCHEMA_NAME = "avro.MemberInfo";

    private final KVStore store;
    private final AvroCatalog catalog;
    private final Schema memberInfoSchema;
    private final JsonAvroBinding binding;

    /**
     * Runs the JsonExample command line program.
     */
    public static void main(String args[]) {
        try {
            JsonExample example = new JsonExample(args);
            example.runExample();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the command line args, opens the KVStore, parses the Avro schemas
     * and creates the Avro binding.
     */
    JsonExample(String[] argv)
        throws IOException {

        String storeName = "kvstore";
        String hostName = "localhost";
        String hostPort = "5000";

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
            } else {
                usage("Unknown argument: " + thisArg);
            }
        }

        /* Open the KVStore. */
        store = KVStoreFactory.getStore
            (new KVStoreConfig(storeName, hostName + ":" + hostPort));

        /* Parse the schemas file. */
        final Schema.Parser parser = new Schema.Parser();
        parser.parse(new File(SCHEMAS_FILE_NAME));

        /* Get the MemberInfo schema from the parser's type map. */
        memberInfoSchema = parser.getTypes().get(MEMBER_INFO_SCHEMA_NAME);

        /* Create a JSON binding for the MemberInfo schema. */
        catalog = store.getAvroCatalog();
        binding = catalog.getJsonBinding(memberInfoSchema);
    }

    private void usage(String message) {
        System.out.println("\n" + message + "\n");
        System.out.println("usage: " + getClass().getName());
        System.out.println("\t-store <instance name> (default: kvstore) " +
                           "-host <host name> (default: localhost) " +
                           "-port <port number> (default: 5000)");
        System.exit(1);
    }

    /**
     * Insert a kv pair if it doesn't exist, or read/update it if it does.
     */
    void runExample() {

        /* Use key "/mb/0000000001" to store the member object. */
        final Key key = Key.createKey(Arrays.asList("mb", "0000000001"));

        /* Read the value we previous stored, if any. */
        final ValueVersion valueVersion = store.get(key);
        final JsonRecord record;
        final ObjectNode member;
        final int age;
        if (valueVersion != null) {

            /* Deserialize the value. */
            record = binding.toObject(valueVersion.getValue());
            member = (ObjectNode) record.getJsonNode();

            /* Print it as a JSON string. */
            System.out.println("INITIAL VALUE:\n" + member.toString());

            /* Increment age field. */
            final int oldAge = member.get("age").getIntValue();
            age = oldAge + 1;
            member.put("age", age);

            /*
             * Fill in the middle name when the schema is upgraded. These lines
             * are initially commented out because the middle name field is not
             * present in the schema.  They can be uncommented when the middle
             * name field is added to the schema.
             */
            //final ObjectNode name = (ObjectNode) member.get("name");
            //name.put("middle", "Lawrence");
        } else {
            System.out.println("NO INITIAL VALUE");

            /* Create a fresh JSON object. */
            member = createMember();
            age = member.get("age").getIntValue();

            /* Wrap the JSON object and its schema in a JsonRecord. */
            record = new JsonRecord(member, memberInfoSchema);
        }

        /* Serialize the JsonRecord and write it. */
        store.put(key, binding.toValue(record));

        /* Read it again to confirm that it was stored. */
        final ValueVersion valueVersion2 = store.get(key);
        final JsonRecord record2 = binding.toObject(valueVersion2.getValue());
        final ObjectNode member2 = (ObjectNode) record2.getJsonNode();

        /* Check for expected age. */
        final int age2 = member2.get("age").getIntValue();
        if (age2 != age) {
            throw new RuntimeException("Expected: " + age +
                                       " but got: " + age2);
        }

        /* Print object as a JSON string. */
        System.out.println("FINAL VALUE:\n" + member2.toString());

        store.close();
    }

    /**
     * Uses the Jackson API to create a JSON object (ObjectNode) that conforms
     * to the MemberInfo schema.
     */
    private ObjectNode createMember() {

        final ObjectNode name = JsonNodeFactory.instance.objectNode();
        name.put("first", "Percival");
        name.put("last", "Lowell");

        final ObjectNode addr = JsonNodeFactory.instance.objectNode();
        addr.put("street", "Mars Hill Rd");
        addr.put("city", "Flagstaff");
        addr.put("state", "AZ");
        addr.put("zip", 86001);

        final ObjectNode member = JsonNodeFactory.instance.objectNode();
        member.put("name", name);
        member.put("age", 156);
        member.put("address", addr);

        return member;
    }
}
