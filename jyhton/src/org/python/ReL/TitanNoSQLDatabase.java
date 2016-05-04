package org.python.ReL;

import java.util.*;

import org.python.ReL.WDB.database.wdb.metadata.*;
import com.thinkaurelius.titan.core.*;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.*;

/**
 * @author Alvin Deng
 * @date 05/03/2016
 */

public class TitanNoSQLDatabase extends DatabaseInterface {

    private static TitanGraph graph = null;
    private static Object rootID = null;

    private static void initTitanDB() {
        TitanFactory.Builder config = TitanFactory.build();
        config.set("storage.backend", "berkeleyje");
        config.set("storage.directory", "db/CarnotKE");
        graph = config.open();
        TitanManagement mg = graph.openManagement();
        boolean initGraph = mg.getGraphIndex("byClassDef") == null;
        if (initGraph) {
            VertexLabel classLabel = mg.makeVertexLabel("classDef").make();
            PropertyKey name = mg.makePropertyKey("name").dataType(String.class).make();
            mg.buildIndex("byClassDef", Vertex.class).addKey(name).indexOnly(classLabel).buildCompositeIndex();
        }
        mg.commit();
        if (initGraph) {
            Vertex root = graph.addVertex(T.label, "classDef", "name", "root node");
            graph.tx().commit();
            rootID = root.id();
        } else {
            GraphTraversalSource g = graph.traversal();
            g.V().hasLabel("classDef").forEachRemaining(n -> {
                if (n.property("name").value().equals("root node")) {
                    rootID = n.id();
                }
            });
        }
    }

    public TitanNoSQLDatabase()
    {
        this.adapter = new TitanNoSQLAdapter(this);
        initTitanDB();
    }

    private static Vertex lookupClass(GraphTraversalSource g, String name) {
        return getSubclass(g, rootID, name);
    }

    private static void throwException(String format, Object... args) {
        throw new RuntimeException(String.format(format, args));
    }

    private static Vertex getSubclass(GraphTraversalSource g, Object classID, String targetClassName) {
        GraphTraversal<Vertex, Vertex> subclasses = g.V(classID).out("superclasses");
        while (subclasses.hasNext()) {
            Vertex subclass = subclasses.next();
            String subclassName = (String) subclass.property("name").value();
            Vertex temp;
            if (subclassName.equals(targetClassName)) {
                return subclass;
            } else if ((temp = getSubclass(g, subclass, targetClassName)) != null) {
                return temp;
            }
        }
        return null;
    }

