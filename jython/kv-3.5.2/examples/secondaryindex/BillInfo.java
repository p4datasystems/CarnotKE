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

import java.util.Arrays;
import java.util.List;

import oracle.kv.Key;
import oracle.kv.Value;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

/**
 * This class embodies the BillInfo schema.
 * <p>
 * BillInfo has an "id" as its primary key and several attributes in the
 * data portion as defined in "billinfo-schema.avsc":
 * <pre>
 * { "type": "record", "name": "PrimaryDBValue", "namespace":
 *   "oracle.kv.secondaryIndex", "fields":
 *   [ {"name": "name", "type": "string", "default": ""},
 *     {"name": "email", "type": "string", "default": ""},
 *     {"name": "phone", "type": "string", "default": ""},
 *     {"name": "date", "type": "string", "default": ""},
 *     {"name": "cost", "type": "long", "default": 0} ] }
 * </pre>
 * This class also implements methods to construct primary DB KV pairs
 * using the Binding class.
 * <p>
 * The major key for BillInfo records has two elements:
 * (1) The literal "BI", and
 * (2) The "id" field.
 * There is no minor key component for BillInfo records. The Value portion
 * of these records are the other components (name, email, phone, date, cost)
 * and are serialized using GenericAvroBinding.
 */
public class BillInfo {

    public static final String SCHEMA_NAME = "secondaryindex.billinfo";

    /* Flags */
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String DATE = "date";
    public static final String COST = "cost";

    /**
     * An identifier used as the first String component of the Key's major path.
     */
    public static final String BILL_INFO_TYPE = "BI";

    /**
     * Id is an unique identifier and is used to construct the Key's major
     * path.
     */
    private final String id;

    /* Persistent fields stored in the Value. */
    private String name;
    private String email;
    private String phone;
    private String date;
    private long cost;

    BillInfo(String id) {
        assert id != null;
        this.id = id;
    }

    BillInfo(String id,
             String name,
             String email,
             String phone,
             String date,
             long cost) {
        assert id != null;
        this.id = id;
        this.name = name == null ? "" : name;
        this.email = email == null ? "" : email;
        this.phone = phone == null ? "" : phone;
        this.date = date == null ? "" : date;
        this.cost = (Long) cost == null ? 0 : cost;
    }

    BillInfo(Binding binding, Key key, Value value) {
        List<String> majorPath = key.getMajorPath();
        if (majorPath.size() != 2 || !BILL_INFO_TYPE.equals(majorPath.get(0))) {
            throw new RuntimeException("Wrong primary record type.");
        }
        this.id = majorPath.get(1);
        GenericRecord record = binding.toObject(value);
        name = record.get(NAME).toString();
        email = record.get(EMAIL).toString();
        phone = record.get(PHONE).toString();
        date = record.get(DATE).toString();
        cost = (Long) record.get(COST);
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    String getPhone() {
        return phone;
    }

    void setPhone(String phone) {
        this.phone = phone;
    }

    String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    long getCost() {
        return cost;
    }

    void setCost(long cost) {
        this.cost = cost;
    }

    /**
     * Checks whether the given attribute name belongs to the BillInfo schema.
     */
    static boolean isProperty(String attributeName) {
        try {
            Class.forName
                ("secondaryindex.BillInfo").getDeclaredField(attributeName);
        } catch (NoSuchFieldException e) {
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }

    static Key getBillInfoPrefixKey() {
        return Key.createKey(BILL_INFO_TYPE);
    }

    Key getStoreKey() {
        return Key.createKey(Arrays.asList(BILL_INFO_TYPE, id));
    }

    /**
     * Serializes bill attributes into the byte array of a Value.
     */
    Value getStoreValue(Binding binding) {
        final GenericRecord rec =
            new GenericData.Record(binding.getSchema(SCHEMA_NAME));
        rec.put(NAME, name);
        rec.put(EMAIL, email);
        rec.put(PHONE, phone);
        rec.put(DATE, date);
        rec.put(COST, cost);
        return binding.toValue(rec);
    }

    @Override
    public String toString() {
        return "BillInfo [id=" + id + ", name=" + name + ", email=" + email +
            ", phone=" + phone + ", date=" + date + ", cost=" + cost + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (cost ^ (cost >>> 32));
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((phone == null) ? 0 : phone.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BillInfo other = (BillInfo) obj;
        if (cost != other.cost) {
            return false;
        }
        if (date == null) {
            if (other.date != null) {
                return false;
            }
        } else if (!date.equals(other.date)) {
            return false;
        }
        if (email == null) {
            if (other.email != null) {
                return false;
            }
        } else if (!email.equals(other.email)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (phone == null) {
            if (other.phone != null) {
                return false;
            }
        } else if (!phone.equals(other.phone)) {
            return false;
        }
        return true;
    }
}
