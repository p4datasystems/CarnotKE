package org.python.ReL;

import java.sql.SQLException;
import java.util.*;

public class SIMHelper {
    PyRelConnection connection = null;
    final String schemaString = "SCHEMA";
    String NoSQLNameSpacePrefix = "";

    /**
     * Build a SIHHelper to process SIM commands and execute them against a connection to
     * an Oracle semantic DB.
     * <p>
     * Note:  The connection should not be closed within this class, as it will be closed
     * by its parent (invoker).
     *
     * @param conn
     */
    public SIMHelper(PyRelConnection conn)
    {
        connection = conn;
        if (connection.getConnectionDB().equals("OracleNoSQL"))
            NoSQLNameSpacePrefix = connection.getDatabase().getNameSpacePrefix();
    }

    public String executeFrom(String className, List<String> dvaAttribs, List<String> evaAttribs,
                              Map<String, String> whereAttrValues) throws SQLException
    {
        // assume only a single 'dva OF eva' form, for now
        List<List<String>> evaOfChains = new ArrayList<List<String>>();
        //String dvaOfEva = null;
        // the variable name for the dva e.g. lastnameOFspouse
        //String dvaOfEvaVar = dvaOfEva + "OF" + eva;
        for (String evaAttr : evaAttribs) {
            // e.g.  ["firstname", "OF", "spouse", "OF", "children"]
            List<String> evaOfChain = trimAndSplitOnDelim(evaAttr, " ");
            // evaOfChain.removeAll(Collections.singletonList("OF"));
            evaOfChain.removeAll(Collections.singletonList("OF"));
            Collections.reverse(evaOfChain);
            evaOfChains.add(evaOfChain); // e.g.  ["children", "spouse", "firstname"]
        }

        // Process Class Names
        String colNames = "";
        Map<String, String> colNameToLabelMap = new HashMap<String, String>();
        String qBody = "";
        String projectString = "";
        if (connection.getConnectionDB().equals("OracleNoSQL")) {
            qBody = "GRAPH " + NoSQLNameSpacePrefix + ":" + className + "_" + schemaString + " { ?indiv rdf:type "
                        + NoSQLNameSpacePrefix + ":" + className + " } GRAPH " + NoSQLNameSpacePrefix + ":" + className
                        + " { ";
        }
        else {
            qBody = "    GRAPH <" + className + "_" + schemaString + "> { ?indiv rdf:type :" + className + " }\n";
        }

        // Process DVAs
        for (int i = 0; i < dvaAttribs.size(); i++) {
            String attrURI = dvaAttribs.get(i);
            String attrName = getName(attrURI);
            if (i == 0)
                colNames += " " + attrName;
            else
                colNames += ", " + attrName;
            projectString += "?" + attrName + " ";
            qBody += (connection.getConnectionDB().equals("OracleNoSQL"))
                    ? " ?indiv " + NoSQLNameSpacePrefix + ":" + attrName + " ?" + attrName + " ."
                    : "  ?indiv " + NoSQLNameSpacePrefix + ":" + attrName + " ?" + attrName + " .\n";
        }

        // Process WHERE Clause
        for (String whereAttr : whereAttrValues.keySet()) {
            qBody += (connection.getConnectionDB().equals("OracleNoSQL"))
                     ? " ?indiv " + NoSQLNameSpacePrefix + ":" + whereAttr + "\"" + whereAttrValues.get(whereAttr) + "\"^^xsd:string ."
                     : "	?indiv " + NoSQLNameSpacePrefix + ":" + whereAttr + " " + NoSQLNameSpacePrefix + ":"
                             + whereAttrValues.get(whereAttr) + " .\n";
        }
        if (connection.getConnectionDB().equals("OracleNoSQL"))
            qBody += " } ";

        // Process EVAs
        if (evaOfChains.size() > 0) {
            qBody += (connection.getConnectionDB().equals("OracleNoSQL"))
                     ? " OPTIONAL { "
                     : "   OPTIONAL { \n";
            int i = -1;
            int h = -1;
            String previous_evaOfChain0 = "";
            for (List<String> evaOfChain : evaOfChains) {
                String evaColName = ""; // the retrieval last var name
                for (int l = evaOfChain.size() - 1; l >= 0; l--) {
                    // evaColName = ((evaColName.length() == 0) ? "" : evaColName + "OF") + evaOfChain.get(l);
                    evaColName = ((evaColName.length() == 0) ? "" : evaColName + "AT") + evaOfChain.get(l);
                }
                // The following will only work if evaOfChains is sorted on the first element of each list.
                if (previous_evaOfChain0.equals("") || !previous_evaOfChain0.equals(evaOfChain.get(0)))
                    i++;
                h++;
                previous_evaOfChain0 = evaOfChain.get(0);
                String priorVarName = null; // previous var
                String thisVarName = "?x" + i + "_0";
                // e.g. lastName == "firstnameOFspouseOFchildren"
                qBody += (connection.getConnectionDB().equals("OracleNoSQL"))
                         ? "GRAPH ?g" + i + " { ?indiv " + NoSQLNameSpacePrefix + ":" + evaOfChain.get(0)
                                 + " " + thisVarName + " . }"

                         : "      ?indiv " + NoSQLNameSpacePrefix + ":" + evaOfChain.get(0) + " "
                                 + thisVarName + " .\n";
                for (int j = 1; j < evaOfChain.size(); j++) {
                    priorVarName = "x" + i + "_" + (j - 1);
                    thisVarName = "x" + h + "_" + j;
                    if (j == evaOfChain.size() - 1) {
                        if (colNames.length() > 0)
                            colNames += ", ";
                        colNames += thisVarName;
                        colNameToLabelMap.put(thisVarName.toUpperCase(), evaColName.toUpperCase());
                    }
                    projectString += "?" + thisVarName + " ";
                    qBody += (connection.getConnectionDB().equals("OracleNoSQL"))
                             ? " GRAPH ?g" + j + " { ?" + priorVarName + " " + NoSQLNameSpacePrefix + ":" + evaOfChain
                                .get(j) + " ?" + thisVarName + " . }"

                             : "      ?" + priorVarName + " " + NoSQLNameSpacePrefix + ":" + evaOfChain.get(j)
                                     + " ?" + thisVarName + " .\n";
                }
            }
            qBody += "      } \n";
        }
        String query = "";
        if (connection.getConnectionDB().equals("OracleNoSQL"))
            query = "select " + projectString + " where { " + qBody + " }";
        else {
            query = "SELECT DISTINCT " + colNames + "\n from table(\n" +
                    "   sem_match('select * where {\n" +
                    qBody;
            query += "   }',\n" +
                    "	SEM_MODELS('" + connection.getModel() + "'), null,\n" +
                    "	SEM_ALIASES( SEM_ALIAS('', '" + connection.getNamespace() + "')), null) )";
            // System.out.println(query);
            // SPARQLDoer.executeAndPrintRdfSelect(connection, query, colNameToLabelMap);
        }
        return query;
    }

