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

import java.io.IOException;
import java.io.InputStream;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import oracle.kv.Depth;
import oracle.kv.Durability;
import oracle.kv.DurabilityException;
import oracle.kv.FaultException;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.Key;
import oracle.kv.KeyRange;
import oracle.kv.Operation;
import oracle.kv.OperationExecutionException;
import oracle.kv.OperationResult;
import oracle.kv.RequestTimeoutException;
import oracle.kv.ReturnValueVersion;
import oracle.kv.Value;
import oracle.kv.Version;
import oracle.kv.lob.KVLargeObject;

/**
 * Performs write operations and retries the operation when a {@link
 * FaultException} occurs in order to handle transient network failures.  While
 * the {@link KVStore} read methods perform retries automatically when a
 * network failure occurs, the {@link KVStore} write methods do not.
 * Therefore, it is recommended that write operations are performed using the
 * methods in this class, or using a similar retry mechanism in the
 * application.
 * <p>
 * The KVStore write methods are not defined to be strictly idempotent which
 * means if they are performed more than once, the outcome may be different
 * than if they were performed a single time. Repeating an operation may occur
 * when it is retried after a network failure, if in fact the operation was
 * successful the first time. Because of the network failure, the client is
 * unaware of whether the first operation failed or succeeded.
 * <p>
 * For example, if {@link KVStore#delete KVStore.delete} throws a
 * FaultException due to a network failure, it may or may not have succeeded.
 * Imagine that it did succeed and the network failure occurred when receiving
 * the operation reply message.  When the KVStore.delete call is retried, it
 * will return false because the previous attempt succeeded.  Of course, this
 * can also occur if another client deletes the record, even when no retries
 * are necessary.  Therefore, the semantics of the {@link #delete delete}
 * method in this class are slightly different than the semantics of {@link
 * KVStore#delete KVStore.delete}.  The delete method here does not return an
 * indication of whether it deleted the record.  The record is guaranteed to be
 * deleted when the delete method returns without an exception, but there is no
 * way to know whether it was deleted by this method or another client.  With
 * this change in semantics, the delete method in this class is idempotent.
 * <p>
 * Several methods in this class fall into the same category as the delete
 * method in that they are idempotent, as they are defined here.  These are:
 * {@link #put put}, {@link #delete delete} and {@link #multiDelete
 * multiDelete}.  When these methods return without an exception, their outcome
 * is always the same, whether or not retries were necessary. For that reason,
 * retrying these methods at the application level is not needed when no
 * exception is thrown.
 * <p>
 * Most of the other methods in this class -- {@link #putIfAbsent putIfAbsent},
 * {@link #putIfPresent putIfPresent}, {@link #putIfVersion putIfVersion} and
 * {@link #deleteIfVersion deleteIfVersion} -- fall into a different category.
 * These methods are not idempotent, even as defined here, because the
 * application needs to decide whether the operations need to be retried in the
 * face of concurrent access.
 * <p>
 * For example, say the application performs a {@link KVStore#get KVStore.get},
 * examines the record value and determines that it qualifies for deletion, and
 * then calls {@link #deleteIfVersion deleteIfVersion}.  Say the call to
 * KVStore.deleteIfVersion succeeds (when first called by the deleteIfVersion
 * method here), but throws a FaultException due to a network failure. When the
 * KVStore.deleteIfVersion call is retried, it returns false because the
 * previous attempt succeeded and the record was deleted as a result; the
 * deleteIfVersion method in this class will return false as well.  Of course,
 * this can also occur if another client has deleted or modified the record,
 * even when no retries are necessary.  Therefore, the meaning of a false
 * return value from the deleteIfVersion method in this class is slightly
 * different than for KVStore.deleteIfVersion.  When false is returned by the
 * deleteIfVersion method here, it may be because another client deleted or
 * modified the record, or because <em>this method</em> itself unknowingly
 * deleted the record and then retried the operation.  Either way, the
 * application should normally retry the higher level operation: call
 * KVStore.get again (or use the prevValue parameter of the deleteIfVersion
 * method) to get the current record value, and examine it again to see if the
 * record still qualifies for deletion.  In the example described, the record
 * will no longer exist and the application should assume that it was deleted
 * by another client or by this method itself; these two cases cannot be
 * distinguished.
 * <p>
 * As an illustration of the difference in semantics, imagine a store that is
 * idle except for a single client thread that is performing KVStore.get and
 * deleteIfVersion calls (the method in this class).  We also guarantee that no
 * data migration takes place in this test, since data migration changes record
 * versions as if another client performed an update.  In this test, one might
 * assume that the deleteIfVersion method should always return true, since no
 * other clients are accessing the store.  However, this is an incorrect
 * assumption.  If the test is run for long enough, transient network failures
 * will eventually occur, and deleteIfVersion will return false due to
 * scenarios such as the one described above.  This may be an unexpected
 * outcome in such a test scenario, but should be expected in a real world
 * application due to other client activity and data migration, as well as
 * network failures.
 * <p>
 * The following example is also noteworthy.  Imagine that {@link #putIfVersion
 * putIfVersion} is used to increment or decrement a bank balance, or make
 * another sort of incremental or conditional update.  If null is returned by
 * the putIfVersion method in this class (or if {@link KVStore#putIfVersion
 * KVStore.putIfVersion} throws a FaultException), this means the operation may
 * or may not have succeeded.  If performing the change exactly once is
 * critical, as it would be when incrementing or decrementing a bank balance,
 * the application must build in some means of determining whether the change
 * succeeded or not.  This explains why putIfVersion and similar methods in
 * this class cannot simply compare the currently stored value to the value
 * requested, to determine whether the operation succeeded.  The test for
 * success or failure must be left to the application in such cases.
 * <p>
 * The execute method is a special case, since it doesn't fall neatly into one
 * of the two categories defined: idempotent like delete, or non-idempotent
 * like deleteIfVersion.  This is because execute can be used to perform a
 * combination of delete and deleteIfVersion, as well as other types of write
 * operations.  The execute method in this class will retry KVStore.execute
 * when a FaultException is thrown, and the meaning of the individual operation
 * results will depend on the type of operation, as defined by the other
 * methods in this class.
 * <p>
 * Note that this class does not do any exception handling, other than to retry
 * the operation after any kind of FaultException is thrown.  Up to {@link
 * #N_RETRIES} (currently two) retries are performed, and a FaultException that
 * occurs in the last attempt is propagated to the caller.  The caller should
 * handle the exception as described in the {@link RunOperation} class in this
 * example.  In this example, calls to methods in this class are always made
 * within the context of a RunOperation execution, and exceptions are handled
 * by RunOperation in all cases.
 * <p>
 * A known deficiency of this class is that a network failure is not
 * distinguished from other types of FaultExceptions that might occur; a retry
 * is performed when any type of FaultException is thrown.  This is not
 * considered a major problem for two reasons: a) other types of failures are
 * likely to be persistent and will quickly occur again when retrying, and b)
 * optimizing performance when handling FaultExceptions is not normally a
 * concern.
 */
