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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import oracle.kv.Direction;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;
import oracle.kv.KeyValueVersion;
import oracle.kv.ParallelScanIterator;
import oracle.kv.StoreIteratorConfig;
import oracle.kv.Value;
import oracle.kv.ValueVersion;
import oracle.kv.Version;
import secondaryindex.IndexViewService.IndexMetadata;

/**
 * This is the "main" class for the IndexView example and implements all of the
 * command line argument parsing and dispatch.
 * <p>
 * IndexViewExample has 8 major commands:
 * <ol>
 * <li>-loaddata Loads all records from a data file, parses them into KV
 * Pairs using an avro-binding and stores the results into the "primary DB". For
 * example,
 * <p>
 * <pre>
 * IndexViewExample -loaddata -file &LT;dir_data_file> ...
 * </pre>
 * The <-file> argument is used to specify the path of data file. When
 * the load is completed the count of records is output.
 * <p>
 * <li>-buildindex builds secondary indexes on the specified fields for all KV
 * Pairs in the primary DB. For example,
 * <p>
 * <pre>
 * IndexViewExample -buildindex -name &LT;field_name> ...
 * </pre>
 * This command also supports building multi-column indexes. For example,
 * <p>
 * <pre>
 * IndexViewExample -buildindex -name &LT;field_name1>,&LT;field_name2>,...
 * </pre>
 * <li> -dropindex drops secondary indexes on the specified fields in the Index
 * View. For example,
 * <p>
 * <pre>
 * IndexViewExample -dropindex -name &LT;field_name>
 * </pre>
 * This command also supports dropping multi-column indexes. For example,
 * <p>
 * <pre>
 * IndexViewExample -dropindex -name &LT;field_name1>,&LT;field_name2>,...
 * </pre>
 * <li> -insert inserts a record into the primary DB with corresponding
 * record(s) into the Index View. For example,
 * <p>
 * <pre>
 * IndexViewExample -insert -key &LT;primary_key_value> -value
 * &LT;field_name1>=&LT;field_value1>, &LT;field_name2>=&LT;field_value2>,...
 * </pre>
 * The <-key> argument must be set to the value of primary key field -- "id" in
 * this particular example -- which is the unique identifier of a BillInfo
 * record. Any BillInfo fields that are not specified are set to their default
 * values.
 * <p>
 * <li>-update updates one or more records in the primary DB as well as the
 * corresponding Index View records. For example,
 * <p>
 * <pre>
 * IndexViewExample -update -key &LT;primary_key_value> -value
 * &LT;field_name1>=&LT;field_value1>, &LT;field_name2>=&LT;field_value2>,...
 * </pre>
 * This command also supports updating multi-column indexes. For example,
 * <p>
 * <pre>
 * IndexViewExample -update -seckey &LT;field_name1>=&LT;field_value1>,
 * &LT;field_name2>=&LT;field_value2>,... -value &LT;field_name1>=&LT;field_value1>,
 * &LT;field_name2>=&LT;field_value2>,...
 * </pre>
 * All primary DB records that are located using the given Index View key are
 * updated.
 * <p>
 * <li>-delete deletes records in primary DB as well as their corresponding
 * secondary index records.
 * <p>
 * <pre>
 * IndexViewExample -delete -key &LT;primary_key_value>
 * </pre>
 * This command also supports deletion using multi-column indexes. For example,
 * <p>
 * <pre>
 * IndexViewExample -delete -seckey &LT;field_name1>=&LT;field_value1>,
 * &LT;field_name2>=&LT;field_value2>,...
 * </pre>
 * <li>-query queries records in the primary DB using one more Index Views.
 * For example,
 * <p>
 * <pre>
 * IndexViewExample -query -seckey &LT;field_name1>=&LT;field_value1>,
 * &LT;field_name2>=&LT;field_value2>,...
 * </pre>
 * This command performs the query using both the Index View and a scan of
 * all primary DB records. The wall clock to perform both operations is output.
 * <p>
 * <li>-showindex outputs the field names, schema name and current status of all
 * Index Views in the store.
 * <p>
 * <pre>
 * IndexViewExample -showindex
 * </pre>
 * To run the example, start a KVStore instance. A simple way is to run KVLite
 * as described in the Oracle NoSQL Database Installation document. After
 * starting the KVStore instance, the Avro schemas used by the example must be
 * added to the store using the administration command line interface (CLI).
 * Start the admin CLI as described in the Oracle NoSQL Database
 * Administrator's Guide. Then enter the following commands to add the example
 * schema:
 * <p>
 * <pre>
 * kv-> ddl add-schema -file billinfo-schema.avsc
 * </pre>
 * After adding the schema, use the KVStore instance name, host and port for
 * running the example. e.g. in a -loaddata command:
 * <p>
 * <pre>
 * IndexViewExample -loaddata -host &LT;host_name default: localhost> -port
 * &LT;port default: 5000> -store &LT;store_name default: kvstore>
 * </pre>
 * In this package the default data file is "example_data.csv" which has 4000
 * records. If more data is required, use the "datagenerator" tool to
 * generate more. For more information, see http://www.generatedata.com.
 */
