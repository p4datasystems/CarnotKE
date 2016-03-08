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

package secondaryindex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import oracle.kv.Direction;
import oracle.kv.FaultException;
import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.KeyValueVersion;
import oracle.kv.ParallelScanIterator;
import oracle.kv.StoreIteratorConfig;
import oracle.kv.Value;
import oracle.kv.ValueVersion;

/**
 * This class implements the methods for creating, updating and deleting Index
 * Views.
 * <p>
 * Index Views are a kind of custom secondary index implemented using the
 * KVStore APIs and are a mappings between the secondary keys and the primary
 * key. The Index View records are stored in the NoSQL Database as key-only
 * records where the Value part of the record is Value.EMPTY_VALUE.
 * <p>
 * It is important to keep minimize key sizes in order to reduce memory
 * usage by NoSQL Database. Therefore, when designing applications that
 * implement Index Views you should also consider an alternative design
 * that uses key-value pairs rather than the key-only records in this
 * example. Such an implementation would instead store the primary keys
 * (which are stored in the minor key in this implementation) in the
 * value portion of each record.
 * <p>
 * The Key of each pair follows the following schema:
 * <p>
 * /INDEX_KEY_PREFIX/INDEX_NAME/FIELD_VALUE1/FIELD_VALUE2/.../-/PRIMARY_KEY/
 * <p>
 *
 * INDEX_KEY_PREFIX is a prefix used to distinguish Index View data from
 * other data.
 * <p>
 *
 * INDEX_NAME represents the index type. It is generated from the index
 * metadata info by the "MD5" message digest algorithm. an INDEX_NAME is the
 * unique identity of a kind of index in the index global name space.
 * <p>
 *
 * FIELD_VALUEs is a list holding values of index fields related to INDEX_NAME.
 * The values are stored in multiple components and its order depends on the
 * sequence of indexFieldNames list specified when the user calls
 * {@link #buildIndexes(List, String)}.
 * <p>
 *
 * Index View metadata info is also stored in the NoSQL Database. Each metadata
 * is a KV pair in which the Value is empty and the Key follows the following
 * schema:
 * <p>
 *
 * /INDEX_METADATA_KEY_PREFIX/INDEX_NAME/-/SCHEMA_NAME/FIELD_NAME1/FIELD_NAME2/
 * .../INDEX_STATE
 * <p>
 *
 * INDEX_METADATA_KEY_PREFIX is a prefix used to distinguish index metadata
 * from other data.
 * <p>
 *
 * INDEX_NAME is the index unique identity.
 * <p>
 *
 * SCHEMA_NAME is the name of primary data schema associated with the current
 * index.
 * <p>
 *
 * FIELD_NAMEs is a list holding names of index fields related to INDEX_NAME.
 * Names are stored in multiple components and just like INDEX_FIELD_VALUES its
 * order is specified while calling {@link #buildIndexes(List, String)}.
 * <p>
 *
 * INDEX_STATE is a flag denoting if the current index is available for the
 * index operations. It includes three types: "BUILDING", "DELETING" and
 * "READY". The index can be used only when its status is "READY".
 * <p>
 *
 * Inconsistencies between user records and Index View data if multiple JVM
 * clients perform concurrent index operations using IndexViewService instances
 * or KVStore APIs directly. The implementation of this class uses a status
 * flag in the index metadata to denote whether the index is available for
 * operations. An Index View can be used only when its status is "READY". The
 * status is "BUILDING" until Index View records have been created for all user
 * records. However a complete consistency can not be guaranteed because all
 * index operations including checking the status flag of index metadata are
 * not atomic. Multiple threads can work while sharing the same
 * IndexViewService instance without any synchronization. Using this class is
 * not recommended for multi-process applications.
 * <p>
 *
 * Initialize this class like this:
 * <p>
 * <pre>
 * Binding binding = new Binding(kvstore.getAvroCatalog());
 * IndexViewService indexViewService = new IndexViewService(kvstore, binding);
 * </pre>
 * <p>
 *
 * This class implements the following methods:
 * <p>
 *
 * {@link #buildIndexes(List, String)}: Builds Index Views on the input fields
 * for primary DB records associated in the given schema. Since this method
 * iterates over primary DB records, it may take an arbitrarily long time to
 * complete.
 * <p>
 *
 * {@link #dropIndexes(List, String)}: Drops one or more Index Views specified
 * by the input fields for primary DB records in the given schema. Since this
 * method iterates over primary DB records, it may take an arbitrarily long
 * time to complete.
 * <p>
 *
 * {@link #putIndexKV(Key)}: Creates Index View records for a primary DB
 * record.
 * <p>
 *
 * {@link #putIndexKV(Key, Value)}: Updates Index View records when a primary DB
 * record is updated.
 * <p>
 *
 * {@link #deleteIndexKV(Key)} Deletes Index View records when a primary DB
 * record is deleted.
 * <p>
 *
 * {@link #getPrimaryKV(List, List, String)}: Gets primary DB KV pairs
 * associated with an Index View.
 */