@SuppressWarnings("javadoc")
class WriteOperations {

    /**
     * Maximum number of retries to perform.
     */
    private static final int N_RETRIES = 2;

    /**
     * The delay prior to each retry in milliseconds.
     */
    private static final int DELAY_MS = 10;

    /**
     * The underlying KVStore used to perform operations.
     */
    private final KVStore store;

    /**
     * The default operation timeout in milliseconds.
     */
    private final long defaultTimeoutMs;

    /**
     * The default LOB chunk timeout in milliseconds.
     */
    private final long defaultLOBTimeoutMs;

    /**
     * Creates a WriteOperations wrapper for a given KVStore.
     */
    WriteOperations(final KVStore store, final KVStoreConfig config) {
        this.store = store;
        defaultTimeoutMs = config.getRequestTimeout(TimeUnit.MILLISECONDS);
        defaultLOBTimeoutMs = config.getLOBTimeout(TimeUnit.MILLISECONDS);
    }

    /**
     * Calls {@link KVStore#put(Key, Value) KVStore.put} and performs retries
     * if a FaultException is thrown.
     * <p>
     * This method is equivalent to {@link #put(Key, Value, ReturnValueVersion,
     * Durability, long, TimeUnit)} except that the prevValue, durability,
     * timeout and timeoutUnit parameters are not specified and take on default
     * values.
     */
    public Version put(final Key key, final Value value)
        throws DurabilityException, RequestTimeoutException, FaultException {

        return put(key, value, null, null, 0, null);
    }

    /**
     * Calls {@link KVStore#put(Key, Value) KVStore.put} and performs retries
     * if a FaultException is thrown.
     * <p>
     * This method is idempotent in the sense that if it is called multiple
     * times and returns without throwing an exception, the outcome is always
     * the same:  the given Key/Value pair will have been stored.
     */
    public Version put(final Key key,
                       final Value value,
                       final ReturnValueVersion prevValue,
                       final Durability durability,
                       final long timeout,
                       final TimeUnit timeoutUnit)
        throws DurabilityException, RequestTimeoutException, FaultException {

        return new WriteOp<Version, RuntimeException>(timeout, timeoutUnit) {
            @Override
            Version doWrite(final long timeoutMs) {
                return store.put(key, value, prevValue, durability, timeoutMs,
                                 TimeUnit.MILLISECONDS);
            }
        }.run();
    }

    /**
     * Calls {@link KVStore#putIfAbsent KVStore.putIfAbsent} and performs
     * retries if a FaultException is thrown.
     * <p>
     * This method is equivalent to {@link #putIfAbsent(Key, Value,
     * ReturnValueVersion, Durability, long, TimeUnit)} except that the
     * prevValue, durability, timeout and timeoutUnit parameters are not
     * specified and take on default values.
     */
    public Version putIfAbsent(final Key key,
                               final Value value)
        throws DurabilityException, RequestTimeoutException, FaultException {

        return putIfAbsent(key, value, null, null, 0, null);
    }

    /**
     * Calls {@link KVStore#putIfAbsent KVStore.putIfAbsent} and performs
     * retries if a FaultException is thrown.
     * <p>
     * This method is not idempotent since if it is called multiple times, the
     * outcome may be different because the key may or may not exist.  When
     * null is returned, the application is expected to take action, such as
     * performing retries, at a higher level.
     * <p>
     * When a retry is performed by this method and it returns null because the
     * key is present, there is no returned indication of whether the key was
     * inserted by an earlier attempt in the same method invocation, or by
     * another client.  The application must be prepared for either case.
     * <p>
     * Because of this ambiguity, it can be difficult to use this method
     * (instead of put) as a self-check when the key is expected to be absent,
     * or as a way to prevent two clients from writing the same key.  To do
     * this reliably, each client must include a unique identifier in the value
     * and check for that identifier (call KVStore.get or use the prevValue
     * parameter of this method) when null is returned.
     */
    public Version putIfAbsent(final Key key,
                               final Value value,
                               final ReturnValueVersion prevValue,
                               final Durability durability,
                               final long timeout,
                               final TimeUnit timeoutUnit)
        throws DurabilityException, RequestTimeoutException, FaultException {

        return new WriteOp<Version, RuntimeException>(timeout, timeoutUnit) {
            @Override
            Version doWrite(final long timeoutMs) {
                return store.putIfAbsent(key, value, prevValue, durability,
                                         timeoutMs, TimeUnit.MILLISECONDS);
            }
        }.run();
    }

