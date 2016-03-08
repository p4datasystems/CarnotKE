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

/**
 * The code and scripts in this package demonstrate some of the basic functions
 * of the Oracle NoSQL Database table API, including creation and use of
 * secondary indexes.
 *<p>
 *
 * There are two examples at this time.
 * <ul>
 * <li> {@link table.TableAPIExample}. A class that combines several examples
 * into a single class.
 * <li> {@link table.MapIndexExample}. A class that demonstrates one use of
 * map indexes.
 * </ul>
 * The examples are independent of one another and have no dependencies.
 *
 * <h3>Before Running the Table Examples</h3>
 * <h4>Start a Store</h4>
 * The examples require a running KVStore instance.  The examples themselves
 * create required tables and indexes.  The examples do not clean up the store,
 * but are designed so that if they are run more than once on the same store,
 * they still work.  A KVLite instance is suitable as a running
 * store.  These instructions assume that a store instance has been started
 * and uses <em>hostName</em> to refer to the host name used, <em>port</em> to
 * refer to the port, and <em>storename</em> to refer to the name of the store.
 * <b>install_dir</b> is used to refer to the location of the Oracle NoSQL
 * Database installation.  Here is an example of how to start KVLite.  The
 * root directory needs to exist before creating the store.  The first time
 * KVLite is started it creates the store.  Subsequent restarts will use an
 * existing store if pointed to a root directory that has a store installed.
 * <pre>
 *  java -jar <b>install_dir</b>/lib/kvstore.jar kvlite -host <em>hostName</em> -port <em>port</em>\
         -store <em>storeName</em> -root <em>rootDirectoryOfStore</em>
 * </pre>
 *
 * <h3>Building and Running Examples</h3>
 * <h4>Build the example code</h4>
 * In the directory <b>install_dir</b>/examples/table:
 * <pre>
 *  javac -d . -cp <b>install_dir</b>/lib/kvclient.jar *.java
 * </pre>
 * <h4>Run the example code</h4>
 * This only requires the client interface, in kvclient.jar.  In the
 * examples/table directory:
 * <pre>
 *  java -cp .:<b>install_dir</b>/lib/kvclient.jar table.TableAPIExample \
 *    -host <em>hostName</em> -port <em>port</em> -store <em>storeName</em>
 *</pre>
 * <pre>
 *  java -cp .:<b>install_dir</b>/lib/kvclient.jar table.MapIndexExample \
 *    -host <em>hostName</em> -port <em>port</em> -store <em>storeName</em>
 *</pre>
 *
 *<p>
 */

package table;
