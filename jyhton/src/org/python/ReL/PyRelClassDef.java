package org.python.ReL;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

import org.python.expose.ExposedType;

import org.python.core.PyObject;
import org.python.core.PyType;
import org.python.core.PyObjectDerived;
import org.python.core.PyStringMap;
import org.python.core.PyString;
import org.python.core.PyBoolean;
import org.python.core.PyInteger;
import org.python.core.BuiltinDocs;
import org.python.ReL.SIMHelper;
import org.python.ReL.PyRelConnection;

/**
 * A PyRelClassDef is resposible for populating class types, and their base types
 * in the database.
 *
 * By convention the details of all parent classes go into the database as well if they are
 * not already present.
 */
@ExposedType(name = "PyRelClassDef", base = PyObject.class, doc = BuiltinDocs.tuple_doc)
public class PyRelClassDef {
    
    // What type are we. 
    public static final PyType TYPE = PyType.fromClass(PyRelClassDef.class);
    
    private SIMHelper simHelper = null; 
    private PyRelConnection connection = null; 
    /**
     * Go through the PyType's dictionary and make sure this guy persists in the database. 
     * @param classType
     */
    public PyRelClassDef(PyObject classType, PyObject conn) 
    {
        connection = (PyRelConnection)conn; 
        simHelper = new SIMHelper(connection); 
        insertType((PyType)classType); 
    }
    
    

    /**
     * Insert a type into the database. 
     * @param type
     */
    private void insertType(PyType type) 
    {
        String name = type.getName(); 
        
        ArrayList<String> dvas = new ArrayList<String>();
        ArrayList<String> attrs = new ArrayList<String>(); 
        // Go through each of the types attributes and add a dva string 
        // that will be used by SIMHelper.
        ConcurrentMap<Object, PyObject> dict = ((PyStringMap)(type.fastGetDict())).getMap();
        for (Object key : dict.keySet()) 
        {
            String dvaName = (String)key;
            // We ignore any attributes that start with "__" because these are not user attributes.  
            if(!dvaName.startsWith("__"))
            {
            PyObject attr = dict.get(dvaName); 
            // Generate a dva string that can be consumed by SimHelper for the different attribute types. 
            String dva = null; 
            if(attr instanceof PyString) { dva = makeDva(dvaName, (PyString)attr); }
            else if (attr instanceof PyBoolean) { dva = makeDva(dvaName, (PyBoolean)attr); }
            else if (attr instanceof PyInteger) { dva = makeDva(dvaName, (PyInteger)attr); } 
            
            // Only add the attribute if it was a supported type. 
            if (dva != null){
                dvas.add("dvaAttribute");
                attrs.add(dva); 
            }
            }
        }
        
        simHelper.executeClass(name, dvas, attrs, 1, 2, null); 
        // We insert every base type as well. 
        for (PyObject base : type.getBasesArray()) 
        {
            PyType baseType = (PyType)base;
            try {
                SPARQLDoer.insertObjectPropQuad(connection, baseType.getName(), "rdfs:subClassOf",
                                       type.getName());
            } catch (SQLException e) {
                System.err.println("Database error"); 
                e.printStackTrace();
            }
            insertType(baseType); 
        }
        
    }
    
    // Some helper functions for generating dva strings that will
    // be passed to SIMHelper
    private String makeDva(String name, PyObject obj) 
    { 
        return null; 
    }
    private String makeDva(String name, PyString obj) 
    {
        return "dva:" + name + "::"+"STRINGDATA"; 
    }
    private String makeDva(String name, PyInteger obj) 
    {
        return "dva:" + name + "::"+"INTEGERDATA"; 
    }
    private String makeDva(String name, PyBoolean obj) 
    {
        return "dva:" + name + "::"+"BOOLEANDATA"; 
    }

}
