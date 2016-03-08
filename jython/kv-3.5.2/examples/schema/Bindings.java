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

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;

import oracle.kv.avro.AvroCatalog;
import oracle.kv.avro.GenericAvroBinding;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;

/**
 * Contains the Avro schemas and bindings used to perform serialization of the
 * Value in a kv pair.  The Avro schemas are stored as resource files, and
 * would normally be part of the application jar file.
 * <p>
 * In this example, GenericAvroBinding is used.  Other types of bindings are
 * also available. See the {@link oracle.kv.avro} package and the Oracle NoSQL
 * Database Getting Started Guide for more information.
 */
class Bindings {
    private static final String GENDER_SCHEMA = "schema.Gender";
    private static final String USER_INFO_RESOURCE = "user-info.avsc";
    private static final String USER_INFO_SCHEMA = "schema.UserInfo";
    private static final String USER_IMAGE_RESOURCE = "user-image.avsc";
    private static final String USER_IMAGE_SCHEMA = "schema.UserImage";
    private static final String LOGIN_SESSION_RESOURCE = "login-session.avsc";
    private static final String LOGIN_SESSION_SCHEMA = "schema.LoginSession";
    private static final String LOGIN_SUMMARY_RESOURCE = "login-summary.avsc";
    private static final String LOGIN_SUMMARY_SCHEMA = "schema.LoginSummary";

    private final Schema genderSchema;
    private final Schema userInfoSchema;
    private final Schema userImageSchema;
    private final Schema loginSessionSchema;
    private final Schema loginSummarySchema;
    private final GenericAvroBinding userInfoBinding;
    private final GenericAvroBinding userImageBinding;
    private final GenericAvroBinding loginSessionBinding;
    private final GenericAvroBinding loginSummaryBinding;

    Bindings(AvroCatalog avroCatalog) {

        /* Parse all schema resource files. */
        final Parser parser = new Parser();
        parseResource(parser, USER_INFO_RESOURCE);
        parseResource(parser, USER_IMAGE_RESOURCE);
        parseResource(parser, LOGIN_SESSION_RESOURCE);
        parseResource(parser, LOGIN_SUMMARY_RESOURCE);

        /* Get schemas by name from parser. */
        final Map<String, Schema> types = parser.getTypes();
        genderSchema = types.get(GENDER_SCHEMA);
        userInfoSchema = types.get(USER_INFO_SCHEMA);
        userImageSchema = types.get(USER_IMAGE_SCHEMA);
        loginSessionSchema = types.get(LOGIN_SESSION_SCHEMA);
        loginSummarySchema = types.get(LOGIN_SUMMARY_SCHEMA);

        /* Create bindings from schemas. */
        userInfoBinding = avroCatalog.getGenericBinding(userInfoSchema);
        userImageBinding = avroCatalog.getGenericBinding(userImageSchema);
        loginSessionBinding =
            avroCatalog.getGenericBinding(loginSessionSchema);
        loginSummaryBinding =
            avroCatalog.getGenericBinding(loginSummarySchema);
    }

    private void parseResource(Parser parser, String resourceName) {
        final InputStream in = getClass().getResourceAsStream(resourceName);
        if (in == null) {
            throw new RuntimeException("Resource not found in classpath: " +
                                       resourceName);
        }
        try {
            try {
                parser.parse(in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error parsing schema: " +
                                       resourceName, e);
        }
    }

    Schema getGenderSchema() {
        return genderSchema;
    }

    Schema getUserInfoSchema() {
        return userInfoSchema;
    }

    Schema getUserImageSchema() {
        return userImageSchema;
    }

    Schema getLoginSessionSchema() {
        return loginSessionSchema;
    }

    Schema getLoginSummarySchema() {
        return loginSummarySchema;
    }

    GenericAvroBinding getUserInfoBinding() {
        return userInfoBinding;
    }

    GenericAvroBinding getUserImageBinding() {
        return userImageBinding;
    }

    GenericAvroBinding getLoginSessionBinding() {
        return loginSessionBinding;
    }

    GenericAvroBinding getLoginSummaryBinding() {
        return loginSummaryBinding;
    }
}
