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

package hadoop.hive.table;

import java.security.SecureRandom;

import oracle.kv.FaultException;
import oracle.kv.KVSecurityConstants;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.StatementResult;

import oracle.kv.table.ArrayValue;
import oracle.kv.table.MapValue;
import oracle.kv.table.PrimaryKey;
import oracle.kv.table.RecordValue;
import oracle.kv.table.Row;
import oracle.kv.table.Table;
import oracle.kv.table.TableAPI;
import oracle.kv.table.TableIterator;

/**
 * Class that creates an example table in a given NoSQL Database store and
 * then uses the Table API to populate the table with sample records. The
 * table that is created consists of a variety of NoSQL Table data types;
 * including both primitive and complex types.
 * <p>
 * To run this class, type a command like the following:
 * <pre><code>
 *  &gt; cd  &lt;KVHOME&gt;
 *  &gt; java -classpath lib/kvclient.jar \
 *                   [:lib/oraclepki.jar:lib/osdt_cert.jar:lib/oadt_core.jar] \
 *                    :examples hadoop.hive.table.LoadRmvTable \
 *         -store &lt;storename&gt; -host &lt;hostname&gt; -port &lt;port&gt; \
 *         -nops &lt;number-of-rows-to-generate&gt; \
 *        [-security &lt;login-properties-file&gt;] [-delete]
 * </code></pre>
 * where the additonal JAR files (oraclepki.jar, osdt_cert.jar, and
 * oadt_core.jar) are required only if the password is stored in an Oracle
 * Wallet; which is available only in the enterprise edition of Oracle NoSQL
 * Database.
 * <p>
 * This class demonstrates the use of Oracle NoSQL Table DDL
 * (<em>Data Definition Langue</em>) to create a table named
 * <code>rmvTable</code> with the following format:
 * <pre><code>
 * CREATE TABLE IF NOT EXISTS rmvTable ( \
 *                     zipcode STRING, \
 *                     lastname STRING, \
 *                     firstname STRING, \
 *                     ssn LONG, \
 *                     gender ENUM(male,female), \
 *                     license BINARY(9), \
 *                     phoneinfo MAP(STRING), \
 *                     address RECORD (number INTEGER, \
 *                                     street STRING, \
 *                                     unit INTEGER, \
 *                                     city STRING, \
 *                                     state STRING, \
 *                                     zip INTEGER), \
 *                     vehicleinfo ARRAY (RECORD (type STRING, \
 *                                                make STRING, \
 *                                                model STRING, \
 *                                                class STRING, \
 *                                                color STRING, \
 *                                                value FLOAT, \
 *                                                tax DOUBLE, \
 *                                                paid BOOLEAN)), \
 *                    PRIMARY KEY (SHARD(zipcode), lastname, firstname, ssn))
 * </code></pre>
 */
public final class LoadRmvTable {

    private static final SecureRandom secureRandom = new SecureRandom();

    private final KVStore store;
    private final TableAPI tableAPI;
    private final Table table;

    private long nOps = 10;

    private boolean deleteExisting = false;

    private static final String TABLE_NAME = "rmvTable";

    /*
     * Create the table with the following SHARD and PRIMARY keys:
     *   shardKey : [ "zipcode" ]
     *   primaryKey : [ "zipcode", "lastname", "firstname", "ssn" ]
     */
    private static final String STATEMENT =
        "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
        " (" +
           "zipcode STRING," +
           "lastname STRING," +
           "firstname STRING," +
           "ssn LONG," +
           "gender ENUM(male,female)," +
           "license BINARY(9)," +
           "phoneinfo MAP(STRING)," +
           "address RECORD(number INTEGER," +
                           "street STRING," +
                           "unit INTEGER," +
                           "city STRING," +
                           "state STRING," +
                           "zip INTEGER)," +
           "vehicleinfo ARRAY(" +
                              "RECORD(type STRING," +
                                      "make STRING," +
                                      "model STRING," +
                                      "class STRING," +
                                      "color STRING," +
                                      "value FLOAT," +
                                      "tax DOUBLE," +
                                      "paid BOOLEAN))," +
           "PRIMARY KEY (SHARD(zipcode), lastname, firstname, ssn))";

