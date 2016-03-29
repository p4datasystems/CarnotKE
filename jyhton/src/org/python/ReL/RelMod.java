
/**
 * RelMod class.
 * <p>
 * Implement an accesable rel module from jython.
 */
package org.python.ReL;

import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyObjectDerived;

import java.util.concurrent.ConcurrentHashMap;


/**
 * RelMod class.
 *
 * Implement an accessable rel module from jython.
 */
public class RelMod {

    private static ConcurrentHashMap<Integer, PyObject> instances_map = new ConcurrentHashMap<Integer, PyObject>();
    // XXX get rid of this static initialization somehow. 
    private static PyRelConnection connection;

    public static void SetConnection(PyRelConnection conn)
    {
        connection = conn;
    }


    /**
     * Insert an instance of an object into the database. 
     *
     * Information about this instance is maintained, that way if a query returns 
     * the same instance as this, that this instance will be returned instead of 
     * a new instance. 
     *
     * Also, a new attribute is added to the attribute, dbuniqueid.
     *
     * At this point the instance is pending, it has not yet been added to the database. 
     * @param instance

    public static void insert(PyObject instance) {
    //First add the next dbuniqueid attribute to the instance.
    //Grab a pointer to the dictionary, within the instance's type's dictionary to set a values
    ConcurrentMap<Object, PyObject> inst_dict;
    inst_dict = ((PyStringMap)instance.fastGetDict()).getTable();
    int next_unique_id = ClassDbBuilder.getSingleton().getNextUniqueId();
    inst_dict.put("dbuniqueid", new PyInteger(next_unique_id));
    //Now add the instance to our interal mappings of instances
    instances_map.put(new Integer(next_unique_id), instance);
    //The instance is now pending. It will need to be committed later.
    }*/

    /**
     * Return the dbuniqueid of an instance, or -1 if there was no dbuniqueid found on that 
     * instance. 
     * @param inst

    public static int getDbUniqueId(PyObject instance) {
    ConcurrentMap<Object, PyObject> inst_dict =
    ((PyStringMap)instance.fastGetDict()).getTable();
    Object val = inst_dict.get("dbuniqueid");
    if(val != null && val instanceof PyInteger) {
    return ((PyInteger)val).getValue();
    }
    else{//We failed to find a dbuniqueid
    return -1;
    }

    }*/

    /**
     * Return an instance with the same dbuniqueid that if the object has previously 
     * been added to the session. 
     *
     * Returns null if the instance with that id does not exist. 
     */
    public static PyObjectDerived getInstance(PyInteger dbuniqueid)
    {
        return (PyObjectDerived) instances_map.get(dbuniqueid.getValue());
    }
    /**
     * Commit any objects that we know about to the database. 
     * remove instances that have previously been commited in the database
     * that represent the same instance being recommited. This essentially means
     * update old instances in the database with the new values. 

     public static void commit() throws SQLException {
     SPARQLDoer sDoer = new SPARQLDoer(connection);
     SIMHelper simHelper = new SIMHelper(connection);
     for(PyObject obj : instances_map.values()) {
     //We delete the object if it was already in the database.
     TreeMap<String, Object> key_vals = new TreeMap<String, Object>();
     key_vals.put("dbuniqueid", new Integer(getDbUniqueId(obj)));
     //Do the delete on all objects with this dbuniqueid
     sDoer.deleteSubjectsWithAttrValues(obj.getType().getName(), key_vals);
     //Then we repopulate the instance in the database.
     insertInstance(obj, sDoer, simHelper);
     }

     }   */

    /**
     * Insert a class instance into the database.

     private static void insertInstance(PyObjectDerived instance, SPARQLDoer sDoer, SIMHelper simHelper)
     {
     //So what is going on here...
     //Jython keeps two dictionaries of member data, one dictionary is defined in the
     //PyObjects objtype, this is the initial values of all the data.
     //When an object is used
     //it is then moved over to the PyObject's dictionary itself.
     //So we need to iterate over the PyObject's dictionary, and remember
     //what variables we've used.
     //Then go through and fill the database with variables that are defined but unused.
     TreeSet<String> used_variables = new TreeSet<String>();

     simHelper.executeInstance(Py.idstr(instance), instance.getType().getName());
     //Add the member data.
     //We first add any live data members, i.e data members that
     //have been moved to the instance's
     //dictionary, we then go through and add any remaining data
     //members that may have been missed
     //to looking at the types dictionary.
     PyStringMap variableMap = (PyStringMap)instance.getDict();
     addMemberDataHelper(instance, variableMap, used_variables);
     variableMap = instance.getType().getClassDictionary();
     addMemberDataHelper(instance, variableMap, used_variables);

     }*/

