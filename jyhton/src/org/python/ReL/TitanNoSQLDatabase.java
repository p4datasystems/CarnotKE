package org.python.ReL;
import wdb.metadata.Adapter;
import wdb.metadata.ClassDef;
import wdb.metadata.IndexDef;
import wdb.metadata.WDBObject;

import java.util.ArrayList;

import com.thinkaurelius.titan.core.*;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.thinkaurelius.titan.core.schema.ConsistencyModifier;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

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