    /**
     * Calls {@link KVStore#putIfPresent KVStore.putIfPresent} and performs
     * retries if a FaultException is thrown.
     * <p>
     * This method is equivalent to {@link #putIfPresent(Key, Value,
     * ReturnValueVersion, Durability, long, TimeUnit)} except that the
     * prevValue, durability, timeout and timeoutUnit parameters are not
     * specified and take on default values.
     */
    public Version putIfPresent(final Key key,
                                final Value value)
        throws DurabilityException, RequestTimeoutException, FaultException {

        return putIfPresent(key, value, null, null, 0, null);
    }

    /**
     * Calls {@link KVStore#putIfPresent KVStore.putIfPresent} and performs
     * retries if a FaultException is thrown.
     * <p>
     * This method is not idempotent since if it is called multiple times, the
     * outcome may be different because the key may or may not exist.  When
     * null is returned, the application is expected to take action, such as
     * performing retries, at a higher level.
     * <p>
     * This method is commonly used (instead of put) as a self-check, when the
     * key is expected to be present.
     */
    public Version putIfPresent(final Key key,
                                final Value value,
                                final ReturnValueVersion prevValue,
                                final Durability durability,
                                final long timeout,
                                final TimeUnit timeoutUnit)
        throws DurabilityException, RequestTimeoutException, FaultException {

        return new WriteOp<Version, RuntimeException>(timeout, timeoutUnit) {
            @Override
            Version doWrite(final long timeoutMs) {
                return store.putIfPresent(key, value, prevValue, durability,
                                          timeoutMs, TimeUnit.MILLISECONDS);
            }
        }.run();
    }

    /**
     * Calls {@link KVStore#putIfVersion KVStore.putIfVersion} and performs
     * retries if a FaultException is thrown.
     * <p>
     * This method is equivalent to {@link #putIfVersion(Key, Value, Version,
     * ReturnValueVersion, Durability, long, TimeUnit)} except that the
     * prevValue, durability, timeout and timeoutUnit parameters are not
     * specified and take on default values.
     */
    public Version putIfVersion(final Key key,
                                final Value value,
                                final Version matchVersion)
        throws DurabilityException, RequestTimeoutException, FaultException {

        return putIfVersion(key, value, matchVersion, null, null, 0, null);
    }

    /**
     * Calls {@link KVStore#putIfVersion KVStore.putIfVersion} and performs
     * retries if a FaultException is thrown.
     * <p>
     * This method is not idempotent since if it is called multiple times, the
     * outcome may be different because the version may or may not match.  When
     * null is returned, the application is expected to take action, such as
     * performing retries, at a higher level.
     * <p>
     * When a retry is performed by this method and it returns null because the
     * version does not match, there is no returned indication of whether the
     * version was changed by an earlier attempt in the same method invocation,
     * or by another client.  The application must be prepared for either case.
     * <p>
     * This method is commonly used (instead of put) as a way to avoid lost
     * updates.
     * <p>
     * WARNING: A putIfVersion operation should not be used to perform a
     * self-check because the KVStore system may internally assign a new
     * Version to a Key/Value pair when migrating data for better resource
     * usage.  One should never assume that only the application can change the
     * Version of a Key/Value pair.
     */
    public Version putIfVersion(final Key key,
                                final Value value,
                                final Version matchVersion,
                                final ReturnValueVersion prevValue,
                                final Durability durability,
                                final long timeout,
                                final TimeUnit timeoutUnit)
        throws DurabilityException, RequestTimeoutException, FaultException {

        return new WriteOp<Version, RuntimeException>(timeout, timeoutUnit) {
            @Override
            Version doWrite(final long timeoutMs) {
                return store.putIfVersion(key, value, matchVersion, prevValue,
                                          durability, timeoutMs,
                                          TimeUnit.MILLISECONDS);
            }
        }.run();
    }

    /**
     * Calls {@link KVStore#delete(Key) KVStore.delete} and performs retries
     * if a FaultException is thrown.
     * <p>
     * This method is equivalent to {@link #delete(Key, ReturnValueVersion,
     * Durability, long, TimeUnit)} except that the prevValue, durability,
     * timeout and timeoutUnit parameters are not specified and take on default
     * values.
     */
    public void delete(final Key key)
        throws DurabilityException, RequestTimeoutException, FaultException {

        delete(key, null, null, 0, null);
    }

    /**
     * Calls {@link KVStore#delete(Key) KVStore.delete} and performs retries
     * if a FaultException is thrown.
     * <p>
     * This method is idempotent in the sense that if it is called multiple
     * times and returns without throwing an exception, the outcome is always
     * the same:  the given Key/Value pair will have been deleted.
     * <p>
     * Unlike {@link KVStore#delete(Key) KVStore.delete}, this method does not
     * return any indication of whether the Key/Value pair was deleted by this
     * method or by another client.
     */
    public void delete(final Key key,
                       final ReturnValueVersion prevValue,
                       final Durability durability,
                       final long timeout,
                       final TimeUnit timeoutUnit)
        throws DurabilityException, RequestTimeoutException, FaultException {

        new WriteOp<Void, RuntimeException>(timeout, timeoutUnit) {
            @Override
            Void doWrite(final long timeoutMs) {
                store.delete(key, prevValue, durability, timeoutMs,
                             TimeUnit.MILLISECONDS);
                return null;
            }
        }.run();
    }

