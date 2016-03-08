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

import oracle.kv.Key;
import oracle.kv.Value;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

/**
 * Holds the session duration attribute that is stored as the {@code
 * Value} for the "/user/EMAIL/-/login/TIMESTAMP" {@code Key}.
 * Illustrates the use of a timestamp as a {@code Key} component.  The
 * email and timestamp uniquely identify the login event and the user
 * session.
 */
class LoginSession {

    /*
     * The email address is a unique identifier and is used to construct
     * the Key's major path.
     */
    private final String email;

    /*
     * The login time is a unique identifier for each session and is used to
     * construct the Key's minor path.
     */
    private final long loginTime;

    /* Persistent fields stored in the Value. */
    private int sessionDuration;

    /**
     * Constructs a user object with its unique identifiers, the email address
     * and login time.
     */
    LoginSession(String email, long loginTime) {
        this.email = email;
        this.loginTime = loginTime;
    }

    /**
     * Returns the email identifier.
     */
    String getEmail() {
        return email;
    }

    /**
     * Returns the login time identifier.
     */
    long getLoginTime() {
        return loginTime;
    }

    /**
     * Changes the session duration.
     */
    void setSessionDuration(int sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    /**
     * Returns the session duration.
     */
    int getSessionDuration() {
        return sessionDuration;
    }

    /**
     * Returns a Key that can be used to write or read the LoginSession.
     */
    Key getStoreKey() {
        return KeyDefinition.makeLoginSessionKey(email, loginTime);
    }

    /**
     * Serializes the only attribute, session duration, into the byte array of
     * a Value.
     */
    Value getStoreValue(Bindings bindings) {
        final GenericRecord rec =
            new GenericData.Record(bindings.getLoginSessionSchema());
        rec.put("sessionDuration", sessionDuration);
        return bindings.getLoginSessionBinding().toValue(rec);
    }

    /**
     * Deserializes the only attribute, session duration, from the byte array
     * of a Value.
     */
    void setStoreValue(Bindings bindings, Value value) {
        final GenericRecord rec =
            bindings.getLoginSessionBinding().toObject(value);
        sessionDuration = (Integer) rec.get("sessionDuration");
    }

    @Override
    public String toString() {
        return "<LoginSession " + email +
               "\n    loginTime: " + KeyDefinition.formatTimestamp(loginTime) +
               ", sessionDuration: " +
               KeyDefinition.formatDuration(sessionDuration) +
               ">";
    }
}
