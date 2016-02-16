package org.python.ReL;

import wdb.metadata.Adapter;
import wdb.metadata.ClassDef;
import wdb.metadata.IndexDef;
import wdb.metadata.WDBObject;

import java.util.ArrayList;

/**
 * @author Alvin Deng
 * @date 02/16/2016
 */
public class TitanNoSQLDatabase extends DatabaseInterface {

    private class TitanNoSQLAdapter implements Adapter {

        @Override
        public void putClass(ClassDef classDef) {

        }

        @Override
        public ClassDef getClass(String s) throws ClassNotFoundException {
            return null;
        }

        @Override
        public void putObject(WDBObject wdbObject) {

        }

        @Override
        public WDBObject getObject(String s, Integer integer) {
            return null;
        }

        @Override
        public ArrayList<WDBObject> getObjects(IndexDef indexDef, String s) {
            return null;
        }

        @Override
        public void commit() {

        }

        @Override
        public void abort() {

        }
    }
}