    /**
     * Calls {@link KVStore#deleteIfVersion KVStore.deleteIfVersion} and
     * performs retries if a FaultException is thrown.
     * <p>
     * This method is equivalent to {@link #deleteIfVersion(Key, Version,
     * ReturnValueVersion, Durability, long, TimeUnit)} except that the
     * prevValue, durability, timeout and timeoutUnit parameters are not
     * specified and take on default values.
     */
    public boolean deleteIfVersion(final Key key,
                                   final Version matchVersion)
        throws DurabilityException, RequestTimeoutException, FaultException {

        return deleteIfVersion(key, matchVersion, null, null, 0, null);
    }

    /**
     * Calls {@link KVStore#deleteIfVersion KVStore.deleteIfVersion} and
     * performs retries if a FaultException is thrown.
     * <p>
     * This method is not idempotent since if it is called multiple times, the
     * outcome may be different because the version may or may not match.  When
     * false is returned, the application is expected to take action, such as
     * performing retries, at a higher level.
     * <p>
     * When a retry is performed by this method and it returns false because
     * the version does not match, there is no returned indication of whether
     * the version was changed by an earlier attempt in the same method
     * invocation, or by another client.  The application must be prepared for
     * either case.
     * <p>
     * This method is commonly used (instead of delete) as a way to avoid lost
     * updates.
     * <p>
     * WARNING: A deleteIfVersion operation should not be used to perform a
     * self-check because the KVStore system may internally assign a new
     * Version to a Key/Value pair when migrating data for better resource
     * usage.  One should never assume that only the application can change the
     * Version of a Key/Value pair.
     */
    public boolean deleteIfVersion(final Key key,
                                   final Version matchVersion,
                                   final ReturnValueVersion prevValue,
                                   final Durability durability,
                                   final long timeout,
                                   final TimeUnit timeoutUnit)
        throws DurabilityException, RequestTimeoutException, FaultException {

        return new WriteOp<Boolean, RuntimeException>(timeout, timeoutUnit) {
            @Override
            Boolean doWrite(final long timeoutMs) {
                return store.deleteIfVersion(key, matchVersion, prevValue,
                                             durability, timeoutMs,
                                             TimeUnit.MILLISECONDS);
            }
        }.run();
    }

    /**
     * Calls {@link KVStore#multiDelete(Key, KeyRange, Depth)
     * KVStore.multiDelete} and performs retries if a FaultException is thrown.
     * <p>
     * This method is equivalent to {@link #multiDelete(Key, KeyRange, Depth,
     * Durability, long, TimeUnit)} except that the durability, timeout and
     * timeoutUnit parameters are not specified and take on default values.
     */
    public void multiDelete(final Key parentKey,
                            final KeyRange subRange,
                            final Depth depth)
        throws DurabilityException, RequestTimeoutException, FaultException {

        multiDelete(parentKey, subRange, depth, null, 0, null);
    }

    /**
     * Calls {@link KVStore#multiDelete(Key, KeyRange, Depth)
     * KVStore.multiDelete} and performs retries if a FaultException is thrown.
     * <p>
     * This method is idempotent in the sense that if it is called multiple
     * times and returns without throwing an exception, the outcome is always
     * the same:  the specified Key/Value pairs will have been deleted.
     * <p>
     * Unlike {@link KVStore#multiDelete(Key, KeyRange, Depth)
     * KVStore.multiDelete}, this method does not return any indication of how
     * many Key/Value pairs were deleted by this method.
     */
    public void multiDelete(final Key parentKey,
                            final KeyRange subRange,
                            final Depth depth,
                            final Durability durability,
                            final long timeout,
                            final TimeUnit timeoutUnit)
        throws DurabilityException, RequestTimeoutException, FaultException {

        new WriteOp<Void, RuntimeException>(timeout, timeoutUnit) {
            @Override
            Void doWrite(final long timeoutMs) {
                store.multiDelete(parentKey, subRange, depth, durability,
                                  timeoutMs, TimeUnit.MILLISECONDS);
                return null;
            }
        }.run();
    }

    /**
     * Calls {@link KVStore#execute KVStore.execute} and performs retries if a
     * FaultException is thrown.
     * <p>
     * This method is equivalent to {@link #execute(List, Durability, long,
     * TimeUnit)} except that the durability, timeout and timeoutUnit
     * parameters are not specified and take on default values.
     */
    public List<OperationResult> execute(final List<Operation> operations)
        throws OperationExecutionException,
               DurabilityException,
               FaultException {

        return execute(operations, null, 0, null);
    }

    /**
     * Calls {@link KVStore#execute KVStore.execute} and performs retries if a
     * FaultException is thrown.
     * <p>
     * This method may or may not be idempotent since the specified operations
     * may or may not be idempotent.  Care should be taken when multiple
     * non-idempotent operations are included, because retries may cause some
     * operations to fail.
     * <p>
     * When a retry is performed by this method and an {@link
     * OperationExecutionException} is thrown, there is no returned indication
     * of whether the operation(s) failed due to an operation that succeeded in
     * an earlier attempt in the same method invocation, or due to an operation
     * by another client.  The application must be prepared for either case.
     */
    public List<OperationResult> execute(final List<Operation> operations,
                                         final Durability durability,
                                         final long timeout,
                                         final TimeUnit timeoutUnit)
        throws OperationExecutionException,
               DurabilityException,
               FaultException {

        return new WriteOp<List<OperationResult>,
                           OperationExecutionException>(timeout, timeoutUnit) {
            @Override
            List<OperationResult> doWrite(final long timeoutMs)
                throws OperationExecutionException {

                return store.execute(operations, durability, timeoutMs,
                                     TimeUnit.MILLISECONDS);
            }
        }.run();
    }