    /**
     * A helper method for adding instances to the database.
     * XXX This method properly populates the datastructure necessary to
     * populate the database.

     private static void addMemberDataHelper(PyObject instance, PyStringMap variableMap, TreeSet<String> used_vars)
     {
     ConcurrentMap<Object, PyObject> data = variableMap.getTable();
     for (Map.Entry<Object, PyObject> entry : data.entrySet()) {
     Object key = entry.getKey();
     PyObject value = entry.getValue();
     String value_type = value.getType().getName();
     //keys that start with _ are considered invalid characters in the database.
     if(key.toString().startsWith("_"))
     {
     continue;
     }
     //Only do the logic required for adding member data, if we have not already added the data.
     if(!used_vars.contains(key.toString()))
     {
     //Check to make sure we do not have a null required value.
     for(String required : required_attrs)
     {
     if(required.equals(key.toString()))
     {
     //if(value.toString().equals(""))
     if(value instanceof PyNone)
     {
     requiredException(required + " cannot be a null value when inserted into the database!");
     return;
     }

     }
     }

     if(mv_attrs.contains(key.toString()))
     {
     if(!(value instanceof PyTuple))
     {
     throw new IllegalStateException("Multi-valued attributes must be of type Tuple.");
     }
     for(PyObject obj : ((PyTuple)value).getArray())
     {
     String type = obj.getType().getName();
     simHelper_.executeMemberData((String)Py.idstr(instance), type, key.toString(), obj.toString());
     }


     }
     //Filter out types we don't want, as well as variables that start with _
     else if(!value.getType().getName().equals("function") && !value_type.equals("NoneType") && (value.toString().length() > 0 && !value.toString().substring(0,1).equals("_")))
     {
     if(value instanceof PyTuple)
     {
     throw new IllegalStateException("Only multi-valued attributes can be of type Tuple.");
     }
     simHelper_.executeMemberData((String)Py.idstr(instance), value_type, key.toString(), value.toString());

     }
     used_vars.add(key.toString());
     }
     }
     }*/

    /**
     * Return an instance of an object that has previously been added to the database during
     * this session that was added with the same dbuniqueid
     * @param dbuniqueid
     * @return an instance that was previously inserted into the database. Returns null if 
     * the object with dbuniqueid was not found
     */
    public static PyObjectDerived getInstance(int dbuniqueid)
    {
        return (PyObjectDerived) instances_map.get(dbuniqueid);
    }


    /**
     * Return a PyList of PyDictionary's where each PyDictionary is a 
     * dictionary of all possible values for the type. 

     public static List<PyObject> allTypeQuery(PyObject obj) {
     SPARQLDoer sDoer = ClassDbBuilder.getSingleton().getSDoer();
     SIMHelper sim = ClassDbBuilder.getSingleton().simHelper_;
     String server = ClassDbBuilder.getSingleton().url;
     String uname = ClassDbBuilder.getSingleton().uname;
     String pword = ClassDbBuilder.getSingleton().pword;
     List<PyObject> list = new ArrayList<PyObject>();
     try{
     //Get a list of attributes for this class out of the database
     PyType type = (PyType)obj;
     PyStringMap dict = (PyStringMap)type.fastGetDict();
     ConcurrentMap<Object, PyObject> data_table = dict.getTable();
     ArrayList<String> dvas = new ArrayList<String>();
     dvas.add("*");
     //Grab a set of data from a result query.
     List<String> data = sim.executeAndReturnFrom(type.getName().toUpperCase(), dvas, new ArrayList<String>(), new HashMap<String, String>(), server, uname, pword);
     //Parse the data to make a PyDictionary with the proper data for
     //each instance.
     ConcurrentHashMap<PyObject, PyObject> new_dict = new ConcurrentHashMap<PyObject, PyObject>();
     for(String line : data) {
     //We need to start a new instance, so store the current dictionary and start over
     if(line.equals("BREAK")) {
     list.add((PyObject)(new PyDictionary(new_dict)));
     new_dict = new ConcurrentHashMap<PyObject, PyObject>();
     }
     else{
     //WARNING. We need to ensure case is the same between the database variable names
     //and the python run times.
     String key_val[] = line.split("=");
     PyObject val = null;
     //Determine which type of py object to make.
     //By looking at the types dictionary.
     if(key_val[0].equals("DBUNIQUEID")){
     val = new PyInteger(Integer.parseInt(key_val[1]));
     }
     else if(data_table.get(key_val[0].toLowerCase()) instanceof PyBoolean) {
     if(key_val[1].equals("true"))
     val = new PyBoolean(true);
     else
     val = new PyBoolean(false);
     }
     else if(data_table.get(key_val[0].toLowerCase()) instanceof PyInteger) {
     val = new PyInteger(Integer.parseInt(key_val[1]));
     }
     else if (data_table.get(key_val[0].toLowerCase()) instanceof PyString) {
     val = new PyString(key_val[1]);
     }


     new_dict.put(new PyString(key_val[0].toLowerCase()), val);

     }

     }

     }
     catch (Exception e)
     {
     System.err.println("Query to get all attributes failed");
     System.err.println(e.getMessage());
     e.printStackTrace();
     System.exit(1);
     }
     return list;

     }*/
}
