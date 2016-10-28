package org.python.ReL;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.cyphersim.CypherSimTranslator;
import org.python.ReL.WDB.database.wdb.metadata.*;
import org.python.ReL.WDB.parser.generated.wdb.parser.QueryParser;
import org.python.core.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

public class ProcessLanguages {
    private final boolean DBG;
    PyRelConnection conn;
    ParserAdapter parserAdapter;
    ArrayList<PyType> pyTupleQueryInstanceTypes;
    ArrayList<String> pyTupleQueryInstanceTypeNames;
    DatabaseInterface connDatabase;
    static Boolean parserInitialized = false;
    static QueryParser parser;

    /**
     * Process a language statement such as SQL or SIM.
     * <p>
     * Note:  The connection should not be closed within this class, as it will be closed
     * by its parent (invoker).
     *
     * @param conn
     */
    public ProcessLanguages(PyRelConnection conn)
    {
        this.conn = conn;
        connDatabase = conn.getDatabase();
        this.parserAdapter = null;
        if (connDatabase instanceof ParserAdapter) {
            this.parserAdapter = (ParserAdapter) connDatabase;
        }
        DBG = conn.getDebug().equalsIgnoreCase("debug");
    }

    public ProcessLanguages(PyRelConnection conn, ArrayList<PyType> instanceTypes, ArrayList<String> instanceTypeNames)
    {
        this.conn = conn;
        connDatabase = conn.getDatabase();
        this.parserAdapter = null;
        if (connDatabase instanceof ParserAdapter) {
            this.parserAdapter = (ParserAdapter) connDatabase;
        }
        this.pyTupleQueryInstanceTypes = instanceTypes;
        this.pyTupleQueryInstanceTypeNames = instanceTypeNames;
        DBG = conn.getDebug().equalsIgnoreCase("debug");
    }
    public static void debugMsg(boolean debugOn, String message)
    {
        if (debugOn)
            System.out.println(message);
    }

