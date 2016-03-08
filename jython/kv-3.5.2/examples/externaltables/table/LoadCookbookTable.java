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

package externaltables.table;

import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;

import oracle.kv.table.PrimaryKey;
import oracle.kv.table.Row;
import oracle.kv.table.Table;
import oracle.kv.table.TableAPI;
import oracle.kv.table.TableIterator;

import externaltables.UserInfo;

/**
 * Class that creates sample records and uses the Table API to populate a
 * NoSQL Database with those records.
 *
 * This class assumes that a table of the following name and format has been
 * created and added to the store:
 * <code>
 *   kv -> table create -name cookbookTable
 *   cookbookTable -> add-field -name email   -type STRING
 *   cookbookTable -> add-field -name name    -type STRING
 *   cookbookTable -> add-field -name gender  -type STRING
 *   cookbookTable -> add-field -name address -type STRING
 *   cookbookTable -> add-field -name phone   -type STRING
 *   cookbookTable -> primary-key -field email
 *   cookbookTable -> exit
 *   kv -> plan add-table -wait -name cookbookTable
 * </code>
 * Rather than executing the commands above interactively in the CLI,
 * the examples/table/create_vehicle_table.kvs script can instead be used
 * in the following way:
 * <code>
 *   > cd KVHOME
 *   > java -jar lib/kvcli.jar -host <hostname> -port <port> -store <storename>
 *
 *   kv-> load -file examples/externaltables/table/create_cookbook_table.kvs
 * </code>
 */
public final class LoadCookbookTable {

    private final KVStore store;
    private final TableAPI tableAPI;
    private final Table table;

    private long nOps = 10;

    private boolean deleteExisting = false;

    static final String TABLE_NAME = "cookbookTable";
    static final String USER_OBJECT_TYPE = "user";

    public static void main(final String[] args) {
        try {
            final LoadCookbookTable loadData = new LoadCookbookTable(args);
            loadData.run();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Parses command line args and opens the KVStore.
     */
    private LoadCookbookTable(final String[] argv) {

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

            if ("-store".equals(thisArg)) {
                if (argc < nArgs) {
                    storeName = argv[argc++];
                } else {
                    usage("-store requires an argument");
                }
            } else if ("-host".equals(thisArg)) {
                if (argc < nArgs) {
                    hostName = argv[argc++];
                } else {
                    usage("-host requires an argument");
                }
            } else if ("-port".equals(thisArg)) {
                if (argc < nArgs) {
                    hostPort = argv[argc++];
                } else {
                    usage("-port requires an argument");
                }
            } else if ("-nops".equals(thisArg)) {
                if (argc < nArgs) {
                    nOps = Long.parseLong(argv[argc++]);
                } else {
                    usage("-nops requires an argument");
                }
            } else if ("-delete".equals(thisArg)) {
                deleteExisting = true;
            } else {
                usage("Unknown argument: " + thisArg);
            }
        }

        store = KVStoreFactory.getStore
            (new KVStoreConfig(storeName, hostName + ":" + hostPort));

        tableAPI = store.getTableAPI();

        table = tableAPI.getTable(TABLE_NAME);
        if (table == null) {
            final String msg =
                "Store does not contain table [name=" + TABLE_NAME + "]";
            throw new RuntimeException(msg);
        }
    }

    private void usage(final String message) {
        if (message != null) {
            System.out.println("\n" + message + "\n");
        }

        System.out.println("usage: " + getClass().getName());
        System.out.println
            ("\t-store <instance name>\n" +
             "\t-host <host name>\n" +
             "\t-port <port number>\n" +
             "\t-nops <total records to create>\n" +
             "\t-delete (default: false) [delete all existing data]\n");
        System.exit(1);
    }

    private void run() {
        if (deleteExisting) {
            deleteExistingData();
        }

        doLoad();
    }

    private void doLoad() {
        for (long i = 0; i < nOps; i++) {
            addRow(i);
        }
        displayRow(table);
        System.out.println(nOps + " new records added");
        store.close();
    }

    private void addRow(final long i) {

        final String email = "user" + i + "@example.com";

        final UserInfo userInfo = new UserInfo(email);
        final String gender = (i % 2 == 0) ? "F" : "M";
        final int mod = (int) (i % 10);

        /* Pad the number for nicer column alignment. */
        String iStr = String.format("%03d", i);
        userInfo.setGender(gender);
        userInfo.setName((("F".equals(gender)) ? "Ms." : "Mr.") +
                         " Number-" + iStr);
        userInfo.setAddress(iStr + " Example St, Example Town, AZ");
        userInfo.setPhone("000.000.0000".replace('0', (char) ('0' + mod)));

        final Row row = table.createRow();

        row.put("email", userInfo.getEmail());
        row.put("name", userInfo.getName());
        row.put("gender", userInfo.getGender());
        row.put("address", userInfo.getAddress());
        row.put("phone", userInfo.getPhone());

        tableAPI.putIfAbsent(row, null, null);
    }

    private void deleteExistingData() {

        /* Get an iterator over all the primary keys in the table. */
        final TableIterator<PrimaryKey> itr =
            tableAPI.tableKeysIterator(table.createPrimaryKey(), null, null);

        /* Delete each row from the table. */
        long cnt = 0;
        while (itr.hasNext()) {
            tableAPI.delete(itr.next(), null, null);
            cnt++;
        }
        itr.close();
        System.out.println(cnt + " records deleted");
    }

    /*
     * Convenience method for displaying output when debugging.
     */
    private void displayRow(Table tbl) {
        final TableIterator<Row> itr =
            tableAPI.tableIterator(tbl.createPrimaryKey(), null, null);
        while (itr.hasNext()) {
            System.out.println(itr.next());
        }
        itr.close();
    }
}
