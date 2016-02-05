package org.python.ReL;

import oracle.kv.table.PrimaryKey;
import oracle.kv.table.Row;
import wdb.SleepyCatDataAdapter;
import wdb.metadata.*;

import java.util.*;

import org.apache.commons.lang3.SerializationUtils;

/**
 * Adapter is extending SleepyCatDataAdapter AS A WORKAROUND until
 * I overload the method parameters in ClassDef and WDBObject classes
 * inside the wdb.jar in the extlibs directory.
 * @author Joshua Hurt
 */
public class Adapter extends SleepyCatDataAdapter {
    private Database db;
    private static final String classKeyPrefix = "class";
    private static final String objectKeyPrefix = "object";

    public Adapter(Database db) {
        // Below super call is a workaround until I fix the wdb.jar in extlibs
        super(null, null);
        this.db = db;
    }

    public void commit() {
        // TODO: Operation factory implementation
    }

    public void abort() {
        // TODO: Operation factory implementation
    }

    /**
     * Original usage: classesDb.put(String, ClassDef)
     *
     * key: String class:classDef.name
     * value: ClassDef classDef
     */
    public void putClass(ClassDef classDef) {
        /* *******************************************************
        * How to serialize object to byte array to be
        * used as value in key-store DB.
        * byte[] data = SerializationUtils.serialize(classDef);
        **********************************************************/

        final String keyString = makeClassKey(classDef.name);
        final byte[] data = SerializationUtils.serialize(classDef);

        Row row = db.getClassTable().createRow();
        row.put("key", keyString);
        row.put("value", data);

        db.getTableHandle().put(row, null, null);
    }

    /**
     * Original usage: classesDb.get(String)
     *
     * key: String class:classDef.name
     * @return ClassDef or null if not found
     */
    public ClassDef getClass(String className) throws ClassNotFoundException {
        /* *******************************************************
        * How to de-serialize object from byte array returned after
        * using key to get serialized byte[] value from database to
        * be returned to the user as an object.
        * ClassDef classDef = (ClassDef) SerializationUtils.deserialize(byte[] data)
        **********************************************************/

        PrimaryKey key = db.getClassTable().createPrimaryKey();
        final String keyString = makeClassKey(className);
        key.put("key", keyString);
        Row row = db.getTableHandle().get(key, null);

        if (row == null)
            throw new ClassNotFoundException(String.format(
                    "Key '%s' not present in table", keyString));
        byte[] data = row.get("value").asBinary().get();

        final ClassDef classDef = (ClassDef) SerializationUtils.deserialize(data);
        if (classDef == null) {
            db.ultimateCleanUp(String.format(
                    "Null value returned from ClassesDB lookup for class: %s",
                    keyString));
//            throw new MissingResourceException("Null value returned from ClassesDB lookup",
//                    className, keyString);
        }

        return classDef;
    }

    /**
     * Original usage: objectsDB.put(String, WDBObject)
     *
     * key: String object:Uid.toString()
     * value: WDBObject object
     * @param wdbObject to serialize and store as value
     */
    public void putObject(WDBObject wdbObject) {
        /* *******************************************************
        * How to serialize object to byte array to be
        * used as value in key-store DB.
        * byte[] data = SerializationUtils.serialize(classDef);
        **********************************************************/

        final String keyString = makeObjectKey(wdbObject.getUid());
        final byte[] data = SerializationUtils.serialize(wdbObject);

        Row row = db.getObjectTable().createRow();
        row.put("key", keyString);
        row.put("value", data);

        db.getTableHandle().put(row, null, null);
    }

    /**
     * Original usage: objectsDB.get(String)
     *
     * key: String object:Uid.toString()
     * value: WDBObject object
     * @param className only used for MissingResourceException
     * @param Uid is the key to retrieve the WDBObject
     * @return WDBObject or throws MissingResourceException
     */
    public WDBObject getObject(String className, Integer Uid) {
        /* *******************************************************
        * How to de-serialize object from byte array returned after
        * using key to get serialized byte[] value from database to
        * be returned to the user as an object.
        * WDBObject classDef = (WDBObject) SerializationUtils.deserialize(byte[] data)
        **********************************************************/

        final String keyString = makeObjectKey(Uid);
        PrimaryKey key = db.getObjectTable().createPrimaryKey();
        key.put("key", keyString);
        Row row = db.getTableHandle().get(key, null);

        byte[] data = row.get("value").asBinary().get();

        final WDBObject wdbObject = (WDBObject) SerializationUtils.deserialize(data);
        if (wdbObject == null) {
            db.ultimateCleanUp(String.format(
                    "Null value returned from ClassesDB lookup for class: %s",
                    keyString));
        }

        return wdbObject;
    }

    public ArrayList<WDBObject> getObjects(IndexDef indexDef, String key) {
        System.out.println("getObjects called in Adapter");
        return null;
    }

    /**
     * Just prepends 'className' with 'class:'.
     * @return "class:className"
     */
    private String makeClassKey(String className)
    {
        return classKeyPrefix + ":" + className;
    }

    private String makeObjectKey(Integer Uid) {
        return objectKeyPrefix + ":" + Uid.toString();
    }



}
