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

package externaltables;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.KeyValueVersion;
import oracle.kv.Value;
import oracle.kv.exttab.Formatter;
import oracle.kv.exttab.TableFormatter;
import oracle.kv.table.FieldValue;
import oracle.kv.table.Row;

/**
 * A simple Formatter and TableFormatter implementation used by the External
 * Tables Cookbook example.
 * This class formats UserInfo records from the NoSQL Database to be
 * suitable for reading by the NOSQL_DATA Oracle Database table. The
 * {@link oracle.kv.exttab.Formatter#toOracleLoaderFormat} method accepts a
 * {@link oracle.kv.KeyValueVersion} and
 * {@link oracle.kv.exttab.TableFormatter#toOracleLoaderFormat} method accepts
 * {@link oracle.kv.table.Row}, both return a String which is formatted as a
 * list of key/values
 * (from {@link oracle.kv.exttab.Formatter#toOracleLoaderFormat} method),
 * or fields (from {@link oracle.kv.exttab.TableFormatter#toOracleLoaderFormat}
 * method) separated by delimiter "|". The return String can be interpreted
 * by the ACCESS PARAMETERS of the External Table definition.
 */
public class MyFormatter implements Formatter, TableFormatter {
    private static final String USER_OBJECT_TYPE = "user";
    private static final String INFO_PROPERTY_NAME = "info";

    /**
     * @hidden
     */
    public MyFormatter() { }

    @Override
    public String toOracleLoaderFormat(final KeyValueVersion kvv,
                                       final KVStore kvStore) {
        final Key key = kvv.getKey();
        final Value value = kvv.getValue();

        final List<String> majorPath = key.getMajorPath();
        final List<String> minorPath = key.getMinorPath();
        final String objectType = majorPath.get(0);

        if (!USER_OBJECT_TYPE.equals(objectType)) {
            throw new IllegalArgumentException("Unknown object type: " + key);
        }

        final String email = majorPath.get(1);
        final String propertyName =
            (minorPath.size() > 0) ? minorPath.get(0) : null;

        if (INFO_PROPERTY_NAME.equals(propertyName)) {
            final ByteArrayInputStream bais =
                new ByteArrayInputStream(value.getValue());
            final DataInputStream dis = new DataInputStream(bais);

            try {

                /* Name is not used. */
                /* final String name = */ readString(dis);
                final String gender = readString(dis);
                final String address = readString(dis);
                final String phone = readString(dis);
                final StringBuilder sb = new StringBuilder();
                sb.append(email).append("|");
                sb.append(gender).append("|");
                sb.append(address).append("|");
                sb.append(phone);
                return sb.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    /**
     * Converts Row from TableAPI to a string that will be consumed
     * by ORA process.
     */
    @Override
    public String toOracleLoaderFormat(final Row row,
                                       final KVStore kvStore) {
        String ret;
        if (row == null) {
            throw new Error("No matching row found");
        }

        /*
         * use table simpleUsers in TableAPI example defined in
         * KVHOME/examples/table/create_table.kvs
         */
        try {
            final String delimiter = "|";
            final StringBuilder sb = new StringBuilder();
            boolean needDelimiter = false;
            final Vector<String> allCols = new Vector<String>();
            allCols.add("userID");
            allCols.add("firstName");
            allCols.add("lastName");

            for (String col : allCols) {
                /* skip first delimiter */
                if (needDelimiter) {
                    sb.append(delimiter);
                } else {
                    needDelimiter = true;
                }

                /*
                 * For simple data type, it will convert to string
                 * automatically, and for other non-simple data types,
                 * it will convert to JSON. If the field is null, append
                 * nothing and Oracle External table will treat it as
                 * missing value and set it as null.
                 */
                final FieldValue v = row.get(col);
                if (!v.isNull()) {
                    sb.append(row.get(col));
                } else {
                    /* append nothing for null values */
                }
            }
            ret = sb.toString();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }


    private String readString(final DataInput in)
        throws IOException {

        if (!in.readBoolean()) {
            return null;
        }

        return in.readUTF();
    }
}