    private void processClassDef(ClassDef cd) {
        GraphTraversalSource g = graph.traversal();
        try {
            cd.name = cd.name.toLowerCase();
            TitanVertex currentVertex = (TitanVertex) lookupClass(g, cd.name);
            if ((currentVertex != null && !currentVertex.property("ForwardInit").isPresent()) || (currentVertex != null && currentVertex.property("ForwardInit").value().equals("No"))) {
                throwException("Class %s already exists!\n", cd.name);
            }
            TitanVertex newClass;
            if (currentVertex == null) {
                newClass = graph.addVertex(T.label, "classDef", "name", cd.name);
            } else {
                currentVertex.property("ForwardInit", "No");
                newClass = currentVertex;
            }

            if (cd.comment != null) {
                newClass.property("comment", cd.comment);
            }
            for (int i = 0; i < cd.numberOfAttributes(); i++) {
                Attribute attr = cd.getAttribute(i);
                attr.name = attr.name.toLowerCase();
                TitanVertex attrVertex = graph.addVertex(T.label, "attribute", "name", attr.name);
                if (attr.comment != null) {
                    attrVertex.property("comment", attr.comment);
                }

                boolean required = attr.required != null && attr.required;
                Edge attrEdge = newClass.addEdge("has", attrVertex);
                if (required) {
                    attrEdge.property("required", true);
                }

                if (attr instanceof DVA) /* Simple Attribute */ {
                    attrEdge.property("isDVA", true);
                    DVA dva = (DVA) attr;
                    attrVertex.property("data_type", dva.type.toLowerCase());
                    if (dva.initialValue != null) {
                        attrVertex.property("default_value", dva.initialValue);
                    }
                } else if (attr instanceof EVA) /* This is a classDef */ {
                    EVA eva = (EVA) attr;
                    eva.baseClassName = eva.baseClassName.toLowerCase();
                    eva.inverseEVA = eva.inverseEVA.toLowerCase();
                    Vertex targetClass = lookupClass(g, eva.baseClassName);

                    if (targetClass == null) {
                        targetClass = graph.addVertex(T.label, "classDef", "name", eva.baseClassName);
                        targetClass.property("ForwardInit", "Yes");

                        GraphTraversal<Vertex, Vertex> traversal = g.V().hasLabel("classDef").has("name", "root node");
                        Vertex root = traversal.next();
                        targetClass.addEdge("subclasses", root);
                        root.addEdge("superclasses", targetClass);
//                        throwException("Class %s cannot create a relationship with %s because %2$s does not exist!",
//                                cd.name, eva.baseClassName);
                    }

                    attrVertex.property("class", eva.baseClassName, "isSV", eva.cardinality.equals(EVA.SINGLEVALUED));
                    TitanVertex inverseVertex = graph.addVertex(T.label, "attribute",
                            "name", eva.inverseEVA, "class", cd.name, "isSV", true);
                    if (attr.comment != null) {
                        inverseVertex.property("comment", attr.comment);
                    }
                    Edge inverseEdge = targetClass.addEdge("has", inverseVertex);
                    if (required) {
                        inverseEdge.property("required", true);
                    }

                    attrVertex.addEdge("inverse", inverseVertex);
                    inverseVertex.addEdge("inverse", attrVertex);

                    if (eva.distinct != null) {
                        attrVertex.property("distinct", eva.distinct);
                    }
                    if (eva.max != null) {
                        attrVertex.property("max", eva.max);
                    }

                    graph.tx().commit();

                } else {
                    throwException("Attribute %s is not a DVA or an EVA!", attr);
                }
            }

            if (cd instanceof SubclassDef) {
                SubclassDef scd = (SubclassDef) cd;
                for (int i = 0; i < scd.numberOfSuperClasses(); i++) {
                    String superClassName = scd.getSuperClass(i);
                    if (superClassName.equals(cd.name)) {
                        throwException("Class %s cannot subclass itself!", cd.name);
                    }
                    Vertex superClass = lookupClass(g, superClassName);
                    if (superClass != null) {
                        superClass.addEdge("superclasses", newClass);
                        newClass.addEdge("subclasses", superClass);

                        // copy attributes from superclass
                        GraphTraversal<Vertex, Edge> superAttributes = g.V(superClass.id()).outE("has");
                        while (superAttributes.hasNext()) {
                            Edge edge = superAttributes.next();
                            Edge e = newClass.addEdge("has", edge.inVertex());
                            if (edge.property("isDVA").isPresent()) {
                                e.property("isDVA", true);
                            }
                            if (edge.property("required").isPresent()) {
                                e.property("required", true);
                            }
                        }
                    } else {
                        throwException("Class %s subclasses the non-existent %s class!",
                                cd.name, superClassName);
                    }
                }
            } else {
                GraphTraversal<Vertex, Vertex> traversal = g.V().hasLabel("classDef").has("name", "root node");
                Vertex root = traversal.next();

                boolean found = false;
                Iterator<Edge> iter = newClass.edges(Direction.IN);
                while (iter.hasNext()) {
                    Edge x = iter.next();
                    if (x.outVertex().property("name").value().equals("root node")) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    newClass.addEdge("subclasses", root);
                    root.addEdge("superclasses", newClass);
                }
            }

            graph.tx().commit();
            System.out.printf("Class %s defined\n", cd.name);
        } catch (RuntimeException e) {
            graph.tx().rollback();
            throw e;
        }
    }

    private Vertex getVertex(String s) {
        GraphTraversalSource g = graph.traversal();
        return lookupClass(g, s);
    }


    private class TitanNoSQLAdapter implements Adapter {

        private TitanNoSQLDatabase db;

        private TitanNoSQLAdapter(TitanNoSQLDatabase db)
        {
            this.db = db;
        }

        @Override
        public void putClass(ClassDef cd) {
            db.processClassDef(cd);
        }


        @Override
        public ClassDef getClass(Query query) throws ClassNotFoundException
        {
            return getClass(query.queryName);
        }

        @Override
        public ClassDef getClass(String s) throws ClassNotFoundException
        {
            Vertex currentVertex = db.getVertex(s);
            ClassDef currentClassDef = new ClassDef();
            currentClassDef.name = currentVertex.property("name").value().toString();
            currentClassDef.comment = currentVertex.property("comment").value().toString();

            Iterator<Edge> iter = currentVertex.edges(Direction.OUT, "has");
            while (iter.hasNext()) {
                Edge currentEdge = iter.next();
                Vertex propertyVertex = currentEdge.outVertex();
                Attribute newAttribute = new Attribute();
                newAttribute.name = propertyVertex.property("name").value().toString();
                currentClassDef.addAttribute(newAttribute);
            }

            return currentClassDef;
        }

        /* InsertQuery */
        @Override
        public void putObject(WDBObject wdbObject) {

        }

        /* RetrieveQuery */
        @Override
        public WDBObject getObject(String s, Integer integer)
        {
            return null;
        }

        @Override
        public ArrayList<WDBObject> getObjects(IndexDef indexDef, String s)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void commit()
        {

        }

        @Override
        public void abort()
        {

        }

    }
}