    /**
     * Trim and split String on a delimeter string.
     * <p>
     * 1. trim leading/trailing spaces
     * 2. split to parts on " "
     * 3. trim each part, removing empty parts
     *
     * @param s
     * @return trimmed split parts
     */
    private List<String> trimAndSplitOnDelim(String s, String delim)
    {
        String r = s.trim();
        String[] parts = s.split(delim);
        List<String> parts2 = new ArrayList<String>();
        for (String part : parts) {
            part = part.trim();
            if (part.length() > 0) {
                parts2.add(part);
            }
        }
        return parts2;
    }


    /*
     * Insert an instance of a particular class into the database.
     * This is a triple <instance, rdf:type, class>
     */

    public void executeInstance(String instanceName, String className)
    {
        try {
            SPARQLDoer.insertObjectPropQuad(connection, instanceName.toUpperCase()
                    .trim(), "rdf:type", className.trim());
        } catch (Exception e) {
            System.out.println("Database error");
        }
    }
    /*
     * Insert class member restrictions into the database.
     */

    public void executeMemberData(String instance, String type, String name, String value)
    {
        try {
            //build attribute type
            StringBuilder s = new StringBuilder();
            s.append('"' + value + '"' + "^^");
            if (type == "str") {
                s.append("xsd:string");
            }
            else if (type == "int") {
                s.append("xsd:integer");
            }
            else if (type == "bool") {
                s.append("xsd:boolean");
            }
            else {
                s.append(type);
            }
            SPARQLDoer.insertObjectPropQuad(connection, instance.toUpperCase().trim(), name, s.toString());
        } catch (Exception e) {
            System.out.println("Error occured when adding member data.\n" +
                    e.getMessage());
        }
    }

    private static String getName(String str)
    {
        return str.substring(str.lastIndexOf("#") + 1);
    }

}

