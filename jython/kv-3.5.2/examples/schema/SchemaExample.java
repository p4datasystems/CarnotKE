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

package schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oracle.kv.Depth;
import oracle.kv.Direction;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;
import oracle.kv.KeyRange;
import oracle.kv.KeyValueVersion;
import oracle.kv.Operation;
import oracle.kv.OperationExecutionException;
import oracle.kv.OperationFactory;
import oracle.kv.OperationResult;
import oracle.kv.ReturnValueVersion;
import oracle.kv.Value;
import oracle.kv.ValueVersion;
import oracle.kv.Version;

/**
 * A KVStore client application that illustrates basic schema design.  It also
 * shows:
 * <ul>
 * <li> multi-component Keys with major and minor paths;
 * <li> single-attribute and multi-attribute Values;
 * <li> Avro serialization of Value attributes;
 * <li> storing a Value associated with a parent Key;
 * <li> using putIfVersion to avoid lost updates;
 * <li> using timestamps as Key components;
 * <li> querying a range of Key values;
 * <li> updating multiple Key/Value pairs in an atomic operation;
 * <li> using a RunOperation class to handle exceptions consistently;
 * <li> using a WriteOperations class to retry write operations to account for
 *      transient network failures.
 * </ul>
 *
 * <p>To understand this example, start by reading the {@link
 * KeyDefinition} class comments.  These comments explain the overall
 * schema, key types and data object types.  Then come back and
 * continue reading this class.</p>
 *
 * <p>Since this example uses an Avro binding, the Avro and Jackson jars must
 * be in the classpath, as well as the kvclient jar.  The Avro and Jackson jars
 * are included in the KVHOME/lib directory along with the kvclient jar:</p>
 * <pre>
 *    kvclient.jar avro.jar jackson-core-asl.jar jackson-mapper-asl.jar
 * </pre>
 *
 * <p>As long as all four jars are in the same directory, only the kvclient jar
 * needs to be specified in the classpath, because the kvclient jar references
 * the other three jars.  If they are not in the same directory, all four
 * jars must be explicitly specified in the classpath.</p>
 *
 * <p>To build this example in the examples/avro directory:</p>
 * <pre>
 *   cd KVHOME/examples/schema
 *   mkdir classes
 *   javac -cp KVHOME/lib/kvclient.jar -d classes *.java
 * </pre>
 *
 * <p>Before running this example program, start a KVStore instance.  The
 * simplest way to do that is to run KV Lite as described in the Quickstart
 * document.</p>
 *
 * <p>After starting the KVStore instance, the Avro schemas used by the example
 * must be added to the store using the administration command line interface
 * (CLI).  First start the admin CLI as described in the Oracle NoSQL Database
 * Administrator's Guide. Then enter the following commands to add the example
 * schemas:</p>
 * <pre>
 *  ddl add-schema -file user-info.avsc
 *  ddl add-schema -file user-image.avsc
 *  ddl add-schema -file login-session.avsc
 *  ddl add-schema -file login-summary.avsc
 * </pre>
 *
 * <p>After adding the schema, use the KVStore instance name, host and port for
 * running this program, as follows:</p>
 *
 * <pre>
 * java schema.SchemaExample -store &lt;instance name&gt; \
 *                           -host  &lt;host name&gt;     \
 *                           -port  &lt;port number&gt;
 * </pre>
 *
 * <p>For all examples the default instance name is kvstore, the
 * default host name is localhost and the default port number is 5000.
 * These defaults match the defaults for the run-kvlite.sh script, so the
 * simplest way to run the examples along with kvlite is to omit all
 * parameters.</p>
 *
 * <p>When running this program, the Java classpath must include the
 * kvclient.jar and the example 'classes' directory. The .avsc files must also
 * be found as resources in the classpath, which can be accomplished by adding
 * the 'examples' directory to the classpath or by copying the .avsc files to
 * the 'classes/schema' directory.</p>
 *
 * <p>This program adds a fixed set of Key/Value pairs to the store
 * and then performs queries and prints the results of the queries.
 * It is not interactive.</p>
 *
 * <p>This program is a usage example and is not intended to be used
 * directly for performance testing.  The methods that access the
 * store make some assumptions that access is single-client and
 * single-threaded.  However, the schema approach -- the key
 * structure, object definitions and object serialization -- is
 * intended to scale up and is an example of a design for a large data
 * set with stringent performance requirements.</p>
 *
 * <p>WARNING: To create a performance test, a multi-client
 * multi-threaded client approach will be necessary to avoid limiting
 * throughput at the client side.  This example program is not
 * designed for that type of testing.</p>
 */
