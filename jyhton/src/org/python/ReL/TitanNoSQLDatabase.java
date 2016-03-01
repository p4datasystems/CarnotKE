package org.python.ReL;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import wdb.metadata.Adapter;
import wdb.metadata.ClassDef;
import wdb.metadata.IndexDef;
import wdb.metadata.WDBObject;

import java.util.ArrayList;
import java.util.List;

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
import wdb.parser.QueryParser;

/**
 * @author Alvin Deng
 * @date 02/16/2016
 */
public class TitanNoSQLDatabase extends DatabaseInterface {

    public static QueryParser queryParser = null;
    public static TitanGraph titanGraph = null;
    public static TitanTransaction titanTransaction = null;
    public static Object rootID = null;

    public TitanNoSQLDatabase() {

        this.adapter = new TitanNoSQLAdapter(this);

        TitanFactory.Builder config = TitanFactory.build();
        config.set("storage.backend", "cassandra");
        config.set("storage.directory", "127.0.0.1");
        titanGraph = config.open();

        TitanManagement mg = titanGraph.openManagement();
        boolean initGraph = mg.getGraphIndex("byClassDef") == null;

        if (initGraph) {
            VertexLabel classLabel = mg.makeVertexLabel("classDef").make();
            PropertyKey name = mg.makePropertyKey("name").dataType(String.class).make();
            mg.buildIndex("byClassDef", Vertex.class).addKey(name).indexOnly(classLabel).buildCompositeIndex();
        }
        mg.commit();

        titanTransaction = titanGraph.newTransaction();
        if (initGraph) {
            Vertex root = titanTransaction.addVertex(T.label, "classDef", "name", "Root");

            titanTransaction.commit();  // Regular classDefs can't have spaces
            rootID = root.id();
        } else {
            GraphTraversalSource g = titanGraph.traversal();
            g.V().hasLabel("classDef").forEachRemaining(n -> {
                if (n.property("name").value().equals("Root")) {
                    rootID = n.id();
                }
            });
        }
    }
    
    private class TitanNoSQLAdapter implements Adapter {

        private TitanNoSQLDatabase db;

        private TitanNoSQLAdapter(TitanNoSQLDatabase db) {
            this.db = db;
        }

        @Override
        public void putClass(ClassDef classDef) {

        }

        @Override
        public ClassDef getClass(String s) throws ClassNotFoundException {
            return null;
        }

        /* InsertQuery */
        @Override
        public void putObject(WDBObject wdbObject) {

        }

        /* RetrieveQuery */
        @Override
        public WDBObject getObject(String s, Integer integer) {
            return null;
        }

        @Override
        public ArrayList<WDBObject> getObjects(IndexDef indexDef, String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void commit() {

        }

        @Override
        public void abort() {

        }

    }
}