public class IndexViewExample {

    private Binding binding;

    private KVStore kvstore;
    private IndexViewService indexViewService;

    private ExecutorParser parser;

    private Operation op;

    /**
     * Stores the iterator configuration for parallel scan.
     *
     * In this example we use the default values for multi-threaded store
     * iteration and you may want to customize the values to match your
     * configuration.
     */
    private final StoreIteratorConfig storeIteratorConfig =
        new StoreIteratorConfig();

    enum Operation {

        /**
         * Load record into Oracle NoSQL Database.
         */
        LOAD_DATA,

        /**
         * Build an Index View.
         */
        BUILD_INDEX,

        /**
         * Drop an Index View.
         */
        DROP_INDEX,

        /**
         * Insert a record into the primary DB and create an Index View record.
         */
        INSERT_RECORD,

        /**
         * Update a record in primary DB and the related Index View records.
         */
        UPDATE_RECORD,

        /**
         * Delete a record in primary DB and the related Index View records.
         */
        DELETE_RECORD,

        /**
         * Query records in the primary DB.
         */
        QUERY_RECORD,

        /**
         * Show all Index Views.
         */
        SHOW_INDEX;
    }

    void init(String[] argv) {
        parser = new ExecutorParser(argv);
        parser.setDefaults("kvstore", "localhost", 5000);

        parser.parseArgs();

        KVStoreConfig kvconfig = new KVStoreConfig
            (parser.getStoreName(), parser.getHostname() + ":" +
             parser.getRegistryPort());
        kvstore = KVStoreFactory.getStore(kvconfig);
        if (kvstore == null) {
            throw new RuntimeException("Failed to initialize KVStore");
        }
        binding = new Binding(kvstore.getAvroCatalog());
        indexViewService = new IndexViewService(kvstore, binding);
    }

