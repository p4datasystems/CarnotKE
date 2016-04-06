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
import org.python.core.*;
import wdb.metadata.*;
import wdb.parser.Node;
import wdb.parser.QueryParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

public class ProcessLanguages {
    private final boolean DBG;
    PyRelConnection conn;
    Adapter adapter;
    DatabaseInterface connDatabase;
    String schemaString;
    String NoSQLNameSpacePrefix = "";
    static Boolean parserInitialized = false;
    static QueryParser parser;
    String where = "";

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
        this.adapter = connDatabase.adapter;
        NoSQLNameSpacePrefix = connDatabase.getNameSpacePrefix();
        DBG = conn.getDebug().equalsIgnoreCase("debug");
    }

    public void debugMsg(boolean debugOn, String message)
    {
        if (debugOn)
            System.out.println(message);
    }

    // ------------------------------------- SIM -------------------------
    public synchronized String processSIM(String ReLstmt) throws SQLException
    {
        String Save_ReLstmt = ReLstmt;
        ReLstmt += ";";
        InputStream is = new ByteArrayInputStream(ReLstmt.getBytes());
        Query q = null;
        String sparql = null;
        if (!parserInitialized) {
            parser = new QueryParser(is);
            parserInitialized = true;
        }
        else
            parser.ReInit(is);

        try {
            q = parser.getNextQuery();
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
        // --------------------------------------------------------------------------------- SIM Insert
        // E.g., SQL: INSERT INTO onto_DATA VALUES ( 1, SDO_RDF_TRIPLE_S('onto', '#PERSON', 'rdf:type', 'rdfs:Class'));
        // E.g., SIM: INSERT dept ( DEPTNO := 10 , DNAME := "ACCOUNTING" , LOC := "NEW YORK" );
        if (q instanceof InsertQuery) {
            InsertQuery iq = (InsertQuery) q;
            final String instanceID = String.valueOf(UUID.randomUUID());
            for (int i = 0; i < iq.numberOfAssignments(); i++) {
                DvaAssignment dvaAssignment = (DvaAssignment) iq.getAssignment(i);
                SQLVisitor.insertQuad(conn, iq.className, instanceID, dvaAssignment.getAttributeName(),
                        dvaAssignment.Value.toString(), false);
            }
        }
        else if (q instanceof RetrieveQuery) {
            RetrieveQuery rq = (RetrieveQuery) q;
            String className = rq.className;
            List<String> dvaAttribs = new ArrayList<String>();
            List<String> evaAttribs = new ArrayList<String>();
            Map<String, String> whereAttrValues = new HashMap<String, String>();
            List<String> columns;
            for (int j = 0; j < rq.numAttributePaths(); j++) {
                if (rq.getAttributePath(j).attribute == "*") {
                    columns = SQLVisitor.getSubjects(conn, className + "_" + conn.getSchemaString(), "rdf:type", "owl:DatatypeProperty");
                    if (rq.getAttributePath(j).levelsOfIndirection() == 0) {
                        for (int i = 0; i < columns.size(); i++)
                            dvaAttribs.add(columns.get(i));
                    }
                }
                else if (rq.getAttributePath(j).levelsOfIndirection() == 0)
                    dvaAttribs.add(rq.getAttributePath(j).attribute);
                else {
                    String evaPath = "";
                    for (int k = rq.getAttributePath(j).levelsOfIndirection() - 1; k >= 0; k--) {
                        debugMsg(DBG, "rq.getAttributePath(j).getIndirection(k): " + rq.getAttributePath(j).getIndirection(k));
                        if (k > 0)
                            evaPath = " OF " + rq.getAttributePath(j).getIndirection(k) + evaPath;
                        else
                            evaPath = rq.getAttributePath(j).getIndirection(k) + evaPath;
                    }
                    evaPath = rq.getAttributePath(j).attribute + " OF " + evaPath;
                    evaAttribs.add(evaPath);
                }
            }
            if (DBG) {
                System.out.println("className: " + className);
                System.out.println("dvaAttribs: " + dvaAttribs);
                System.out.println("evaAttribs: " + evaAttribs);
            }
            if (rq.expression != null) {
                traverseWhereInorder(rq.expression);
                where = where.replaceAll("= ", "= :").replaceAll("And", "&&").replaceAll("Or", "||");
            }
            debugMsg(DBG, "where: "+where);
            // The following is temporary until filter is used for the where clause
            String whereTmp = "";
            if (where != "") {
                whereTmp = where.replaceAll(" = :", " ")
                        .replaceAll("&&", " ")
                        .replaceAll("\\|\\|", " ")
                        .replaceAll("  *", " ")
                        .replaceAll("^  *", "")
                        .replaceAll("  *$", ""); //temporary
                debugMsg(DBG, whereTmp);
                String[] whereTmpArray = whereTmp.split(" "); //temporary
                for (int i = 0; i <= whereTmpArray.length - 1; i += 2) //temporary
                    whereAttrValues.put(whereTmpArray[i], whereTmpArray[i + 1]);
            }
            debugMsg(DBG, "whereAttrValues: "+whereAttrValues);
            SIMHelper simhelper = new SIMHelper(conn);
            try {
                sparql = simhelper.executeFrom(className, dvaAttribs, evaAttribs, whereAttrValues);
            } catch (Exception e) {
                System.out.println(e);
            }
            debugMsg(DBG, "In ProcessesLangauges,sparql is: " + sparql);
        }
        else if (q instanceof ModifyQuery) {
            ModifyQuery mq = (ModifyQuery) q;
            String className = mq.className;
            String eva_name = "";
            String eva_class = "";
            int limit = 1000000;
            ArrayList<PyObject> rows = new ArrayList<>();
            List<String> subjects = new ArrayList<>();
            List<String> eva_subjects = new ArrayList<>();

            sparql = "select ?indiv where { ";
            for (int i = 0; i < mq.assignmentList.size(); i++) {
                EvaAssignment evaAssignment = (EvaAssignment) mq.assignmentList.get(i);
                eva_name = evaAssignment.getAttributeName();
                eva_class = evaAssignment.targetClass;
                traverseWhereInorder(evaAssignment.expression.jjtGetChild(i)); // This sets the where variable to e.g., deptno = 20
                String where1 = where.trim();
                sparql += "GRAPH " + NoSQLNameSpacePrefix + ":" + eva_class + " { ?indiv " + NoSQLNameSpacePrefix + ":"
                        + where1.replaceAll(" *= *", " \"") + "\"^^xsd:string }";
            }
            sparql += " }";
            debugMsg(DBG, "\nProcessLanguages SIM Modify, sparql is: \n" + sparql + "\n");
            rows = conn.getDatabase().OracleNoSQLRunSPARQL(sparql);
            for (int i = 1; i < rows.size(); i++) {
                eva_subjects.add(String.format("%s", rows.get(i))
                        .replaceAll("[()]", "")
                        .replaceAll("'", "")
                        .replaceAll(",", "")
                        .replaceAll(conn.getDatabase().getNameSpace(), ""));
            }

            // Process WHERE clause
            if (mq.expression != null) {
                where = "";
                traverseWhereInorder(mq.expression);
                where = where.replaceAll("  *", " ").replaceAll("^ ", "").replaceAll(" $", "");
            }
            sparql = "select ?indiv where { GRAPH " + NoSQLNameSpacePrefix + ":" + className + " { ?indiv "
                    + NoSQLNameSpacePrefix + ":" + where.replaceAll(" *= *", " \"") + "\"^^xsd:string } }";
            debugMsg(DBG, "\nProcessLanguages SIM Modify, sparql is: \n" + sparql + "\n");
            rows = conn.getDatabase().OracleNoSQLRunSPARQL(sparql);
            for (int i = 1; i < rows.size(); i++) {
                subjects.add(String.format("%s", rows.get(i))
                        .replaceAll("[()]", "")
                        .replaceAll("'", "")
                        .replaceAll(",", "")
                        .replaceAll(conn.getDatabase().getNameSpace(), ""));
            }
            for (String subject : subjects) {
                for (String entity : eva_subjects)
                    connDatabase.OracleNoSQLAddQuad(className, subject, eva_name, entity, true);
            }
            sparql = null;
        }
        return sparql;
    }


    public synchronized ArrayList<PyObject> processNativeSIM(String ReLstmt) throws Exception
    {
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
        }
        else {
            parser.ReInit(is);
        }
        try {
            q = parser.getNextQuery();
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
        debugMsg(DBG, "Statement executed: " + ReLstmt);

        /************* BEGIN WDB CODE DUMP ************************/
//		if(q.getClass() == ClassDef.class || q.getClass() == SubclassDef.class)
        if (q instanceof ClassDef) {
            ClassDef cd = (ClassDef) q;
            try {
                if (cd.name != null)
                    q.queryName = cd.name;
                adapter.getClass(q);
                //That class already exists;
                throw new Exception("Class \"" + cd.name + "\" already exists");
            } catch (ClassNotFoundException cnfe) {
                if (cd.getClass() == SubclassDef.class) {
                    ClassDef baseClass = null;
                    for (int i = 0; i < ((SubclassDef) cd).numberOfSuperClasses(); i++) {
                        //Cycles are implicitly checked since getClass will fail for the current defining class
                        ClassDef superClass = adapter.getClass(((SubclassDef) cd).getSuperClass(i));
                        if (baseClass == null)
                            baseClass = superClass.getBaseClass(adapter);
                        else if (!baseClass.name.equals(superClass.getBaseClass(adapter).name))
                            throw new Exception("Super classes of class \"" + cd.name + "\" do not share the same base class");
                    }
                }

                adapter.putClass(cd);
                adapter.commit();
            } catch (Exception e) {
                System.out.println("This class already exists: " + cd.name);
                adapter.abort();
                return null;
            }
        }

        if (q.getClass() == ModifyQuery.class) {
            ModifyQuery mq = (ModifyQuery) q;
            try {
                if (mq.className != null)
                    q.queryName = mq.className;
                ClassDef targetClass = adapter.getClass(q);
                WDBObject[] targetClassObjs = targetClass.search(mq.expression, adapter);
                if (mq.limit > -1 && targetClassObjs.length > mq.limit)
                    throw new Exception("Matching entities exceeds limit of " + mq.limit.toString());
                for (int i = 0; i < targetClassObjs.length; i++)
                    setValues(mq.assignmentList, targetClassObjs[i], adapter);
                adapter.commit();
            } catch (Exception e) {
                System.out.println(e.toString());
                adapter.abort();
            }
        }

        if (q instanceof InsertQuery) {
            InsertQuery iq = (InsertQuery) q;
            try {
                if (iq.className != null)
                    q.queryName = iq.className;
                ClassDef targetClass = adapter.getClass(q);
                WDBObject newObject = null;

                if (iq.fromClassName != null) {
                    //Inserting from an entity of a superclass...
                    if (targetClass.getClass() == SubclassDef.class) {
                        SubclassDef targetSubClass = (SubclassDef) targetClass;
                        ClassDef fromClass = adapter.getClass(iq.fromClassName);
                        if (targetSubClass.isSubclassOf(fromClass.name, adapter)) {
                            WDBObject[] fromObjects = fromClass.search(iq.expression, adapter);
                            if (fromObjects.length <= 0)
                                throw new IllegalStateException("Can't find any entities from class \"" + fromClass.name + "\" to extend");
                            for (int i = 0; i < fromObjects.length; i++) {
                                newObject = targetSubClass.newInstance(fromObjects[i].getBaseObject(adapter), adapter);
                                setValues(iq.assignmentList, newObject, adapter);
                            }
                        }
                        else
                            throw new IllegalStateException("Inserted class \"" + targetClass.name + "\" is not a subclass of the from class \"" + iq.fromClassName);
                    }
                    else
                        throw new IllegalStateException("Can't extend base class \"" + targetClass.name + "\" from class \"" + iq.fromClassName);
                }
                else {
                    //Just inserting a new entity
                    newObject = targetClass.newInstance(null, adapter);
                    setDefaultValues(targetClass, newObject, adapter);
                    setValues(iq.assignmentList, newObject, adapter);
                    checkRequiredValues(targetClass, newObject, adapter);
                }

                if (newObject != null)
                    newObject.commit(adapter);
                adapter.commit();
            } catch (Exception e) {
                System.out.println(e.toString());
                adapter.abort();
            }
        }

        if (q instanceof IndexDef) {
            IndexDef indexQ = (IndexDef) q;
            try {
                if (indexQ.className != null)
                    q.queryName = indexQ.className;
                ClassDef classDef = adapter.getClass(q);
                classDef.addIndex(indexQ, adapter);

                adapter.commit();
            } catch (Exception e) {
                System.out.println(e.toString());
                adapter.abort();
            }
        }

        if (q instanceof RetrieveQuery) {
            //Ok, its a retrieve...
            RetrieveQuery rq = (RetrieveQuery) q;
            try {
                if (rq.className != null)
                    q.queryName = rq.className;
                ClassDef targetClass = adapter.getClass(q);
                WDBObject[] targetClassObjs = targetClass.search(rq.expression, adapter);
                int i, j;
                String[][] table;
                String[][] newtable;
                final ArrayList<PyObject> rows = new ArrayList<>();

                PrintNode node = new PrintNode(0, 0);
                for (j = 0; j < rq.numAttributePaths(); j++)
                    targetClass.printAttributeName(node, rq.getAttributePath(j), adapter);

                table = node.printRow();
                for (i = 0; i < targetClassObjs.length; i++) {
                    node = new PrintNode(0, 0);
                    for (j = 0; j < rq.numAttributePaths(); j++)
                        targetClassObjs[i].PrintAttribute(node, rq.getAttributePath(j), adapter);
                    newtable = joinRows(table, node.printRow());
                    table = newtable;
                }

                adapter.commit();

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

//                        if (j >= table[i].length || table[i][j] == null)
//                            System.out.format("| %" + columnWidths[j].toString() + "s ", "");
//                        else
//                            System.out.format("| %" + columnWidths[j].toString() + "s ", table[i][j]);
                    }
                    rows.add(new PyTuple(columns.toArray(new PyObject[columns.size()])));
//                    System.out.format("|%n");
                }
                return rows;
            // Return here? Table is a 2D array
            } catch (Exception e) {
                System.out.println(e.toString());
                adapter.abort();
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
                    rowResults = conn.getDatabase().OracleNoSQLRunSPARQL(sparql);
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


    private static void setDefaultValues(ClassDef targetClass, WDBObject targetObject, Adapter adapter) throws Exception
    {
        for (int j = 0; j < targetClass.numberOfAttributes(); j++) {
            if (targetClass.getAttribute(j) instanceof DVA) {
                DVA dva = (DVA) targetClass.getAttribute(j);
                if (dva.initialValue != null)
                    targetObject.setDvaValue(dva.name, dva.initialValue, adapter);
            }
        }
    }


    private static void checkRequiredValues(ClassDef targetClass, WDBObject targetObject, Adapter adapter) throws Exception
    {
        for (int j = 0; j < targetClass.numberOfAttributes(); j++) {
            Attribute attribute = (Attribute) targetClass.getAttribute(j);
            if (attribute.required != null && attribute.required && targetObject.getDvaValue(attribute.name, adapter) == null)
                throw new Exception("Attribute \"" + targetClass.getAttribute(j).name + "\" is required");
        }
    }


    private static void setValues(ArrayList assignmentList, WDBObject targetObject, Adapter adapter) throws Exception
    {
        for (int j = 0; j < assignmentList.size(); j++) {
            if (assignmentList.get(j) instanceof DvaAssignment) {
                DvaAssignment dvaAssignment = (DvaAssignment) assignmentList.get(j);
                targetObject.setDvaValue(dvaAssignment.AttributeName, dvaAssignment.Value, adapter);
            }

            else if (assignmentList.get(j) instanceof EvaAssignment) {
                EvaAssignment evaAssignment = (EvaAssignment) assignmentList.get(j);
                if (evaAssignment.mode == EvaAssignment.REPLACE_MODE) {
                    WDBObject[] currentObjects = targetObject.getEvaObjects(evaAssignment.AttributeName, adapter);
                    targetObject.removeEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, currentObjects, adapter);
                    targetObject.addEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, evaAssignment.expression, adapter);
                }
                else if (evaAssignment.mode == EvaAssignment.EXCLUDE_MODE)
                    targetObject.removeEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, evaAssignment.expression, adapter);
                else if (evaAssignment.mode == EvaAssignment.INCLUDE_MODE)
                    targetObject.addEvaObjects(evaAssignment.AttributeName, evaAssignment.targetClass, evaAssignment.expression, adapter);
                else
                    throw new Exception("Unsupported multivalue EVA insert/modify mode");
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


    public void traverseWhereInorder(Node node)
    {
        if (node != null) {
            if (node.jjtGetNumChildren() > 0)
                traverseWhereInorder(node.jjtGetChild(0));
            if (node.toString() != "Root")
                where += " " + node.toString();
            if (node.jjtGetNumChildren() > 1)
                traverseWhereInorder(node.jjtGetChild(1));
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