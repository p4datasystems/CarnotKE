// Copyright (c) Corporation for National Research Initiatives
package org.python.core;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.python.ReL.OracleRDFNoSQLDatabase;
import org.python.ReL.ProcessLanguages;
import org.python.ReL.ProcessOracleEESQL;
import org.python.ReL.PyRelConnection;
import org.python.expose.ExposedMethod;
import org.python.expose.ExposedNew;
import org.python.expose.ExposedType;
import org.python.expose.MethodType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * A builtin python tuple.
 */
@ExposedType(name = "tuple", base = PyObject.class, doc = BuiltinDocs.tuple_doc)
public class PyTuple extends PySequenceList implements List {

    public static final PyType TYPE = PyType.fromClass(PyTuple.class);
    // If we use a class type, mark down that we may have a select that is expected to return an instance of the type
    private ArrayList<PyType> relQueryInstancesType = new ArrayList<>();
    private ArrayList<String> relQueryInstancesTypeNames = new ArrayList<>();

    // private final PyObject[] array;
    private PyObject[] array;           // Changed for ReL

    private volatile List<PyObject> cachedList = null;

    private static final PyTuple EMPTY_TUPLE = new PyTuple();

    public PyTuple()
    {
        this(TYPE, Py.EmptyObjects);
    }

    public PyTuple(PyObject... elements)
    {
        this(TYPE, elements);
    }

    public PyTuple(PyType subtype, PyObject[] elements)
    {
        super(subtype);
        if (elements == null) {
            array = new PyObject[0];
        }
        else {
            array = new PyObject[elements.length];
            System.arraycopy(elements, 0, array, 0, elements.length);
        }
    }

    public PyTuple(PyObject[] elements, boolean copy)
    {
        this(TYPE, elements, copy);
    }

    public PyTuple(PyType subtype, PyObject[] elements, boolean copy)
    {
        super(subtype);

        if (copy) {
            array = new PyObject[elements.length];
            System.arraycopy(elements, 0, array, 0, elements.length);
        }
        else {
            array = elements;
        }
    }

    // This constructor was added for ReL
    public PyTuple(PyObject[] elements, String ReLstring, String ReLmode, PyObject connection)
    {
        this(TYPE, elements, ReLstring, ReLmode, connection);
    }


   public void debugMsg(boolean debugOn, String message)
   {
       if (debugOn)
           System.out.println(message);
   }


    public String reconstructQuery(String statement,
                                   PyObject[] elements,
                                   String[] commands,
                                   int size)
    {
        for (int i = 0; i < size; i++) {
            if (i + 1 < commands.length) {
                if (commands[i + 1].charAt(0) == ' ')
                    statement += commands[i + 1].substring(1); // substring gets rid of an extraneous space at the beginning of the string
                else
                    statement += commands[i + 1];
            }
            if (i < elements.length) {
                if (elements[i] instanceof PyType) {
                    // Maintain information about the PyTypes since this means we will need to return
                    // instances of these types.
                    relQueryInstancesType.add((PyType) elements[i]);
                    relQueryInstancesTypeNames.add(((PyType) elements[i]).getName());
                    //statement += " " + ((PyType)elements[i]).getName() + " ";
                    statement += ((PyType) elements[i]).getName();
                }
                else if (elements[i].getType().pyGetName().toString() == "str") {
                    if (elements[i].toString().trim().charAt(0) == '(' ||
                            elements[i].toString().trim().charAt(0) == '[' ||
                            elements[i].toString().trim().charAt(0) == '{' ||
                            //elements[i].toString().trim().charAt(0) == '"') statement += " " + elements[i].toString() + " ";
                            elements[i].toString().trim().charAt(0) == '"') statement += elements[i].toString();
                        // else statement += " \'" + elements[i].toString() + "' ";
                    else
                        statement += elements[i].toString();
                }
                else
                    statement += elements[i].toString();
            }
        }
        return statement.trim();
    }


