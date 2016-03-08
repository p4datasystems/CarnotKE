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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import oracle.kv.Key;
import oracle.kv.Value;

/**
 * Defines the use of Keys in this application.
 *<p>
 * In the schema of a KVStore application, an important design aspect is the
 * Key structure, including the use of the Key major path and minor path. In a
 * Key, the major and minor path each consists of a List of String components.
 * In comments here we describe the Key as if it were a single String with '/'
 * separators between components of each path, and a "/-/" separator between the
 * major and minor paths.
 *<p>
 *<code>
 *   /majorComponent1/majorComponent2/-/minorComponent1/minorComponent2
 *</code>
 *<p>
 * In many applications, including this one, the first String component of the
 * major path identifies the object type.  Although in this example there is
 * only one object type -- the "user" type -- it is expected that other types
 * would be present in a real application.  For example, a "group" object type
 * might identify Key/Value pairs that contain sets of user identifiers.  The
 * second component of the major path is the user's unique identifier, the
 * email address in this case.  The Key major path in this application is
 * simply:
 *<p>
 *<code>
 *   /user/EMAIL
 *</code>
 *<p>
 * where "user" is the String constant identifying the object type, and EMAIL
 * is the user's email address.
 *<p>
 * Four different types of Key/Value pairs are maintained for each user, and
 * are identified by the Key minor path.  Because all Key/Value pairs for a
 * given user have the same Key major path, they are stored together physically
 * and can be accessed in a single atomic operation such as KVStore.multiGet or
 * KVStore.execute.  The different data types and the full Key structures are:
 *<p>
 *<code>
 *  /user/EMAIL/-/info
 *</code>
 *<p>
 *    A single Key/Value pair per user containing a set of small attributes
 *    such as name, phone and address.  A UserInfo class instance represents
 *    each Key/Value pair.
 *<p>
 *<code>
 *  /user/EMAIL/-/image
 *</code>
 *<p>
 *    A single Key/Value pair per user containing a potentially large, binary
 *    image.  A UserImage class instance represents each Key/Value pair.
 *<p>
 *<code>
 *  /user/EMAIL/-/login
 *</code>
 *<p>
 *    A single Key/Value pair per user containing a summary of all login
 *    information for the user.  A LoginSummary class instance represents each
 *    Key/Value pair.
 *<p>
 *<code>
 *  /user/EMAIL/-/login/TIMESTAMP
 *</code>
 *<p>
 *    Multiple Key/Value pairs per user, each containing a record of a single
 *    session.  The TIMESTAMP is a UTC-format String identifying the start time
 *    of the session. The TIMESTAMP orders session information chronologically
 *    and can be used to query session information for a specific date/time
 *    interval.  A LoginSession class instance represents each Key/Value pair.
 */
class KeyDefinition {
    static final String USER_OBJECT_TYPE = "user";
    static final String INFO_PROPERTY_NAME = "info";
    static final String IMAGE_PROPERTY_NAME = "image";
    static final String LOGIN_PROPERTY_NAME = "login";

