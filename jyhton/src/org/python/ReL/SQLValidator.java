package org.python.ReL;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class SQLValidator {

    private boolean valid;

    /**
     * Validates SQL syntax to be converted into Oracle SQL statements.
     * The integrity of SQL statements has to be done on the user side.
     */
    public SQLValidator()
    {
        this.valid = false;
    }

    /**
     * Validates table name
     */
    public String validateTable(HashMap<String, String> tablesAliases, String tableName, String temp)
    {
        String key = tableName;
        String ownException = "";
        if (!tablesAliases.containsKey(key))
            key = (String) getKeyByValue(tablesAliases, tableName);
        if (key == null || !key.equals(tableName)) {
            ownException = "ORA-00904: \"" + tableName + (temp.isEmpty() ? "" : "\".\"" + temp) + "\": invalid identifier";
        }
        return ownException;
    }

    /**
     * Gets table's alias
     */
    private String getKeyByValue(HashMap<String, String> map, String value)
    {
        for (String entry : map.keySet()) {
            if (map.get(entry).equals(value)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Validates column
     */
    public String validateColumn(HashMap<String, List<String>> tablesColumns, String colName)
    {
        int j = 0;
        String tName = "";

        this.valid = true;
        //validate the column agains the table used by the query
        //if several tables contain such a column then there exists an ambiguity
        for (String st : tablesColumns.keySet()) {
            if (tablesColumns.get(st).contains(colName)) {
                j++;
                tName = st;
            }
        }
        if (j == 1) {
            return tName + "." + colName;
        }
        // else j > 1
        this.valid = false;
        return "\"" + colName + "\" ORA-00918: column ambiguously defined";
    }

    /**
     * Return boolean where it indicates weather there is valid column
     */
    public boolean isValidColumn()
    {
        return valid;
    }

    /**
     * Validate columns with aliases
     */
    public String validateColumnAs(LinkedHashMap<String, String> columnsAs, String temp)
    {
        int j = 0;
        String tableName = "";
        this.valid = true;
        for (String entry : columnsAs.keySet()) {
            if (temp.contains(colname(entry))) {
                j++;
                tableName = tablename(entry);
            }
        }
        //is there amiguity
        if (j == 1) {
            return tableName;
        }
        this.valid = false;
        return "\"" + temp + "\" ORA-0091?: column ambiguously defined";


    }

    /**
     * Gets the table name, removes column
     */
    private String tablename(String item)
    {
        if (item.indexOf('.') > 0)
            return item.substring(0, item.indexOf('.'));
        return "tbl";
    }

    /**
     * Gets the column name, removes table
     */
    private String colname(String item)
    {
        if (item.indexOf('.') > 0)
            return item.substring(item.indexOf('.') + 1);
        return item;
    }
}

