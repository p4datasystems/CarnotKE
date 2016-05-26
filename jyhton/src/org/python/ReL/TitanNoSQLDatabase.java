package org.python.ReL;

import java.util.*;
import java.io.*;

import org.apache.commons.lang3.SerializationUtils;
import org.python.ReL.WDB.database.wdb.metadata.*;
import com.thinkaurelius.titan.core.*;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.*;
import org.python.ReL.WDB.database.wdb.metadata.Attribute;
import org.python.ReL.WDB.database.wdb.metadata.ClassDef;
import org.python.ReL.WDB.database.wdb.metadata.DVA;
import org.python.ReL.WDB.database.wdb.metadata.EVA;
import org.python.ReL.WDB.database.wdb.metadata.IndexDef;
import org.python.ReL.WDB.database.wdb.metadata.InsertQuery;
import org.python.ReL.WDB.database.wdb.metadata.Query;
import org.python.ReL.WDB.database.wdb.metadata.SubclassDef;
import org.python.ReL.WDB.database.wdb.metadata.WDBObject;
import org.python.ReL.WDB.parser.generated.wdb.parser.*;
import org.python.core.*;

/**
 * @author Alvin Deng
 * @author Raymond Chee
 * @date 05/03/2016
 *
 * TitanDB Adapter for CarnotKE
 */

public class TitanNoSQLDatabase extends DatabaseInterface {

    private static TitanGraph graph = null;
    private static Object rootID = null;
    private static final String classKeyPrefix = "class";
    private static final String objectKeyPrefix = "object";
    private static File INSTALLATION_ROOT;
    private static String PROPERTIES_PATH;

    /**
     * Initialize TitanDB with defined configurations and populate the database with root node.
     */
    private void initTitanDB() {
        validateInstallationRoot();
        graph = TitanFactory.open(PROPERTIES_PATH);

        TitanManagement mg = graph.openManagement();
        boolean initGraph = mg.getGraphIndex("byClassDef") == null;
        if (initGraph) {
            VertexLabel classLabel = mg.makeVertexLabel("classDef").make();
            PropertyKey name = mg.makePropertyKey("name").dataType(String.class).make();
            mg.buildIndex("byClassDef", Vertex.class).addKey(name).indexOnly(classLabel).buildCompositeIndex();
        }
        mg.commit();
        if (initGraph) {
            Vertex root = graph.addVertex(T.label, "classDef", "name", "superNode");
            graph.tx().commit();
            rootID = root.id();
        } else {
            GraphTraversalSource g = graph.traversal();
            g.V().hasLabel("classDef").forEachRemaining(n -> {
                if(n.property("name").isPresent()) {
                    if (n.property("name").value().equals("superNode")) {
                        rootID = n.id();
                    }
                }
            });
        }
    }

    /**
     * Initialize the INSTALLATION_ROOT path
     */
    public void validateInstallationRoot() {
        final String serverRoot = System.getenv("INSTALLATION_ROOT");
        if (serverRoot == null) {
            // maybe print?
        }

        INSTALLATION_ROOT = new File(serverRoot);
        if (!INSTALLATION_ROOT.isDirectory()) {
            // maybe print?
        }
        PROPERTIES_PATH = new File(serverRoot, "CarnotKE.properties").getAbsolutePath();
    }

    /**
     * Default constructor to support TitanNoSQLAdapter.
     */
    public TitanNoSQLDatabase() {
        this.adapter = new TitanNoSQLAdapter(this);
        initTitanDB();
    }

    /**
     * Iterate through the graph and find the proper ClassDef vertex.
     *
     * @param g    Traversal source to iterate through the graph
     * @param name Name of ClassDef
     */
    private Vertex lookupClass(GraphTraversalSource g, String name) {
        return getSubclass(g, rootID, name);
    }

    /**
     * Throw exceptions.
     *
     * @param format Format of the error
     * @param args
     */
    private void throwException(String format, Object... args) {
        throw new RuntimeException(String.format(format, args));
    }