    // ------------------------------------- SIM -------------------------
    public synchronized ArrayList<PyObject> processSIM(String ReLstmt) throws Exception {
        final boolean DBG = true;
        ReLstmt = ReLstmt.replaceAll("\\_\\^\\_", ";");
        if (DBG) {
            if (ReLstmt.equalsIgnoreCase("clear database")) {
                OracleNoSQLDatabase db = (OracleNoSQLDatabase) connDatabase;
                db.clearDatabase();
                return null;
            }
            if (ReLstmt.equalsIgnoreCase("stop database")) {
                OracleNoSQLDatabase db = (OracleNoSQLDatabase) connDatabase;
                db.ultimateCleanUp("Cleaning up from test.");
                return null;
            }
        }
        InputStream is = new ByteArrayInputStream(ReLstmt.getBytes());
        Query q = null;
        if (!parserInitialized) {
            parser = new QueryParser(is);
            parserInitialized = true;
        } else {
            parser.ReInit(is);
        }
        try {
            q = parser.getNextQuery();
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
        debugMsg(DBG, "Statement executed: " + ReLstmt);

        if (q instanceof ClassDef) {
            ClassDef cd = (ClassDef) q;
            try {
                parserAdapter.getClass(q);
                //That class already exists;
                throw new Exception("Class \"" + cd.name + "\" already exists");
            } catch (ClassNotFoundException cnfe) {
                if (cd.getClass() == SubclassDef.class) {
                    ClassDef baseClass = null;
                    for (int i = 0; i < ((SubclassDef) cd).numberOfSuperClasses(); i++) {
                        //Cycles are implicitly checked since getClass will fail for the current defining class
                        ClassDef superClass = parserAdapter.getClass(((SubclassDef) cd).getSuperClass(i));
                        if (baseClass == null)
                            baseClass = superClass.getBaseClass(parserAdapter);
                        else if (!baseClass.name.equals(superClass.getBaseClass(parserAdapter).name))
                            throw new Exception("Super classes of class \"" + cd.name + "\" do not share the same base class");
                    }
                }

                parserAdapter.putClass(cd);
                parserAdapter.commit();

            } catch (Exception e) {
                System.out.println("This class already exists: " + cd.name);
                parserAdapter.abort();
                return null;
            }
        }

        if (q instanceof ModifyQuery) {
            if (connDatabase instanceof NonDefaultParser) {
                ((NonDefaultParser) connDatabase).modify(q);
                return null;
            }
            ModifyQuery mq = (ModifyQuery) q;
            try {
                ClassDef targetClass = parserAdapter.getClass(q);
                WDBObject[] targetClassObjs = targetClass.search(mq.expression, parserAdapter);
                if (mq.limit > -1 && targetClassObjs.length > mq.limit)
                    throw new Exception("Matching entities exceeds limit of " + mq.limit.toString());
                for (int i = 0; i < targetClassObjs.length; i++)
                    setValues(mq.assignmentList, targetClassObjs[i], parserAdapter);
                parserAdapter.commit();
            } catch (Exception e) {
                System.out.println(e.toString());
                parserAdapter.abort();
            }
        }

        if (q instanceof InsertQuery) {
            if (connDatabase instanceof NonDefaultParser) {
                ((NonDefaultParser) connDatabase).insert(q);
                return null;
            }
            InsertQuery iq = (InsertQuery) q;
            try {
                ClassDef targetClass = parserAdapter.getClass(q);
                WDBObject newObject = null;

                if (iq.fromClassName != null) {
                    //Inserting from an entity of a superclass...
                    if (targetClass.getClass() == SubclassDef.class) {
                        SubclassDef targetSubClass = (SubclassDef) targetClass;
                        ClassDef fromClass = parserAdapter.getClass(iq.fromClassName);
                        if (targetSubClass.isSubclassOf(fromClass.name, parserAdapter)) {
                            WDBObject[] fromObjects = fromClass.search(iq.expression, parserAdapter);
                            if (fromObjects.length <= 0) {
                                throw new IllegalStateException("Can't find any entities from class \"" + fromClass.name + "\" to extend");
                            }
                            for (int i = 0; i < fromObjects.length; i++) {
                                newObject = targetSubClass.newInstance(fromObjects[i].getBaseObject(parserAdapter), parserAdapter);
                                setValues(iq.assignmentList, newObject, parserAdapter);
                            }
                        }
                        else {
                            throw new IllegalStateException("Inserted class \"" + targetClass.name + "\" is not a subclass of the from class \"" + iq.fromClassName);
                        }
                    }
                    else {
                        throw new IllegalStateException("Can't extend base class \"" + targetClass.name + "\" from class \"" + iq.fromClassName);
                    }
                }
                else {
                    newObject = targetClass.newInstance(null, parserAdapter);
                    setDefaultValues(targetClass, newObject, parserAdapter);
                    setValues(iq.assignmentList, newObject, parserAdapter);
                    checkRequiredValues(targetClass, newObject, parserAdapter);
                }

                if (newObject != null) {
                    newObject.commit(parserAdapter);
                }
                parserAdapter.commit();
            } catch (Exception e) {
                try {
                    if (iq.className.contains(".")) { // SCHEMALESS SUBCLASS
                        System.out.println("SubClass '" + iq.className + "' does not exist. Attempting schemaless insert...");
                        if (iq.expression == null) {
                            throw new IllegalStateException("The WHERE clause is required for schemaless inserts of a subclass");
                        }
                        String child = iq.className.substring(iq.className.indexOf(".") + 1, iq.className.length());
                        String parent = iq.className.substring(0, iq.className.indexOf("."));
                        iq.className = child;
                        iq.fromClassName = parent;

                        // Creating SubClass
                        SubclassDef foo = new SubclassDef(child, "(SubClass) Schemaless Insert");
                        foo.addSuperClass(parent);
                        for (int x = 0; x < iq.assignmentList.size(); x++) {
                            DVA dva = new DVA();
                            DvaAssignment _dva = (DvaAssignment) iq.getAssignment(x);
                            dva.required = true;
                            dva.comment = "";
                            dva.name = _dva.AttributeName;

                            // Find value type
                            Object temp1 = _dva.Value;
                            String temp = temp1.toString();
                            if (temp.equalsIgnoreCase("false") || temp.equalsIgnoreCase("true"))
                                dva.type = "Boolean";
                            else if (temp.length() > 0 && temp.matches("[0-9]+"))
                                dva.type = "Integer";
                            else if (temp.length() == 1) // necessary?
                                dva.type = "Char";
                            else
                                dva.type = "String";

                            foo.addAttribute(dva);
                        }

                        ClassDef baseClass = null;
                        for (int i = 0; i < ((SubclassDef) foo).numberOfSuperClasses(); i++) {
                            ClassDef superClass = parserAdapter.getClass(((SubclassDef) foo).getSuperClass(i));
                            if (baseClass == null)
                                baseClass = superClass.getBaseClass(parserAdapter);
                            else if (!baseClass.name.equals(superClass.getBaseClass(parserAdapter).name))
                                throw new Exception("Super classes of class \"" + foo.name + "\" does not share the same base class");
                        }

                        parserAdapter.putClass(foo);
                        parserAdapter.commit();

                        // Insert into our new SubClass

                        ClassDef targetClass = parserAdapter.getClass(iq.className);
                        WDBObject newObject = null;

                        SubclassDef targetSubClass = (SubclassDef) targetClass;
                        ClassDef fromClass = parserAdapter.getClass(iq.fromClassName);
                        if (targetSubClass.isSubclassOf(fromClass.name, parserAdapter)) {
                            WDBObject[] fromObjects = fromClass.search(iq.expression, parserAdapter);
                            if (fromObjects.length <= 0) {
                                throw new IllegalStateException("Can't find any entities from class \"" + fromClass.name + "\" to extend");
                            }
                            for (int i = 0; i < fromObjects.length; i++) {
                                newObject = targetSubClass.newInstance(fromObjects[i].getBaseObject(parserAdapter), parserAdapter);
                                setValues(iq.assignmentList, newObject, parserAdapter);
                            }
                        } else {
                            throw new IllegalStateException("Inserted class \"" + targetClass.name + "\" is not a subclass of the from class \"" + iq.fromClassName);
                        }

                        if (newObject != null) {
                            newObject.commit(parserAdapter);
                        }

                        parserAdapter.commit();
                        System.out.println("Schemaless insert succeeded!");
                    }
                    else { // SCHEMALESS CLASS
                        System.out.println("Class '" + iq.className + "' does not exist. Attempting schemaless insert...");
                        // Creating Class
                        ClassDef foo = new ClassDef(iq.className, "Schemaless Insert");
                        for (int x = 0; x < iq.assignmentList.size(); x++) {
                            DVA dva = new DVA();
                            DvaAssignment _dva = (DvaAssignment) iq.getAssignment(x);
                            dva.required = true;
                            dva.comment = "";
                            dva.name = _dva.AttributeName;

                            // Find value type
                            Object temp1 = _dva.Value;
                            String temp = temp1.toString();
                            if (temp.equalsIgnoreCase("false") || temp.equalsIgnoreCase("true"))
                                dva.type = "Boolean";
                            else if (temp.length() > 0 && temp.matches("[0-9]+"))
                                dva.type = "Integer";
                            else if (temp.length() == 1) // necessary?
                                dva.type = "Char";
                            else
                                dva.type = "String";

                            foo.addAttribute(dva);
                        }

                        parserAdapter.putClass(foo);
                        parserAdapter.commit();

                        // Inserting into our new Class
                        ClassDef targetClass = parserAdapter.getClass(iq.className);
                        WDBObject newObject = null;

                        newObject = targetClass.newInstance(null, parserAdapter);
                        setDefaultValues(targetClass, newObject, parserAdapter);
                        setValues(iq.assignmentList, newObject, parserAdapter);
                        checkRequiredValues(targetClass, newObject, parserAdapter);

                        if (newObject != null)
                            newObject.commit(parserAdapter);

                        parserAdapter.commit();
                        System.out.println("Schemaless insert succeeded!");
                    }
                } catch (Exception foo) {
                    System.out.println("Schemaless insert failed due to the following:\n" + foo);
                    parserAdapter.abort();
                }
            }
        }

        if (q instanceof IndexDef) {
            IndexDef indexQ = (IndexDef) q;
            try {
                ClassDef classDef = parserAdapter.getClass(q);
                classDef.addIndex(indexQ, parserAdapter);

                parserAdapter.commit();
            } catch (Exception e) {
                System.out.println(e.toString());
                parserAdapter.abort();
            }
        }

        if (q instanceof RetrieveQuery) {
            //Ok, it's a retrieve...
            if (connDatabase instanceof NonDefaultParser) {
                return ((NonDefaultParser) connDatabase).retrieve(q);
            }
            RetrieveQuery rq = (RetrieveQuery) q;
            try {
                ClassDef targetClass = parserAdapter.getClass(q);
                WDBObject[] targetClassObjs = targetClass.search(rq.expression, parserAdapter);
                int i, j;
                String[][] table;
                String[][] newtable;
                final ArrayList<PyObject> rows = new ArrayList<>();

                PrintNode node = new PrintNode(0, 0);
                for (j = 0; j < rq.numAttributePaths(); j++)
                    targetClass.printAttributeName(node, rq.getAttributePath(j), parserAdapter);

                table = node.printRow();
                for (i = 0; i < targetClassObjs.length; i++) {
                    node = new PrintNode(0, 0);
                    for (j = 0; j < rq.numAttributePaths(); j++)
                        targetClassObjs[i].PrintAttribute(node, rq.getAttributePath(j), parserAdapter);
                    newtable = joinRows(table, node.printRow());
                    table = newtable;
                }

                parserAdapter.commit();

                Integer[] columnWidths = new Integer[table[0].length];

                for (i = 0; i < columnWidths.length; i++) {
                    columnWidths[i] = 0;
                    for (j = 0; j < table.length; j++) {
                        if (i < table[j].length && table[j][i] != null && table[j][i].length() > columnWidths[i])
                            columnWidths[i] = table[j][i].length();
                    }
                }

                for (i = 0; i < table.length; i++) {
                    ArrayList<PyObject> columns = new ArrayList<>();
                    for (j = 0; j < table[0].length; j++) {
                        String colElement = table[i][j];
                        try {
                            Double.parseDouble(colElement);
                            try {
                                Integer.parseInt(colElement);
                                columns.add(new PyInteger(Integer.parseInt(colElement)));
                            } catch (NumberFormatException e) {
                                columns.add(new PyFloat(Float.parseFloat(colElement)));
                            }
                        } catch (NumberFormatException e) {
                            columns.add(new PyString(colElement));
                        }

                    }
                    rows.add(new PyTuple(columns.toArray(new PyObject[columns.size()])));
                }
                return rows;
                // Return here? Table is a 2D array
            } catch (Exception e) {
                System.out.println(e.toString());
                parserAdapter.abort();
            }
        }

        /******************* END WDB CODE DUMP *****************/
        return null;
    }


    public synchronized PyObject[] processSQLRdfMode(Statement statement, PyRelConnection conn, ArrayList<PyType> relQueryInstancesType, ArrayList<String> relQueryInstancesTypeNames)
    {
        if (statement instanceof CreateTable) {
            try {
                CreateTable caststmt = (CreateTable) statement;
                SQLVisitor visitor = new SQLVisitor(conn);
                visitor.doCreateTable(caststmt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (statement instanceof Insert) {
            try {
                Insert caststmt = (Insert) statement;
                SQLVisitor visitor = new SQLVisitor(conn);
                visitor.doInsert(caststmt, conn.getConnectionType());//visitor.doInsert takes 2 arguments now instead of 1 argument
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (statement instanceof Select) {
            try {

                Select caststmt = (Select) statement;
                SQLVisitor visitor = new SQLVisitor(conn);
                String sparql = "";
                if (relQueryInstancesTypeNames.size() > 0)
                    sparql = visitor.getSelect(caststmt, relQueryInstancesTypeNames, conn.getConnectionType());
                else
                    sparql = visitor.getSelect(caststmt, null, conn.getConnectionType());
                // an oo query forces the session to be committed first.
                conn.commit_oorel_session();
                ArrayList<PyObject> rowResults;
                if (conn.getConnectionDB().equals("OracleNoSQL")) {
                    rowResults = ((OracleRDFNoSQLDatabase) conn.getDatabase()).OracleNoSQLRunSPARQL(sparql);
                    //a lot of conversion going on here. . .
                    return rowResults.toArray(new PyObject[rowResults.size()]);
                }
                else {
                    ProcessOracleEESQL processOracleEESQL = new ProcessOracleEESQL(conn, relQueryInstancesType, relQueryInstancesTypeNames);
                    rowResults = processOracleEESQL.processSQL(sparql);
                    //a lot of conversion going on here. . .
                    return rowResults.toArray(new PyObject[rowResults.size()]);
                }
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        else if (statement instanceof Delete) {
            Delete caststmt = (Delete) statement;
            String sqlstmt = "";

            BinaryExpression expression = ((BinaryExpression) caststmt.getWhere());
            Expression left = expression.getLeftExpression();
            Expression right = expression.getRightExpression();
            String left_exp = left + "";
            String right_exp = right + "";
            if (right.toString().charAt(0) == '\'')
                right_exp = right_exp.substring(1, right_exp.length() - 1);

            String table = conn.getTable();
            String this_value = right_exp;
            String this_column = left_exp;
            String rvalue = "";
            String lvalue = "";
            try {
                Double.parseDouble(this_value);
                try {
                    Integer.parseInt(this_value);
                    rvalue = "a.triple.get_obj_value() = '\"" + this_value + "\"^^<http://www.w3.org/2001/XMLSchema#integer>'";
                } catch (NumberFormatException e) {
                    rvalue = "a.triple.get_obj_value() = '\"" + this_value + "\"^^<http://www.w3.org/2001/XMLSchema#float>'";
                }
            } catch (NumberFormatException e) {
                rvalue = "a.triple.get_obj_value() = '\"" + this_value + "\"^^<http://www.w3.org/2001/XMLSchema#string>'";
            }
            try {
                Double.parseDouble(this_column);
                try {
                    Integer.parseInt(this_column);
                    lvalue = "a.triple.get_obj_value() = '\"" + this_column + "\"^^<http://www.w3.org/2001/XMLSchema#integer>'";
                } catch (NumberFormatException e) {
                    lvalue = "a.triple.get_obj_value() = '\"" + this_column + "\"^^<http://www.w3.org/2001/XMLSchema#float>'";
                }
            } catch (NumberFormatException e) {
                lvalue = "a.triple.get_obj_value() = '\"" + this_column + "\"^^<http://www.w3.org/2001/XMLSchema#string>'";
            }

            String lcolumn = "a.triple.get_property() = '<" + conn.getNamespace() + this_column + ">'";
            String rcolumn = "a.triple.get_property() = '<" + conn.getNamespace() + this_value + ">'";
            String subject = "a.triple.get_subject() IN (SELECT distinct b.triple.GET_SUBJECT()  from " + conn.getTable()
                    + " b where b.triple.get_property() = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>' and b.triple.get_obj_value() = '<"
                    + conn.getNamespace() + caststmt.getTable() + ">')";

            /* Example:
            DELETE FROM RDF_CS347_PROF_DATA a
            WHERE ((a.triple.get_property() = '<http://www.example.org/people.owl#VAL1>' AND a.triple.get_obj_value() = '"one"^^<http://www.w3.org/2001/XMLSchema#string>')
            OR (a.triple.get_property() = '<http://www.example.org/people.owl#one>' AND a.triple.get_obj_value() = '"VAL1"^^<http://www.w3.org/2001/XMLSchema#string>'))
            AND a.triple.get_subject() IN
            (SELECT distinct b.triple.GET_SUBJECT()
               from RDF_CS347_PROF_DATA b
               where b.triple.get_property() = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>' and b.triple.get_obj_value() = '<http://www.example.org/people.owl#TEST_DELETE>')
            */

            sqlstmt = "DELETE FROM " + table + " a WHERE ((" + lcolumn + " AND " + rvalue + ") OR (" + rcolumn + " AND " + lvalue + ")) AND " + subject;
            debugMsg(DBG, "\n"+sqlstmt+"\n");
            try {
                conn.executeStatement(sqlstmt);
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        else if (statement instanceof Update) {
            Update caststmt = (Update) statement;
            String sqlstmt = "";

            String table = conn.getTable();
            String subject = "a.triple.get_subject() IN (SELECT distinct b.triple.GET_SUBJECT()  from " + conn.getTable()
                    + " b where b.triple.get_obj_value() = '<"
                    + conn.getNamespace() + ">')";
            System.out.println("In Update\ntable is : " + table);
            System.out.println("subject is : " + subject);
        }
        else if (statement instanceof Drop) {
            Drop caststmt = (net.sf.jsqlparser.statement.drop.Drop) statement;
            String sqlstmt = "";

            String table = conn.getTable();
            String subject = "a.triple.get_subject() IN (SELECT distinct b.triple.GET_SUBJECT()  from " + conn.getTable()
                    + " b where b.triple.get_obj_value() = '<"
                    + conn.getNamespace() + caststmt.getName() + ">')";

            sqlstmt = "DELETE FROM " + table + " a WHERE " + subject;
            debugMsg(DBG, "\n"+sqlstmt+"\n");
            try {
                conn.executeStatement(sqlstmt);
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        return null;
    }


    private static void setDefaultValues(ClassDef targetClass, WDBObject targetObject, ParserAdapter parserAdapter) throws Exception
    {
        for (int j = 0; j < targetClass.numberOfAttributes(); j++) {
            if (targetClass.getAttribute(j) instanceof DVA) {
                DVA dva = (DVA) targetClass.getAttribute(j);
                if (dva.initialValue != null)
                    targetObject.setDvaValue(dva.name, dva.initialValue, parserAdapter);
            }
        }
    }


    private static void checkRequiredValues(ClassDef targetClass, WDBObject targetObject, ParserAdapter parserAdapter) throws Exception
    {
        for (int j = 0; j < targetClass.numberOfAttributes(); j++) {
            Attribute attribute = (Attribute) targetClass.getAttribute(j);
            if (attribute.required != null && attribute.required && targetObject.getDvaValue(attribute.name, parserAdapter) == null)
                throw new Exception("Attribute \"" + targetClass.getAttribute(j).name + "\" is required");
        }
    }


    private static void setValues(ArrayList assignmentList, WDBObject targetObject, ParserAdapter parserAdapter) throws Exception
    {
        for (int j = 0; j < assignmentList.size(); j++) {
            if (assignmentList.get(j) instanceof DvaAssignment) {
                DvaAssignment dvaAssignment = (DvaAssignment) assignmentList.get(j);
                targetObject.setDvaValue(dvaAssignment.AttributeName, dvaAssignment.Value, parserAdapter);
            }
            else if (assignmentList.get(j) instanceof EvaAssignment) {
                EvaAssignment evaAssignment = (EvaAssignment) assignmentList.get(j);
                if (evaAssignment.mode == EvaAssignment.REPLACE_MODE) {
                    WDBObject[] currentObjects = targetObject.getEvaObjects(evaAssignment.AttributeName, parserAdapter);
                    targetObject.removeEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, currentObjects, parserAdapter);
                    targetObject.addEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, evaAssignment.expression, parserAdapter);
                }
                else if (evaAssignment.mode == EvaAssignment.EXCLUDE_MODE)
                    targetObject.removeEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, evaAssignment.expression, parserAdapter);
                else if (evaAssignment.mode == EvaAssignment.INCLUDE_MODE)
                    targetObject.addEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, evaAssignment.expression, parserAdapter);
                else {
                    throw new Exception("Unsupported multivalue EVA insert/modify mode");
                }
            }
        }
    }


    private static String[][] joinRows(String[][] row1, String[][] row2)
    {
        if (row1.length <= 0)
            return row2;
        else {
            String[][] newRow = new String[row1.length + row2.length][row1[0].length];
            int i, j;
            for (i = 0; i < row1.length; i++)
                newRow[i] = row1[i];
            for (j = i; j < row2.length + i; j++)
                newRow[j] = row2[j - i];
            return newRow;
        }
    }


    // ------------------- Neo4j -------------------------
    public String processNeo4j(String ReLstmt)
    {
        CypherSimTranslator t = new CypherSimTranslator();
        debugMsg(DBG, "SIM is: " + t.translate(ReLstmt));
        return t.translate(ReLstmt);
    }
}
