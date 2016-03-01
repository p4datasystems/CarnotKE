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
            VertexLabel classLabel = mg.containsVertexLabel("classDef") ? mg.getVertexLabel("classDef") : mg.makeVertexLabel("classDef").make();
            PropertyKey name = mg.containsPropertyKey("name") ? mg.getPropertyKey("name") : mg.makePropertyKey("name").dataType(String.class).make();
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

    private static Vertex lookupClass(GraphTraversalSource g, String name) {
        return getSubclass(g, rootID, name);
    }

    // returns the number of edges inserted
    private static int[] doInsert(InsertQuery iq, GraphTraversalSource g, Vertex classDef, Vertex entity) {
        setDefaultDVAs(g, classDef, entity);
        int[] counts = doAssignments(g, entity, classDef, iq);
        checkRequiredInserts(iq, g, classDef, entity);
        checkEVARestrictions(iq, g, classDef, entity);
        return counts;
    }

    private static void setDefaultDVAs(GraphTraversalSource g, Vertex classDef, Vertex entity) {
        GraphTraversal<Vertex, Vertex> dvas = g.V(classDef.id()).outE("has").has("isDVA").inV().has("default_value");
        while (dvas.hasNext()) {
            Vertex dvaVertex = dvas.next();
            Object initValue = dvaVertex.property("default_value").value();
            String dvaName = (String) dvaVertex.property("name").value();
            if (!entity.property(dvaName).isPresent()) {
                entity.property(dvaName, initValue);
            }
        }
    }

    private static void checkRequiredInserts(InsertQuery iq, GraphTraversalSource g, Vertex classDef, Vertex entity) {
        GraphTraversal<Vertex, Edge> requiredAttrs = g.V(classDef.id()).outE("has").has("required");
        while (requiredAttrs.hasNext()) {
            Edge attr = requiredAttrs.next();
            Vertex required = attr.inVertex();
            String name = (String) required.property("name").value();
            if (attr.property("isDVA").isPresent()) {
                // didn't insert a required DVA
                if (!entity.property(name).isPresent()) {
                    throwException("Did not insert required value %s into an instance of class %s!",
                            name, iq.className);
                }
            } else {
                // didn't insert a required EVA
                if (!g.V(entity.id()).outE(name).hasNext()) {
                    throwException("Did not insert required EVA %s into an instance of class %s!",
                            name, iq.className);
                }
            }
        }
    }

    // check if the insertion respects max number of references defined and distinctness restriction
    private static void checkEVARestrictions(InsertQuery iq, GraphTraversalSource g, Vertex classDef, Vertex entity) {
        GraphTraversal<Vertex, Vertex> evaAttrs = g.V(classDef.id()).outE("has").hasNot("isDVA").inV();
        while (evaAttrs.hasNext()) {
            Vertex evaVertex = evaAttrs.next();
            Vertex inverseEVA = g.V(classDef.id()).out("inverse").next();
            boolean singleValued = (Boolean) evaVertex.property("isSV").value();
            boolean inverseSingleValued = (Boolean) inverseEVA.property("isSV").value();
            int maxReferences = singleValued ? 1 : (Integer) evaVertex.property("max").value();
            int inverseMaxReferences = inverseSingleValued ? 1 : (Integer) inverseEVA.property("max").value();
            boolean checkDistinct = !singleValued && (Boolean) evaVertex.property("distinct").value();
            boolean inverseCheckDistinct = !inverseSingleValued && (Boolean) inverseEVA.property("distinct").value();
            Set<Object> connectedVertexIds = new HashSet<>();
            String evaName = (String) evaVertex.property("name").value();
            String inverseName = (String) inverseEVA.property("name").value();
            GraphTraversal<Vertex, Vertex> connections = g.V(entity.id()).out(evaName);
            int count = 0;
            while (connections.hasNext()) {
                Vertex connection = connections.next();
                if (checkDistinct && !connectedVertexIds.add(connection.id())) {
                    // we need to check for distinctness and connected vertices failed to add
                    // (and thus we have a duplicate)
                    throwException("EVA %s is DISTINCT but class %s entity %s " +
                                    "references class %s entity %s more than once!",
                            evaName, iq.className, entity.id(),
                            evaVertex.property("class").value(), connection.id());
                }
                if (++count > maxReferences) {
                    throwException("EVA %s has a limit of %d references which was exceeded!",
                            evaName, maxReferences);
                }
                int inverseCount = 0;
                GraphTraversal<Vertex, Vertex> inverses = g.V(connection.id()).out(inverseName);
                while (inverses.hasNext()) {
                    if (inverseCheckDistinct && inverseCount > 0) {
                        throwException("Eva %s is DISTINCT but class %s entity %s " +
                                        "references class %s entity %s more than once!",
                                inverseName, evaVertex.property("class").value(), connection.id(),
                                iq.className, entity.id());
                    }
                    if (++inverseCount > inverseMaxReferences) {
                        throwException("EVA %s has a limit of %d references which was exceeded!",
                                inverseName, inverseMaxReferences);
                    }
                }
            }
        }
    }

    // int[] data; data[0] = num edges replaced, data[1] = num edges inserted, data[2] = num edges removed
    private static int[] doAssignments(GraphTraversalSource g, Vertex entity, Vertex classDef, UpdateQuery query) {
        int[] counts = new int[3];
        for (Assignment assignment : query.assignmentList) {
            if (assignment instanceof DvaAssignment) {
                Vertex dvaVertex = getAttribute(g, classDef, true, assignment.AttributeName);
                if (dvaVertex == null) {
                    throwException("Class %s does not have an dva %s!", query.className, assignment.AttributeName);
                }
                DvaAssignment dvaAssignment = (DvaAssignment) assignment;
                checkAssignmentType(query, assignment.AttributeName, dvaVertex, dvaAssignment);
                entity.property(assignment.AttributeName, dvaAssignment.Value);
            } else if (assignment instanceof EvaAssignment) {
                Vertex evaVertex = getAttribute(g, classDef, false, assignment.AttributeName);
                if (evaVertex == null) {
                    throwException("Class %s does not have an eva %s!", query.className, assignment.AttributeName);
                }
                EvaAssignment evaAssignment = (EvaAssignment) assignment;
                String evaClass = (String) evaVertex.property("class").value();
                if (!evaClass.equals(evaAssignment.targetClass) &&
                        !isSubclass(g, lookupClass(g, evaClass), evaAssignment.targetClass)) {
                    throwException("EVA %s cannot be assigned from class %s to class %s!",
                            assignment.AttributeName, query.className, evaClass);
                }

                Vertex evaClassDef = lookupClass(g, evaClass);
                if (evaClassDef == null) {
                    throwException("Class %s is not defined!", evaClass);
                }
                Set<Vertex> instances = getInstances(g, evaClassDef, query.expression);
                String evaInverseName = (String) g.V(evaClassDef.id()).out("inverse").next().property("name").value();
                switch (evaAssignment.mode) {
                    case 0: { // REPLACE_MODE
                        // remove existing edges between entity and instances
                        for (Vertex instance : instances) {
                            // may want to remove at the end in case it messes up graph traversals
                            List<Edge> toRemove = new ArrayList<>();
                            scheduleDisconnect(g, entity, instance, toRemove);
                            scheduleDisconnect(g, instance, entity, toRemove);
                            for (Edge e : toRemove) {
                                e.remove();
                                counts[0]++;
                            }
                        }
                        // continue on to insert
                    }
                    case 1: { // INSERT_MODE
                        for (Vertex instance : instances) {
                            entity.addEdge("eva", instance, "name", evaAssignment.AttributeName);
                            instance.addEdge("eva", entity, "name", evaInverseName);
                            counts[1]++;
                        }
                        break;
                    }
                    case 2: { // EXCLUDE_MODE
                        for (Vertex instance : instances) {
                            GraphTraversal<Vertex, Edge> evas =
                                    g.V(instance.id()).outE("eva").has("name");
                            while (evas.hasNext()) {
                                Edge edge = evas.next();
                                if (edge.property("name").value().equals(evaAssignment.AttributeName)) {
                                    GraphTraversal<Edge, Edge> connected = g.E(edge.id()).inV().outE("eva").has("name");
                                    List<Edge> toRemove = new ArrayList<>();
                                    toRemove.add(edge);
                                    while (connected.hasNext()) {
                                        Edge next = connected.next();
                                        if (next.property("name").value().equals(evaInverseName)) {
                                            toRemove.add(next);
                                        }
                                    }
                                    for (Edge e : toRemove) {
                                        e.remove();
                                        counts[2]++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return counts;
    }

    private static void scheduleDisconnect(GraphTraversalSource g, Vertex start, Vertex end, List<Edge> toRemove) {
        GraphTraversal<Vertex, Edge> forwards = g.V(start.id()).outE("eva");
        while (forwards.hasNext()) {
            Edge e = forwards.next();
            if (e.inVertex().id().equals(end.id())) {
                toRemove.add(e);
            }
        }
    }

    private static Set<Vertex> getInstances(GraphTraversalSource g, Vertex classDef, SimpleNode expression) {
        Set<Vertex> res = new HashSet<>();
        GraphTraversal<Vertex, Vertex> instances = g.V(classDef.id()).out("instance");
        while (instances.hasNext()) {
            Vertex instance = instances.next();
            if (matches(g, classDef, expression, instance)) {
                res.add(instance);
            }
        }

        GraphTraversal<Vertex, Vertex> subclasses = g.V(classDef.id()).out("superclasses");
        while (subclasses.hasNext()) {
            res.addAll(getInstances(g, subclasses.next(), expression));
        }
        return res;
    }

    private static boolean matches(GraphTraversalSource g, Vertex classDef, Node expression, Vertex instance) {
        if (expression instanceof Root) {
            return matches(g, classDef, expression.jjtGetChild(0), instance);
        }
        if (expression instanceof Cond) {
            String[] split = expression.toString().split(" ", 3);
            String attributeName = split[0];
            Vertex attrVertex = getAttribute(g, classDef, true, attributeName);
            if (attrVertex == null) {
                throwException("Class %s doesn't have attribute %s!", classDef.property("name"), attributeName);
            }
            Property<Object> property = instance.property(attributeName);
            Object attribute = property.isPresent() ? property.value() : null;
            String quantifier = split[1];
            String value = split[2];
            switch (quantifier) {
                case "=": {
                    return attribute == null && value.equals("NULL") ||
                            attribute != null && attribute.toString().equals(value);
                }
                case "<>": {
                    return attribute == null && !value.equals("NULL") ||
                            attribute != null && !attribute.toString().equals(value);
                }
                case "<": {
                    checkAttributeIsInteger(attributeName, attrVertex, quantifier);
                    return attribute != null && ((Integer) attribute) < Integer.parseInt(value);
                }
                case ">": {
                    checkAttributeIsInteger(attributeName, attrVertex, quantifier);
                    return attribute != null && ((Integer) attribute) > Integer.parseInt(value);
                }
                case "<=": {
                    checkAttributeIsInteger(attributeName, attrVertex, quantifier);
                    return attribute != null && ((Integer) attribute) <= Integer.parseInt(value);
                }
                case ">=": {
                    checkAttributeIsInteger(attributeName, attrVertex, quantifier);
                    return attribute != null && ((Integer) attribute) >= Integer.parseInt(value);
                }
                default: {
                    throwException("Invalid quantifier %s!", quantifier);
                }
            }
        }
        if (expression instanceof And) {
            int numChildren = expression.jjtGetNumChildren();
            for (int i = 0; i < numChildren; i++) {
                if (!matches(g, classDef, expression.jjtGetChild(i), instance)) {
                    return false;
                }
            }
            return true;
        }
        if (expression instanceof Or) {
            int numChildren = expression.jjtGetNumChildren();
            for (int i = 0; i < numChildren; i++) {
                if (matches(g, classDef, expression.jjtGetChild(i), instance)) {
                    return true;
                }
            }
            return false;
        }
        if (expression instanceof Not) {
            return !matches(g, classDef, expression.jjtGetChild(0), instance);
        }
        if (expression instanceof False) {
            return false;
        }
        if (expression instanceof True) {
            return true;
        }
        throw new IllegalStateException("Unknown type of Node! " + expression);
    }

    private static void throwException(String format, Object... args) {
        throw new RuntimeException(String.format(format, args));
    }

    private static void checkAttributeIsInteger(String attributeName, Vertex attrVertex, String quantifier) {
        String data_type = (String) attrVertex.property("data_type").value();
        if (!data_type.equals("integer")) {
            throwException("Cannot compare attribute %s of type %s with quantifer '%s'",
                    attributeName, data_type, quantifier);
        }
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

    private static boolean isSubclass(GraphTraversalSource g, Vertex classDef, String targetClassName) {
        return getSubclass(g, classDef.id(), targetClassName) != null;
    }

    private static void checkAssignmentType(UpdateQuery query, String name, Vertex attrVertex, DvaAssignment dvaAssignment) {
        String type = (String) attrVertex.property("data_type").value();
        switch (type) {
            case "boolean": {
                if (!(dvaAssignment.Value instanceof Boolean)) {
                    throwException("Attribute %s of class %s stores boolean values! Found %s",
                            name, query.className, dvaAssignment.Value);
                }
                break;
            }
            case "integer": {
                if (!(dvaAssignment.Value instanceof Integer)) {
                    throwException("Attribute %s of class %s stores integer values! Found %s",
                            name, query.className, dvaAssignment.Value);
                }
                break;
            }
            case "string": {
                if (!(dvaAssignment.Value instanceof String)) {
                    throwException("Attribute %s of class %s stores string values! Found %s",
                            name, query.className, dvaAssignment.Value);
                }
                break;
            }
        }
    }

    private static Vertex getAttribute(GraphTraversalSource g, Vertex classDef, boolean isDVA, String attributeName) {
        GraphTraversal<Vertex, Vertex> attrs;
        if (isDVA) {
            attrs = g.V(classDef.id()).outE("has").has("isDVA").inV().has("name");
        } else {
            attrs = g.V(classDef.id()).outE("has").hasNot("isDVA").inV().has("name");
        }
        while (attrs.hasNext()) {
            Vertex v = attrs.next();
            if (v.property("name").isPresent() && v.property("name").value().equals(attributeName)) {
                return v;
            }
        }
        return null;
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