    // This constructor was added for ReL
    public PyTuple(PyType subtype, PyObject[] elements, String ReLstring, String ReLmode, PyObject connection)
    {
        super(subtype);
        PyRelConnection conn = (PyRelConnection) connection;
        final boolean DBG = false;
        ArrayList<PyObject> rows = new ArrayList<PyObject>();

        String[] strings = ReLstring.split(";");
        int size = Math.max(strings.length - 1, elements.length);
        String ReLstmt = strings[0];
        ReLstmt = reconstructQuery(ReLstmt, elements, strings, size);
        debugMsg(DBG, "ReLstmt is: "+ReLstmt);

        if (ReLmode == "RDF") {
            System.out.println("Saw an RDF: " + ReLstmt);
            String[] s = ReLstmt.split(" ");
            // TODO(jhurt): Handle this more elegantly as the database may not always be OracleRDFNoSQL.
            // DO NOT add a method to DatabaseInterface class. Do it better than that.
            ((OracleRDFNoSQLDatabase)conn.getDatabase()).OracleNoSQLAddQuad(s[0], s[1], s[2], s[3], false);
        }

        if (ReLmode == "JAPI") {
            String jsonRequest = "{" +
                    "    \"auth\": {" +
                    "        \"type\" : \"basic\"," +
                    "    }," +
                    "    \"method\": {" +
                    "      \"name\": \"getMitsLeases\"," +
                    "      \"params\": {" +
                    "        \"propertyId\": \"141159\"," +
                    "        \"customerId\": \"14349034\"," +
                    "        \"includeLeaseHistory\": \"0\"," +
                    "        \"leaseStatusTypeIds\": \"1,2,3,4,5,6\"," +
                    "        \"sendUnitSpaces\": \"0\"," +
                    "        \"includeDemographics\": \"0\"," +
                    "        \"includeOtherIncomeLeases\": \"0\"" +
                    "      }" +
                    "    }" +
                    "}";

            CloseableHttpClient httpClient = null;
            HttpPost httpPost = null;
            CloseableHttpResponse response = null;

            String json = "";
            try {

                httpClient = HttpClients.createDefault();
                httpPost = new HttpPost("https://aspenheights.entrata.com/api/leases");

                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("content-type", "application/json"));

                StringEntity input = new StringEntity(jsonRequest);
                input.setContentType("application/json");
                httpPost.setEntity(input);

                for (NameValuePair h : nvps)
                    httpPost.addHeader(h.getName(), h.getValue());

                response = httpClient.execute(httpPost);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatusLine().getStatusCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (response.getEntity().getContent())));

                //System.out.println("Output from Server .... \n");
                String out = "";
                while ((out = br.readLine()) != null)
                    json += out;

