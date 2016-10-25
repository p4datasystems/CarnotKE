package org.python.ReL;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.python.core.PyObject;

import org.python.ReL.PyRelConnection;

/**
 * SPARQLDoer is a set of static utility functions that are useful
 * for generating sparql, and executing the sparql on a database.
 */
public class SPARQLDoer {

    /**
     * Create Sequences, RDF_DATA_TABLE and Model if they don't already exist
     *
     * @param modelName Model name, usually "RDF_MODEL_" + uname
     * @throws SQLException
     */
    public static void createQuadStore(PyRelConnection connection)
    {
        String s = "DECLARE\ncnt NUMBER;\n";
        s += "BEGIN\nSELECT count(*) INTO cnt FROM MDSYS.SEM_MODEL$ WHERE MODEL_NAME = '" + connection.getModel()
                .toUpperCase() + "';\n";
        s += "IF ( cnt = 0 )\nTHEN\n";
        s +=
                "EXECUTE IMMEDIATE 'CREATE SEQUENCE " + connection.getModel() + "_SQNC MINVALUE 1 START WITH 1 INCREMENT BY 1 NOCACHE';\n";
        s += "EXECUTE IMMEDIATE 'CREATE SEQUENCE " + connection.getModel() + "_GUID_SQNC MINVALUE 1 START WITH 1 INCREMENT BY 1 NOCACHE';\n";
        s += "EXECUTE IMMEDIATE 'CREATE TABLE " + connection.getTable() + "( id NUMBER, triple SDO_RDF_TRIPLE_S)';\n";
        s += "SEM_APIS.CREATE_RDF_MODEL('" + connection.getModel() + "', '" + connection.getTable() + "', 'triple');\n";
        s += "END IF;\n";
        s += "END;\n";
        try {
            connection.executeStatement(s);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Insert an ObjectProperty quad statement into the RDF data table
     *
     * @param subject  The subject of the statement  e.g. PERSONT
     * @param property The object-property  e.g. rdf:type
     * @param object   The object of the statement  e.g. rdfs:Class
     * @throws SQLException
     */
    public static void insertObjectPropQuad(PyRelConnection connection,
                                            String subject, String property,
                                            String object) throws SQLException
    {
        String quadName = connection.getModel() + ":<" + connection.getGraph() + ">";

        if (subject.indexOf(":") < 0) {
            // no specified connection.getNamespace():  use current default connection.getNamespace()
            subject = connection.getNamespace() + subject;
        }
        if (property.indexOf(":") < 0) {
            // no specified connection.getNamespace():  use current default connection.getNamespace()
            property = connection.getNamespace() + property;
        }
        if (object.indexOf(":") < 0) {
            // no specified connection.getNamespace():  use current default connection.getNamespace()
            object = connection.getNamespace() + object;
        }

        String s =
                "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + quadName +
                        "', '" + subject + "', '" + property + "', '" + object + "'))";
        connection.executeStatement(s);
    }

    /**
     * Insert a DatatypeProperty quad statement into the RDF data table
     *
     * @param subject  The subject of the statement  e.g. _:i2
     * @param property The object-property  e.g. lastname
     * @param object   The object of the statement  e.g. DummyDawer
     * @throws SQLException
     */
    public static String insertDataPropQuad(PyRelConnection connection,
                                            String subject, String predicate, String object, String table, String type) throws SQLException
    {

        // Append type data to the object being inserted if provided
        if (type != null) {
            object = "\"" + object + "\"^^xsd:" + type;
        }
        if (subject == null) subject = "i" + getNextGUID(connection);
        String quadName = connection.getModel() + ":<" + connection.getGraph() + ">";
        if (subject.indexOf(":") < 0) {
            // no specified connection.getNamespace():  use current default connection.getNamespace()
            subject = connection.getNamespace() + subject;
        }
        if (predicate.indexOf(":") < 0) {
            // no specified connection.getNamespace():  use current default connection.getNamespace()
            predicate = connection.getNamespace() + predicate;
        }

        String s =
                "INSERT INTO " + connection.getTable() + " VALUES ( " + connection.getModel() + "_SQNC.nextval, " + "SDO_RDF_TRIPLE_S('" + quadName +
                        "', '" + subject + "', '" + predicate + "', '" + object.toString() + "'))";
        connection.executeStatement(s);
        return subject;
    }

    /**
     * Insert DVA Meta Data
     *
     * @param connection.getGraph()Name The context/connection.getGraph() name  e.g. PERSONT
     * @param subject                   The subject of the statement  e.g. PERSONT
     * @param object                    The object of the statement  e.g. rdfs:Class
     * @throws SQLException
     */
    public static void insertDVAMetaData(PyRelConnection connection,
                                         String subject, String object) throws SQLException
    {
        insertObjectPropQuad(connection, subject, "rdf:type", object);
    }

    /**
     * Get the next GUID for this semantic DB.
     *
     * @return
     * @throws SQLException
     */
    public static int getNextGUID(PyRelConnection connection)
    {
        String q = "select " + connection.getModel() + "_GUID_SQNC.nextval from dual";
        ResultSet rs1 = null;
        try {
            try {
                rs1 = connection.executeQuery(q);
                if (rs1 != null && rs1.next()) {
                    int r = rs1.getInt(1);
                    rs1.close();
                    return (r);
                }
            } catch (SQLException e) {
                try {
                    rs1.close();
                } catch (Exception ignore) {
                }
                System.out.println(e);
            }
            return (0);
        } finally {
            try {
                rs1.close();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * @param attr the name of an attribute.
     * @return the type name of an attribute
     * @throws SQLException
     */
    public static String getType(PyRelConnection connection, String attr) throws SQLException
    {
        String q =
                "select case when a.triple.GET_OBJECT() like '%integer%' then '^^xsd:integer' " + "when a.triple.GET_OBJECT() like '%string%' then '^^xsd:string' " +
                        "when a.triple.GET_OBJECT() like '%decimal%' then '^^xsd:decimal' " +
                        "when a.triple.GET_OBJECT() like '%date%' then '^^xsd:date' " + "else 'unknown' " + "end " +
                        "from " + connection.getTable() + " a where a.triple.GET_SUBJECT() like '%" + attr +
                        "%' and a.triple.GET_PROPERTY() like '%range%'";
        ResultSet rs1 = null;
        try {
            try {
                rs1 = connection.executeQuery(q);
            } catch (SQLException e) {
                try {
                    rs1.close();
                } catch (Exception ignore) {
                }
                System.out.println(e);
            }
            if (rs1 != null && rs1.next()) {
                String r = rs1.getString(1);
                rs1.close();
                return (r);
            }
        } finally {
            try {
                rs1.close();
            } catch (Exception ignore) {
            }
        }
        return ("unknown");
    }

    public static String doesClassExist(PyRelConnection connection,
                                        String className) throws SQLException
    {
        String q = "select sub from table(\n" +
                "   sem_match('select * where {\n" +
                "      ?sub rdf:type rdfs:Class .\n" +
                "   }',\n" +
                "	 SEM_MODELS('" + connection.getModel() + "'), null,\n" +
                "	 SEM_ALIASES( SEM_ALIAS('', '*')), null) )\n" +
                "   where sub = '" + className.trim() + "'";
        ResultSet rs1 = null;
        try {
            try {
                rs1 = connection.executeQuery(q);
            } catch (SQLException e) {
                try {
                    rs1.close();
                } catch (Exception ignore) {
                }
                System.out.println(e);
            }
            if (rs1 != null && rs1.next()) {
                String r = rs1.getString(1);
                rs1.close();
                return (r);
            }
        } finally {
            try {
                rs1.close();
            } catch (Exception ignore) {
            }
        }
        return (null);
    }

    public static String doesColumnExist(PyRelConnection connection,
                                         String className, String columnName) throws SQLException
    {
        String q = "select pred from table(\n" +
                "   sem_match('select * where {\n" +
                "      :" + columnName + " ?pred rdfs:" + className + " .\n" +
                "   }',\n" +
                "	 SEM_MODELS('" + connection.getModel() + "'), null,\n" +
                "	 SEM_ALIASES( SEM_ALIAS('', '*')), null) )\n";
        ResultSet rs1 = null;
        try {
            try {
                rs1 = connection.executeQuery(q);
            } catch (SQLException e) {
                try {
                    rs1.close();
                } catch (Exception ignore) {
                }
                System.out.println(e);
            }
            if (rs1 != null && rs1.next()) {
                String r = rs1.getString(1);
                rs1.close();
                return (r);
            }
        } finally {
            try {
                rs1.close();
            } catch (Exception ignore) {
            }
        }
        return (null);
    }

    /**
     * Get an anonymous node for an individual,
     * format     _:i123
     *
     * @return
     * @throws SQLException
     */
    public static String getNextAnonNodeForInd(PyRelConnection connection) throws SQLException
    {
        return "i" + getNextGUID(connection);
    }

    /**
     * Get an anonymous node for a model object
     * i.e. Restriction or class,
     * format     _:m123
     *
     * @return
     * @throws SQLException
     */
    public static String getNextAnonNodeForModel(PyRelConnection connection) throws SQLException
    {
        return "_:m" + getNextGUID(connection);
    }

    public static List<String> getAllColumns(PyRelConnection connection, String className) throws SQLException
    {
        String stmt = "select col from table(sem_match(\n" +
                "  'select * where {\n" +
                "        ?col rdfs:domain :" + className + " .\n" +
                "}',\n" +
                "SEM_MODELS('" + connection.getModel() + "'), null,\n" +
                "SEM_ALIASES( SEM_ALIAS('', '" + connection.getNamespace() + "')), null) )";
        return executeRdfSelect(connection, stmt);
    }

    /**
     * Get inverse of evaName
     *
     * @param evaName
     * @return
     * @throws SQLException
     */
    public static void insertEvaValue(PyRelConnection connection,
                                      String individual, String attrName, String entity) throws SQLException
    {
        String stmt = "select attr from table(sem_match(\n" +
                "  'select * where {\n" +
                "        :" + attrName + " owl:inverseOf ?attr .\n" +
                "}',\n" +
                "SEM_MODELS('" + connection.getModel() + "'), null,\n" +
                "SEM_ALIASES( SEM_ALIAS('', '" + connection.getNamespace() + "')), null) )";

        List<String> inverses = executeRdfSelect(connection, stmt);
        insertObjectPropQuad(connection, individual, attrName, entity);
        for (String inverse : inverses) {
            insertObjectPropQuad(connection, entity, inverse, individual);
        }
    }

    /**
     * Get inverse of evaName
     *
     * @param evaName
     * @return
     * @throws SQLException
     */
    public static void insertEvaValueWithGraph(PyRelConnection connection, String graph,
                                               String individual, String attrName, String entity) throws SQLException
    {
        String quadName = connection.getModel() + ":<" + graph + ">";
        String stmt = "select attr from table(sem_match(\n" +
                "  'select * where {\n" +
                "        :" + attrName + " owl:inverseOf ?attr .\n" +
                "}',\n" +
                "SEM_MODELS('" + connection.getModel() + "'), null,\n" +
                "SEM_ALIASES( SEM_ALIAS('', '" + connection.getNamespace() + "')), null) )";

        List<String> inverses = executeRdfSelect(connection, stmt);
        insertObjectPropQuad(connection, individual, attrName, entity);
        for (String inverse : inverses) {
            insertObjectPropQuad(connection, entity, inverse, individual);
        }
    }

    public static List<String> executeRdfSelect(PyRelConnection connection, String selectStmt) throws SQLException
    {
        if (connection.getDebug() == "debug") System.out.println("In executeRdfSelect, selectStmt is: " + selectStmt);
        List<String> rowIds = new ArrayList<String>();
        if (connection.getConnectionDB().equals("OracleNoSQL")) {
            ArrayList<PyObject> rows = new ArrayList<PyObject>();
            rows = ((OracleRDFNoSQLDatabase) connection.getDatabase()).OracleNoSQLRunSPARQL(selectStmt);
            for (int i = 1; i < rows.size(); i++) {
                rowIds.add(String.format("%s", rows.get(i))
                        .replaceAll("[()]", "")
                        .replaceAll("'", "")
                        .replaceAll(",", "")
                        .replaceAll(connection.getDatabase().getNameSpace(), ""));
            }
        }
        else {
            ResultSet rs = null;
            try {
                try {
                    rs = connection.executeQuery(selectStmt);
                } catch (SQLException e) {
                    try {
                        rs.close();
                    } catch (Exception ignore) {
                    }
                    System.out.println(e);
                }
                // ResultSetMetaData rd = rs.getMetaData();
                // int cc = rd.getColumnCount();
                while (rs.next()) {
                    rowIds.add(getValue(rs.getString(1)));
                }
                rs.close();
            } finally {
                try {
                    rs.close();
                } catch (Exception ignore) {
                }
            }
        }
        if (connection.getDebug() == "debug") System.out.println("In executeRdfSelect, rowIds is: " + rowIds);
        return rowIds;
    }

    /**
     * @param selectStmt
     * @param columnLabelMap Maps from the column name to the column label
     * @throws SQLException
     */
    public static void executeAndPrintRdfSelect(PyRelConnection connection, String selectStmt, Map<String, String> columnLabelMap) throws SQLException
    {
        ResultSet rs = null;
        try {
            rs = connection.executeQuery(selectStmt);
            ResultSetMetaData rd = rs.getMetaData();
            int cc = rd.getColumnCount();
            System.out.print("\n|");
            for (int i = 1; i <= cc; i++) {
                String label = rd.getColumnLabel(i);
                if (columnLabelMap != null) {
                    String mapdLabel = columnLabelMap.get(label);
                    if (mapdLabel != null) {
                        label = mapdLabel;
                    }
                }
                System.out.printf("%-20s|", label);
            }
            System.out.print("\n|");

            for (int i = 1; i <= cc - 1; i++)
                System.out.printf("--------------------+");
            System.out.print("--------------------|\n");
            while (rs.next()) {
                System.out.print("|");
                for (int i = 1; i <= cc; i++) {
                    String columnVal = rs.getString(i);
                    if (columnVal == null) {
                        columnVal = "";
                    }
                    columnVal = columnVal.substring(columnVal.lastIndexOf('/') + 1);
                    System.out.printf("%-20s|", columnVal);
                }
                System.out.println();
            }
            System.out.println("\n");
            rs.close();
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
        }
    }

    private static String getValue(String str)
    {
        return str.substring(str.lastIndexOf("#") + 1);
    }

    /**
     * Get members (individuals) of a class, with datatype properties
     * that match the attr-value pairs provided
     *
     * @param className  (optional)
     * @param attrValues
     * @return
     * @throws SQLException
     */
    public static List<String> getMembersWithAttrValues(PyRelConnection connection, String className, Map<String, Object> attrValues) throws SQLException
    {
        List<String> members = new ArrayList<String>();
        String NoSQLNameSpacePrefix = "";
        if (connection.getConnectionDB().equals("OracleNoSQL"))
            NoSQLNameSpacePrefix = connection.getDatabase().getNameSpacePrefix();
        String typeTriple = "";
        if (className != null) {
            if (connection.getConnectionDB().equals("OracleNoSQL")) typeTriple = " ?indiv rdf:type " + className + ".";
            else typeTriple = " ?indiv rdf:type " + ":" + className + ".\n";
        }
        String attrValuesQ = "";
        for (String attr : attrValues.keySet()) {
            Object val = attrValues.get(attr);
            if (val instanceof String) {
                if (connection.getConnectionDB().equals("OracleNoSQL"))
                    attrValuesQ += "?indiv " + NoSQLNameSpacePrefix + ":" + attr + " " + "\"" + ((String) val).replaceAll("'", "") + "\"^^xsd:string" + " .";
                else attrValuesQ += "?indiv " + ":" + attr + " " + ":" + ((String) val).replaceAll("'", "") + " .\n";
            }
            else if (val instanceof Integer) {
                if (connection.getConnectionDB().equals("OracleNoSQL"))
                    attrValuesQ += "?indiv " + NoSQLNameSpacePrefix + ":" + attr + " " + "\"" + ((String) val) + "\"^^xsd:integer" + " .";
                else attrValuesQ += "?indiv " + ":" + attr + " " + attrValues.get(attr).toString() + " .\n";
            }
        }
        String q = formatSPARQL(connection, "indiv", className, typeTriple, className, attrValuesQ);
        // if (connection.getDebug() == "debug") System.out.println("\ngetMembersWithAttrValues: query=\n" + q);
        members = executeRdfSelect(connection, q);
        return members;
    }

    /**
     * Get the subject nodes with the supplied attribute value pair.
     *
     * @param attr
     * @param value
     * @return
     * @throws SQLException
     */
    public static List<String> getSubjectsWithAttrValue(PyRelConnection connection,
                                                        String attr, String value) throws SQLException
    {
        Map<String, Object> attrValues = new HashMap<String, Object>();
        return getMembersWithAttrValues(connection, null, attrValues);
    }

    public static List<String> getObjects(PyRelConnection connection,
                                          String subject, String property) throws SQLException
    {
        if (subject.indexOf(":") < 0)
            subject = ":" + subject;
        if (property.indexOf(":") < 0)
            property = ":" + property;
        String q = "select obj from table(sem_match(\n" +
                "  'select * where {\n" +
                "         " + subject + " " + property + " ?obj" + " .\n" +
                "}',\n" +
                "SEM_MODELS('" + connection.getModel() + "'), null,\n" +
                "SEM_ALIASES( SEM_ALIAS('', '" + connection.getNamespace() + "')), null) )";
        if (connection.getDebug() == "debug") System.out.println("\ngetSubjectsWithAttrValue: query=\n" + q);
        List<String> objects = executeRdfSelect(connection, q);
        return objects;
    }

    public static List<String> getObjectsWithGraph(PyRelConnection connection,
                                                   String graph, String subject, String property) throws SQLException
    {
        if (!subject.contains(":") && !subject.contains("<"))
            subject = ":" + subject;
        if (!property.contains(":") && !property.contains("<"))
            property = ":" + property;
        String q = "select obj from table(sem_match(\n" +
                "'select * where {\n" +
                "GRAPH <" + graph + "> { " + subject + " " + property + " ?obj" + " }\n" +
                "}',\n" +
                "SEM_MODELS('" + connection.getModel() + "'), null,\n" +
                "SEM_ALIASES( SEM_ALIAS('', '" + connection.getNamespace() + "')), null) )";
        if (connection.getDebug() == "debug") System.out.println("\ngetObjectsWithGraph: query=\n" + q);
        List<String> objects = executeRdfSelect(connection, q);
        return objects;
    }

    public static List<String> getClassForAttr(PyRelConnection connection, String property) throws SQLException
    {
        if (property.indexOf(":") < 0)
            property = ":" + property;
        String q = "select distinct t1 from table(sem_match(\n" +
                "  'select * where {\n" +
                "?tbl rdf:type ?t1  .\n" +
                "?tbl " + property + " ?X .\n" +
                property + " rdfs:domain ?t1 .\n" +
                "}',\n" +
                "SEM_MODELS('" + connection.getModel() + "'), null,\n" +
                "SEM_ALIASES( SEM_ALIAS('', '" + connection.getNamespace() + "')), null) )";
        if (connection.getDebug() == "debug") System.out.println("\ngetClassForAttr: query=\n" + q);
        List<String> classes = executeRdfSelect(connection, q);
        return classes;
    }

    /**
     * Delete all quads with the provided subject and property.
     *
     * @param subject
     * @param property
     * @throws SQLException
     */
    public static void deleteQuadsWithSubjectProp(PyRelConnection connection,
                                                  String subject, String property) throws SQLException
    {
        String q = "select obj from table(sem_match(\n" +
                "  'select * where {\n" +
                "         :" + subject + " :" + property + " ?obj" + " .\n" +
                "}',\n" +
                "SEM_MODELS('" + connection.getModel() + "'), null,\n" +
                "SEM_ALIASES( SEM_ALIAS('', '" + connection.getNamespace() + "')), null) )";
        List<String> rowIds = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = connection.executeQuery(q);
            while (rs.next()) {
                String obj = getValue(rs.getString(1));
                String quadName = connection.getModel() + ":<" + connection.getGraph() + ">";
                String deleteSql =
                        "DELETE FROM " + connection.getTable() + " WHERE TRIPLE = SDO_RDF_TRIPLE_S('" + quadName + "', '" + connection
                                .getNamespace() +
                                subject + "', '" + connection.getNamespace() + property + "', " + "'\"" + obj + "\"')";
                connection.executeStatement(deleteSql);
            }
            rs.close();
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Delete instances of a class whose attributes match
     * those in the key_vals map.
     * key_vals map looks like "attribute_name" = "attribute_val"
     *
     * @param subject
     * @param property
     * @throws SQLException
     */
    public static void deleteSubjectsWithAttrValues(PyRelConnection connection, String classname, Map<String, Object> key_vals) throws SQLException
    {
        List<String> instances = getMembersWithAttrValues(connection, classname, (Map) key_vals);

        if (instances.size() > 0) {
            String where_stmt = "";
            String delete_stmt = "DELETE FROM " + connection.getModel() + "_DATA a where";

            int i = 0;
            for (String inst_name : instances) {
                where_stmt = where_stmt + "a.triple.get_subject() = \'<" + connection.getGraph() + "#" + inst_name + ">\'";
                i = i + 1;
                if (i != instances.size()) {
                    where_stmt = where_stmt + "\nOR\n";
                }
                else {
                    where_stmt = where_stmt + "";
                }

            }
            String sql_stmt = delete_stmt + " " + where_stmt;
            connection.executeStatement(sql_stmt);
        }
    }

    public static String formatSPARQL(PyRelConnection connection, String returnAttr, String graph1, String body1, String graph2, String body2)
    {
        String q = "";
        if (connection.getConnectionDB().equals("OracleNoSQL")) {
            // sparqlHelper = new SPARQLHelper(connection);
            String NoSQLNameSpacePrefix = connection.getDatabase().getNameSpacePrefix();
            q = "select ?" + returnAttr + "  where { GRAPH " + NoSQLNameSpacePrefix + ":" + graph1 + "_SCHEMA" + " { " + body1 + " } GRAPH " + NoSQLNameSpacePrefix + ":" + graph2 + " { " + body2 + "}}";
        }
        else {
            q = "select indiv from table(sem_match(\n" +
                    "  'select * where {\n" + body1 + body2 +
                    "}',\n" +
                    "SEM_MODELS('"
                    + connection.getModel() + "'), null,\n" +
                    "SEM_ALIASES( SEM_ALIAS('', '"
                    + connection.getNamespace() + "')), null) )";
        }
        return (q);
    }
}