    /*
     * The timestamp for use in the LoginSession key is formatted for proper
     * sorting, with the most significant fields first and fixed size numeric
     * fields.
     *
     * Separator characters between fields are used for readability in the
     * example.  Removing these separators will reduce the key size, and is
     * recommended for best performance.
     */
    static final SimpleDateFormat DATE_TIME_FORMAT =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    static {
        DATE_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    static final SimpleDateFormat TIME_FORMAT =
        new SimpleDateFormat("HH:mm:ss.SSS");
    static {
        TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Returns a Key that can be used as a parentKey to select all user
     * Key/Value pairs when using KVStore.storeIterator or storeKeysIterator.
     */
    static Key makeUserTypeKey() {
        return Key.createKey(USER_OBJECT_TYPE);
    }

    /**
     * Returns a Key that can be used to access UserInfo Key/Value pairs.
     */
    static Key makeUserInfoKey(String email) {
        return Key.createKey(Arrays.asList(USER_OBJECT_TYPE, email),
                             INFO_PROPERTY_NAME);
    }

    /**
     * Returns a Key that can be used to access UserImage Key/Value pairs.
     */
    static Key makeUserImageKey(String email) {
        return Key.createKey(Arrays.asList(USER_OBJECT_TYPE, email),
                             IMAGE_PROPERTY_NAME);
    }

    /**
     * Returns a Key that can be used to access LoginSummary Key/Value pairs.
     */
    static Key makeLoginSummaryKey(String email) {
        return Key.createKey(Arrays.asList(USER_OBJECT_TYPE, email),
                             LOGIN_PROPERTY_NAME);
    }

    /**
     * Returns a Key that can be used to access LoginSession Key/Value pairs.
     */
    static Key makeLoginSessionKey(String email, long loginTimeMs) {
        final String timestamp = formatTimestamp(loginTimeMs);
        return Key.createKey(Arrays.asList(USER_OBJECT_TYPE, email),
                             Arrays.asList(LOGIN_PROPERTY_NAME, timestamp));
    }

    /**
     * Returns a timestamp String for the given time in millis.  The timestamp
     * is used as the last Key component in the LoginSession Key.  It can be
     * used for creating KeyRange objects for selecting a range of LoginSession
     * Key/Value pairs.
     */
    static String formatTimestamp(long millis) {
        /* Format objects are unsynchronized. */
        synchronized (DATE_TIME_FORMAT) {
            return DATE_TIME_FORMAT.format(new Date(millis));
        }
    }

    /**
     * Parses the timestamp and returns the time in millis.  Used to convert
     * the timestamp in a LoginSession key to millis.
     */
    static long parseTimestamp(String timestamp) {
        /* Format objects are unsynchronized. */
        synchronized (DATE_TIME_FORMAT) {
            try {
                return DATE_TIME_FORMAT.parse(timestamp).getTime();
            } catch (ParseException e) {
                throw new IllegalArgumentException(timestamp, e);
            }
        }
    }

    /**
     * Formats a time duration for reporting purposes.
     */
    static String formatDuration(long millis) {
        /* Format objects are unsynchronized. */
        synchronized (TIME_FORMAT) {
            return TIME_FORMAT.format(new Date(millis));
        }
    }

    /**
     * Returns the email address for a given Key.  The 2nd major path component
     * is the email address, for all user objects.
     */
    static String getUserEmail(Key key) {

        final List<String> majorPath = key.getMajorPath();

        if (!USER_OBJECT_TYPE.equals(majorPath.get(0))) {
            throw new IllegalArgumentException("Not a user object: " + key);
        }

        return majorPath.get(1);
    }

    /**
     * Returns the login time in millis for a given LoginSession Key.  The 2nd
     * minor path component is the timestamp.
     */
    static long getSessionLoginTime(Key key) {

        final List<String> majorPath = key.getMajorPath();
        final List<String> minorPath = key.getMinorPath();

        if (!USER_OBJECT_TYPE.equals(majorPath.get(0))) {
            throw new IllegalArgumentException("Not a user object: " + key);
        }

        if (minorPath.size() < 2 ||
            !LOGIN_PROPERTY_NAME.equals(minorPath.get(0))) {
            throw new IllegalArgumentException("Not a LoginSession: " + key);
        }

        final String timestamp = minorPath.get(1);
        return parseTimestamp(timestamp);
    }

    /**
     * Translates the given Key/Value to its corresponding Java object.  This
     * is useful when an arbitrary user Key/Value pair is obtained, for example
     * by iterating over all Key/Value pairs in the store or all Key/Value
     * pairs for a particular user.
     */
    static Object deserializeAny(Bindings bindings, Key key, Value value) {

        final List<String> majorPath = key.getMajorPath();
        final List<String> minorPath = key.getMinorPath();
        final String objectType = majorPath.get(0);

        if (!USER_OBJECT_TYPE.equals(objectType)) {
            throw new IllegalArgumentException("Unknown object type: " + key);
        }

        final String email = majorPath.get(1);
        final String propertyName =
            (minorPath.size() > 0) ? minorPath.get(0) : null;

        if (INFO_PROPERTY_NAME.equals(propertyName) &&
            minorPath.size() == 1) {
            final UserInfo userInfo = new UserInfo(email);
            userInfo.setStoreValue(bindings, value);
            return userInfo;
        }

        if (IMAGE_PROPERTY_NAME.equals(propertyName) &&
            minorPath.size() == 1) {
            final UserImage userImage = new UserImage(email);
            userImage.setStoreValue(bindings, value);
            return userImage;
        }

        if (LOGIN_PROPERTY_NAME.equals(propertyName)) {
            if (minorPath.size() == 1) {
                final LoginSummary loginSummary = new LoginSummary(email);
                loginSummary.setStoreValue(bindings, value);
                return loginSummary;
            }
            if (minorPath.size() == 2) {
                final String timestamp = minorPath.get(1);
                final long loginMs = parseTimestamp(timestamp);
                final LoginSession loginSession =
                    new LoginSession(email, loginMs);
                loginSession.setStoreValue(bindings, value);
                return loginSession;
            }
        }

        throw new IllegalArgumentException("Unknown key property: " + key);
    }
}