    /**
     * Internal class used to perform retries for a write operation.
     */
    abstract class WriteOp<R, E extends Exception> {
        private long timeoutMs;
        private final long endTime;

        /**
         * Creates the write operation with the requested timeout parameters.
         */
        WriteOp(final long timeout, final TimeUnit timeoutUnit) {
            timeoutMs = (timeout > 0) ?
                         timeoutUnit.toMillis(timeout) :
                         defaultTimeoutMs;
            endTime = System.currentTimeMillis() + timeoutMs;
        }

        /**
         * Implemented for each write operation.
         *
         * @param tmOutMS the timeout in milliseconds to use for this attempt
         * of the write operation.
         */
        abstract R doWrite(long tmOutMS) throws FaultException, E;

        /**
         * Calls the doWrite method and perform retries when a FaultException
         * is thrown.
         */
        R run() throws FaultException, E {
            for (int i = 0; true; i += 1) {
                try {
                    return doWrite(timeoutMs);
                } catch (final FaultException fe) {
                    /* Throw the fault exception if max retries is exceeded. */
                    if (i >= N_RETRIES) {
                        throw fe;
                    }
                    /* Delay before the retry, if there is enough time left. */
                    long now = System.currentTimeMillis();
                    final long delayMs =
                        Math.min(DELAY_MS, (endTime - now) - 1);
                    if (delayMs > 0) {
                        try {
                            Thread.sleep(delayMs);
                        } catch (final InterruptedException ie) {
                            /* Don't swallow the interrupt status. */
                            Thread.currentThread().interrupt();
                            throw fe;
                        }
                        now = System.currentTimeMillis();
                    }
                    /* Adjust the timeout before retrying. */
                    timeoutMs = endTime - now;
                    /* Throw the fault exception if the timeout is exceeded. */
                    if (timeoutMs <= 0) {
                        throw fe;
                    }
                    /* Retry with the adjusted timeout. */
                }
            }
        }
    }

    /**
     * Calls {@link KVLargeObject#putLOB KVLargeObject.putLOB} and performs
     * retries, at least until the specified timeout(s) have been exceeded, if
     * a <code>FaultException</code> is encountered. Note that if a
     * <em>partial</em> LOB is encountered by this method, the operation will
     * overwrite that value.
     *
     * @param lobKey the key associated with the LOB to insert or update.
     *
     * @param lobStreamCallback instance of the interface
     * <code>WriteOperations.LOBStreamListener</code>; which specifies how the
     * <code>InputStream</code> for the LOB should be created or reset when the
     * insert or update operation performed by this method is retried as a
     * result of a failure.
     *
     * @param durability the durability associated with the insert or update
     * operation performed by this method. If the value input for this
     * parameter is <code>null</code>, then the value returned by
     * {@link KVStoreConfig#getDurability} will be used.
     *
     * @param lobTimeout the timeout to use when attempting the insert or
     * update operation on each LOB "chunk". The value input for this parameter
     * is an upper bound on the time taken to perform the operation on each
     * chunk. A best effort is made not to exceed the specified limit. If zero
     * is input, then the value returned by {@link KVStoreConfig#getLOBTimeout}
     * is used. If the value input for this parameter is less than 0, then an
     * <code>IllegalArgumentException</code> is thrown. Note that if a
     * <code>RequestTimeoutException</code> is encountered while operating on a
     * given chunk, then if the <code>operationTimeout</code> has not yet been
     * exceeded, the operation may be retried, possibly on a different node in
     * the store.
     *
     * @param lobTimeoutUnit the unit of time on which the value input for the
     * <code>lobTimeout</code> parameter is based; for example,
     * <code>TimeUnit.MILLISECONDS</code>.
     *
     * @param operationTimeout the timeout to use when attempting the insert or
     * update operation on a given LOB (stream); that is, the sequence of
     * chunks making up the LOB. The value input for this parameter is an upper
     * bound on the time taken to perform the operation on the LOB as a
     * whole. If zero is input, then the value returned by {@link
     * KVStoreConfig#getRequestTimeout} is used. If the value input for this
     * parameter is less than 0 or greater than <code>Integer.MAX_VALUE</code>,
     * an <code>IllegalArgumentException</code> is thrown. The reason this
     * parameter must be less than or equal to <code>Integer.MAX_VALUE</code>
     * is because a value greater than <code>Integer.MAX_VALUE</code> will
     * result in overflow when constructing a
     * <code>RequestTimeoutException</code>; which occurs when the requested
     * operation timeout has been exceeded before a retry can be initiated.
     *
     * @param operationTimeoutUnit the unit of time on which the value input
     * for the <code>operationTimeout</code> parameter is based; for example,
     * <code>TimeUnit.MILLISECONDS</code>.
     *
     * @return the <code>Version</code> associated with the newly inserted or
     * updated LOB.
     *
     * @throws <code>DurabilityException</code> if the specified
     * {@link Durability} cannot be satisfied.
     *
     * @throws <code>RequestTimeoutException</code> if the
     * <code>lobTimeout</code> interval was exceeded during the insertion or
     * update of a chunk or LOB metadata.
     *
     * @throws <code>ConcurrentModificationException</code> if it is detected
     * that an attempt has been made to modify the LOB while the requested
     * insertion or update was in progress.
     *
     * @throws <code>FaultException</code> if the requested insertion or update
     * cannot be completed for any reason.
     *
     * @throws <code>IOException</code> if one is generated by the
     * <code>lobStream</code>.
     *
     * @see oracle.kv.lob.KVLargeObject#putLOB
     * @see schema.WriteOperations.LOBStreamListener
     */
    public Version putLOB(final Key lobKey,
                          final LOBStreamListener lobStreamCallback,
                          final Durability durability,
                          final long lobTimeout,
                          final TimeUnit lobTimeoutUnit,
                          final long operationTimeout,
                          final TimeUnit operationTimeoutUnit)
        throws DurabilityException, RequestTimeoutException,
               ConcurrentModificationException, FaultException, IOException {

        return new LobOp<Version, RuntimeException>(
            lobStreamCallback, lobTimeout, lobTimeoutUnit,
            operationTimeout, operationTimeoutUnit) {
            @Override
            Version doLobOp(InputStream inStream,
                            final long lobTimeoutMs) throws IOException {
                return store.putLOB(lobKey, inStream, durability,
                                    lobTimeoutMs, TimeUnit.MILLISECONDS);
            }
        }.run();
    }

