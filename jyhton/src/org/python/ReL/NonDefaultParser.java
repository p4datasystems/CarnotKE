package org.python.ReL;

import org.python.ReL.WDB.database.wdb.metadata.Query;
import org.python.core.PyObject;

import java.util.ArrayList;

/**
 * Created by jhurt on 10/21/16.
 */
public interface NonDefaultParser {
    void insert(Query insertQuery);
    void modify(Query modifyQuery);
    ArrayList<PyObject> retrieve(Query retrieveQuery);
}
