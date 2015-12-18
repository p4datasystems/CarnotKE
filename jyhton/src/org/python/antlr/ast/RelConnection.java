package org.python.antlr.ast;

import org.antlr.runtime.Token;

import org.python.antlr.AST;
import org.python.antlr.base.expr;
import org.python.core.PyType;
import org.python.expose.ExposedGet;
import org.python.expose.ExposedSet;
import org.python.expose.ExposedType;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.antlr.adapter.AstAdapters;

/**
 * A RelConnection node is placed on the AST by a toConnect
 * statement.
 *
 * When the RelConnection is visited, then a PyRelConnection is created,
 * the PyRelConnection can be used to interface with the database.
 */
 @ExposedType(name = "_ast.RelConnection", base = AST.class)
public class RelConnection extends expr implements Context{

    @Override
    public void setContext(expr_contextType ctx) {
    }
    // Required type. 
    // public static final PyType TYPE = PyType.fromClass(RelConnection.class);
    
    // information to create a database connection. 
    private String url, uname, pword, connType, model, table, debug; 
    // The name that the PyRelConnection created should be associated with. 
    private Name name; 
    

    // Construct a new RelConnection node on the AST, which will later
    // create a PyRelConnection that will be a global object accessable via
    // objName. 
    public RelConnection(Integer ttype, Token t, expr objName, String url,
        String uname, String pword, String conn_type, String model, String debug) {
                         // String url, String uname, String pword, String conn_type, String model, String debug) {
        super(ttype, t);
        this.url = url;
        this.uname = uname;
        this.pword = pword;
        if (model == "") model = "RDF";
        this.pword = pword;
        this.connType = conn_type; 
        this.model = model + "_" + uname;
        this.table = this.model + "_" + "DATA";
        this.debug = debug;
        name = (Name)objName;
    }
    
    // Provide access to the name node. 
    public Name getInternalName() { return name; }
    
    // Accessors neccessary to create a connection. 
    public String getUrl() { return url;}
    public String getUsername() { return uname; }
    public String getPassword() { return pword; }
    public String getConnectionType() { return connType; }
    public String getModel() { return model; }
    public String getTable() { return table; }
    public String getDebug() { return debug; }
    
    
    // Stuff for visiting the node. 
    public <R> R accept(VisitorIF<R> visitor) throws Exception {
        // System.out.println("Accepting visitor"); 
        return visitor.visitRelConnection(this);
    }

    public void traverse(VisitorIF<?> visitor) throws Exception {
        // System.out.println("Traversing"); 
    }


    // Stuff for error reporting i guess. 
    private int lineno = -1;
    @ExposedGet(name = "lineno")
    public int getLineno() {
        if (lineno != -1) {
            return lineno;
        }
        return getLine();
    }

    @ExposedSet(name = "lineno")
    public void setLineno(int num) {
        lineno = num;
    }

    private int col_offset = -1;
    @ExposedGet(name = "col_offset")
    public int getCol_offset() {
        if (col_offset != -1) {
            return col_offset;
        }
        return getCharPositionInLine();
    }

    @ExposedSet(name = "col_offset")
    public void setCol_offset(int num) {
        col_offset = num;
    }

}
