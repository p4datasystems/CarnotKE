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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.kv.Value;
import oracle.kv.Value.Format;
import oracle.kv.avro.AvroCatalog;
import oracle.kv.avro.GenericAvroBinding;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

/**
 * Implements a generic conversion between Value and Avro schema instances.
 * <p>
 * The class is used by IndexViewService to parse the Value part of KV
 * pairs. It also supplies some general conversion methods for user records
 * with different schemas. The example defines a BillInfo as its primary DB
 * record schema which is defined in the resource file
 * "billinfo-schema.avsc".
 */
public class Binding {

    private final GenericAvroBinding binding;

    private Map<String, Schema> schemaMap = new HashMap<String, Schema>();

    public Binding(AvroCatalog avroCatalog) {
        if (avroCatalog == null) {
            throw new RuntimeException
                ("The input AvroCatalog can not be null!");
        }

        /* Creates binding from schemas in the Oracle NoSQL Database. */
        schemaMap = avroCatalog.getCurrentSchemas();
        binding = avroCatalog.getGenericMultiBinding(schemaMap);
    }

    /**
     * Returns the Value that conforms to the schema of the given
     * {@link GenericRecord}.
     *
     * @param record
     *
     * @return Value
     */
    public Value toValue(GenericRecord record) {
        return binding.toValue(record);
    }

    /**
     * Returns the deserialized {@link GenericRecord} instance. If the given
     * Value is not in Avro format then null will be returned.
     *
     * @param value
     *
     * @return a GenericRecord or null if the given Value is not an Avro data.
     */
    public GenericRecord toObject(Value value) {
        if (Format.AVRO.equals(value.getFormat())) {
            return binding.toObject(value);
        }

        return null;
    }

    /**
     * Given a list of Avro field names, extracts the field values from the
     * {@code value} parameter. null is returned if the given Value is not
     * valid Avro data.
     *
     * @param value
     *
     * @param fieldNames the Avro field names to extract from value. The fields
     * must be contained in the value's schema definition.
     *
     * @return a list holding field values in the same order as the input field
     * name list. null is return if the given Value is not valid Avro data.
     */
    public List<Object> toFields(Value value, List<String> fieldNames) {
        List<Object> result = null;
        if (Format.AVRO.equals(value.getFormat())) {
            result = new ArrayList<Object>();
            GenericRecord rec = binding.toObject(value);
            for (String fieldName : fieldNames) {
                Object fieldValue = rec.get(fieldName);
                if (fieldValue == null) {
                    throw new RuntimeException
                        ("Field does not exist in the schema. fieldName = " +
                         fieldName);
                }
                result.add(fieldValue);
            }
        }

        return result;
    }

    /**
     * Returns the schema name of value. If the value is not an Avro
     * data then null is returned.
     *
     * @param value
     *
     * @return null if the given Value is not valid Avro data.
     */
    public String getSchemaName(Value value) {
        if (Format.AVRO.equals(value.getFormat())) {
            GenericRecord rec = binding.toObject(value);
            return rec.getSchema().getFullName();
        }
        return null;
    }

    /**
     * Returns the Schema object for the given schema name.
     *
     * @param schemaName
     *
     * @return Schema
     */
    public Schema getSchema(String schemaName) {
        return schemaMap.get(schemaName);
    }
}