public class IndexViewService {

    private final KVStore kvstore;

    /* Majorpath prefix of Index View KV pairs. */
    private static final String INDEX_KEY_PREFIX = "IDX";

    /* Majorpath prefix of Index View metadata KV pairs. */
    private static final String INDEX_METADATA_KEY_PREFIX = "META";

    private final MessageDigest digest;

    /**
     * Stores the iterator configuration for parallel scan.
     *
     * In this example we use the default values for multi-threaded store
     * iteration and you may want to customize the values to match your
     * configuration.
     */
    private final StoreIteratorConfig storeIteratorConfig =
        new StoreIteratorConfig();

    /**
     * Used to serialize and deserialize the Value of primary DB KV pairs.
     */
    private final Binding binding;

    public IndexViewService(KVStore kvstore, Binding binding) {
        this.kvstore = kvstore;
        this.binding = binding;

        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm unavailable");
        }
    }

    /**
     * Checks whether the given Key is an index or metadata key.
     */
    public static boolean isIndexOrMetadata(Key key) {
        boolean result = false;
        if (key != null) {
            String prefix = key.getMajorPath().get(0);
            if (INDEX_KEY_PREFIX.equals(prefix) ||
                INDEX_METADATA_KEY_PREFIX.equals(prefix)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Returns a Map mapping primary DB KV keys to their related values.
     * Returns an empty map if there are no records found in the
     * database. Returns null if there are no proper indexes that can be used
     * to query or the index state is not READY.
     */
    public SortedMap<Key, ValueVersion>
        getPrimaryKV(List<String> indexFieldNames,
                     List<Object> indexFieldValues,
                     String schemaName) {

        boolean useCompleteIndex = true;

        SortedMap<Key, ValueVersion> resultMap =
            new TreeMap<Key, ValueVersion>();

        /* Checks which type of Index View can be used for the query. */
        String indexName = getIndexName(schemaName, indexFieldNames);
        IndexState state = getIndexState(indexName);

        if (state == null) {

            /**
             * If there is no Index View built using indexFieldNames, try to
             * find an existing multi-column index which can be used to filter
             * primary DB records on the input indexFieldNames.
             */
            indexName = getAvailableIndexName(schemaName, indexFieldNames);
            if ("".equals(indexName)) {
                return null;
            }
            state = getIndexState(indexName);
            useCompleteIndex = false;
        }

        /**
         * The operation can be performed only when the index state is READY
         * and the index metadata conforms to schema name of the user
         * record.
         */
        if (!IndexState.READY.equals(state)) {
            return null;
        }

        /* Query using the Index View. */
        Key parentKey = getIndexViewKey(indexName, indexFieldValues, null);

        if (useCompleteIndex) {
            Set<Key> keySet = kvstore.multiGetKeys(parentKey, null, null);
            for (Key key : keySet) {

                /* Get the primary key referred to by the Index View. */
                Key primaryKey = Key.fromString(key.getMinorPath().get(0));

                /* Queries primary DB records. */
                ValueVersion vv = kvstore.get(primaryKey);
                if (vv != null) {
                    resultMap.put(primaryKey, vv);
                }
            }
        } else {
            final ParallelScanIterator<Key> psIt =
                kvstore.storeKeysIterator(Direction.UNORDERED,
                                          0, /* batchSize */
                                          parentKey,
                                          null /* subRange */,
                                          null, /* depth */
                                          null, /* consistency */
                                          0, /* timeout */
                                          null,
                                          storeIteratorConfig);
            while (psIt.hasNext()) {
                final Key itKey = psIt.next();

                /* Get the primary key referred to by the Index View. */
                Key primaryKey = Key.fromString(itKey.getMinorPath().get(0));

                /* Finds the primary DB record. */
                ValueVersion vv = kvstore.get(primaryKey);
                if (vv != null) {
                    resultMap.put(primaryKey, vv);
                }
            }
        }

        return resultMap;
    }

    /**
     * Builds an Index View(s) for primary DB records associated with the given
     * schema. If all indexes have been built successfully, returns true sets
     * the index state to READY. Returns false if the indexes to be built
     * already exist in the database or a FaultException occurs in the process
     * of building indexes, in which case this method will iterate over the
     * KVStore to delete the metadata and related indexes that have been
     * created.
     */
    public synchronized boolean buildIndexes(List<String> indexFieldNames,
                                             String schemaName) {

        /**
         * Creates the index metadata record in the database and sets the index
         * state to "BUILDING". Any operations related to the current index are
         * not permitted until building is completed. If the index metadata is
         * present false will be returned.
         */
        String indexName = getIndexName(schemaName, indexFieldNames);
        if (createIndexMetadata(indexName, schemaName, indexFieldNames)) {

            /**
             * Iterates on all records in the KVStore and builds indexes for
             * records that conform to the given schema.
             */
            try {
                final ParallelScanIterator<Key> psIt =
                    kvstore.storeKeysIterator(Direction.UNORDERED,
                                              0, /* batchSize */
                                              null, /* parentKey */
                                              null /* subRange */,
                                              null, /* depth */
                                              null, /* consistency */
                                              0, /* timeout */
                                              null,
                                              storeIteratorConfig);
                buildIndexesInternal(psIt, indexName, indexFieldNames,
                                     schemaName);
                setIndexState(indexName, IndexState.READY);
                return true;
            } catch (FaultException e) {

                /**
                 * Tries to do some compensating operations including deleting
                 * the metadata and indexes that have been created to avoid
                 * inconsistency between user records and indexes.
                 */
                unwindIndexViewBuild(indexName);
            }
        }

        return false;
    }

    private void buildIndexesInternal(Iterator<Key> iterator,
                                      String indexName,
                                      List<String> indexFieldNames,
                                      String schemaName) {

        Key indexKeyPrefix = Key.createKey(INDEX_KEY_PREFIX);
        Key indexTypeKeyPrefix = Key.createKey(INDEX_METADATA_KEY_PREFIX);

        /**
         * Iterates over the primary DB and adds Index View records for every
         * primary DB KV pair.
         */
        while (iterator.hasNext()) {
            Key itKey = iterator.next();

            /* Checks if the current key belongs to primary DB. */
            if (!indexKeyPrefix.isPrefix(itKey) &&
                !indexTypeKeyPrefix.isPrefix(itKey)) {
                Value value = kvstore.get(itKey).getValue();

                /* Checks if the Value conforms to the given schema name. */
                String itSchemaName = binding.getSchemaName(value);
                if (itSchemaName == null || // null if not an Avro data.
                    !itSchemaName.equals(schemaName)) {
                    continue;
                }

                /* Gets the value list of given index fields. */
                List<Object> indexFieldValues =
                    binding.toFields(value, indexFieldNames);

                /* Generates the Key of the Index View KV pair. */
                Key secondaryKey = getIndexViewKey
                    (indexName, indexFieldValues, itKey.toString());

                /* Creates a key-only record for the Index View. */
                kvstore.putIfAbsent(secondaryKey, Value.EMPTY_VALUE);
            }
        }
    }

    /**
     * Perform compensating operations. Delete the metadata
     * and indexes that have been created to avoid inconsistency between user
     * records and indexes when a FaultException occurs in the process of
     * building indexes.
     */
    private void unwindIndexViewBuild(String indexName) {

        deleteIndexMetadata(indexName);

        /**
         * Iterates over the KVStore to delete related indexes that have been
         * created.
         */
        Key parentKey = getIndexViewKey(indexName, null, null);
        final ParallelScanIterator<Key> psIt =
            kvstore.storeKeysIterator(Direction.UNORDERED,
                                      0, /* batchSize */
                                      parentKey,
                                      null /* subRange */,
                                      null, /* depth */
                                      null, /* consistency */
                                      0, /* timeout */
                                      null,
                                      storeIteratorConfig);
        while (psIt.hasNext()) {
            final Key itKey = psIt.next();
            kvstore.delete(itKey);
        }
    }

    /**
     * Drops Index Views on the given fields. Returns true if all indexes and
     * metadata are successfully deleted. Returns false if the status of
     * indexes to be dropped is not READY or a FaultException occurs in the
     * process of dropping indexes. If a failure occurs during this operation
     * this method does nothing else to unwind and the user should call {@link
     * #buildIndexes(List, String)} to rebuild the Index Views that have been
     * deleted to keep the consistency between user records and indexes.
     *
     * @param indexFieldNames
     * @param schemaName
     * @return boolean
     */
    public synchronized boolean dropIndexes(List<String> indexFieldNames,
                                            String schemaName) {

        String indexName = getIndexName(schemaName, indexFieldNames);

        if (!IndexState.READY.equals(getIndexState(indexName))) {
            return false;
        }

        setIndexState(indexName, IndexState.DELETING);

        /* Drops Index Views. */
        List<String> majorPath = new ArrayList<String>();
        majorPath.add(INDEX_KEY_PREFIX);
        majorPath.add(indexName);
        Key parentKey = Key.createKey(majorPath);

        try {
            final ParallelScanIterator<KeyValueVersion> psIt =
                kvstore.storeIterator(Direction.UNORDERED,
                                      0, /* batchSize */
                                      parentKey,
                                      null /* subRange */,
                                      null, /* depth */
                                      null, /* consistency */
                                      0, /* timeout */
                                      null,
                                      storeIteratorConfig);
            while (psIt.hasNext()) {
                final KeyValueVersion kvv = psIt.next();
                kvstore.delete(kvv.getKey());
            }
            deleteIndexMetadata(indexName);
            return true;
        } catch (FaultException e) {

            /*
             * Do nothing to unwind. User may need to call {@link
             * #buildIndexes(List, String)} to rebuild the indexes that have
             * been deleted.
             */
        }

        return false;
    }

    /**
     * Creates Index View records for a newly inserted user record.  This
     * function is called when a new record is inserted into the primary
     * DB. Returns true if indexes are created successfully. Returns false if
     * one of the following cases occurs: 1. the given Key does not exist in
     * the database, 2. the Value related to the given Key is not an Avro data
     * record, 3. The status of some types of indexes to be built is not READY
     * in which case this method will undo all executed operations before
     * return.
     */
    public boolean putIndexKV(Key primaryKey) {

        /**
         * Stores the index key of KV pairs that have been created in case they
         * are needed for unwinding after a failure.
         */
        Set<Key> createdKeyCache = new HashSet<Key>();

        ValueVersion vv = kvstore.get(primaryKey);

        /* Checks if the user record exists. */
        if (vv == null) {
            return false;
        }

        /* Gets schema name that the user Value conforms to. */
        String schemaName = binding.getSchemaName(vv.getValue());

        /* Checks if the Value is in Avro format. */
        if (schemaName == null) {
            return false;
        }

        Set<IndexMetadata> set = getIndexMetadatas();
        for (IndexMetadata metadata : set) {

            /**
             * The operation can only be performed if the index state is READY
             * and index metadata conforms to schema name of the record.
             */
            if (!metadata.getIndexState().equals(IndexState.READY)) {

                /* Undo */
                for (Key key : createdKeyCache) {
                    kvstore.delete(key);
                }
                return false;
            }

            if (schemaName.equals(metadata.getSchemaName())) {

                /* Gets the value list of the index fields. */
                List<Object> indexFieldValues = binding.toFields
                    (vv.getValue(), metadata.getIndexFieldNames());

                /* Generates the Key of secondary index KV pair. */
                Key secondaryKey =
                    getIndexViewKey(metadata.getIndexName(),
                                    indexFieldValues,
                                    primaryKey.toString());

                /* Creates a key-only Index View. */
                kvstore.putIfAbsent(secondaryKey, Value.EMPTY_VALUE);

                /* Caches the KV pair in case an unwind is necessary. */
                createdKeyCache.add(secondaryKey);
            }
        }

        return true;
    }

    /**
     * Updates Index View(s) for a user record being updated. Generally this
     * function is called before an existing record is updated in the primary
     * DB. Returns true if indexes are updated successfully.  Returns false if
     * one of the following occur: 1. the given Key does not exist in
     * the database, 2. the Value related to the given Key is not an Avro
     * record. 3.the new Value is inconsistent with the old Value in
     * schema. 4. the status of some types of indexes to be updated is not
     * READY in which case this method will undo all executed operations before
     * returning.
     */
    public boolean putIndexKV(Key primaryKey, Value newValue) {

        /**
         * Stores the index key of KV pairs that have been deleted or created
         * in case they are needed for unwinding after a failure.
         */
        Set<Key> createdKeyCache = new HashSet<Key>();
        Set<Key> deletedKeyCache = new HashSet<Key>();

        ValueVersion oldVv = kvstore.get(primaryKey);

        /* Checks if the user record exists. */
        if (oldVv == null || newValue == null) {
            return false;
        }
        Value oldValue = oldVv.getValue();

        /* Gets schema name that the user Value conforms to. */
        String schemaName = binding.getSchemaName(oldValue);

        /**
         * Checks if the Value is an Avro data and the new Value is consistent
         * with the old Value in schema.
         */
        if (schemaName == null ||
            !schemaName.equals(binding.getSchemaName(newValue))) {
            return false;
        }

        /**
         * Updates Index Views built on the current primary KV associated with
         * the primaryKey.
         */
        Set<IndexMetadata> set = getIndexMetadatas();
        for (IndexMetadata metadata : set) {

            /**
             * The operation can be performed only when the index state is
             * READY and index metadata conforms to schema name of the user
             * record.
             */
            if (!metadata.getIndexState().equals(IndexState.READY)) {

                /* Undo */
                for (Key key : createdKeyCache) {
                    kvstore.delete(key);
                }
                for (Key key : deletedKeyCache) {
                    kvstore.put(key, Value.EMPTY_VALUE);
                }
                return false;
            }

            if (schemaName.equals(metadata.getSchemaName())) {

                List<Object> oldFieldValues = binding.toFields
                    (oldValue, metadata.getIndexFieldNames());
                List<Object> newFieldValues = binding.toFields
                    (newValue, metadata.getIndexFieldNames());

                /* Checks if the index needs to be updated. */
                if (oldFieldValues.equals(newFieldValues)) {
                    continue;
                }

                /* Deletes old index. */
                Key oldKey = getIndexViewKey(metadata.getIndexName(),
                                             oldFieldValues,
                                             primaryKey.toString());
                kvstore.delete(oldKey);
                deletedKeyCache.add(oldKey);

                /* Creates new index. */
                Key newKey = getIndexViewKey
                    (metadata.getIndexName(), newFieldValues,
                     primaryKey.toString());
                kvstore.putIfAbsent(newKey, Value.EMPTY_VALUE);
                createdKeyCache.add(newKey);
            }
        }
        return true;
    }

    /**
     * Deletes an Index View record for a user record being deleted. Generally
     * this function is called before a primary DB record is deleted. Returns
     * true if indexes are deleted successfully.  Returns false if one of the
     * following cases occurs: 1. the given Key does not exist in the
     * database. 2. the Value related to the given Key is not an Avro
     * data. 3. the status of some types of indexes to be deleted is not READY
     * in which case this method will undo all executed operations before
     * return.
     */
    public boolean deleteIndexKV(Key primaryKey) {

        /**
         * Stores the index key of KV pairs that have been deleted in case they
         * are needed for unwinding after a failure.
         */
        Set<Key> deletedKeyCache = new HashSet<Key>();

        ValueVersion vv = kvstore.get(primaryKey);

        /* Checks if the user record exists. */
        if (vv == null) {
            return false;
        }

        /* Gets schema name that the user Value conforms to. */
        String schemaName = binding.getSchemaName(vv.getValue());

        /* Checks if the Value is an Avro record. */
        if (schemaName == null) {
            return false;
        }

        Set<IndexMetadata> set = getIndexMetadatas();
        for (IndexMetadata metadata : set) {

            /**
             * The operation can be performed only when the index state is
             * READY and index metadata matches the schema name of the user
             * record.
             */
            if (!metadata.getIndexState().equals(IndexState.READY)) {

                /* Undo */
                for (Key key : deletedKeyCache) {
                    kvstore.put(key, Value.EMPTY_VALUE);
                }

                return false;
            }

            if (schemaName.equals(metadata.getSchemaName())) {

                /* Gets the value list of the index fields. */
                List<Object> indexFieldValues = binding.toFields
                    (vv.getValue(), metadata.getIndexFieldNames());

                /* Generates the Key of Index View KV pair. */
                Key secondaryKey = getIndexViewKey
                    (metadata.getIndexName(), indexFieldValues,
                     primaryKey.toString());

                /* Deletes the Index View. */
                kvstore.delete(secondaryKey);

                /* Caches the KV pair for undoing operations. */
                deletedKeyCache.add(secondaryKey);
            }
        }

        return true;
    }

    /**
     * Returns all Index View metadata.
     */
    public Set<IndexMetadata> getIndexMetadatas() {

        Set<IndexMetadata> result = new HashSet<IndexMetadata>();
        Key parentKey = Key.createKey(INDEX_METADATA_KEY_PREFIX);
        final ParallelScanIterator<Key> psIt =
            kvstore.storeKeysIterator(Direction.UNORDERED,
                                      0, /* batchSize */
                                      parentKey,
                                      null /* subRange */,
                                      null, /* depth */
                                      null, /* consistency */
                                      0, /* timeout */
                                      null,
                                      storeIteratorConfig);
        while (psIt.hasNext()) {
            final Key key = psIt.next();
            List<String> majorPath = key.getMajorPath();
            List<String> minorPath = key.getMinorPath();
            String indexName = majorPath.get(1);
            String schemaName = minorPath.get(0);
            List<String> indexFieldNames =
                minorPath.subList(1, minorPath.size() - 1);
            IndexState state =
                IndexState.valueOf(minorPath.get(minorPath.size() - 1));
            result.add(new IndexMetadata(indexName, schemaName,
                                         indexFieldNames, state));
        }

        return result;
    }

    /**
     * Creates the Index View metadata. Sets the state to BUILDING. Returns
     * false if the index metadata already exists in the database.
     */
    private boolean createIndexMetadata(String indexName,
                                        String schemaName,
                                        List<String> indexFieldNames) {
        boolean result = false;

        /* Creates the index metadata key. */
        Key key = getIndexMetadataKey(indexName, schemaName, indexFieldNames,
                                      IndexState.BUILDING);

        /* Checks if the metadata exists. */
        Set<Key> set = kvstore.multiGetKeys
            (getIndexMetadataKey(indexName, null, null, null), null, null);
        if (set.isEmpty()) {

            /**
             * Creates index metadata KV in the KVStore.
             */
            if (kvstore.putIfAbsent(key, Value.EMPTY_VALUE) != null) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Deletes Index View metadata.
     */
    private void deleteIndexMetadata(String indexName) {
        List<String> majorPath = new ArrayList<String>();
        majorPath.add(INDEX_METADATA_KEY_PREFIX);
        majorPath.add(indexName);
        Key key = Key.createKey(majorPath);
        kvstore.multiDelete(key, null, null);
    }

    /**
     * Generates a Key for Index View metadata.
     */
    private Key getIndexMetadataKey(String indexName,
                                    String schemaName,
                                    List<String> indexFieldNames,
                                    IndexState state) {
        List<String> majorPath = new ArrayList<String>();
        List<String> minorPath = new ArrayList<String>();
        majorPath.add(INDEX_METADATA_KEY_PREFIX);
        majorPath.add(indexName);
        if (schemaName != null) {
            minorPath.add(schemaName);
        }
        if (indexFieldNames != null) {
            minorPath.addAll(indexFieldNames);
        }
        if (state != null) {
            minorPath.add(state.toString());
        }

        return Key.createKey(majorPath, minorPath);
    }

    /**
     * Returns the index status related for a given indexName. Return null if
     * the indexes specified by indexName do not exist in the database.
     */
    private IndexState getIndexState(String indexName) {

        Key parentKey = getIndexMetadataKey(indexName, null, null, null);
        SortedSet<Key> set = kvstore.multiGetKeys(parentKey, null, null);
        if (set.size() == 0) {
            return null;
        }
        Key metadataKey = set.first();
        List<String> minorPath = metadataKey.getMinorPath();
        return IndexState.valueOf(minorPath.get(minorPath.size() - 1));
    }

    /**
     * Sets the index status.
     */
    private void setIndexState(String indexName, IndexState state) {

        /* Creates index metadata key. */
        Key parentKey = getIndexMetadataKey(indexName, null, null, null);

        SortedSet<Key> set = kvstore.multiGetKeys(parentKey, null, null);
        Key metadataKey = set.first();
        List<String> oldMinorPath = metadataKey.getMinorPath();
        List<String> minorPath = new ArrayList<String>
            (oldMinorPath.subList(0, oldMinorPath.size() - 1));
        minorPath.add(state.toString());
        kvstore.delete(metadataKey);
        kvstore.putIfAbsent(Key.createKey
                            (metadataKey.getMajorPath(), minorPath),
                            Value.EMPTY_VALUE);
    }

    /**
     * Constructs and returns an index name representing an index type.
     */
    private String getIndexName(String schemaName,
                                List<String> indexFieldNames) {
        List<String> minorPath = new ArrayList<String>();
        minorPath.add(schemaName);
        minorPath.addAll(indexFieldNames);
        byte[] bytes = Key.createKey("", minorPath).toString().getBytes();
        digest.update(bytes);
        String result = new String(digest.digest());
        digest.reset();
        return result;
    }

    /**
     * Returns an existing multi-column Index View name which can be used to
     * filter primary DB records using indexFieldNames. If there are multiple
     * proper indexes only one will be returned.
     */
    private String getAvailableIndexName(String schemaName,
                                         List<String> indexFieldNames) {
        Set<IndexMetadata> metadatas = getIndexMetadatas();
        String indexName = "";
        for (IndexMetadata metadata : metadatas) {
            if (!metadata.getSchemaName().equals(schemaName)) {
                continue;
            }
            List<String> list = metadata.getIndexFieldNames();
            if (indexFieldNames.size() > 0 &&
                list.size() > indexFieldNames.size() &&
                indexFieldNames.equals
                (list.subList(0, indexFieldNames.size()))) {
                indexName = metadata.getIndexName();
            }
        }

        return indexName;
    }

    /* Returns an Index View Key. */
    private Key getIndexViewKey(String indexName,
                                List<Object> indexFieldValues,
                                String primaryKey) {
        List<String> majorPath = new ArrayList<String>();
        majorPath.add(INDEX_KEY_PREFIX);
        majorPath.add(indexName);
        if (indexFieldValues != null) {
            for (Object value : indexFieldValues) {
                majorPath.add(value.toString());
            }
        }

        if (primaryKey == null) {
            return Key.createKey(majorPath);
        }

        return Key.createKey(majorPath, primaryKey);
    }

    /**
     * An enum for the current status of an Index View.
     */
    enum IndexState {
        BUILDING, DELETING, READY;
    }

    /**
     * A structure holding all info for an Index View.
     */
    public final class IndexMetadata {

        @Override
        public String toString() {
            return "IndexMetadata [indexFieldNames=" + indexFieldNames +
                ", schemaName=" + schemaName +
                ", indexState=" + indexState + "]";
        }

        /* Index name */
        private final String indexName;

        /* A list holding index field names */
        private final List<String> indexFieldNames;

        /* Schema name */
        private final String schemaName;

        /* The current status */
        private final IndexState indexState;

        IndexMetadata(String indexName,
                      String schemaName,
                      List<String> indexFieldNames,
                      IndexState indexState) {
            this.indexName = indexName;
            this.indexFieldNames = indexFieldNames;
            this.schemaName = schemaName;
            this.indexState = indexState;
        }

        String getIndexName() {
            return indexName;
        }

        List<String> getIndexFieldNames() {
            return indexFieldNames;
        }

        String getSchemaName() {
            return schemaName;
        }

        IndexState getIndexState() {
            return indexState;
        }
    }
}