    /**
     * Obtain the vertex that is a subclass of superclass.
     *
     * @param g               Traversal source to iterate through the graph
     * @param classID         Specific vertex ID of TitanDB
     * @param targetClassName Name of the superclass
     * @return Vertex of the subclass
     */
    private Vertex getSubclass(GraphTraversalSource g, Object classID, String targetClassName) {
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

    /**
     * Get the proper vertex with given attribute.
     *
     * @param g             Traversal source to iterate through the graph
     * @param classDef      ClassDef of the instance
     * @param isDVA         Indicates if the attribute is DVA or not
     * @param attributeName Name of the attribute
     * @return Vertex with the given attribute
     */
    private Vertex getAttribute(GraphTraversalSource g, Vertex classDef, boolean isDVA, String attributeName) {
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

    /**
     * Checking the assignment type and throw proper exception.
     *
     * @param query         UpdateQuery query that contains the class name
     * @param name          Attribute name
     * @param attrVertex    Attribute Vertex
     * @param dvaAssignment dvaAssignment Object to check the attribute value
     */
    private void checkAssignmentType(UpdateQuery query, String name, Vertex attrVertex, DvaAssignment dvaAssignment) {
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

    /**
     * Initialize default values for DVAs.
     *
     * @param g        Traversal source to iterate through the graph
     * @param classDef Vertex of the ClassDef
     * @param entity   Vertex of the instance
     */
    private void setDefaultDVAs(GraphTraversalSource g, Vertex classDef, Vertex entity) {
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

    /**
     * Traverse through the graph and obtain an instance vertex based on the requirements.
     *
     * @param classDef Vertex of a ClassDef
     * @param UID      Identifier number in the WDBObject for an instance
     * @return An instance vertex of the ClassDef with a specific UID
     */
    private Vertex getInstanceVertex(Vertex classDef, Integer UID) {
        GraphTraversalSource g = graph.traversal();
        GraphTraversal<Vertex, Vertex> iter = g.V(classDef.id()).out("instance");
        while (iter.hasNext()) {
            Vertex currentInstance = iter.next();
            HashMap<String, WDBObject> map = (HashMap<String, WDBObject>) currentInstance.property("wdbobject").value();
            WDBObject wdbObject = map.get("wdbobject");
            if (wdbObject != null && wdbObject.getUid().equals(UID)) {
                return currentInstance;
            }
        }
        return null;
    }

    /**
     * Retrieve instances of a class.
     *
     * @param g          Traversal source to iterate through the graph
     * @param classDef   Vertex of the ClassDef
     * @param expression Expression so that can be checked through the parse tree
     * @return A set of Vertices that are instances of classDef
     */
    private Set<Vertex> getInstances(GraphTraversalSource g, Vertex classDef, SimpleNode expression) {
        Set<Vertex> res = new HashSet<>();
        GraphTraversal<Vertex, Vertex> iter = g.V(classDef.id()).out("instance");
        while (iter.hasNext()) {
            Vertex currentInstance = iter.next();
            if (matches(g, classDef, expression, currentInstance)) {
                res.add(currentInstance);
            }
        }

        GraphTraversal<Vertex, Vertex> subclasses = g.V(classDef.id()).out("superclasses");
        while (subclasses.hasNext()) {
            res.addAll(getInstances(g, subclasses.next(), expression));
        }
        return res;
    }

    /**
     * Check current attribute is an integer.
     *
     * @param attributeName Name of the attribute
     * @param attrVertex    Vertex of the attribute
     * @param quantifier    Value of the attribute
     */
    private void checkAttributeIsInteger(String attributeName, Vertex attrVertex, String quantifier) {
        String data_type = (String) attrVertex.property("data_type").value();
        if (!data_type.equals("integer")) {
            throwException("Cannot compare attribute %s of type %s with quantifier '%s'",
                    attributeName, data_type, quantifier);
        }
    }

    /**
     * Check if this instsance of ClassDef satisfies the expression
     *
     * @param g          Traversal source to iterate through the graph
     * @param classDef   Vertex of ClassDef
     * @param expression Expression that will go through the parse tree
     * @param instance   Instance of the ClassDef
     * @return True or False
     */
    private boolean matches(GraphTraversalSource g, Vertex classDef, Node expression, Vertex instance) {
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

    /**
     * Check if the current classDef is a subclass of targetClassName
     *
     * @param g               Traversal source to iterate through the graph
     * @param classDef        Vertex of ClassDef
     * @param targetClassName Name of the superclass
     * @return True or False depends on if classDef is subclass of the targetClassName
     */
    private boolean isSubclass(GraphTraversalSource g, Vertex classDef, String targetClassName) {
        return getSubclass(g, classDef.id(), targetClassName) != null;
    }

    /**
     * Remove the list of edges between start vertex and end vertex
     *
     * @param g        Traversal source to iterate through the graph
     * @param start    Start vertex
     * @param end      End vertex
     * @param toRemove List of edges to remove
     */
    private void scheduleDisconnect(GraphTraversalSource g, Vertex start, Vertex end, List<Edge> toRemove) {
        GraphTraversal<Vertex, Edge> forwards = g.V(start.id()).outE("eva");
        while (forwards.hasNext()) {
            Edge e = forwards.next();
            if (e.inVertex().id().equals(end.id())) {
                toRemove.add(e);
            }
        }
    }

    /**
     * Find the inverse relationship name of a ClassDef and an edge
     *
     * @param classDefName String of classDef
     * @param edgeName String of an edge name
     * @return String of inverse relationship
     */
    private String findInverseEdgeName(String classDefName, String edgeName) {
        Iterator<Vertex> iter = graph.vertices();
        while (iter.hasNext()) {
            Vertex currentVertex = iter.next();
            if (currentVertex.label().equals("attribute")) {
                if (currentVertex.property("name").value().equals(edgeName)) {
                    Iterator<Edge> edges = currentVertex.edges(Direction.OUT);
                    while (edges.hasNext()) {
                        Edge currentE = edges.next();
                        String inverseEdgeName = currentE.inVertex().property("name").value().toString();
                        if (currentE.inVertex().property("class").value().equals(classDefName)) {
                            return inverseEdgeName;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Obtain an instance of a classDef with proper attribute and value
     *
     * @param classDef String of a ClassDef
     * @param attribute String of an attribute key
     * @param value String of an attribute value
     * @return An instance with above requirements
     */
    private Vertex getVertex(String classDef, String attribute, String value) {
        Iterator<Vertex> vertices = graph.vertices();

        while (vertices.hasNext()) {
            Vertex nextV = vertices.next();
            String currentLabel = nextV.label();
            if (currentLabel.equals("entity") && nextV.property("class").value().equals(classDef)) {
                if (nextV.property(attribute).value().toString().equals(value)) {
                    return nextV;
                }
            }
        }
        return null;
    }

    /**
     * Process ModifyQuery to construct relationships between instances
     *
     * @param mq ModifyQuery
     */
    private void processModifyQuery(ModifyQuery mq) {
        GraphTraversalSource g = graph.traversal();
        ArrayList<Vertex> instanceList = new ArrayList<>();
        Iterator<Vertex> vertices = graph.vertices();
        ArrayList<Vertex> modifyingVertex = new ArrayList<>();

        while (vertices.hasNext()) {
            Vertex nextV = vertices.next();
            String currentLabel = nextV.label();
            if (currentLabel.equals("entity") && nextV.property("class").value().equals(mq.className)) {
                instanceList.add(nextV);
            }
        }

        Vertex classDef = lookupClass(g, mq.className);
        for (Vertex instance : instanceList) {
            if (matches(g, classDef, mq.expression, instance)) {
                modifyingVertex.add(instance);
            }
        }

        for (Vertex startVertex : modifyingVertex) {
            for (Assignment y : mq.assignmentList) {
                EvaAssignment current = (EvaAssignment) y;
                String[] targetArray = current.expression.jjtGetChild(0).toString().split(" ");
                String targetAttribute = targetArray[0];
                String targetValue = targetArray[2];
                Vertex targetVertex = getVertex(current.targetClass, targetAttribute, targetValue);
                String edgeName = y.AttributeName;
                startVertex.addEdge(edgeName, targetVertex);

                String inverseEdgeName = findInverseEdgeName(mq.className, edgeName);
                assert targetVertex != null;
                targetVertex.addEdge(inverseEdgeName, startVertex);
            }
        }

        System.out.println("ModifyQuery Complete");
        graph.tx().commit();
    }

    /**
     * Calculate number of edges replaced, inserted, and deleted
     *
     * @param g        Traversal source to iterate through the graph
     * @param entity
     * @param classDef Vertex of a classDef
     * @param query    UpdateQuery
     * @return data[0] = num edges replaced, data[1] = num edges inserted, data[2] = num edges removed
     */
    private int[] doAssignments(GraphTraversalSource g, Vertex entity, Vertex classDef, UpdateQuery query) {
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

    /**
     * Check required conditions for inserts
     *
     * @param iq       InsertQuery
     * @param g        Traversal source to iterate through the graph
     * @param classDef Vertex of classDef
     * @param entity   Instance of the ClassDef
     */
    private void checkRequiredInserts(InsertQuery iq, GraphTraversalSource g, Vertex classDef, Vertex entity) {
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

    /**
     * Print all UpdateQuery results
     *
     * @param newVertexCount Count of Vertices
     * @param edgeCounts     edgeCounts[0] = number edges of replaced,
     *                       edgeCounts[1] = number edges of inserted,
     *                       edgeCounts[2] = number of edges removed
     */
    private void printUpdateQueryResults(int newVertexCount, int[] edgeCounts) {
        if (newVertexCount != 0) {
            System.out.printf("%d vertices inserted.\n", newVertexCount);
        }
        if (edgeCounts[0] != 0) {
            System.out.printf("%d edges replaced.\n", edgeCounts[0]);
        }
        if (edgeCounts[1] != 0) {
            System.out.printf("%d edges inserted.\n", edgeCounts[1]);
        }
        if (edgeCounts[2] != 0) {
            System.out.printf("%d edges removed.\n", edgeCounts[2]);
        }
    }

    /**
     * Perform insertion into the graph
     *
     * @param iq       InsertQuery
     * @param g        Traversal source to iterate through the graph
     * @param classDef Vertex of ClassDef
     * @param entity   Instance Vertex of ClassDef
     * @return The number of edges inserted
     */
    private int[] doInsert(InsertQuery iq, GraphTraversalSource g, Vertex classDef, Vertex entity) {
        setDefaultDVAs(g, classDef, entity);
        int[] counts = doAssignments(g, entity, classDef, iq);
        checkRequiredInserts(iq, g, classDef, entity);
        return counts;
    }

    /**
     * Recursively traverse through the parse tree and obtain vertices from the graph.
     *
     * @param classDef String of a classDef
     * @param overall  An overall vertex list to keep track of based on the query
     */
    private static void recurRetrieve(String classDef, ArrayList<Vertex> overall) {
        Iterator<Vertex> vertices = graph.vertices();
        HashSet<String> children = new HashSet<>();

        while (vertices.hasNext()) {
            Vertex nextV = vertices.next();
            String currentLabel = nextV.label();
            if (currentLabel.equals("classDef")) {
                Iterator<Edge> iter = nextV.edges(Direction.OUT);
                while (iter.hasNext()) {
                    Edge currentE = iter.next();
                    if (currentE.label().equals("subclasses")) {
                        String inbound = currentE.inVertex().property("name").value().toString();
                        if (inbound.equals(classDef)) {
                            children.add(currentE.outVertex().property("name").value().toString());
                        }
                    }
                }
            }
            if (currentLabel.equals("entity") && nextV.property("class").value().equals(classDef)) {
                overall.add(nextV);
            }
        }

        for (String x : children) {
            recurRetrieve(x, overall);
        }
    }

    /**
     * Process RetrieveQuery and return a list of PyObjects for printing
     * @param rq RetrieveQuery
     * @return A list of PyObjects to be printed
     */
    private ArrayList<PyObject> processRetrieveQuery(RetrieveQuery rq) {

        ArrayList<Vertex> overall = new ArrayList<>();
        ArrayList<PyObject> rows = new ArrayList<>();
        boolean allToggle = false;

        recurRetrieve(rq.className, overall);
        for (Vertex instance : overall) {
            ArrayList<PyObject> column = new ArrayList<PyObject>();
            for (int j = 0; j < rq.numAttributePaths(); j++) {
                AttributePath path = rq.getAttributePath(j);
                HashMap<Vertex, String> neighborMap = new HashMap<>();

                for (int i = 0; i < path.levelsOfIndirection(); i++) {
                    Iterator<Edge> iter = instance.edges(Direction.OUT);
                    while (iter.hasNext()) {
                        Edge current = iter.next();
                        if (current.label().equals(path.getIndirection(i))) {
                            if (!neighborMap.containsKey(current.inVertex())) {
                                neighborMap.put(current.inVertex(), path.getIndirection(i));
                            }
                        }
                    }
                }

                if (path.attribute.equals("*")) {
                    Iterator<VertexProperty<Object>> it = instance.properties();
                    while (it.hasNext()) {
                        VertexProperty<Object> property = it.next();
                        if (!property.key().equals("property")) {
                            String colElement = property.value().toString();
                            try {
                                Double.parseDouble(colElement);
                                try {
                                    Integer.parseInt(colElement);
                                    column.add(new PyInteger(Integer.parseInt(colElement)));
                                } catch (NumberFormatException e) {
                                    column.add(new PyFloat(Float.parseFloat(colElement)));
                                }
                            } catch (NumberFormatException e) {
                                column.add(new PyString(colElement));
                            }
                        }
                    }
                    allToggle = true;
                } else {
                    if(!allToggle) {
                        Iterator<VertexProperty<Object>> it = instance.properties();
                        while (it.hasNext()) {
                            VertexProperty<Object> property = it.next();
                            if (property.key().equals(path.attribute)) {
                                String colElement = property.value().toString();
                                try {
                                    Double.parseDouble(colElement);
                                    try {
                                        Integer.parseInt(colElement);
                                        column.add(new PyInteger(Integer.parseInt(colElement)));
                                    } catch (NumberFormatException e) {
                                        column.add(new PyFloat(Float.parseFloat(colElement)));
                                    }
                                } catch (NumberFormatException e) {
                                    column.add(new PyString(colElement));
                                }
                            }
                        }
                    }
                }

                for (Vertex current : neighborMap.keySet()) {
                    String colElement = current.property(path.attribute).value().toString();
                    try {
                        Double.parseDouble(colElement);
                        try {
                            Integer.parseInt(colElement);
                            column.add(new PyInteger(Integer.parseInt(colElement)));
                        } catch (NumberFormatException e) {
                            column.add(new PyFloat(Float.parseFloat(colElement)));
                        }
                    } catch (NumberFormatException e) {
                        column.add(new PyString(colElement));
                    }
                }
            }
            rows.add(new PyTuple(column.toArray(new PyObject[column.size()])));
        }
        return rows;
    }

    /**
     * Process InsertQuery and insert the instance into the graph.
     *
     * @param iq InsertQuery
     */
    private void processInsertQuery(InsertQuery iq) {
        GraphTraversalSource g = graph.traversal();
        try {
            int newVertexCount = 0, edgeCounts[];
            Vertex classDef = lookupClass(g, iq.className);
            if (classDef == null) {
                throwException("Cannot insert into class %s because it does not exist!", iq.fromClassName);
            }
            if (iq.fromClassName == null) {
                // just inserting a new entity
                Vertex entity = graph.addVertex(T.label, "entity", "class", iq.className);

                newVertexCount++;
                // make the entity an instance of the class
                classDef.addEdge("instance", entity);
                edgeCounts = doInsert(iq, g, classDef, entity);
            } else {
                // inserting a subclass into an existing superclass
                Vertex superclassDef = lookupClass(g, iq.fromClassName);
                if (superclassDef == null) {
                    throwException("Cannot extend into class %s because it does not exist!", iq.fromClassName);
                }
                if (!isSubclass(g, superclassDef, iq.className)) {
                    throwException("Cannot extend class %s to class %s because %2$s is not a subclass of %1$s!",
                            iq.fromClassName, iq.className);
                }
                edgeCounts = new int[3];
                for (Vertex instance : getInstances(g, superclassDef, iq.expression)) {
                    g.V(instance.id()).inE("instance").next().remove();
                    classDef.addEdge("instance", instance);
                    int[] counts = doInsert(iq, g, classDef, instance);
                    for (int i = 0; i < 3; i++) {
                        edgeCounts[i] += counts[i];
                    }
                }
            }

            printUpdateQueryResults(newVertexCount, edgeCounts);
            graph.tx().commit();
        } catch (RuntimeException e) {
            System.err.println("Insert failed! Rolling back changes.");
            graph.tx().rollback();
            throw e;
        }
    }

    /**
     * Process the ClassDef and insert it into the graph.
     *
     * @param cd ClassDef that needs to be inserted
     */
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

            /* Serialization for ClassDef */
            HashMap<String, ClassDef> map = new HashMap<>();
            map.put("classdefobject", cd);
            newClass.property("classdefobject", map);

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
                    Vertex targetClass = getClassDefVertex(eva.baseClassName);

                    if (targetClass == null) {
                        targetClass = graph.addVertex(T.label, "classDef", "name", eva.baseClassName);
                        targetClass.property("ForwardInit", "Yes");

                        GraphTraversal<Vertex, Vertex> traversal = g.V().hasLabel("classDef").has("name", "superNode");
                        Vertex root = traversal.next();
                        targetClass.addEdge("subclasses", root);
                        root.addEdge("superclasses", targetClass);
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
                    Vertex superClass = getClassDefVertex(superClassName);
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
                GraphTraversal<Vertex, Vertex> traversal = g.V().hasLabel("classDef").has("name", "superNode");
                Vertex root = traversal.next();

                boolean found = false;
                Iterator<Edge> iter = newClass.edges(Direction.IN);
                while (iter.hasNext()) {
                    Edge x = iter.next();
                    if (x.outVertex().property("name").value().equals("superNode")) {
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

    /**
     * Get the vertex with given ClassDef name.
     *
     * @param s ClassDef name
     * @return ClassDef Vertex with string name of s
     */
    private Vertex getClassDefVertex(String s) {
        GraphTraversalSource g = graph.traversal();
        return lookupClass(g, s);
    }

    /* Bo's Implementation */
    private String makeClassKey(String className)
    {
        return classKeyPrefix + ":" + className;
    }

    private String makeObjectKey(Integer Uid)
    {
        return objectKeyPrefix + ":" + Uid.toString();
    }

    private void putClassDef(ClassDef classDef) {
        final String keyString = makeClassKey(classDef.name);

        Vertex classDefVertex = graph.addVertex(T.label, "classDef", "keyString", keyString);
        classDefVertex.property("valueData", classDef);
        graph.tx().commit();
        System.out.println("Inserting class complete");
    }

    private ClassDef getClassDef(String classDefName) {
        final String keyString = makeClassKey(classDefName);
        GraphTraversalSource g = graph.traversal();
        GraphTraversal<Vertex, Vertex> graphTraversal = g.V();

        while (graphTraversal.hasNext()) {
            Vertex currentVertex = graphTraversal.next();
            if(currentVertex.property("keyString").isPresent()) {
                if (currentVertex.property("keyString").value().equals(keyString)) {
                    return (ClassDef) currentVertex.property("valueData").value();
                }
            }
        }
        return null;
    }

    private void putWDBObject(WDBObject wdbObject) {
        final String keyString = makeObjectKey(wdbObject.getUid());

        Vertex WDBObjectVertex = graph.addVertex(T.label, "WDBObject", "keyString", keyString);
        WDBObjectVertex.property("valueData", wdbObject);
        graph.tx().commit();
        System.out.println("Inserting object complete");
    }

    private WDBObject getWDBObject(String className, Integer Uid) {
        final String keyString = makeObjectKey(Uid);

        GraphTraversalSource g = graph.traversal();
        GraphTraversal<Vertex, Vertex> graphTraversal = g.V();
        while (graphTraversal.hasNext()) {
            Vertex currentVertex = graphTraversal.next();
            if(currentVertex.property("keyString").isPresent()) {
                if (currentVertex.property("keyString").value().equals(keyString)) {
                    return (WDBObject) currentVertex.property("valueData").value();
                }
            }
        }
        return null;
    }

    private class TitanNoSQLAdapter implements Adapter {

        private TitanNoSQLDatabase db;

        /**
         * Constructor of TitanNoSQLDatabase.
         *
         * @param db TitanNoSQLDatabase Object
         */
        private TitanNoSQLAdapter(TitanNoSQLDatabase db) {
            this.db = db;
        }

        /**
         * Inserting a new ClassDef into the graph.
         *
         * @param cd ClassDef that needs to be inserted
         */
        @Override
        public void putClass(ClassDef cd) {
            db.putClassDef(cd);
        }

        /**
         * Searches for a ClassDef that matches the query in the graph.
         *
         * @param query Query that searches for the ClassDef
         * @return ClassDef that the query searches for
         * @throws ClassNotFoundException When the ClassDef does not exist in the graph
         */
        @Override
        public ClassDef getClass(Query query) throws ClassNotFoundException {
            return getClass(query.queryName);
        }

        /**
         * Searches for a ClassDef that matches to the string name in the graph.
         *
         * @param s Name of the ClassDef
         * @return ClassDef with the name of s
         * @throws ClassNotFoundException
         */
        @Override
        public ClassDef getClass(String s) throws ClassNotFoundException {
            System.out.println("Retrieving classes");
            ClassDef classDef = db.getClassDef(s);
            if (classDef == null) {
                throw new ClassNotFoundException("Key is not present in table");
            }
            return classDef;
        }


        @Override
        public void putObject(WDBObject wdbObject) {
            db.putWDBObject(wdbObject);
        }

        @Override
        public WDBObject getObject(String className, Integer Uid) {
            return db.getWDBObject(className, Uid);
        }

        @Override
        public ArrayList<WDBObject> getObjects(IndexDef indexDef, String s) {
            throw new UnsupportedOperationException();
        }


        @Override
        public void commit() {
            graph.tx().commit();
        }

        @Override
        public void abort() {

        }

    }
}