    void handle(Operation operation) {
        if (operation == Operation.LOAD_DATA) {
            try {
                loadData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (operation == Operation.BUILD_INDEX) {
            buildIndexes();
        } else if (operation == Operation.DROP_INDEX) {
            dropIndexes();
        } else if (operation == Operation.INSERT_RECORD) {
            insertRecord();
        } else if (operation == Operation.UPDATE_RECORD) {
            updateRecord();
        } else if (operation == Operation.DELETE_RECORD) {
            deleteRecord();
        } else if (operation == Operation.QUERY_RECORD) {
            queryRecord();
        } else if (operation == Operation.SHOW_INDEX) {
            showIndex();
        }
    }

    /**
     * Loads records. A record is a String containing BillInfo field values
     * with a fixed order. All field values in the input file are separated by
     * semicolons.
     *
     * @throws IOException
     */
    void loadData()
        throws IOException {

        File file = new File(parser.dataFileDir);
        long count = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String buffer;

            while ((buffer = br.readLine()) != null) {
                BillInfo mobileBill = parse(buffer);
                if (mobileBill == null) {
                    continue;
                }

                kvstore.put(mobileBill.getStoreKey(),
                            mobileBill.getStoreValue(binding));
                count++;
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
        System.out.println("Load completed. " + count +
                           " records are loaded into Oracle NoSQL Database.");
    }

    /**
     * Given an input file record, returns a BillInfo instance.
     */
    private BillInfo parse(String recordStr) {
        String[] strs = recordStr.split(";");
        if (strs.length != 6) { // 6: the number of fields in BillInfo
            return null;
        }

        return new BillInfo(strs[0], // id
                            strs[1], // name
                            strs[2], // email
                            strs[3], // phone
                            strs[4], // date
                            Long.parseLong(strs[5])); // cost
    }

    /**
     * Builds Index View(s) for the fields supplied on the command line.
     */
    void buildIndexes() {
        if (!indexViewService.buildIndexes(parser.indexFieldNames,
                                           BillInfo.SCHEMA_NAME)) {
            System.out.println("Failed to build indexes.");
        }
    }

    /**
     * Drops Index View(s) on the fields supplied on the command line.
     */
    void dropIndexes() {
        if (!indexViewService.dropIndexes(parser.indexFieldNames,
                                          BillInfo.SCHEMA_NAME)) {
            System.out.println("Failed to drop indexes.");
        }
    }

    /**
     * Inserts a record into primary DB and creates Index View record(s).
     */
    void insertRecord() {
        BillInfo billInfo = assemble();

        /* Inserts record into the Database. */
        Version version = kvstore.putIfAbsent(billInfo.getStoreKey(),
                                              billInfo.getStoreValue(binding));
        if (version == null) {
            throw new RuntimeException
                ("Record has existed in Oracle NoSQL Database");
        }

        /* Create Index View records. */
        if (!indexViewService.putIndexKV(billInfo.getStoreKey())) {
            System.out.println("Failed to create indexes");
            kvstore.delete(billInfo.getStoreKey());
        }
    }

    /**
     * Assembles and returns a BillInfo object using the command line arguments.
     */
    private BillInfo assemble() {
        String costStr = parser.fieldMap.get(BillInfo.COST);
        long cost = 0;
        if (costStr != null) {
            cost = Long.parseLong(costStr);
        }
        return new BillInfo(parser.primaryKey,
                            parser.fieldMap.get(BillInfo.NAME),
                            parser.fieldMap.get(BillInfo.EMAIL),
                            parser.fieldMap.get(BillInfo.PHONE),
                            parser.fieldMap.get(BillInfo.DATE), cost);
    }

    /**
     * Updates records in the primary DB and any related Index View record(s).
     */
    void updateRecord() {

        if (parser.secIndexUsed) {

            /**
             * Updates primary DB record associated with the given Index View
             * key. More than one record may be updated.
             */
            Map<Key, ValueVersion> resultMap =
                indexViewService.getPrimaryKV(parser.indexFieldNames,
                                              parser.indexFieldValues,
                                              BillInfo.SCHEMA_NAME);
            for (Entry<Key, ValueVersion> entry : resultMap.entrySet()) {
                BillInfo mb = new BillInfo
                    (binding, entry.getKey(), entry.getValue().getValue());
                updateBillInfo(mb);

                /* Updates the Index View record(s). */
                Value newValue = mb.getStoreValue(binding);
                if (!indexViewService.putIndexKV(entry.getKey(), newValue)) {
                    System.out.println("Failed to update indexes.");
                    return;
                }
                kvstore.putIfPresent(mb.getStoreKey(),
                                     mb.getStoreValue(binding));
            }
        } else {

            /**
             * Updates the primary DB record associated to the primary key
             * supplied by the user. Only one record is updated at a time.
             */
            Key key = new BillInfo(parser.primaryKey).getStoreKey();
            ValueVersion vv = kvstore.get(key);
            if (vv == null) {
                throw new RuntimeException
                    ("Record does not exist in the database.");
            }
            BillInfo mb = new BillInfo(binding, key, vv.getValue());
            updateBillInfo(mb);

            /* Updates the Index View records. */
            if (!indexViewService.putIndexKV(key, mb.getStoreValue(binding))) {
                System.out.println("Failed to update indexes.");
                return;
            }
            kvstore.putIfPresent(mb.getStoreKey(), mb.getStoreValue(binding));
        }
    }

    /**
     * Replaces the field values in billInfo with the new field user-supplied
     * values.
     */
    private void updateBillInfo(BillInfo billInfo) {
        String name = parser.fieldMap.get(BillInfo.NAME);
        String email = parser.fieldMap.get(BillInfo.EMAIL);
        String phone = parser.fieldMap.get(BillInfo.PHONE);
        String date = parser.fieldMap.get(BillInfo.DATE);
        String cost = parser.fieldMap.get(BillInfo.COST);
        if (name != null) {
            billInfo.setName(name);
        }
        if (email != null) {
            billInfo.setEmail(email);
        }
        if (phone != null) {
            billInfo.setPhone(phone);
        }
        if (date != null) {
            billInfo.setDate(date);
        }
        if (cost != null) {
            billInfo.setCost(Long.parseLong(cost));
        }
    }

    /**
     * Deletes record(s) in the primary DB and any related Index View record(s).
     */
    void deleteRecord() {

        if (parser.secIndexUsed) {

            /**
             * Deletes primary DB records associated with the given Index View
             * key. Multiple records may be deleted.
             */
            Map<Key, ValueVersion> resultMap =
                indexViewService.getPrimaryKV(parser.indexFieldNames,
                                              parser.indexFieldValues,
                                              BillInfo.SCHEMA_NAME);
            for (Entry<Key, ValueVersion> entry : resultMap.entrySet()) {

                /* Delete the Index View before deleting the record. */
                if (!indexViewService.deleteIndexKV(entry.getKey())) {
                    System.out.println("Failed to delete indexes.");
                }

                /* Deletes records in primary DB. */
                kvstore.delete(entry.getKey());
            }
        } else {

            /**
             * Deletes the primary DB record.
             */
            Key key = new BillInfo(parser.primaryKey).getStoreKey();

            /* Deletes the Index View before deleting the record. */
            if (!indexViewService.deleteIndexKV(key)) {
                System.out.println("Failed to delete indexes.");
            }

            /* Deletes the record in primary DB. */
            kvstore.delete(key);
        }
    }

    /**
     * Retrieves the primary DB record(s) associated with the Index View key
     * argument(s). In order to compare the query performance with and without
     * Index Views, this function outputs the elapsed time for both cases.
     */
    void queryRecord() {
        long msWithIndex = 0;
        long msWithoutIndex = 0;

        /* Queries primary DB records using Index Views. */
        long start = System.currentTimeMillis();
        Set<BillInfo> resultWithIndex = queryWithIndex();
        msWithIndex = System.currentTimeMillis() - start;

        /* Queries primary DB records without using Index Views. */
        start = System.currentTimeMillis();
        Set<BillInfo> resultWithoutIndex = queryWithoutIndex();
        msWithoutIndex = System.currentTimeMillis() - start;

        /* Checks if the query results are consistent. */
        if (!resultWithIndex.equals(resultWithoutIndex)) {
            throw new RuntimeException
                ("Inconsistency detected between Index View and " +
                 "non-Index View retrievals.");
        }

        /* Prints stats */
        System.out.println("Query result: ");
        for (BillInfo mb : resultWithIndex) {
            System.out.println(mb);
        }

        if (!resultWithIndex.isEmpty()) {
            System.out.println("Query stats: " + "\n" +
                               "durationUsingIndex(ms) = " + msWithIndex +
                               "\n" + "durationNotUsingIndex(ms) = " +
                               msWithoutIndex);
        }
    }

    /* Queries primary DB records using Index Views. */
    private Set<BillInfo> queryWithIndex() {
        Set<BillInfo> resultWithIndex = new HashSet<BillInfo>();
        System.out.println(parser.indexFieldValues);
        SortedMap<Key, ValueVersion> resultMap =
            indexViewService.getPrimaryKV(parser.indexFieldNames,
                                          parser.indexFieldValues,
                                          BillInfo.SCHEMA_NAME);

        if (resultMap == null) {
            throw new RuntimeException("Failed to query using indexes");
        }

        for (Entry<Key, ValueVersion> entry : resultMap.entrySet()) {
            BillInfo mobileBill = new BillInfo
                (binding, entry.getKey(), entry.getValue().getValue());
            resultWithIndex.add(mobileBill);
        }

        return resultWithIndex;
    }

    /**
     * Queries primary DB records without using Index Views by performing a
     * scan over the primary DB records.
     */
    private Set<BillInfo> queryWithoutIndex() {
        Set<BillInfo> resultWithoutIndex = new HashSet<BillInfo>();
        Key parentKey = BillInfo.getBillInfoPrefixKey();
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

            if (IndexViewService.isIndexOrMetadata(kvv.getKey())) {
                continue;
            }

            List<Object> list =
                binding.toFields(kvv.getValue(), parser.indexFieldNames);

            if (list == null) {
                continue;
            }

            List<String> strList = new ArrayList<String>();
            for (Object o : list) {
                strList.add(o.toString());
            }

            if (parser.indexFieldValues.equals(strList)) {
                BillInfo mobileBill =
                    new BillInfo(binding, kvv.getKey(), kvv.getValue());
                resultWithoutIndex.add(mobileBill);
            }
        }
        return resultWithoutIndex;
    }


    /**
     * Outputs field names, schema name and current status of all Index Views
     * in Oracle NoSQL Database.
     */
    void showIndex() {
        Set<IndexMetadata> metadataSet = indexViewService.getIndexMetadatas();
        for (IndexMetadata metadata : metadataSet) {
            System.out.println(metadata);
        }
    }

    /**
     * Parses command line arguments.
     */
    class ExecutorParser {

        /**
         * Flag strings
         */
        static final String HOST_FLAG = "-host";
        static final String STORE_FLAG = "-store";
        static final String PORT_FLAG = "-port";

        String hostname;
        String storeName;
        int registryPort;

        private final String[] argArray;
        private int argc;

        /* Flags */
        private static final String DATA_FILE_USAGE = "-file <dir_data_file>";
        private static final String FIELDS_FLAG =
            "<field_name1>=<field_value1>[,<field_name2>=<field_value2>]*";
        private static final String FIELD_NAME_USAGE =
            "-name <field_name1>[,field_name2]*";
        private static final String PRIMARY_KEY_USAGE =
            "-key <primary_key_field_value>";
        private static final String SECONDARY_KEY_USAGE =
            "-seckey " + FIELDS_FLAG;
        private static final String VALUE_USAGE = "-value " + FIELDS_FLAG;
        private static final String PRIMARY_KV_USAGE =
            PRIMARY_KEY_USAGE + " " + VALUE_USAGE;
        private static final String LOAD_DATA_FLAG = "-loaddata";
        private static final String BUILD_INDEX_FLAG = "-buildindex";
        private static final String DROP_INDEX_FLAG = "-dropindex";
        private static final String INSERT_RECORD_FLAG = "-insert";
        private static final String DELETE_RECORD_FLAG = "-delete";
        private static final String UPDATE_RECORD_FLAG = "-update";
        private static final String QUERY_RECORD_FALG = "-query";
        private static final String SHOW_INDEX_FLAG = "-showindex";

        private static final String FILE_FLAG = "-file";
        private static final String NAME_FLAG = "-name";
        private static final String PRIMARY_KEY_FLAG = "-key";
        private static final String SECONDARY_KEY_FLAG = "-seckey";
        private static final String VALUE_FLAG = "-value";

        /* Data file directory */
        String dataFileDir = "example_data.csv";

        /* Index View field names. */
        List<String> indexFieldNames = new ArrayList<String>();

        /* Index View field values. */
        List<Object> indexFieldValues = new ArrayList<Object>();

        /* Primary key for inserting, updating or deleting primary DB record. */
        String primaryKey = null;

        /**
         * A map used to store field arguments for inserting and updating
         * primary DB records. Map key is the field name and Map value is the
         * field value.
         */
        Map<String, String> fieldMap = new HashMap<String, String>();

        /**
         * A flag used by update and delete operations. If it is set to true,
         * the example will update or delete all primary DB records using the
         * user-supplied Index View key. In this case possibly more than one
         * record is updated or deleted. If false, update or delete record
         * using input primary DB key directly and only one record is updated
         * or deleted at a time.
         */
        Boolean secIndexUsed;

        ExecutorParser(String[] args) {
            if (args == null) {
                throw new IllegalArgumentException();
            }
            argArray = args;
            argc = 0;
        }

        /**
         * Used to extend the base arguments to additional ones. Returns true
         * if the argument is expected and valid. Calls usage() if the arg is
         * recognized but invalid. Returns false if arg is not recognized.
         */
        boolean checkArg(String arg) {
            if (arg.equals(LOAD_DATA_FLAG)) {
                op = Operation.LOAD_DATA;
                return true;
            }
            if (arg.equals(BUILD_INDEX_FLAG)) {
                op = Operation.BUILD_INDEX;
                return true;
            }
            if (arg.equals(DROP_INDEX_FLAG)) {
                op = Operation.DROP_INDEX;
                return true;
            }
            if (arg.equals(INSERT_RECORD_FLAG)) {
                op = Operation.INSERT_RECORD;
                return true;
            }
            if (arg.equals(UPDATE_RECORD_FLAG)) {
                op = Operation.UPDATE_RECORD;
                return true;
            }
            if (arg.equals(DELETE_RECORD_FLAG)) {
                op = Operation.DELETE_RECORD;
                return true;
            }
            if (arg.equals(QUERY_RECORD_FALG)) {
                op = Operation.QUERY_RECORD;
                return true;
            }
            if (arg.equals(SHOW_INDEX_FLAG)) {
                op = Operation.SHOW_INDEX;
                return true;
            }
            if (arg.equals(FILE_FLAG)) {
                dataFileDir = nextArg(arg);
                return true;
            }
            if (arg.equals(NAME_FLAG)) {
                String nameString = nextArg(arg);
                indexFieldNames.addAll(Arrays.asList(nameString.split(",")));
                if (indexFieldNames.size() == 0) {
                    throw new RuntimeException
                        ("Failed to parse argument " + NAME_FLAG);
                }
                return true;
            }
            if (arg.equals(PRIMARY_KEY_FLAG)) {
                primaryKey = nextArg(arg);
                return true;
            }
            if (arg.equals(SECONDARY_KEY_FLAG)) {
                if (!parseFieldArg(nextArg(arg), 0)) { // 0 : a couple of lists
                    throw new RuntimeException
                        ("Failed to parse argument " + SECONDARY_KEY_FLAG);
                }
                return true;
            }
            if (arg.equals(VALUE_FLAG)) {
                if (!parseFieldArg(nextArg(arg), 1)) { // 1 : fieldMap
                    throw new RuntimeException
                        ("Failed to parse argument " + VALUE_FLAG);
                }
                return true;
            }
            if (arg.equals(HOST_FLAG)) {
                hostname = nextArg(arg);
                return true;
            }
            if (arg.equals(STORE_FLAG)) {
                storeName = nextArg(arg);
                return true;
            }
            if (arg.equals(PORT_FLAG)) {
                registryPort = Integer.parseInt(nextArg(arg));
                return true;
            }

            return false;
        }

        /**
         * Parses the following form of arguments:
         * <field_name1>=<field_value1>[,<field_name2>=<field_value2>]* and
         * stores results into the corresponding variable. The variable is
         * specified by the argument "flag".
         *
         * @param fieldString
         * @param flag : Used to specify where to store parsing results. The
         * meaning is as follows: 0 : {@link #indexFieldNames}
         * {@link #indexFieldValues} 1 : {@link #fieldMap}
         * @return boolean
         */
        private boolean parseFieldArg(String fieldString, int flag) {
            String[] values = fieldString.split(",");
            if (values.length == 0) {
                return false;
            }
            String[] field = null;
            String fieldName = null;
            String fieldValue = null;
            for (String value : values) {
                field = value.split("=");
                if (field.length != 2) {
                    return false;
                }
                fieldName = field[0];
                fieldValue = field[1];
                if (flag == 0) { // Use a couple of lists to store results
                    indexFieldNames.add(fieldName);
                    indexFieldValues.add(fieldValue);
                } else if (flag == 1) { // Use fieldMap to store results
                    fieldMap.put(fieldName, fieldValue);
                } else {
                    return false;
                }

            }
            return true;
        }

        /**
         * Called after parsing all args. Calls usage() if all required
         * arguments are not set.
         */
        void verifyArgs() {
            if (op == Operation.LOAD_DATA) {

            } else if (op == Operation.BUILD_INDEX ||
                       op == Operation.DROP_INDEX) {

                /**
                 * For Index View build and drop operations, index field names
                 * are required.
                 */
                if (indexFieldNames.size() == 0) {
                    missingArg(NAME_FLAG);
                }
                check(indexFieldNames);
            } else if (op == Operation.INSERT_RECORD) {

                /**
                 * For the insertion operation, a primary key is
                 * required. Other fields are optional but at least one must be
                 * specified.
                 */
                if (primaryKey == null) {
                    missingArg(PRIMARY_KEY_FLAG);
                }
                if (fieldMap.size() == 0) {
                    missingArg(VALUE_FLAG);
                }
                check(fieldMap.keySet());
            } else if (op == Operation.UPDATE_RECORD ||
                       op == Operation.DELETE_RECORD) {
                if (indexFieldNames.size() != indexFieldValues.size()) {
                    usage("");
                }

                /**
                 * For update and delete operations, either the primary key or
                 * Index View fields must be specified (but not both).
                 */
                if (indexFieldNames.size() == 0 && primaryKey == null) {
                    missingArg(SECONDARY_KEY_FLAG + " or " + PRIMARY_KEY_FLAG);
                } else if (indexFieldNames.size() == 0) {
                    secIndexUsed = false;
                } else if (primaryKey == null) {
                    secIndexUsed = true;
                    check(indexFieldNames);
                } else {
                    usage("");
                }
            } else if (op == Operation.QUERY_RECORD) {
                if (indexFieldNames.size() != indexFieldValues.size()) {
                    usage("");
                }

                /**
                 * For a query operation, Index View key arguments are required.
                 */
                if (indexFieldNames.size() == 0) {
                    missingArg(SECONDARY_KEY_FLAG);
                }
                check(indexFieldNames);
            } else if (op == Operation.SHOW_INDEX) {
                // Nothing to check.
            } else {
                usage("");
            }
        }

        /**
         * Checks if all input attribute names are valid.
         */
        private void check(Collection<String> attrNames) {
            for (String attrName : attrNames) {
                if (!BillInfo.isProperty(attrName)) {
                    throw new RuntimeException
                        ("Input field name is invalid:" + attrName);
                }
            }
        }

        void usage(String errorMsg) {
            if (errorMsg != null) {
                System.err.println(errorMsg);
            }

            System.err.println("Usage:" + "\n\t" + "[" + LOAD_DATA_FLAG + " " +
                               optional(DATA_FILE_USAGE) + "]" + "\n\t" + "[" +
                               BUILD_INDEX_FLAG + " " + FIELD_NAME_USAGE +
                               "]" + "\n\t" + "[" + DROP_INDEX_FLAG + " " +
                               FIELD_NAME_USAGE + "]" + "\n\t" + "[" +
                               INSERT_RECORD_FLAG + " " + PRIMARY_KV_USAGE +
                               "]" + "\n\t" + "[" + UPDATE_RECORD_FLAG + " " +
                               "[" + PRIMARY_KEY_USAGE + " " + "| " +
                               SECONDARY_KEY_USAGE + "]" + "\n\t" +
                               "         " + VALUE_USAGE + "]" + "\n\t" + "[" +
                               DELETE_RECORD_FLAG + " " + "[" +
                               PRIMARY_KEY_USAGE + " " + "|" + "\n\t" +
                               "          " + SECONDARY_KEY_USAGE + "]" + "]" +
                               "\n\t" + "[" + QUERY_RECORD_FALG + " " +
                               SECONDARY_KEY_USAGE + "]" + "\n\t" + "[" +
                               SHOW_INDEX_FLAG + "]" + "\n\t" +
                               optional(getHostUsage()) + " " +
                               optional(getPortUsage()) + " " +
                               optional(getStoreUsage()));
            System.exit(-1);
        }

        void setDefaults(String storeName,
                         String hostname,
                         int registryPort) {
            this.storeName = storeName;
            this.hostname = hostname;
            this.registryPort = registryPort;
        }

        String getHostname() {
            return hostname;
        }

        String getStoreName() {
            return storeName;
        }

        int getRegistryPort() {
            return registryPort;
        }

        private String nextArg(String arg) {
            if (argc >= argArray.length) {
                usage("Flag " + arg + " requires an argument");
            }
            return argArray[argc++];
        }

        private void missingArg(String arg) {
            usage("Flag " + arg + " is required");
        }

        private void unknownArg(String arg) {
            usage("Unknown argument: " + arg);
        }

        void parseArgs() {

            int nArgs = argArray.length;
            while (argc < nArgs) {
                String thisArg = argArray[argc++];
                if (!checkArg(thisArg)) {
                    unknownArg(thisArg);
                }
                continue;
            }
            verifyArgs();
        }

        /**
         * Methods for implementing classes.
         */
        String optional(String msg) {
            return "[" + msg + "]";
        }

        String getHostUsage() {
            return HOST_FLAG + " <hostname>";
        }

        String getStoreUsage() {
            return STORE_FLAG + " <storeName>";
        }

        String getPortUsage() {
            return PORT_FLAG + " <port>";
        }
    }

    void runExample(String[] argv) {
        init(argv);
        handle(op);
        kvstore.close();
    }

    public static void main(String[] argv) {
        IndexViewExample example = new IndexViewExample();
        example.runExample(argv);
    }
}
