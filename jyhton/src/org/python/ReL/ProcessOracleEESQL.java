package org.python.ReL;

import org.python.core.*;

import java.util.Collection;
import java.util.*;
import java.lang.*;


import java.sql.*;

import java.util.concurrent.ConcurrentMap;


public class ProcessOracleEESQL {
    PyRelConnection conn = null;
    private ArrayList<PyType> relQueryInstancesType = new ArrayList<PyType>();
    private Collection<String> relQueryInstancesTypeNames = new ArrayList<String>();

    /**
     * Process a language statement such as SQL or SIM.
     * <p>
     * Note:  The connection should not be closed within this class, as it will be closed
     * by its parent (invoker).
     *
     * @param conn
     */
    public ProcessOracleEESQL(PyRelConnection conn, ArrayList<PyType> relQueryInstancesType, Collection<String> relQueryInstancesTypeNames)
    {
        this.conn = conn;
        this.relQueryInstancesType = relQueryInstancesType;
        this.relQueryInstancesTypeNames = relQueryInstancesTypeNames;
    }

    public synchronized ArrayList<PyObject> processSQL(String ReLstmt) throws SQLException
    {
        ArrayList<PyObject> rows = new ArrayList<PyObject>();
        java.sql.ResultSet rs = null;
        try {
            try {
                int dbuniqueid_column_idx = 0;
                if (ReLstmt.trim().toUpperCase().indexOf("SELECT") != 0) {
                    String[] stmts = ReLstmt.split("~");
                    for (String s : stmts) {
                        if (conn.getDebug() == "debug") System.out.println("stmt : " + s);
                        if (s.length() > 5 && s.trim().indexOf("--") != 0) conn.executeStatement(s);
                    }
                }
                else {
                    rs = conn.executeQuery(ReLstmt);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int cc = rsmd.getColumnCount();
                    PyObject[] temp;
                    ArrayList<PyObject> columns = new ArrayList<PyObject>();
                    int nTuple = cc;
                    int dbuniqueid_idx = -1;
                    for (int i = 1; i <= cc; i++) {
                        columns.add(new PyString(rsmd.getColumnLabel(i)));
                        if (rsmd.getColumnName(i).equals("DBUNIQUEID")) {
                            dbuniqueid_idx = i - 1;
                        }
                    }

                    // If we are not returning class instance's, then 
                    // the first tuple should be a list of the column names. 
                    if (this.relQueryInstancesType.size() == 0) {
                        temp = listtoarray(columns);
                        rows.add(new PyTuple(temp));
                    }

                    while (rs.next()) {
                        ArrayList<PyObject> items = new ArrayList<PyObject>();
                        for (int i = 1; i <= cc; i++) {   // cc is the column count
                            int type = rsmd.getColumnType(i);
                            if (rs.getString(i) == null) items.add(new PyString("null"));
                            else {
                                String rsString = rs.getString(i).replace(conn.getNamespace(), "");
                                try {
                                    Double.parseDouble(rsString);
                                    try {
                                        Integer.parseInt(rsString);
                                        items.add(new PyInteger(Integer.parseInt(rsString)));
                                    } catch (NumberFormatException e) {
                                        items.add(new PyFloat(Float.parseFloat(rsString)));
                                    }
                                } catch (NumberFormatException e) {
                                    items.add(new PyString(rsString));
                                }
                            }
                        }

                        // Return tuples of the data or return class instances with the data populated in them.
                        if (this.relQueryInstancesType.size() == 0) {
                            temp = listtoarray(items);
                            rows.add(new PyTuple(temp));
                        }
                        else { // return the instances of a class.
                            // So the first type in the query is the type that is actually returned.
                            // then pointers to the other types are added to the first types dictionary.
                            // so select * from a, b, c where a.id == b.id and a.id == c.id;
                            // will return instance of A and in a's dictionary there will be A.b -> instance of b, etc.
                            // Create instances for each table we queried data from.
                            PyObjectDerived top_instance = createInstanceFromResults(conn, relQueryInstancesType.get(0), columns, items);
                            ConcurrentMap<Object, PyObject> main_inst_dict;
                            main_inst_dict = ((PyStringMap) top_instance.fastGetDict()).getMap();
                            for (int i = 1; i < relQueryInstancesType.size(); i++) {
                                PyObjectDerived new_instance = createInstanceFromResults(conn, relQueryInstancesType.get(i), columns, items);
                                main_inst_dict.put(relQueryInstancesType.get(i).getName(), new_instance);
                            }
                            rows.add(top_instance);
                        }
                    }
                    rs.close();
                }
            } catch (Exception e) {
                try {
                    rs.close();
                } catch (Exception ignore) {
                }
                PyObject[] temp = new PyObject[1];
                temp[0] = new PyString(e.toString());
                rows.add(new PyTuple(temp));
            }
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
        }
        return (rows);
    }

    /* A helper for creating user object instances.
       This is useful for returning instance during joins. This guy expects the columns associated with
       each type to have the types name_ appended to the front of the column name. 
    */
    private PyObjectDerived createInstanceFromResults(PyRelConnection connection, PyType instance_type, ArrayList<PyObject> columns, ArrayList<PyObject> data)
    {
        String typeName = instance_type.getName() + "_";
        PyObjectDerived instance = new PyObjectDerived(instance_type);
        ConcurrentMap<Object, PyObject> inst_dict;
        inst_dict = ((PyStringMap) instance.fastGetDict()).getMap();
        // Add data as we find columns that should represent this type. 
        for (int i = 0; i < columns.size(); i++) {
            String column = ((PyString) columns.get(i)).toString();
            if (column.startsWith(typeName)) {
                // If we found the dbuniqueid, check to see this id is already in our runtime's session, if so just return that item.
                if (column.equals(typeName + "DBUNIQUEID")) {
                    PyInteger unique_id = (PyInteger) data.get(i);
                    PyObjectDerived possible_instance_in_session = (PyObjectDerived) connection.getInstance(unique_id.getValue());
                    if (possible_instance_in_session != null) {
                        return possible_instance_in_session;
                    }
                }
                inst_dict.put(columns.get(i).toString().replaceFirst(typeName, ""), data.get(i));

            }
        }
        return instance;
    }

    //helper to convert lists to arrays
    private PyObject[] listtoarray(ArrayList<PyObject> a)
    {
        PyObject[] results = new PyObject[a.size()];
        int iter = 0;
        for (PyObject pt : a) {
            results[iter] = pt;
            iter++;
        }
        return results;
    }
}