    /**
     * Calls {@link KVLargeObject#putLOBIfAbsent KVLargeObject.putLOBIfAbsent}
     * and performs retries, at least until the specified timeout(s) have been
     * exceeded, if a <code>FaultException</code> is encountered. This method
     * takes the same parameters and throws the same exceptions (for the same
     * reasons) as the {@link WriteOperations#putLOB putLOB} method.
     *
     * @return the <code>Version</code> of the new value, or <code>null</code>
     * if an existing value is present and the insert opertion is unsuccessful.
     *
     * @see oracle.kv.lob.KVLargeObject#putLOB
     * @see schema.WriteOperations#putLOB
     * @see schema.WriteOperations.LOBStreamListener
     */
    public Version putLOBIfAbsent(final Key lobKey,
                                  final LOBStreamListener lobStreamCallback,
                                  final Durability durability,
                                  final long lobTimeout,
                                  final TimeUnit lobTimeoutUnit,
                                  final long operationTimeout,
                                  final TimeUnit operationTimeoutUnit)
        throws DurabilityException, RequestTimeoutException,
               ConcurrentModificationException, FaultException, IOException {

        return new LobOp<Version, RuntimeException>(
            lobStreamCallback, lobTimeout, lobTimeoutUnit,
            operationTimeout, operationTimeoutUnit) {
            @Override
            Version doLobOp(InputStream inStream,
                            final long lobTimeoutMs) throws IOException {
                return store.putLOBIfAbsent(lobKey, inStream,
                                            durability, lobTimeoutMs,
                                            TimeUnit.MILLISECONDS);
            }
        }.run();
    }

    /**
     * Calls {@link KVLargeObject#putLOBIfPresent
     * KVLargeObject.putLOBIfPresent} and performs retries, at least until the
     * specified timeout(s) have been exceeded, if a
     * <code>FaultException</code> is encountered. This method takes the same
     * parameters and throws the same exceptions (for the same reasons) as the
     * {@link WriteOperations#putLOB putLOB} method.
     *
     * @return the <code>Version</code> of the new value, or <code>null</code>
     * if no existing value is present and the update operation is
     * unsuccessful.
     *
     * @see oracle.kv.lob.KVLargeObject#putLOB
     * @see schema.WriteOperations#putLOB
     * @see schema.WriteOperations.LOBStreamListener
     */
    public Version putLOBIfPresent(final Key lobKey,
                                   final LOBStreamListener lobStreamCallback,
                                   final Durability durability,
                                   final long lobTimeout,
                                   final TimeUnit lobTimeoutUnit,
                                   final long operationTimeout,
                                   final TimeUnit operationTimeoutUnit)
        throws DurabilityException, RequestTimeoutException,
               ConcurrentModificationException, FaultException, IOException {

        return new LobOp<Version, RuntimeException>(
            lobStreamCallback, lobTimeout, lobTimeoutUnit,
            operationTimeout, operationTimeoutUnit) {
            @Override
            Version doLobOp(InputStream inStream,
                            final long lobTimeoutMs) throws IOException {
                return store.putLOBIfPresent(lobKey, inStream,
                                             durability, lobTimeoutMs,
                                             TimeUnit.MILLISECONDS);
            }
        }.run();
    }

