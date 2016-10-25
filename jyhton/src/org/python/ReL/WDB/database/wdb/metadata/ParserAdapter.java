package org.python.ReL.WDB.database.wdb.metadata;
import java.util.ArrayList;

/**
 * Created by Josh Hurt on 5/3/16.
 */

public interface ParserAdapter {
    String classKeyPrefix = "class";
    String objectKeyPrefix = "object";

    void putClass(Query classDefQuery);

    ClassDef getClass(Query query)
        throws ClassNotFoundException;
    ClassDef getClass(String className)
        throws ClassNotFoundException;

    void putObject(WDBObject wdbObject);

    WDBObject getObject(String className, Integer Uid);
    ArrayList<WDBObject> getObjects(Query indexDefQuery, String key);

    void commit();
    void abort();
}
