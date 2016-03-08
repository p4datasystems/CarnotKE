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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import oracle.kv.Key;
import oracle.kv.Value;

/**
 * A simple class which represents a User in the External Tables Cookbook
 * example.
 */
public class UserInfo {

    /*
     * The email address is a unique identifier and is used to construct
     * the Key's major path.
     */
    private final String email;

    /* Persistent fields stored in the Value. */
    private String name;
    private String gender;
    private String address;
    private String phone;

    private static final String INFO_PROPERTY_NAME = "info";

    /**
     * Constructs a user object with its unique identifier, the email address.
     */
    public UserInfo(final String email) {
        this.email = email;
    }

    /**
     * Returns the email identifier.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Changes the name attribute.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the name attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the gender attribute.
     */
    public void setGender(final String gender) {
        this.gender = gender;
    }

    /**
     * Returns the gender attribute.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Changes the address attribute.
     */
    public void setAddress(final String address) {
        this.address = address;
    }

    /**
     * Returns the address attribute.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Changes the phone attribute.
     */
    public void setPhone(final String phone) {
        this.phone = phone;
    }

    /**
     * Returns the phone attribute.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns a Key that can be used to write or read the UserInfo.
     */
    public Key getStoreKey() {
        return Key.createKey(Arrays.asList(LoadCookbookData.USER_OBJECT_TYPE,
                                           email),
                             INFO_PROPERTY_NAME);
    }

    /**
     * Serializes user info attributes into the byte array of a Value.
     */
    public Value getStoreValue() {

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);

        try {
            writeString(dos, name);
            writeString(dos, gender);
            writeString(dos, address);
            writeString(dos, phone);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Value.createValue(baos.toByteArray());
    }

    /**
     * Utility that writes a UTF string and accomodates null values.
     */
    private void writeString(final DataOutput out, final String val)
        throws IOException {

        if (val == null) {
            out.writeBoolean(false);
            return;
        }
        out.writeBoolean(true);
        out.writeUTF(val);
    }

    @Override
    public String toString() {
        return "<UserInfo " + email +
               "\n    name: " + name + ", gender: " + gender + "," +
               "\n    address: " + address + ", phone: " + phone +
               ">";
    }
}