@SuppressWarnings("javadoc")
public class SchemaExample {

    private final KVStore store;
    private final WriteOperations writeOps;
    private final Bindings bindings;

    /**
     * Runs the SchemaExample command line program.
     */
    public static void main(String args[]) {
        try {
            SchemaExample example = new SchemaExample(args);
            example.runExample();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses command line args and opens the KVStore.
     */
    SchemaExample(String[] argv) {

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

        final KVStoreConfig config =
            new KVStoreConfig(storeName, hostName + ":" + hostPort);
        store = KVStoreFactory.getStore(config);
        writeOps = new WriteOperations(store, config);
        bindings = new Bindings(store.getAvroCatalog());
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
     * Performs example operations and closes the KVStore.
     */
    void runExample() {

        /*
         * Load initial user data.
         */
        printBanner("Add and update user objects, then query all user data");
        deleteExistingData();
        addUsers();
        updateUserInfo();
        updateUserImage();
        queryAllUsers();

        /*
         * Add initial session history.
         */
        printBanner("Add initial session history, then query all user data");
        addSessionHistory();
        queryAllUsers();

        /*
         * Query session history by key range.
         */
        final String cutoffDateTime =
            KeyDefinition.formatTimestamp(InputData.CUTOFF_LOGIN_DATE);

        printBanner("Query session history on or after date/time: " +
                    cutoffDateTime);

        final KeyRange onOrAfterCutoffDate =
            new KeyRange(cutoffDateTime /*start*/, true /*startInclusive*/,
                         null /*end*/, false /*endInclusive*/);

        querySessionHistory(onOrAfterCutoffDate);

        /*
         * Query session history by key prefix.
         */
        final String cutoffDate = cutoffDateTime.substring(0, 10);
        printBanner("Query session history for date prefix: " + cutoffDate);
        final KeyRange onCutoffDate = new KeyRange(cutoffDate /*prefix*/);
        querySessionHistory(onCutoffDate);

        /*
         * Delete session history by key range.
         */
        printBanner("Delete session history before date/time " +
                    cutoffDateTime + ", then query all session history");

        final KeyRange beforeCutoffDate = new KeyRange
            (null /*start*/, false /*startInclusive*/,
             cutoffDateTime /*end*/, false /*endInclusive*/);

        deleteSessionHistory(beforeCutoffDate);
        querySessionHistory(null);

        /*
         * Close the store handle and release resources.  After calling close,
         * the application should discard the KVStore instance to allow network
         * connections to be garbage collected and closed.
         */
        printBanner("Close KVStore");
        store.close();
    }

    /**
     * Prints a banner.
     */
    private void printBanner(String heading) {

        final char[] separatorChars = new char[79];
        Arrays.fill(separatorChars, '=');
        final String separator = new String(separatorChars);

        System.out.println(separator);
        System.out.println(heading);
        System.out.println(separator);
    }

    /**
     * To make the example simple we delete all existing user data first and
     * start from scratch.
     *
     * This method is unrealistic in that it uses KVStore.storeKeysIterator to
     * iterate over all user Keys and delete each of them individually.  In a
     * real application with multiple clients or client threads, this approach
     * wouldn't work well because the set of deletion operations is not atomic,
     * and new Key/Value pairs might be inserted by other clients concurrently.
     * There is no way to delete all user Key/Value pairs atomically, because
     * they are distributed across all nodes in the KVStore system.
     *
     * However, the use of KVStore.storeKeysIterator is realistic for an
     * application that needs to iterate over all objects of a specified type.
     * Iteration by object type is possible because the object type ("user" in
     * this case) is the first component of all Keys and can be used as a
     * parentKey for the iteration.
     */
    private void deleteExistingData() {

        /*
         * The simple Key "user" is a prefix for all user Keys and can be used
         * as the parentKey for querying all user Key/Value pairs.
         */
        final Key userTypeKey = KeyDefinition.makeUserTypeKey();

        /*
         * Create an iterator over all user Keys.  The direction parameter is
         * Direction.UNORDERED because ordering is not necessary and in fact
         * not currently supported by the storeKeysIterator method.  The
         * batchSize is zero to use a default iterator batch size.  The
         * subRange is null to select all children Keys.  The depth is null to
         * select all descendant Keys.
         */
        final Iterator<Key> iter = store.storeKeysIterator
            (Direction.UNORDERED, 0 /*batchSize*/, userTypeKey,
             null /*subRange*/, null /*depth*/);

        new RunOperation() {
            @Override
            void doOperation() {

                /*
                 * Delete the Key/Value pair for each Key returned by the
                 * iterator.
                 */
                while (iter.hasNext()) {
                    final Key key = iter.next();
                    writeOps.delete(key);
                }
            }
        }.run();
    }

    /**
     * Inserts all UserInfo and UserImage Key/Value pairs.  Also inserts a
     * LoginSummary for each user with zero values, so that the session
     * handling code can be simpler by assuming that this record exists.
     *
     * KVStore.execute is used here to insert the UserInfo, UserImage and
     * LoginSummary for each user, in a single atomic operation.  This is more
     * efficient than three separate operations because it reduces the number
     * of round trips.  Note that KVStore.execute can only be used to insert
     * the Key/Value pairs for a single user at a time.  When Keys have
     * different major paths, their Key/Value pairs may be stored at different
     * physical locations and cannot be updated atomically.
     *
     * We use OperationFactory.createPut to insert each object.  Note that we
     * did not use createPutIfAbsent as a self-check, because this can fail if
     * the operation is retried due to a transient network error.
     */
    private void addUsers() {
        final OperationFactory factory = store.getOperationFactory();

        for (int i = 0; i < InputData.N_USERS; i += 1) {

            final String email = InputData.USER_EMAIL[i];
            final UserInfo userInfo = InputData.USER_INFO[i];
            final UserImage userImage = InputData.USER_IMAGE[i];

            final LoginSummary loginSummary = new LoginSummary(email);
            loginSummary.setTotalLoginCount(0);
            loginSummary.setTotalLoginDuration(0);

            final List<Operation> ops = new ArrayList<Operation>();

            ops.add(factory.createPut
                    (userInfo.getStoreKey(),
                     userInfo.getStoreValue(bindings)));

            ops.add(factory.createPut
                    (userImage.getStoreKey(),
                     userImage.getStoreValue(bindings)));

            ops.add(factory.createPut
                    (loginSummary.getStoreKey(),
                     loginSummary.getStoreValue(bindings)));

            new RunOperation() {
                @Override
                void doOperation() {

                    try {
                        writeOps.execute(ops);
                    } catch (OperationExecutionException e) {
                        /* One of the insertions failed unexpectedly. */
                        throw new IllegalStateException
                            ("Unexpected failure during initial load", e);
                    }
                }
            }.run();
        }
    }

    /**
     * Updates all UserInfo Key/Value pairs.  The phone number is reformatted
     * by changing the separators from '.' to '-'.  This is a meaningless
     * change intended only to show how to do updates to a multi-attribute
     * Key/Value pair.
     *
     * Although this example is a single-threaded single-client application,
     * in a real application multiple clients and client threads may update a
     * UserInfo object concurrently.  It is critical in this situation to avoid
     * "lost updates".  For example, thread A and B may both read the value for
     * a given Key, and then both A and B may change one attribute (imagine
     * that each thread changes a different attribute) and update the Value.
     * Since the read and the write are not atomically executed, the two
     * read-modify-write operations are not isolated from each other.  One
     * attribute update may be lost in this situation.
     *
     * To avoid a lost update, the KVStore.putIfVersion method should be used
     * to perform the update.  The Version is obtained from the KVStore.get
     * method, and used to conditionally update the Value only if its Version
     * has not changed since the read.  If its Version has changed, the client
     * can simply retry the operation.
     *
     * When retrying the operation, rather than call KVStore.get again, to save
     * a round trip we use the ReturnValueVersion parameter of
     * KVStore.putIfVersion.  If putIfVersion fails, the ReturnValueVersion
     * will contain the Value and Version of the Key/Value pair currently in
     * the store.  Note that performance will only be impacted by this sort of
     * optimization when retries are frequent, so in some cases it may not be
     * worth the added complexity.
     */
    private void updateUserInfo() {

        for (final String email : InputData.USER_EMAIL) {
            final Key key = KeyDefinition.makeUserInfoKey(email);

            new RunOperation() {
                @Override
                void doOperation() {

                    /* Get existing UserInfo Key/Value pair. */
                    final ValueVersion valueVersion = store.get(key);
                    if (valueVersion == null) {
                        throw new IllegalStateException
                            ("Unexpected failure getting: " + key);
                    }
                    Value oldValue = valueVersion.getValue();
                    Version oldVersion = valueVersion.getVersion();

                    boolean updateDone = false;
                    while (!updateDone) {

                        /* Deserialize as UserInfo object. */
                        final UserInfo userInfo = new UserInfo(email);
                        userInfo.setStoreValue(bindings, oldValue);

                        /*
                         * Modify phone attribute and get new serialized Value.
                         */
                        userInfo.setPhone
                            (userInfo.getPhone().replace('.', '-'));
                        final Value newValue =
                            userInfo.getStoreValue(bindings);

                        /*
                         * Update the UserInfo if the version has not changed.
                         * If the version has changed, return the existing
                         * Value and Version and use them to retry the update.
                         */
                        final ReturnValueVersion oldValueVersion =
                            new ReturnValueVersion
                            (ReturnValueVersion.Choice.ALL);

                        final Version newVersion = writeOps.putIfVersion
                            (key, newValue, oldVersion, oldValueVersion,
                             null /*durability*/, 0 /*timeout*/,
                             null /*timeoutUnit*/);

                        /*
                         * If null is returned, the operation failed because
                         * the Version was changed by another thread or client.
                         * Continue the loop and retry the read-modify-write
                         * operation.
                         */
                        if (newVersion == null) {
                            oldValue = oldValueVersion.getValue();
                            oldVersion = oldValueVersion.getVersion();
                            if (oldValue == null || oldVersion == null) {
                                throw new IllegalStateException
                                    ("Unexpected failure getting: " + key);
                            }
                            continue;
                        }

                        /* Operation succeeded.  Exit the loop. */
                        updateDone = true;
                    }
                }
            }.run();
        }
    }

    /**
     * Updates all UserImage Key/Value pairs.  The image is replaced with a
     * byte array of a different size than was originally inserted.  This is a
     * meaningless change intended only to show how to do blind updates, which
     * are explained below.
     *
     * Unlike the updateUserInfo method, this method does not use
     * KVStore.putIfVersion to guard against lost updates.  Instead, it
     * performs a "blind update" that unconditionally replaces the existing
     * Key/Value pair, assuming that it exists.  With a blind update, there is
     * no requirement to read the existing Key/Value pair with KVStore.get.
     *
     * The Value has only a single attribute (the image), so there is no
     * possibility that another thread will concurrently perform a
     * read-modify-write operation that changes a different attribute.  The
     * operation semantics that are illustrated are "last update always wins",
     * in the case of multiple concurrent updates.  If these semantics are not
     * desired, KVStore.putIfVersion should be used instead.
     *
     * Because the updates are performed after the initial load, we use
     * KVStore.putIfPresent which will return a null Version if the Key does
     * not exist.  This is a self-check to ensure that we are doing updates and
     * not insertions.  Because we inserted all existing data beforehand,
     * KVStore.putIfPresent should always succeed and return a non-null
     * Version.  To unconditionally insert or update a Key/Value pair, use
     * KVStore.put instead.
     */
    private void updateUserImage() {

        int nextImageSize = 9990;

        for (final String email : InputData.USER_EMAIL) {

            final Key key = KeyDefinition.makeUserImageKey(email);
            final int useImageSize = nextImageSize;
            nextImageSize += 1;

            /* Make a replacement for the UserImage object. */
            final UserImage userImage = new UserImage(email);
            userImage.setImage(new byte[useImageSize]);
            final Value value = userImage.getStoreValue(bindings);

            new RunOperation() {
                @Override
                void doOperation() {

                    /* Do a blind update. */
                    final Version version = writeOps.putIfPresent(key, value);

                    /* The update is expected to succeed. */
                    if (version == null) {
                        throw new IllegalStateException
                            ("Unexpected failure updating: " + userImage);
                    }

                }
            }.run();
        }
    }

    /**
     * Queries and prints all user Key/Value pairs in the store.
     *
     * KVStore.storeIterator is used to iterate over all objects of a specified
     * type.  Iteration by object type is possible because the object type
     * ("user" in this case) is the first component of all Keys and can be used
     * as a parentKey for the iteration.
     */
    private void queryAllUsers() {

        /*
         * The simple Key "user" is a prefix for all user Keys and can be used
         * as the parentKey for querying all user Key/Value pairs.
         */
        final Key userTypeKey = KeyDefinition.makeUserTypeKey();

        /*
         * Create an iterator over all user Key/Value pairs.  The direction
         * parameter is Direction.UNORDERED because ordering is not currently
         * supported by the storeIterator method.  The batchSize is zero to use
         * a default iterator batch size.  The subRange is null to select all
         * children Keys.  The depth is null to select all descendant Keys.
         */
        final Iterator<KeyValueVersion> iter = store.storeIterator
            (Direction.UNORDERED, 0 /*batchSize*/, userTypeKey,
             null /*subRange*/, null /*depth*/);

        new RunOperation() {
            @Override
            void doOperation() {

                /*
                 * Print each Key/Value pair returned by the iterator.
                 */
                while (iter.hasNext()) {
                    final KeyValueVersion keyValue = iter.next();
                    final Object o = KeyDefinition.deserializeAny
                        (bindings, keyValue.getKey(), keyValue.getValue());
                    System.out.println(o.toString());
                }
            }
        }.run();
    }

    /**
     * Adds start and end session data illustrating user login and logout (or
     * session timeout).
     *
     * For the example we record the start of a session and then immediately
     * record the end of that session, based on duration of the session from
     * the input data.  In a real application, the app server might provide
     * triggers for recording these events as they actually happen.
     *
     * Because the loginTime is the key to the LoginSession Key/Value pair, we
     * must pass it to endSession as well as startSession.  In a real
     * application, the loginTime could be cached in the session data that is
     * maintained by the app server.
     */
    private void addSessionHistory() {

        for (int i = 0; i < InputData.N_USERS; i += 1) {

            final String email = InputData.USER_EMAIL[i];
            final long[] loginTimes = InputData.LOGIN_TIMES[i];

            for (int j = 0; j < loginTimes.length; j += 1) {

                final long loginTime = loginTimes[j];
                final int sessionDuration = InputData.SESSION_DURATIONS[i][j];

                startSession(email, loginTime);
                endSession(email, loginTime, sessionDuration);
            }
        }
    }

    /**
     * When a session starts we insert a LoginSession object.  Only if
     * endSession is called do we update the LoginSummary record for the user.
     * If endSession is never called, because of a crash for example, the
     * LoginSession can be identified as incomplete because it will have a
     * zero duration.
     *
     * Note that we used KVStore.put rather than putIfAbsent to store the
     * session record.  Because the loginTime is expected to identify a unique
     * session for the user, we could have used KVStore.putIfAbsent as a
     * self-check, since it will return a null Version if the Key already
     * exists.  This was not done because it would add complexity to the
     * example.  The putIfAbsent method may return null if there is a
     * transient network failure (see {@link WriteOperations}), and additional
     * code would be necessary to handle that possibility.
     */
    private void startSession(final String email, final long loginTime) {

        /* Make a LoginSession object with a zero duration. */
        final LoginSession loginSession = new LoginSession(email, loginTime);
        loginSession.setSessionDuration(0);

        new RunOperation() {
            @Override
            void doOperation() {

                /* Do an unconditional insertion. */
                final Key key = loginSession.getStoreKey();
                final Value value = loginSession.getStoreValue(bindings);
                final Version version = writeOps.put(key, value);

                /* The insertion is expected to succeed. */
                if (version == null) {
                    throw new IllegalStateException
                        ("Unexpected failure inserting: " + loginSession);
                }
            }
        }.run();
    }

    /**
     * When a session ends we update the LoginSession object to set the session
     * duration, and we update the LoginSummary to add this session to the
     * totals.
     *
     * The LoginSession will be updated with a putIfPresent operation.  Because
     * the LoginSession is inserted when the session starts, we use
     * putIfPresent as a self-check.  If it fails because the LoginSession does
     * not exist, we know something went wrong earlier.
     *
     * A "blind update" (without reading the previous version) is performed for
     * the LoginSession, because it has only one attribute, the duration, and
     * the duration is being replaced.  When the LoginSession was initially
     * inserted, a zero duration was specified.
     *
     * Because we inserted a LoginSummary for each user initially, we can
     * update it here using a putIfVersion operation.  If the update fails
     * because the LoginSummary is updated concurrently by another client or
     * thread, we will retry the operation.  This approach prevents lost
     * updates.
     *
     * WARNING: A putIfVersion operation should not be used to perform a
     * self-check.  For example, if the application guarantees that concurrent
     * updates to LoginSummary do not occur, when putIfVersion fails it may be
     * tempting to assume that the application state is invalid, and to give up
     * and report an error instead of retrying.  This is incorrect because the
     * KVStore system may internally assign a new Version to a Key/Value pair
     * when migrating data for better resource usage.  Therefore, one should
     * never assume that only the application can change the Version of a
     * Key/Value pair.
     *
     * The LoginSession update and the LoginSummary update are done in a single
     * atomic operation using KVStore.execute.  This ensures that the summary
     * information is always correct.  If the two operations were done
     * separately and one of them failed and one succeeded, then the
     * LoginSummary attributes would not accurately reflect the sum of the
     * LoginSession information.
     */
    private void endSession(final String email,
                            final long loginTime,
                            final int sessionDuration) {

        final OperationFactory factory = store.getOperationFactory();
        final Key summaryKey = KeyDefinition.makeLoginSummaryKey(email);
        final Key sessionKey =
            KeyDefinition.makeLoginSessionKey(email, loginTime);

        new RunOperation() {
            @Override
            void doOperation() {

                /* Get the current LoginSummary Value and Version, if any. */
                final ValueVersion valueVersion = store.get(summaryKey);
                if (valueVersion == null) {
                    throw new IllegalStateException
                        ("Unexpected failure getting: " + summaryKey);
                }
                Value oldSummaryValue = valueVersion.getValue();
                Version oldSummaryVersion = valueVersion.getVersion();

                boolean updateDone = false;
                while (!updateDone) {

                    /*
                     * Create a replacement for the LoginSession.  Use a
                     * putIfPresent operation to blindly update the
                     * LoginSession.
                     */
                    final LoginSession loginSession =
                        new LoginSession(email, loginTime);
                    loginSession.setSessionDuration(sessionDuration);

                    final Operation sessionOp = factory.createPutIfPresent
                        (loginSession.getStoreKey(),
                         loginSession.getStoreValue(bindings),
                         null /*prevReturn*/, true /*abortIfUnsuccessful*/);

                    /*
                     * Create a putIfVersion operation to update the
                     * LoginSummary.  Deserialize the old value and bump the
                     * total login count and duration attributes to include the
                     * session that is ending.
                     */
                    final LoginSummary loginSummary = new LoginSummary(email);
                    loginSummary.setStoreValue(bindings, oldSummaryValue);

                    loginSummary.setTotalLoginCount
                        (loginSummary.getTotalLoginCount() + 1);
                    loginSummary.setTotalLoginDuration
                        (loginSummary.getTotalLoginDuration() +
                         sessionDuration);

                    final Operation summaryOp = factory.createPutIfVersion
                        (loginSummary.getStoreKey(),
                         loginSummary.getStoreValue(bindings),
                         oldSummaryVersion,
                         ReturnValueVersion.Choice.ALL,
                         true /*abortIfUnsuccessful*/);

                    /* Add sessionOp at index 0 and summaryOp at index 1. */
                    final List<Operation> ops = new ArrayList<Operation>();
                    ops.add(sessionOp);
                    ops.add(summaryOp);

                    try {
                        writeOps.execute(ops);
                    } catch (OperationExecutionException e) {

                        /* Self-check for LoginSession update failed. */
                        if (e.getFailedOperationIndex() == 0) {
                            throw new IllegalStateException
                                ("Unexpected failure updating: " +
                                 sessionKey, e);
                        }

                        /*
                         * If the LoginSummary update fails, it was inserted or
                         * updated by another thread or client.  Continue the
                         * loop and retry the operation.
                         */
                        if (e.getFailedOperationIndex() == 1) {
                            final OperationResult failedResult =
                                e.getFailedOperationResult();
                            oldSummaryValue = failedResult.getPreviousValue();
                            oldSummaryVersion =
                                failedResult.getPreviousVersion();
                            if (oldSummaryValue == null ||
                                oldSummaryVersion == null) {
                                throw new IllegalStateException
                                    ("Unexpected failure getting: " +
                                     summaryKey);
                            }
                            continue;
                        }

                        throw new IllegalStateException("Unexpected failure",
                                                        e);
                    }

                    /* Operation succeeded.  Exit the loop. */
                    updateDone = true;
                }
            }
        }.run();
    }

    /**
     * Queries and prints the LoginSummary and LoginSession objects for each
     * user in the store.  The LoginSession objects are restricted to the given
     * KeyRange.  Depth.PARENT_AND_CHILDREN is used with the KVStore.multiGet
     * method to return the summary and session information in a single query.
     *
     * KVStore.multiGet performs the query in a single atomic operation that is
     * isolated from changes by other clients and threads.  This ensures that
     * LoginSummary accurately reflects the sum of the LoginSession
     * information.  However, if the KeyRange is non-null then a subset of
     * LoginSessions are printed, so of course the LoginSessions won't add up
     * to the totals in the LoginSummary.
     *
     * An application may use KVStore.multiGetIterator rather than multiGet
     * when read isolation is not needed and the size of the returned data is
     * larger than desired for a single operation round trip.  With
     * multiGetIterator, the data is returned in smaller blocks to avoid
     * monopolizing the available bandwidth.
     */
    private void querySessionHistory(final KeyRange keyRange) {

        for (int i = 0; i < InputData.N_USERS; i += 1) {

            final String email = InputData.USER_EMAIL[i];
            final Key parentKey =
                KeyDefinition.makeLoginSummaryKey(email);

            new RunOperation() {
                @Override
                void doOperation() {

                    final Map<Key, ValueVersion> results = store.multiGet
                        (parentKey, keyRange, Depth.PARENT_AND_CHILDREN);

                    for (Map.Entry<Key, ValueVersion> entry :
                         results.entrySet()) {
                        final Object o = KeyDefinition.deserializeAny
                            (bindings, entry.getKey(),
                             entry.getValue().getValue());
                        System.out.println(o.toString());
                    }
                }
            }.run();
        }
    }

    /**
     * Deletes the LoginSession objects in a given KeyRange, for each user in
     * the store.  Depth.CHILDREN_ONLY is used with the KVStore.multiDelete so
     * that the the LoginSummary object is not deleted.  This approach could be
     * used to remove LoginSession details prior to a given date/time, when not
     * all session history needs to be retained forever.
     */
    private void deleteSessionHistory(final KeyRange keyRange) {

        for (int i = 0; i < InputData.N_USERS; i += 1) {

            final String email = InputData.USER_EMAIL[i];
            final Key parentKey = KeyDefinition.makeLoginSummaryKey(email);

            new RunOperation() {
                @Override
                void doOperation() {
                    writeOps.multiDelete(parentKey, keyRange,
                                         Depth.CHILDREN_ONLY);
                    System.out.println
                        ("Deleted all LoginSession objects for " + email); }
            }.run();
        }
    }
}