    /**
     * Calls {@link KVLargeObject#deleteLOB KVLargeObject.deleteLOB} and
     * performs retries, at least until the specified timeout(s) have been
     * exceeded, if a <code>FaultException</code> is encountered. Note that if
     * the key corresponds to a <em>partial</em> LOB, this method will still
     * attempt to remove the specified LOB from the store.
     *
     * @param lobKey the key associated with the LOB to delete.
     *
     * @param durability the durability associated with the delete
     * operation. If the value input for this parameter is <code>null</code>,
     * then the value returned by {@link KVStoreConfig#getDurability} will be
     * used.
     *
     * @param lobTimeout the timeout to use when attempting the delete
     * operation on each LOB "chunk". The value input for this parameter is an
     * upper bound on the time taken to perform the operation on each chunk. A
     * best effort is made not to exceed the specified limit. If zero is input,
     * then the value returned by {@link KVStoreConfig#getLOBTimeout} is
     * used. If the value input for this parameter is less than 0, then an
     * <code>IllegalArgumentException</code> is thrown. Note that if a
     * <code>RequestTimeoutException</code> is encountered while operating on a
     * given chunk, then if the <code>operationTimeout</code> has not yet been
     * exceeded, the operation may be retried, possibly on a different node in
     * the store.
     *
     * @param lobTimeoutUnit the unit of time on which the value input for the
     * <code>lobTimeout</code> parameter is based; for example,
     * <code>TimeUnit.MILLISECONDS</code>.
     *
     * @param operationTimeout the timeout to use when attempting the delete
     * operation on a given LOB; that is, the sequence of chunks making up the
     * LOB. The value input for this parameter is an upper bound on the time
     * taken to perform the operation on the LOB as a whole. If zero is input,
     * then the value returned by {@link KVStoreConfig#getRequestTimeout} is
     * used. If the value input for this parameter is less than 0 or greater
     * than <code>Integer.MAX_VALUE</code>, an
     * <code>IllegalArgumentException</code> is thrown. The reason this
     * parameter must be less than or equal to <code>Integer.MAX_VALUE</code>
     * is because a value greater than <code>Integer.MAX_VALUE</code> will
     * result in overflow when constructing a
     * <code>RequestTimeoutException</code>; which occurs when the requested
     * operation timeout has been exceeded before a retry can be initiated.
     *
     * @param operationTimeoutUnit the unit of time on which the value input
     * for the <code>operationTimeout</code> parameter is based; for example,
     * <code>TimeUnit.MILLISECONDS</code>.
     *
     * @return <code>true</code> if the pair is successfully deleted, and
     * <code>false</code> if the key is unknown to the store. Note that
     * <code>true</code> is returned if a <em>partial</em> LOB is successfully
     * deleted.
     *
     * @throws <code>DurabilityException</code> if the specified
     * {@link Durability} cannot be satisfied.
     *
     * @throws <code>RequestTimeoutException</code> if the
     * <code>lobTimeout</code> interval was exceeded during the deletion of a
     * chunk.
     *
     * @throws <code>ConcurrentModificationException</code> if it is detected
     * that an attempt has been made to modify the LOB while the deletion was
     * in progress.
     *
     * @throws <code>FaultException</code> if the deletion cannot be completed
     * for any reason.
     *
     * @see oracle.kv.lob.KVLargeObject#deleteLOB
     */
    public boolean deleteLOB(final Key lobKey,
                             final Durability durability,
                             final long lobTimeout,
                             final TimeUnit lobTimeoutUnit,
                             final long operationTimeout,
                             final TimeUnit operationTimeoutUnit)
        throws DurabilityException, RequestTimeoutException,
               ConcurrentModificationException, FaultException {

        try {
            return (new LobOp<Boolean, RuntimeException>(
                lobTimeout, lobTimeoutUnit,
                operationTimeout, operationTimeoutUnit) {
                @Override
                Boolean doLobOp(InputStream inStream,
                                final long lobTimeoutMs) throws IOException {
                    return store.deleteLOB(lobKey, durability, lobTimeoutMs,
                                           TimeUnit.MILLISECONDS);
                }
            }.run()).booleanValue();
        } catch (IOException e) {
            throw new AssertionError(
                "Unexpected IOException in WriteOperations.deleteLOB");
        }
    }

    /**
     * Internal <code>abstract</code> class used to perform retries for a
     * <em>put</em> or <em>delete</em> operation applied to a LOB.
     */
    abstract class LobOp<R, E extends Exception> {

        private final LOBStreamListener inStreamCallback;
        private final long opEndTime;

        private long chunkTimeoutMs;
        private long opTimeoutMs;
        private long initialOpTimeoutMs;

        /**
         * Creates a concrete instance of this class representing the desired
         * LOB <em>put</em> or <em>delete</em> operation with the requested
         * parameters described below. Although this class (<code>LobOp</code>)
         * provides the implementation of the retry logic that will be employed
         * by each LOB operation when failure occurs, it is the entity that
         * calls the operation that must provide an implementation of the
         * <code>inStreamCallback</code> parameter; which is used by the retry
         * mechanism to obtain a new or reset <code>InputStream</code> instance
         * positioned at the first byte of the associated LOB value.
         *
         * @param inStreamCallback instance of the interface
         * <code>WriteOperations.LOBStreamListener</code>; which specifies how
         * the associated <code>InputStream</code> should be created or reset
         * when the operation represented by the current instance of this class
         * is retried as a result of a failure. If the operation represented by
         * this class is <code>deleteLOB</code>, then <code>null</code> should
         * be input for this parameter; otherwise, the calling entity must
         * provide an application-specific implementation of the
         * <code>WriteOperations.LOBStreamListener</code> interface for this
         * parameter.
         *
         * @param chunkTimeout the timeout to use when attempting the desired
         * operation on each LOB "chunk". The value input for this parameter is
         * an upper bound on the time taken to perform the operation on each
         * chunk. A best effort is made not to exceed the specified limit. If
         * zero is input, then the value returned by
         * {@link KVStoreConfig#getLOBTimeout} is used. If the value input
         * for this parameter is less than 0, then an
         * <code>IllegalArgumentException</code> is thrown.
         *
         * @param chunkTimeoutUnit the unit of time on which the value input
         * for the chunkTimeout parameter is based; for example,
         * <code>TimeUnit.MILLISECONDS</code>.
         *
         * @param opTimeout the timeout to use when attempting the desired
         * operation on a given LOB (stream); that is, the sequence of chunks
         * making up the LOB. The value input for this parameter is an upper
         * bound on the time taken to perform the operation on the LOB as a
         * whole. If zero is input, then the value returned by {@link
         * KVStoreConfig#getRequestTimeout} is used. If the value input for
         * this parameter is less than 0 or greater than
         * <code>Integer.MAX_VALUE</code>, an
         * <code>IllegalArgumentException</code> is thrown. This parameter must
         * be less than or equal to <code>Integer.MAX_VALUE</code> because a
         * value greater than <code>Integer.MAX_VALUE</code> when constructing
         * a <code>RequestTimeoutException</code> (when the requested operation
         * timeout has been exceeded before a retry can be initiated) will
         * result in overflow.
         *
         * @param opTimeoutUnit the unit of time on which the value input for
         * the opTimeout parameter is based; for example,
         * <code>TimeUnit.MILLISECONDS</code>.
         */
        LobOp(final LOBStreamListener inStreamCallback,
              final long chunkTimeout,
              final TimeUnit chunkTimeoutUnit,
              final long opTimeout,
              final TimeUnit opTimeoutUnit) {

            /* Verify input parameters */

            if (chunkTimeout < 0) {
                throw new IllegalArgumentException
                    ("negative lobTimeout [" + chunkTimeout + "]");
            }
            if (opTimeout < 0) {
                throw new IllegalArgumentException
                    ("negative operationTimeout [" + opTimeout + "]");
            }
            chunkTimeoutMs = (chunkTimeout > 0) ?
                         chunkTimeoutUnit.toMillis(chunkTimeout) :
                         defaultLOBTimeoutMs;
            opTimeoutMs = (opTimeout > 0) ?
                         opTimeoutUnit.toMillis(opTimeout) :
                         defaultTimeoutMs;
            if (opTimeoutMs > Integer.MAX_VALUE) {
                throw new IllegalArgumentException
                    ("operationTimeout > Integer.MAX_VALUE [" +
                     opTimeoutMs + " > " + Integer.MAX_VALUE + "]");
            }

            opEndTime = System.currentTimeMillis() + opTimeoutMs;
            initialOpTimeoutMs = opTimeoutMs;

            this.inStreamCallback = inStreamCallback;
        }