                //System.out.println(json);
                rows.add(new PyTuple(new PyString(json)));
                //a lot of conversion going on here. . .
                PyObject[] results = rows.toArray(new PyObject[rows.size()]);
                //put results in array for this tuple object
                array = new PyObject[results.length];
                System.arraycopy(results, 0, array, 0, results.length);
            } catch (IOException e) {

                e.printStackTrace();

            } finally {
                try {
                    response.close();
                    httpClient.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (ReLmode == "SPARQL") {
            // TODO(jhurt): Handle this more elegantly. Perhaps create a SPARQL interface
            // which contains the 'runSPARQL' method and have the database implementations implement that.
            // Then you could cast the below call as '((SPARQL) conn.getDatabase()).runSPARQL(ReLstmt)'
            rows = ((OracleRDFNoSQLDatabase) conn.getDatabase()).OracleNoSQLRunSPARQL(ReLstmt);
            PyObject[] results = rows.toArray(new PyObject[rows.size()]);
            //put results in array for this tuple object
            array = new PyObject[results.length];
            System.arraycopy(results, 0, array, 0, results.length);
        }

        if (ReLmode == "Neo4j") {
            ProcessLanguages processLanguage = new ProcessLanguages(conn);
            // Translate Cyper to SIM
            String sim = processLanguage.processNeo4j(ReLstmt);
            ReLmode = "SIM";
            ReLstmt = sim;
        }

        if (ReLmode == "SQL") {
            if (conn.getConnectionType() == "native_mode") {
                if (ReLstmt.charAt(0) == '\'')
                    ReLstmt = ReLstmt.substring(1, ReLstmt.length() - 1); //This happens for a py program like:
                debugMsg(DBG, "Remote: "+ReLstmt);
                // runAndOutputTuples(conn, ReLstmt);
                ProcessOracleEESQL processOracleEESQL = new ProcessOracleEESQL(conn, relQueryInstancesType, relQueryInstancesTypeNames);
                try {
                    ArrayList<PyObject> rowResults = processOracleEESQL.processSQL(ReLstmt);
                    PyObject[] results = rows.toArray(new PyObject[rows.size()]);
                    //put results in array for this tuple object
                    array = new PyObject[results.length];
                    System.arraycopy(results, 0, array, 0, results.length);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            else if (conn.getConnectionType() == "rdf_mode") {
                CCJSqlParserManager pm = new CCJSqlParserManager();
                Statement statement = null;
                try {
                    statement = pm.parse(new StringReader(ReLstmt));
                } catch (Exception e) {
                    System.out.println(e);
                }
                debugMsg(DBG, "jsqlstmt is: "+statement.toString());
                ProcessLanguages processLanguage = new ProcessLanguages(conn);
                PyObject[] results = processLanguage.processSQLRdfMode(statement, conn, relQueryInstancesType, relQueryInstancesTypeNames);
                if (results != null) {
                    array = new PyObject[results.length];
                    System.arraycopy(results, 0, array, 0, results.length);
                }
            }
            else {
                System.out.println("Connection type must be \"native_mode\", or \"rdf_mode\", not \"" + conn.getConnectionType() + "\"");
            }
        }
        else if (ReLmode.equals("SIM")) {
            debugMsg(DBG, "PyTuple sim is: " + ReLstmt);
            ProcessLanguages processLanguage;
            try {
                processLanguage = new ProcessLanguages(conn, relQueryInstancesType, relQueryInstancesTypeNames);
                ArrayList<PyObject> rowResults = processLanguage.processSIM(ReLstmt);
                if (rowResults != null) {
                    PyObject[] results = rowResults.toArray(new PyObject[rowResults.size()]);
                    array = new PyObject[results.length];
                    System.arraycopy(results, 0, array, 0, results.length);
                }
            } catch (Exception e) {
                // Should shut down the connection
                System.out.println(e);
            }
        }
    }


    /* A helper for creating user object instances.
       This is useful for returning instance during joins. This guy expects the columns associated with
       each type to have the types name_ appended to the front of the column name. 
    */

    private PyObjectDerived createInstanceFromResults(PyRelConnection connection, PyType instance_type, ArrayList<PyObject> columns, ArrayList<PyObject> data)
    {
        String typeName = instance_type.getName() + "_";
        PyObjectDerived instance = new PyObjectDerived(instance_type);
        ConcurrentMap<Object, PyObject> inst_dict;
        inst_dict = ((PyStringMap) instance.fastGetDict()).getMap();
        // Add data as we find columns that should represent this type. 
        for (int i = 0; i < columns.size(); i++) {
            String column = ((PyString) columns.get(i)).toString();
            if (column.startsWith(typeName)) {
                // If we found the dbuniqueid, check to see this id is already in our runtime's session, if so just return that item.
                if (column.equals(typeName + "DBUNIQUEID")) {
                    PyInteger unique_id = (PyInteger) data.get(i);
                    PyObjectDerived possible_instance_in_session = (PyObjectDerived) connection.getInstance(unique_id.getValue());
                    if (possible_instance_in_session != null) {
                        return possible_instance_in_session;
                    }
                }
                inst_dict.put(columns.get(i).toString().replaceFirst(typeName, ""), data.get(i));

            }
        }
        return instance;
    }

// End ReL addition

    private static PyTuple fromArrayNoCopy(PyObject[] elements)
    {
        return new PyTuple(elements, false);
    }

    List<PyObject> getList()
    {
        if (cachedList == null) {
            cachedList = Arrays.asList(array);
        }
        return cachedList;
    }

    @ExposedNew
    final static PyObject tuple_new(PyNewWrapper new_, boolean init, PyType subtype,
                                    PyObject[] args, String[] keywords)
    {
        ArgParser ap = new ArgParser("tuple", args, keywords, new String[]{"sequence"}, 0);
        PyObject S = ap.getPyObject(0, null);
        if (new_.for_type == subtype) {
            if (S == null) {
                return Py.EmptyTuple;
            }
            if (S.getType() == PyTuple.TYPE) {
                return S;
            }
            if (S instanceof PyTupleDerived) {
                return new PyTuple(((PyTuple) S).getArray());
            }
            return fromArrayNoCopy(Py.make_array(S));
        }
        else {
            if (S == null) {
                return new PyTupleDerived(subtype, Py.EmptyObjects);
            }
            return new PyTupleDerived(subtype, Py.make_array(S));
        }
    }

    /**
     * Return a new PyTuple from an iterable.
     * <p>
     * Raises a TypeError if the object is not iterable.
     *
     * @param iterable an iterable PyObject
     * @return a PyTuple containing each item in the iterable
     */
    public static PyTuple fromIterable(PyObject iterable)
    {
        return fromArrayNoCopy(Py.make_array(iterable));
    }

    protected PyObject getslice(int start, int stop, int step)
    {
        if (step > 0 && stop < start) {
            stop = start;
        }
        int n = sliceLength(start, stop, step);
        PyObject[] newArray = new PyObject[n];

        if (step == 1) {
            System.arraycopy(array, start, newArray, 0, stop - start);
            return fromArrayNoCopy(newArray);
        }
        for (int i = start, j = 0; j < n; i += step, j++) {
            newArray[j] = array[i];
        }
        return fromArrayNoCopy(newArray);
    }

    protected PyObject repeat(int count)
    {
        if (count < 0) {
            count = 0;
        }
        int size = size();
        if (size == 0 || count == 1) {
            if (getType() == TYPE) {
                // Since tuples are immutable, we can return a shared copy in this case
                return this;
            }
            if (size == 0) {
                return Py.EmptyTuple;
            }
        }

        int newSize = size * count;
        if (newSize / size != count) {
            throw Py.MemoryError("");
        }

        PyObject[] newArray = new PyObject[newSize];
        for (int i = 0; i < count; i++) {
            System.arraycopy(array, 0, newArray, i * size, size);
        }
        return fromArrayNoCopy(newArray);
    }

    @Override
    public int __len__()
    {
        return tuple___len__();
    }

    @ExposedMethod(doc = BuiltinDocs.tuple___len___doc)
    final int tuple___len__()
    {
        return size();
    }

    @ExposedMethod(doc = BuiltinDocs.tuple___contains___doc)
    final boolean tuple___contains__(PyObject o)
    {
        return super.__contains__(o);
    }

    @ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___ne___doc)
    final PyObject tuple___ne__(PyObject o)
    {
        return super.__ne__(o);
    }

    @ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___eq___doc)
    final PyObject tuple___eq__(PyObject o)
    {
        return super.__eq__(o);
    }

    @ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___gt___doc)
    final PyObject tuple___gt__(PyObject o)
    {
        return super.__gt__(o);
    }

    @ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___ge___doc)
    final PyObject tuple___ge__(PyObject o)
    {
        return super.__ge__(o);
    }

    @ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___lt___doc)
    final PyObject tuple___lt__(PyObject o)
    {
        return super.__lt__(o);
    }

    @ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___le___doc)
    final PyObject tuple___le__(PyObject o)
    {
        return super.__le__(o);
    }

    @Override
    public PyObject __add__(PyObject generic_other)
    {
        return tuple___add__(generic_other);
    }

    @ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___add___doc)
    final PyObject tuple___add__(PyObject generic_other)
    {
        PyTuple sum = null;
        if (generic_other instanceof PyTuple) {
            PyTuple other = (PyTuple) generic_other;
            PyObject[] newArray = new PyObject[array.length + other.array.length];
            System.arraycopy(array, 0, newArray, 0, array.length);
            System.arraycopy(other.array, 0, newArray, array.length, other.array.length);
            sum = fromArrayNoCopy(newArray);
        }
        return sum;
    }

    @Override
    public PyObject __mul__(PyObject o)
    {
        return tuple___mul__(o);
    }

    @ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___mul___doc)
    final PyObject tuple___mul__(PyObject o)
    {
        if (!o.isIndex()) {
            return null;
        }
        return repeat(o.asIndex(Py.OverflowError));
    }

    @Override
    public PyObject __rmul__(PyObject o)
    {
        return tuple___rmul__(o);
    }

    @ExposedMethod(type = MethodType.BINARY, doc = BuiltinDocs.tuple___rmul___doc)
    final PyObject tuple___rmul__(PyObject o)
    {
        if (!o.isIndex()) {
            return null;
        }
        return repeat(o.asIndex(Py.OverflowError));
    }

    @Override
    public PyObject __iter__()
    {
        return tuple___iter__();
    }

    @ExposedMethod(doc = BuiltinDocs.tuple___iter___doc)
    public PyObject tuple___iter__()
    {
        return new PyFastSequenceIter(this);
    }

    @ExposedMethod(defaults = "null", doc = BuiltinDocs.tuple___getslice___doc)
    final PyObject tuple___getslice__(PyObject s_start, PyObject s_stop, PyObject s_step)
    {
        return seq___getslice__(s_start, s_stop, s_step);
    }

    @ExposedMethod(doc = BuiltinDocs.tuple___getitem___doc)
    final PyObject tuple___getitem__(PyObject index)
    {
        PyObject ret = seq___finditem__(index);
        if (ret == null) {
            throw Py.IndexError("index out of range: " + index);
        }
        return ret;
    }

    @ExposedMethod(doc = BuiltinDocs.tuple___getnewargs___doc)
    final PyTuple tuple___getnewargs__()
    {
        return new PyTuple(new PyTuple(getArray()));
    }

    @Override
    public PyTuple __getnewargs__()
    {
        return tuple___getnewargs__();
    }

    @Override
    public int hashCode()
    {
        return tuple___hash__();
    }

    @ExposedMethod(doc = BuiltinDocs.tuple___hash___doc)
    final int tuple___hash__()
    {
        // strengthened hash to avoid common collisions. from CPython
        // tupleobject.tuplehash. See http://bugs.python.org/issue942952
        int y;
        int len = size();
        int mult = 1000003;
        int x = 0x345678;
        while (--len >= 0) {
            y = array[len].hashCode();
            x = (x ^ y) * mult;
            mult += 82520 + len + len;
        }
        return x + 97531;
    }

    private String subobjRepr(PyObject o)
    {
        if (o == null) {
            return "null";
        }
        return o.__repr__().toString();
    }

    @Override
    public String toString()
    {
        return tuple___repr__();
    }

    @ExposedMethod(doc = BuiltinDocs.tuple___repr___doc)
    final String tuple___repr__()
    {
        StringBuilder buf = new StringBuilder("(");
        if (array != null) {
            for (int i = 0; i < array.length - 1; i++) {
                buf.append(subobjRepr(array[i]));
                buf.append(", ");
            }
            if (array.length > 0) {
                buf.append(subobjRepr(array[array.length - 1]));
            }
            if (array.length == 1) {
                buf.append(",");
            }
        }
        buf.append(")");
        return buf.toString();
    }

    public List subList(int fromIndex, int toIndex)
    {
        if (fromIndex < 0 || toIndex > size()) {
            throw new IndexOutOfBoundsException();
        }
        else if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }
        PyObject elements[] = new PyObject[toIndex - fromIndex];
        for (int i = 0, j = fromIndex; i < elements.length; i++, j++) {
            elements[i] = array[j];
        }
        return new PyTuple(elements);
    }

    public Iterator iterator()
    {
        return new Iterator() {

            private final Iterator<PyObject> iter = getList().iterator();

            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            public boolean hasNext()
            {
                return iter.hasNext();
            }

            public Object next()
            {
                return iter.next().__tojava__(Object.class);
            }
        };
    }

    public boolean add(Object o)
    {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection coll)
    {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection coll)
    {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection coll)
    {
        throw new UnsupportedOperationException();
    }

    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    public Object set(int index, Object element)
    {
        throw new UnsupportedOperationException();
    }

    public void add(int index, Object element)
    {
        throw new UnsupportedOperationException();
    }

    public Object remove(int index)
    {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection c)
    {
        throw new UnsupportedOperationException();
    }

    public ListIterator listIterator()
    {
        return listIterator(0);
    }

    public ListIterator listIterator(final int index)
    {
        return new ListIterator() {

            private final ListIterator<PyObject> iter = getList().listIterator(index);

            public boolean hasNext()
            {
                return iter.hasNext();
            }

            public Object next()
            {
                return iter.next().__tojava__(Object.class);
            }

            public boolean hasPrevious()
            {
                return iter.hasPrevious();
            }

            public Object previous()
            {
                return iter.previous().__tojava__(Object.class);
            }

            public int nextIndex()
            {
                return iter.nextIndex();
            }

            public int previousIndex()
            {
                return iter.previousIndex();
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            public void set(Object o)
            {
                throw new UnsupportedOperationException();
            }

            public void add(Object o)
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected String unsupportedopMessage(String op, PyObject o2)
    {
        if (op.equals("+")) {
            return "can only concatenate tuple (not \"{2}\") to tuple";
        }
        return super.unsupportedopMessage(op, o2);
    }

    public void pyset(int index, PyObject value)
    {
        throw Py.TypeError("'tuple' object does not support item assignment");
    }

    @Override
    public boolean contains(Object o)
    {
        return getList().contains(Py.java2py(o));
    }

    @Override
    public boolean containsAll(Collection c)
    {
        if (c instanceof PyList) {
            return getList().containsAll(((PyList) c).getList());
        }
        else if (c instanceof PyTuple) {
            return getList().containsAll(((PyTuple) c).getList());
        }
        else {
            return getList().containsAll(new PyList(c));
        }
    }

    public int count(PyObject value)
    {
        return tuple_count(value);
    }

    @ExposedMethod(doc = BuiltinDocs.tuple_count_doc)
    final int tuple_count(PyObject value)
    {
        int count = 0;
        for (PyObject item : array) {
            if (item.equals(value)) {
                count++;
            }
        }
        return count;
    }

    public int index(PyObject value)
    {
        return index(value, 0);
    }

    public int index(PyObject value, int start)
    {
        return index(value, start, size());
    }

    public int index(PyObject value, int start, int stop)
    {
        return tuple_index(value, start, stop);
    }

    @ExposedMethod(defaults = {"null", "null"}, doc = BuiltinDocs.tuple_index_doc)
    final int tuple_index(PyObject value, PyObject start, PyObject stop)
    {
        int startInt = start == null ? 0 : PySlice.calculateSliceIndex(start);
        int stopInt = stop == null ? size() : PySlice.calculateSliceIndex(stop);
        return tuple_index(value, startInt, stopInt);
    }

    final int tuple_index(PyObject value, int start, int stop)
    {
        int validStart = boundToSequence(start);
        int validStop = boundToSequence(stop);
        for (int i = validStart; i < validStop; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        throw Py.ValueError("tuple.index(x): x not in list");
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) {
            return true;
        }

        if (other instanceof PyObject) {
            return _eq((PyObject) other).__nonzero__();
        }
        if (other instanceof List) {
            return other.equals(this);
        }
        return false;
    }

    @Override
    public Object get(int index)
    {
        return array[index].__tojava__(Object.class);
    }

    @Override
    public PyObject[] getArray()
    {
        return array;
    }

    @Override
    public int indexOf(Object o)
    {
        return getList().indexOf(Py.java2py(o));
    }

    @Override
    public boolean isEmpty()
    {
        return array.length == 0;
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return getList().lastIndexOf(Py.java2py(o));
    }

    @Override
    public void pyadd(int index, PyObject element)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean pyadd(PyObject o)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public PyObject pyget(int index)
    {
        return array[index];
    }

    @Override
    public void remove(int start, int stop)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size()
    {
        return array.length;
    }

    @Override
    public Object[] toArray()
    {
        Object[] converted = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            converted[i] = array[i].__tojava__(Object.class);
        }
        return converted;
    }

    @Override
    public Object[] toArray(Object[] converted)
    {
        Class<?> type = converted.getClass().getComponentType();
        if (converted.length < array.length) {
            converted = (Object[]) Array.newInstance(type, array.length);
        }
        for (int i = 0; i < array.length; i++) {
            converted[i] = type.cast(array[i].__tojava__(type));
        }
        if (array.length < converted.length) {
            for (int i = array.length; i < converted.length; i++) {
                converted[i] = null;
            }
        }
        return converted;
    }


    /* Traverseproc implementation */
    @Override
    public int traverse(Visitproc visit, Object arg)
    {
        int retVal;
        for (PyObject ob : array) {
            if (ob != null) {
                retVal = visit.visit(ob, arg);
                if (retVal != 0) {
                    return retVal;
                }
            }
        }
        if (cachedList != null) {
            for (PyObject ob : cachedList) {
                if (ob != null) {
                    retVal = visit.visit(ob, arg);
                    if (retVal != 0) {
                        return retVal;
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public boolean refersDirectlyTo(PyObject ob)
    {
        if (ob == null) {
            return false;
        }
        for (PyObject obj : array) {
            if (obj == ob) {
                return true;
            }
        }
        if (cachedList != null) {
            for (PyObject obj : cachedList) {
                if (obj == ob) {
                    return true;
                }
            }
        }
        return false;
    }
}
