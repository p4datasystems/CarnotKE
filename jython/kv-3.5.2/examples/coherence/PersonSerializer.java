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

package coherence;

import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

/**
 * This class provides POF serialization of Person.
 */
public class PersonSerializer implements PofSerializer {
    final private static int VERSION_ID = 1;
    final private static int FIRST_NAME = 1;
    final private static int LAST_NAME = 2;
    final private static int PHONE = 3;
    final private static int AGE = 4;

    /**
     * Default constructor
     */
    public PersonSerializer() {
    }

    /**
     * Serialize a Person object.
     *
     * @param out The PofWriter to write to
     * @param o The Person object to write
     * @throws IOException if the object o is not a Person
     */
    @Override
    public void serialize(PofWriter out, Object o)
        throws IOException
    {
        if (o.getClass() == Person.class) {
            out.setVersionId(VERSION_ID);
            out.writeString(FIRST_NAME, ((Person)o).getFirstname());
            out.writeString(LAST_NAME, ((Person)o).getLastname());
            out.writeString(PHONE, ((Person)o).getPhone());
            out.writeInt(AGE, ((Person)o).getAge());
            out.writeRemainder(null);
        } else {
            throw new IOException(
                "Invalid type presented to Person Serializer: " + o.getClass());
        }
    }

    /**
     * Deserialize a Person object.
     *
     * @param in The PofReader to read from
     * @return  The Person object read from the input stream
     * @throws IOException if the stream does not contain the serialization
     *         of a Person object in an expected version.
     */
    @Override
    public Object deserialize(PofReader in)
        throws IOException
    {
        if (in.getVersionId() != VERSION_ID) {
            throw new IOException(
                "PersonSerializer encountered unexpected " +
                "version id: " + in.getVersionId());
        }

        final String firstName = in.readString(FIRST_NAME);
        final String lastName = in.readString(LAST_NAME);
        final String phone = in.readString(PHONE);
        final int age = in.readInt(AGE);
        in.readRemainder();

        final Person person = new Person();
        person.setFirstname(firstName);
        person.setLastname(lastName);
        person.setPhone(phone);
        person.setAge(age);
        return person;
    }
}

