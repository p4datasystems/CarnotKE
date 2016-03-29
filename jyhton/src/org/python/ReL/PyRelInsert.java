package org.python.ReL;

import org.python.core.*;
import org.python.expose.ExposedType;

import java.util.concurrent.ConcurrentMap;

/**
 * A class that is responsible for executing an insert of a python class instance into the database.
 * The classes type must already be defined in the database. A run time exception will be thrown if the
 * class is not already defined.
 * <p>
 * Attributes that the instance object has which is not already in the database will be added at the time of
 * insert.
 */
@ExposedType(name = "PyRelInsert", base = PyObject.class, doc = BuiltinDocs.tuple_doc)
public class PyRelInsert extends PyObject {

    // The object instance is inserted at construction. 
    public PyRelInsert(PyObject conn, PyObject instance)
    {
        super();
        // We expected a PyObjectDerived, which is an instance in jython. 
        PyObjectDerived obj = (PyObjectDerived) instance;
        PyRelConnection connection = (PyRelConnection) conn;

        //First add the next dbuniqueid attribute to the instance. 
        //Grab a pointer to the dictionary, within the instance's type's dictionary to set a values
        ConcurrentMap<Object, PyObject> inst_dict;
        inst_dict = ((PyStringMap) instance.fastGetDict()).getMap();
        int id = connection.addToSession(instance);
        // Add the db unique id to the instance's dictionary.
        inst_dict.put("DBUNIQUEID", new PyInteger(id));
        //The instance is now pending. It will need to be committed later.  
        //It doesn't persist until the session is 

    }


}