    public static void main(final String[] args) {
        try {
            final LoadRmvTable loadData = new LoadRmvTable(args);
            loadData.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses command line args and opens the KVStore.
     */
    private LoadRmvTable(final String[] argv) {

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
            } else if ("-security".equals(thisArg)) {
                if (argc < nArgs) {
                    System.setProperty(
                        KVSecurityConstants.SECURITY_FILE_PROPERTY,
                        argv[argc++]);
                } else {
                    usage("-security requires an argument");
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
        createTable();
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
             "\t[-security <login properties file>]\n" +
             "\t-delete (default: false) [delete all existing data]\n");
        System.exit(1);
    }

    private void run() {
        if (deleteExisting) {
            deleteExistingData();
        }

        doLoad();
    }

    private void createTable() {
        try {
            final StatementResult result = store.executeSync(STATEMENT);
            if (result.isSuccessful()) {
                System.out.println("table created [" + TABLE_NAME + "]");
            } else if (result.isCancelled()) {
                System.out.println("table creation CANCELLED [" +
                                   TABLE_NAME + "]");
            } else {
                if (result.isDone()) {
                    System.out.println("table creation FAILED:\n\t" +
                                       STATEMENT);
                    System.out.println("ERROR:\n\t" +
                                       result.getErrorMessage());
                } else {
                    System.out.println("table creation IN PROGRESS:\n\t" +
                                       STATEMENT);
                    System.out.println("STATUS:\n\t" + result.getInfo());
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid statement:");
            e.printStackTrace();
        } catch (FaultException e) {
            System.out.println("Failure on statement execution:");
            e.printStackTrace();
        }
    }

    private void doLoad() {
        for (long i = 0; i < nOps; i++) {
            addRow();
        }
        displayRow(table);
        System.out.println(nOps + " new records added");
        store.close();
    }

    private void addRow() {

        final Row row = table.createRow();

        /* Generate zipcode */
        final StringBuilder zipBuf = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            final int zipDigit = secureRandom.nextInt(10);
            zipBuf.append(zipDigit);
        }
        final String zipStr = zipBuf.toString();
        row.put("zipcode", zipStr);

        /* Generate ssn */
        final StringBuilder ssnBuf = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            final int ssnDigit = secureRandom.nextInt(10);
            ssnBuf.append(ssnDigit);
        }
        row.put("ssn", Long.parseLong(ssnBuf.toString()));

        /* Generate lastname */
        final int lastnameIndx = secureRandom.nextInt(LAST_NAMES.length);
        row.put("lastname", LAST_NAMES[lastnameIndx]);

        /* Generate firstname and gender */
        int firstnameIndx = secureRandom.nextInt(FIRST_NAMES_FEMALE.length);
        String firstname = FIRST_NAMES_FEMALE[firstnameIndx];
        String gender = "female";
        final int maleFemale = secureRandom.nextInt(2);
        if (maleFemale == 0) {
            firstnameIndx = secureRandom.nextInt(FIRST_NAMES_MALE.length);
            firstname = FIRST_NAMES_MALE[firstnameIndx];
            gender = "male";
        }
        row.put("firstname", firstname);
        row.putEnum("gender", gender);

        /* Generate license id as String, but store it as BINARY */
        final StringBuilder licenseBuf = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (i > 0) {
                final int licenseDigit = secureRandom.nextInt(10);
                licenseBuf.append(String.valueOf(licenseDigit));
            } else {
                licenseBuf.append("S");
            }
        }
        row.putFixed("license", (licenseBuf.toString()).getBytes());

        /* Generate phoneinfo */
        final MapValue phoneMap = row.putMap("phoneinfo");
        for (int i = 0; i < PHONE_TYPE.length; i++) {
            final String phonetype = PHONE_TYPE[i];

            final int areaCodeIndx = secureRandom.nextInt(AREA_CODES.length);
            final int exchangeIndx = secureRandom.nextInt(EXCHANGES.length);
            final String areacode = AREA_CODES[areaCodeIndx];
            final String exchange = EXCHANGES[exchangeIndx];

            final StringBuilder phoneBuf = new StringBuilder();
            phoneBuf.append(areacode);
            phoneBuf.append("-");
            phoneBuf.append(exchange);
            phoneBuf.append("-");

            for (int j = 0; j < 4; j++) {
                final int phoneDigit = secureRandom.nextInt(10);
                phoneBuf.append(phoneDigit);
            }
            final String phone = phoneBuf.toString();
            phoneMap.put(phonetype, phone);
        }

        final RecordValue addressRec = row.putRecord("address");

        final int streetNumber = secureRandom.nextInt(99999);
        final int streetNameIndx = secureRandom.nextInt(STREET_NAMES.length);
        final int unitNumber = secureRandom.nextInt(11) - 1;
        final int cityNameIndx = secureRandom.nextInt(CITY_NAMES.length);
        final int stateIndx = secureRandom.nextInt(STATE_ABBREVIATIONS.length);

        addressRec.put("number", streetNumber);
        addressRec.put("street", STREET_NAMES[streetNameIndx]);
        addressRec.put("unit", unitNumber);
        addressRec.put("city", CITY_NAMES[cityNameIndx]);
        addressRec.put("state", STATE_ABBREVIATIONS[stateIndx]);
        addressRec.put("zip", Integer.parseInt(zipStr));

        /* Generate vehicleinfo */
        final ArrayValue vehicleArray = row.putArray("vehicleinfo");

        final int nVehicles = 1 + secureRandom.nextInt(3);

        for (int i = 0; i < nVehicles; i++) {

            final RecordValue vehicleRec = vehicleArray.addRecord();

            final int typeIndx = secureRandom.nextInt(TYPES.length);
            final int makeIndx = secureRandom.nextInt(MAKES.length);
            final int classIndx = secureRandom.nextInt(CLASSES.length);
            final int colorIndx = secureRandom.nextInt(COLORS.length);

            final String type  = TYPES[typeIndx];
            final String make  = MAKES[makeIndx];
            final String vClass = CLASSES[classIndx];
            final String color = COLORS[colorIndx];

            String[] models = MODELS_FORD_AUTO;
            float valueMult = 2.0f;

            if ("suv".equals(type)) {
                valueMult = 4.0f;
                if ("Chrysler".equals(make)) {
                    models = MODELS_CHRYSLER_SUV;
                } else if ("GM".equals(make)) {
                    models = MODELS_GM_SUV;
                } else {
                    /* Default to make "Ford" */
                    models = MODELS_FORD_SUV;
                }
            } else if ("truck".equals(type)) {
                valueMult = 3.0f;
                if ("Chrysler".equals(make)) {
                    models = MODELS_CHRYSLER_TRUCK;
                } else if ("GM".equals(make)) {
                    models = MODELS_GM_TRUCK;
                } else {
                    /* Default to make "Ford" */
                    models = MODELS_FORD_TRUCK;
                }
            } else {
                /* Default to type "auto" */
                if ("Chrysler".equals(make)) {
                    models = MODELS_CHRYSLER_AUTO;
                } else if ("GM".equals(make)) {
                    models = MODELS_GM_AUTO;
                }
            }
            final int modelIndx = secureRandom.nextInt(models.length);
            final String model = models[modelIndx];

            final float baseValue = 10371.59f;
            final float deltaValue = secureRandom.nextFloat();
            final float value = (valueMult * baseValue) + deltaValue;

            final double tax = TAX_RATE * value;

            boolean paid = true;
            final int paidOrNot = secureRandom.nextInt(2);
            if (paidOrNot == 0) {
                paid = false;
            }

            vehicleRec.put("type", type);
            vehicleRec.put("make", make);
            vehicleRec.put("model", model);
            vehicleRec.put("class", vClass);
            vehicleRec.put("color", color);
            vehicleRec.put("value", value);
            vehicleRec.put("tax", tax);
            vehicleRec.put("paid", paid);
        }

        /* Handle duplicates. */
        final PrimaryKey dupKey = row.createPrimaryKey();
        final Row dupRow = tableAPI.get(dupKey, null);
        if (dupRow != null) {
            /* Change the ssn */
            int newSsnDigit = secureRandom.nextInt(10);
            final StringBuilder newSsnBuf = new StringBuilder(newSsnDigit);
            for (int i = 0; i < 8; i++) {
                newSsnDigit = secureRandom.nextInt(10);
                newSsnBuf.append(newSsnDigit);
            }
            row.put("ssn", Long.parseLong(newSsnBuf.toString()));
        }

        /* Finally, write the row created above to the table */
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

    /* Static values used to populate the table that is created above. */

    /* For populating last names. */
    private static final String[] LAST_NAMES = {
        "SMITH", "JOHNSON", "WILLIAMS", "JONES", "BROWN", "DAVIS", "MILLER",
        "WILSON", "MOORE", "TAYLOR", "ANDERSON", "THOMAS", "JACKSON",
        "WHITE", "HARRIS", "MARTIN", "THOMPSON", "GARCIA", "MARTINEZ",
        "ROBINSON", "CLARK", "RODRIGUEZ", "LEWIS", "LEE", "WALKER",
        "HALL", "ALLEN", "YOUNG", "HERNANDEZ", "KING", "WRIGHT", "LOPEZ",
        "HILL", "SCOTT", "GREEN", "ADAMS", "BAKER", "GONZALEZ", "NELSON",
        "CARTER", "MITCHELL", "PEREZ", "ROBERTS", "TURNER", "PHILLIPS",
        "CAMPBELL", "PARKER", "EVANS", "EDWARDS", "COLLINS", "STEWART",
        "SANCHEZ", "MORRIS", "ROGERS", "REED", "COOK", "MORGAN", "BELL",
        "MURPHY", "BAILEY", "RIVERA", "COOPER", "RICHARDSON", "COX", "HOWARD",
        "WARD", "TORRES", "PETERSON", "GRAY", "RAMIREZ", "JAMES", "WATSON",
        "BROOKS", "KELLY", "SANDERS", "PRICE", "BENNETT", "WOOD", "BARNES",
        "ROSS", "HENDERSON", "COLEMAN", "JENKINS", "PERRY", "POWELL", "LONG",
        "PATTERSON", "HUGHES", "FLORES", "WASHINGTON", "BUTLER", "SIMMONS",
        "FOSTER", "GONZALES", "BRYANT", "ALEXANDER", "RUSSELL", "GRIFFIN",
        "DIAZ", "HAYES", "MYERS", "FORD", "HAMILTON", "GRAHAM", "SULLIVAN",
        "WALLACE", "WOODS", "COLE", "WEST", "JORDAN", "OWENS", "REYNOLDS",
        "FISHER", "ELLIS", "HARRISON", "GIBSON", "MCDONALD", "CRUZ",
        "MARSHALL", "ORTIZ", "GOMEZ", "MURRAY", "FREEMAN", "WELLS", "WEBB",
        "SIMPSON", "STEVENS", "TUCKER", "PORTER", "HUNTER", "HICKS",
        "CRAWFORD", "HENRY", "BOYD", "MASON", "MORALES", "KENNEDY", "WARREN",
        "DIXON", "RAMOS", "REYES", "BURNS", "GORDON", "SHAW", "HOLMES", "RICE",
        "ROBERTSON", "HUNT", "BLACK", "DANIELS", "PALMER", "MILLS", "NICHOLS",
        "GRANT", "KNIGHT", "FERGUSON", "ROSE", "STONE", "HAWKINS", "DUNN",
        "PERKINS", "HUDSON", "SPENCER", "GARDNER", "STEPHENS", "PAYNE",
        "PIERCE", "BERRY", "MATTHEWS", "ARNOLD", "WAGNER", "WILLIS", "RAY",
        "WATKINS", "OLSON", "CARROLL", "DUNCAN", "SNYDER", "HART",
        "CUNNINGHAM", "BRADLEY", "LANE", "ANDREWS", "RUIZ", "HARPER", "FOX",
        "RILEY", "ARMSTRONG", "CARPENTER", "WEAVER", "GREENE", "LAWRENCE",
        "ELLIOTT", "CHAVEZ", "SIMS", "AUSTIN", "PETERS", "KELLEY", "FRANKLIN",
        "LAWSON", "FIELDS", "GUTIERREZ", "RYAN", "SCHMIDT", "CARR", "VASQUEZ",
        "CASTILLO", "WHEELER", "CHAPMAN", "OLIVER", "MONTGOMERY", "RICHARDS",
        "WILLIAMSON", "JOHNSTON", "BANKS", "MEYER", "BISHOP", "MCCOY",
        "HOWELL", "ALVAREZ", "MORRISON", "HANSEN", "FERNANDEZ", "GARZA",
        "HARVEY", "LITTLE", "BURTON", "STANLEY", "NGUYEN", "GEORGE", "JACOBS",
        "REID", "KIM", "FULLER", "LYNCH", "DEAN", "GILBERT", "GARRETT",
        "ROMERO", "WELCH", "LARSON", "FRAZIER", "BURKE", "HANSON", "DAY",
        "MENDOZA", "MORENO", "BOWMAN", "MEDINA", "FOWLER", "BREWER", "HOFFMAN",
        "CARLSON", "SILVA", "PEARSON", "HOLLAND", "DOUGLAS", "FLEMING",
        "JENSEN", "VARGAS", "BYRD", "DAVIDSON", "HOPKINS", "MAY", "TERRY",
        "HERRERA", "WADE", "SOTO", "WALTERS", "CURTIS", "NEAL", "CALDWELL",
        "LOWE", "JENNINGS", "BARNETT", "GRAVES", "JIMENEZ", "HORTON",
        "SHELTON", "BARRETT", "OBRIEN", "CASTRO", "SUTTON", "GREGORY",
        "MCKINNEY", "LUCAS", "MILES", "CRAIG", "RODRIQUEZ", "CHAMBERS", "HOLT",
        "LAMBERT", "FLETCHER", "WATTS", "BATES", "HALE", "RHODES", "PENA",
        "BECK", "NEWMAN", "HAYNES", "MCDANIEL", "MENDEZ", "BUSH", "VAUGHN",
        "PARKS", "DAWSON", "SANTIAGO", "NORRIS", "HARDY", "LOVE", "STEELE",
        "CURRY", "POWERS", "SCHULTZ", "BARKER", "GUZMAN", "PAGE", "MUNOZ",
        "BALL", "KELLER", "CHANDLER", "WEBER", "LEONARD", "WALSH", "LYONS",
        "RAMSEY", "WOLFE", "SCHNEIDER", "MULLINS", "BENSON", "SHARP", "BOWEN",
        "DANIEL", "BARBER", "CUMMINGS", "HINES", "BALDWIN", "GRIFFITH",
        "VALDEZ", "HUBBARD", "SALAZAR", "REEVES", "WARNER", "STEVENSON",
        "BURGESS", "SANTOS", "TATE", "CROSS", "GARNER", "MANN", "MACK", "MOSS",
        "THORNTON", "DENNIS", "MCGEE", "FARMER", "DELGADO", "AGUILAR", "VEGA",
        "GLOVER", "MANNING", "COHEN", "HARMON", "RODGERS", "ROBBINS", "NEWTON",
        "TODD", "BLAIR", "HIGGINS", "INGRAM", "REESE", "CANNON", "STRICKLAND",
        "TOWNSEND", "POTTER", "GOODWIN", "WALTON", "ROWE", "HAMPTON", "ORTEGA",
        "PATTON", "SWANSON", "JOSEPH", "FRANCIS", "GOODMAN", "MALDONADO",
        "YATES", "BECKER", "ERICKSON", "HODGES", "RIOS", "CONNER", "ADKINS",
        "WEBSTER", "NORMAN", "MALONE", "HAMMOND", "FLOWERS", "COBB", "MOODY",
        "QUINN", "BLAKE", "MAXWELL", "POPE", "FLOYD", "OSBORNE", "PAUL",
        "MCCARTHY", "GUERRERO", "LINDSEY", "ESTRADA", "SANDOVAL", "GIBBS",
        "TYLER", "GROSS", "FITZGERALD", "STOKES", "DOYLE", "SHERMAN",
        "SAUNDERS", "WISE", "COLON", "GILL", "ALVARADO", "GREER", "PADILLA",
        "SIMON", "WATERS", "NUNEZ", "BALLARD", "SCHWARTZ", "MCBRIDE",
        "HOUSTON", "CHRISTENSEN", "KLEIN", "PRATT", "BRIGGS", "PARSONS",
        "MCLAUGHLIN", "ZIMMERMAN", "FRENCH", "BUCHANAN", "MORAN", "COPELAND",
        "ROY", "PITTMAN", "BRADY", "MCCORMICK", "HOLLOWAY", "BROCK", "POOLE",
        "FRANK", "LOGAN", "OWEN", "BASS", "MARSH", "DRAKE", "WONG",
        "JEFFERSON", "PARK", "MORTON", "ABBOTT", "SPARKS", "PATRICK", "NORTON",
        "HUFF", "CLAYTON", "MASSEY", "LLOYD", "FIGUEROA", "CARSON", "BOWERS",
        "ROBERSON", "BARTON", "TRAN", "LAMB", "HARRINGTON", "CASEY", "BOONE",
        "CORTEZ", "CLARKE", "MATHIS", "SINGLETON", "WILKINS", "CAIN", "BRYAN",
        "UNDERWOOD", "HOGAN", "MCKENZIE", "COLLIER", "LUNA", "PHELPS",
        "MCGUIRE", "ALLISON", "BRIDGES", "WILKERSON", "NASH", "SUMMERS",
        "ATKINS", "WILCOX", "PITTS", "CONLEY", "MARQUEZ", "BURNETT", "RICHARD",
        "COCHRAN", "CHASE", "DAVENPORT", "HOOD", "GATES", "CLAY", "AYALA",
        "SAWYER", "ROMAN", "VAZQUEZ", "DICKERSON", "HODGE", "ACOSTA", "FLYNN",
        "ESPINOZA", "NICHOLSON", "MONROE", "WOLF", "MORROW", "KIRK", "RANDALL",
        "ANTHONY", "WHITAKER", "OCONNOR", "SKINNER", "WARE", "MOLINA", "KIRBY",
        "HUFFMAN", "BRADFORD", "CHARLES", "GILMORE", "DOMINGUEZ", "ONEAL",
        "BRUCE", "LANG", "COMBS", "KRAMER", "HEATH", "HANCOCK", "GALLAGHER",
        "GAINES", "SHAFFER", "SHORT", "WIGGINS", "MATHEWS", "MCCLAIN",
        "FISCHER", "WALL", "SMALL", "MELTON", "HENSLEY", "BOND", "DYER",
        "CAMERON", "GRIMES", "CONTRERAS", "CHRISTIAN", "WYATT", "BAXTER",
        "SNOW", "MOSLEY", "SHEPHERD", "LARSEN", "HOOVER", "BEASLEY", "GLENN",
        "PETERSEN", "WHITEHEAD", "MEYERS", "KEITH", "GARRISON", "VINCENT",
        "SHIELDS", "HORN", "SAVAGE", "OLSEN", "SCHROEDER", "HARTMAN",
        "WOODARD", "MUELLER", "KEMP", "DELEON", "BOOTH", "PATEL", "CALHOUN",
        "WILEY", "EATON", "CLINE", "NAVARRO", "HARRELL", "LESTER", "HUMPHREY",
        "PARRISH", "DURAN", "HUTCHINSON", "HESS", "DORSEY", "BULLOCK",
        "ROBLES", "BEARD", "DALTON", "AVILA", "VANCE", "RICH", "BLACKWELL",
        "YORK", "JOHNS", "BLANKENSHIP", "TREVINO", "SALINAS", "CAMPOS",
        "PRUITT", "MOSES", "CALLAHAN", "GOLDEN", "MONTOYA", "HARDIN", "GUERRA",
        "MCDOWELL", "CAREY", "STAFFORD", "GALLEGOS", "HENSON", "WILKINSON",
        "BOOKER", "MERRITT", "MIRANDA", "ATKINSON", "ORR", "DECKER", "HOBBS",
        "PRESTON", "TANNER", "KNOX", "PACHECO", "STEPHENSON", "GLASS", "ROJAS",
        "SERRANO", "MARKS", "HICKMAN", "ENGLISH", "SWEENEY", "STRONG",
        "PRINCE", "MCCLURE", "CONWAY", "WALTER", "ROTH", "MAYNARD", "FARRELL",
        "LOWERY", "HURST", "NIXON", "WEISS", "TRUJILLO", "ELLISON", "SLOAN ",
        "JUAREZ", "WINTERS", "MCLEAN", "RANDOLPH", "LEON", "BOYER",
        "VILLARREAL", "MCCALL", "GENTRY", "CARRILLO", "KENT", "AYERS", "LARA",
        "SHANNON", "SEXTON", "PACE", "HULL", "LEBLANC", "BROWNING",
        "VELASQUEZ", "LEACH", "CHANG", "HOUSE", "SELLERS", "HERRING", "NOBLE",
        "FOLEY", "BARTLETT", "MERCADO", "LANDRY", "DURHAM", "WALLS", "BARR",
        "MCKEE", "BAUER", "RIVERS", "EVERETT", "BRADSHAW", "PUGH", "VELEZ",
        "RUSH", "ESTES", "DODSON", "MORSE", "SHEPPARD", "WEEKS", "CAMACHO",
        "BEAN", "BARRON", "LIVINGSTON", "MIDDLETON", "SPEARS", "BRANCH",
        "BLEVINS", "CHEN", "KERR", "MCCONNELL", "HATFIELD", "HARDING",
        "ASHLEY", "SOLIS", "HERMAN", "FROST", "GILES", "BLACKBURN", "WILLIAM",
        "PENNINGTON", "WOODWARD", "FINLEY", "MCINTOSH", "KOCH", "BEST",
        "SOLOMON", "MCCULLOUGH", "DUDLEY", "NOLAN", "BLANCHARD", "RIVAS",
        "BRENNAN", "MEJIA", "KANE", "BENTON", "JOYCE", "BUCKLEY", "HALEY",
        "VALENTINE", "MADDOX", "RUSSO", "MCKNIGHT", "BUCK", "MOON", "MCMILLAN",
        "CROSBY", "BERG", "DOTSON", "MAYS", "ROACH", "CHURCH", "CHAN",
        "RICHMOND", "MEADOWS", "FAULKNER", "ONEILL", "KNAPP", "KLINE", "BARRY",
        "OCHOA", "JACOBSON", "GAY", "AVERY", "HENDRICKS", "HORNE", "SHEPARD",
        "HEBERT", "CHERRY", "CARDENAS", "MCINTYRE", "WHITNEY", "WALLER",
        "HOLMAN", "DONALDSON", "CANTU", "TERRELL", "MORIN", "GILLESPIE",
        "FUENTES", "TILLMAN", "SANFORD", "BENTLEY", "PECK", "KEY", "SALAS",
        "ROLLINS", "GAMBLE", "DICKSON", "BATTLE", "SANTANA", "CABRERA",
        "CERVANTES", "HOWE", "HINTON", "HURLEY", "SPENCE", "ZAMORA", "YANG",
        "MCNEIL", "SUAREZ", "CASE", "PETTY", "GOULD", "MCFARLAND", "SAMPSON",
        "CARVER", "BRAY", "ROSARIO", "MACDONALD", "STOUT", "HESTER",
        "MELENDEZ", "DILLON", "FARLEY", "HOPPER", "GALLOWAY", "POTTS",
        "BERNARD", "JOYNER", "STEIN", "AGUIRRE", "OSBORN", "MERCER", "BENDER",
        "FRANCO", "ROWLAND", "SYKES", "BENJAMIN", "TRAVIS", "PICKETT", "CRANE",
        "SEARS", "MAYO", "DUNLAP", "HAYDEN", "WILDER", "MCKAY", "COFFEY",
        "MCCARTY", "EWING", "COOLEY", "VAUGHAN", "BONNER", "COTTON", "HOLDER",
        "STARK", "FERRELL", "CANTRELL", "FULTON", "LYNN", "LOTT", "CALDERON",
        "ROSA", "POLLARD", "HOOPER", "BURCH", "MULLEN", "FRY", "RIDDLE",
        "LEVY", "DAVID", "DUKE", "ODONNELL", "GUY", "MICHAEL", "BRITT",
        "FREDERICK", "DAUGHERTY", "BERGER", "DILLARD", "ALSTON", "JARVIS",
        "FRYE", "RIGGS", "CHANEY", "ODOM", "DUFFY", "FITZPATRICK",
        "VALENZUELA", "MERRILL", "MAYER", "ALFORD", "MCPHERSON", "ACEVEDO",
        "DONOVAN", "BARRERA", "ALBERT", "COTE", "REILLY", "COMPTON", "RAYMOND",
        "MOONEY", "MCGOWAN", "CRAFT", "CLEVELAND", "CLEMONS", "WYNN",
        "NIELSEN", "BAIRD", "STANTON", "SNIDER", "ROSALES", "BRIGHT", "WITT",
        "STUART", "HAYS", "HOLDEN", "RUTLEDGE", "KINNEY", "CLEMENTS",
        "CASTANEDA", "SLATER", "HAHN", "EMERSON", "CONRAD", "BURKS", "DELANEY",
        "PATE", "LANCASTER", "SWEET", "JUSTICE", "TYSON", "SHARPE",
        "WHITFIELD", "TALLEY", "MACIAS", "IRWIN", "BURRIS", "RATLIFF",
        "MCCRAY", "MADDEN", "KAUFMAN", "BEACH", "GOFF", "CASH", "BOLTON",
        "MCFADDEN", "LEVINE", "GOOD", "BYERS", "KIRKLAND", "KIDD", "WORKMAN",
        "CARNEY", "DALE", "MCLEOD", "HOLCOMB", "ENGLAND", "FINCH", "HEAD",
        "BURT", "HENDRIX", "SOSA", "HANEY", "FRANKS", "SARGENT", "NIEVES",
        "DOWNS", "RASMUSSEN", "BIRD", "HEWITT", "LINDSAY", "LE", "FOREMAN",
        "VALENCIA", "ONEIL", "DELACRUZ", "VINSON", "DEJESUS", "HYDE", "FORBES",
        "GILLIAM", "GUTHRIE", "WOOTEN", "HUBER", "BARLOW", "BOYLE", "MCMAHON",
        "BUCKNER", "ROCHA", "PUCKETT", "LANGLEY", "KNOWLES", "COOKE",
        "VELAZQUEZ", "WHITLEY", "NOEL", "VANG" };

    /* For populating female first names. */
    private static final String[] FIRST_NAMES_FEMALE = {
        "MARY", "PATRICIA", "LINDA", "BARBARA", "ELIZABETH", "JENNIFER",
        "MARIA", "SUSAN", "MARGARET", "DOROTHY", "LISA", "NANCY", "KAREN",
        "BETTY", "HELEN", "SANDRA", "DONNA", "CAROL", "RUTH", "SHARON",
        "MICHELLE", "LAURA", "SARAH", "KIMBERLY", "DEBORAH", "JESSICA",
        "SHIRLEY", "CYNTHIA", "ANGELA", "MELISSA", "BRENDA", "AMY", "ANNA",
        "REBECCA", "VIRGINIA", "KATHLEEN", "PAMELA", "MARTHA", "DEBRA",
        "AMANDA", "STEPHANIE", "CAROLYN", "CHRISTINE", "MARIE", "JANET",
        "CATHERINE", "FRANCES", "ANN", "JOYCE", "DIANE", "ALICE", "JULIE",
        "HEATHER", "TERESA", "DORIS", "GLORIA", "EVELYN", "JEAN", "CHERYL",
        "MILDRED", "KATHERINE", "JOAN", "ASHLEY", "JUDITH", "ROSE", "JANICE",
        "KELLY", "NICOLE", "JUDY", "CHRISTINA", "KATHY", "THERESA", "BEVERLY",
        "DENISE", "TAMMY", "IRENE", "JANE", "LORI", "RACHEL", "MARILYN",
        "ANDREA", "KATHRYN", "LOUISE", "SARA", "ANNE", "JACQUELINE", "WANDA",
        "BONNIE", "JULIA", "RUBY", "LOIS", "TINA", "PHYLLIS", "NORMA", "PAULA",
        "DIANA", "ANNIE", "LILLIAN", "EMILY", "ROBIN", "PEGGY", "CRYSTAL",
        "GLADYS", "RITA", "DAWN", "CONNIE", "FLORENCE", "TRACY", "EDNA",
        "TIFFANY", "CARMEN", "ROSA", "CINDY", "GRACE", "WENDY", "VICTORIA",
        "EDITH", "KIM", "SHERRY", "SYLVIA", "JOSEPHINE", "THELMA", "SHANNON",
        "SHEILA", "ETHEL", "ELLEN", "ELAINE", "MARJORIE", "CARRIE",
        "CHARLOTTE", "MONICA", "ESTHER", "PAULINE", "EMMA", "JUANITA", "ANITA",
        "RHONDA", "HAZEL", "AMBER", "EVA", "DEBBIE", "APRIL", "LESLIE",
        "CLARA", "LUCILLE", "JAMIE", "JOANNE", "ELEANOR", "VALERIE",
        "DANIELLE", "MEGAN", "ALICIA", "SUZANNE", "MICHELE" };

    /* For populating male first names. */
    private static final String[] FIRST_NAMES_MALE = {
        "JAMES", "JOHN", "ROBERT", "MICHAEL", "WILLIAM", "DAVID", "RICHARD",
        "CHARLES", "JOSEPH", "THOMAS", "CHRISTOPHER", "DANIEL", "PAUL", "MARK",
        "DONALD", "GEORGE", "KENNETH", "STEVEN", "EDWARD", "KEVIN", "BRIAN",
        "RONALD", "ANTHONY", "KEVIN", "JASON", "MATTHEW", "GARY", "TIMOTHY",
        "JOSE", "LARRY", "JEFFREY", "FRANK", "SCOTT", "ERIC", "STEPHEN",
        "ANDREW", "RAYMOND", "GREGORY", "JOSHUA", "JERRY", "DENNIS", "WALTER",
        "PATRICK", "PETER", "HAROLD", "DOUGLAS", "HENRY", "CARL", "ARTHUR",
        "RYAN", "ROGER", "JOE", "JUAN", "JACK", "ALBERT", "JONATHAN", "JUSTIN",
        "TERRANCE", "GERALD", "KEITH", "SAMUEL", "WILLIE", "RALPH", "LAWRENCE",
        "NICHOLAS", "ROY", "BENJAMIN", "BRUCE", "BRANDON", "ADAM", "HARRY",
        "FRED", "WAYNE", "BILLY", "STEVE", "LOUIS", "JEREMY", "AARON", "RANDY",
        "HOWARD", "EUGENE", "CARLOS", "RUSSELL", "BOBBY", "VICTOR", "MARTIN",
        "ERNEST", "PHILLIP", "TODD", "JESSE", "CRAIG", "ALAN", "ROCH",
        "CLARENCE", "SEAN", "PHILIP", "CHRIS" };

    /* For populating phone nunmber information. */
    private static final String[] PHONE_TYPE = {"home", "cell", "work"};

    private static final String[] AREA_CODES = {
        "313", "617", "978", "408", "901", "616", "303", "415", "603", "213" };

    private static final String[] EXCHANGES = {
        "837", "656", "879", "804", "351", "227", "234", "541", "630", "312" };

    /* For populating addresses. */
    private static final String[] STREET_NAMES = {
        "Second Avenue", "Third Avenue", "First Street", "Fourth Street",
        "Park Street", "Fifth Avenue", "Main Street", "Sixth Avenue",
        "Oak Street", "Seventh Avenue", "Pine Street", "Maple Street",
        "Cedar Street", "Eighth Avenue", "Elm Street", "View Street",
        "Washington Avenue", "Ninth Avenue", "Lake Street",
        "Mullberry Street", "Hill Street", "Vaughan Avenue", "County Road 15",
        "Summer Street", "Durand Street", "MAC Avenue", "Wallbury Court",
        "Lane Avenue", "High Street" };

    private static final String[] CITY_NAMES = {
        "Greenville", "Franklin", "Springfield", "Clinton", "Salem",
        "Bristol", "Georgetown", "Madison", "Arlington", "Cambridge",
        "Lexington", "Concord", "Boston", "Burlington", "Acton" };

    private static final String[] STATE_ABBREVIATIONS = {
        "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI",
        "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI",
        "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC",
        "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT",
        "VT", "VA", "WA", "WV", "WI", "WY" };

    /* For populating vehicleInfo field */
    private static final String[] TYPES = {"auto", "truck", "suv"};
    private static final String[] MAKES = {"Ford", "GM", "Chrysler"};

    private static final String[] MODELS_FORD_AUTO =
        {"Focus", "Taurus", "Fiesta", "Edge"};
    private static final String[] MODELS_FORD_TRUCK = {"F150", "F250", "F350"};
    private static final String[] MODELS_FORD_SUV =
        {"Escape", "Expedition", "Explorer"};

    private static final String[] MODELS_GM_AUTO =
        {"Camaro", "Corvette", "Impala", "Malibu"};
    private static final String[] MODELS_GM_TRUCK =
        {"Sierra", "Silverado1500", "Silverado2500"};
    private static final String[] MODELS_GM_SUV =
        {"Tahoe", "Equinox", "Blazer"};

    private static final String[] MODELS_CHRYSLER_AUTO =
        {"Sebring", "Imperial", "Lebaron", "PTCruiser"};
    private static final String[] MODELS_CHRYSLER_TRUCK =
        {"Ram1500", "Ram2500", "Ram3500"};
    private static final String[] MODELS_CHRYSLER_SUV =
        {"Aspen", "Pacifica", "Journey"};

    private static final String[] CLASSES =
        {"4WheelDrive", "AllWheelDrive", "FrontWheelDrive", "RearWheelDrive"};

    private static final String[] COLORS =
        {"red", "blue", "green", "yellow", "white", "black"};

    /* For computing tax on a vehicle's value. */
    private static final float TAX_RATE = 0.0273f;
}