        /**
         * Convenience constructor for the <code>deleteLOB</code> operation.
         */        LobOp(final long chunkTimeout,
              final TimeUnit chunkTimeoutUnit,
              final long opTimeout,
              final TimeUnit opTimeoutUnit) {

            this(null, chunkTimeout, chunkTimeoutUnit,
                 opTimeout, opTimeoutUnit);
        }

        /**
         * Implemented for each LOB operation.
         *
         * @param chunkTmOutMS the timeout in milliseconds to use when
         * attempting the desired operation on each LOB "chunk".
         */
        abstract R doLobOp(InputStream inStream, long chunkTmOutMS)
            throws FaultException, ConcurrentModificationException,
                   IOException, E;

        /**
         * Calls the doLobOp method and performs retries when a FaultException
         * is thrown.
         */
        R run() throws FaultException, ConcurrentModificationException,
                       IOException, E {
            InputStream inputStream = (inStreamCallback == null ? null :
                                       inStreamCallback.getInputStream());
            for (int i = 0; true; i += 1) {
                try {
                    return doLobOp(inputStream, chunkTimeoutMs);
                } catch (final FaultException e) {
                    inputStream = prepareForRetry(i, e);
                }
            }
        }

        /**
         * Applies the appropriate logic to determine if a retry of the current
         * operation can/should be made; updating the necessary state to
         * prepare for and support such a retry.
         * <p>
         * If the <code>LOBStreamListener</code> is <code>null</code>, then the
         * current operation must be <code>deleteLOB</code> rather than one of
         * the <em>putLob</em> operations. For that case, since there is no
         * stream for <code>deleteLOB</code> to operate on, this method returns
         * <code>null</code>. On the other hand, if the
         * <code>LOBStreamListener</code> is not <code>null</code>, then the
         * current operation must be either <code>putLob</code> or one of its
         * counterparts; in which case, before retrying the operation, this
         * method uses the callback mechanism provided by the listener to
         * produce and return a new or reset <code>InputStream</code> instance
         * positioned at the first byte of the associated LOB.
         */
        InputStream prepareForRetry(int iTry, FaultException fe)
            throws FaultException, ConcurrentModificationException,
                   IOException {

            /* If max retries is exceeded, throw the exception that caused
             * a retry to be considered.
             */
            if (iTry >= N_RETRIES) {
                throw fe;
            }

            /* Delay before the retry, if there is enough time left;
             * otherwise, throw the exception that caused a retry to be
             * considered.
             */
            long now = System.currentTimeMillis();
            final long delayMs = Math.min(DELAY_MS, (opEndTime - now) - 1);
            if (delayMs > 0) {
                try {
                    Thread.sleep(delayMs);
                } catch (final InterruptedException ie) {
                    /* Don't swallow the interrupt status. */
                    Thread.currentThread().interrupt();
                    throw fe;
                }
                now = System.currentTimeMillis();
            }

            /* Adjust the timeout before retrying. */
            opTimeoutMs = opEndTime - now;

            /* Throw a timeout exception if the requested operation timeout
             * has been exceeded before a retry can be initiated.
             */
            if (opTimeoutMs <= 0) {
                throw new RequestTimeoutException(
                        (int) initialOpTimeoutMs, "LOB operation timed out",
                        fe, false);
            }

            /*
             * Return and retry with the adjusted timeout. Return null if
             * the current operation is deleteLOB; otherwise, return a
             * stream (either new or reset) positioned at the first byte
             * of the associated LOB.
             */

            if (inStreamCallback == null) {
                return null;
            }
            return inStreamCallback.getInputStream();
        }

    }

    /**
     * For any entity that invokes one of the following methods of the
     * <code>WriteOperations</code> utility class, that entity must supply an
     * implementation of this interface:
     *
     * <code><ul>
     *  <li>putLOB
     *  <li>putLOBIfAbsent
     *  <li>putLOBIfPresent
     * </ul></code>
     *
     * The <code>getInputStream</code> method specified by this interface
     * returns an instance of <code>InputStream</code> for the LOB to which the
     * desired LOB operation is applied; where the stream that is returned can
     * be either a new stream or a reset stream positioned at the first byte of
     * the associated LOB.
     */
    public interface LOBStreamListener {
        InputStream getInputStream() throws IOException;
    }

}
