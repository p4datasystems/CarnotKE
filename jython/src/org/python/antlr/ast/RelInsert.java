package org.python.antlr.ast;

import org.antlr.runtime.Token;

import org.python.antlr.AST;
import org.python.antlr.base.expr;
import org.python.expose.ExposedGet;
import org.python.expose.ExposedSet;
import org.python.expose.ExposedType;


@ExposedType(name = "_ast.RelInsert", base = AST.class)
public class RelInsert extends expr implements Context {
    private Name connection_name; 
    public Name getConnectionName() { return connection_name; }
    
    private Name obj_name;
    public Name getInsertedObjectName() { return obj_name; }
    public RelInsert(Integer ttype, Token token, Name obj_name, Name conn_name) {
        super(ttype, token);
        connection_name = conn_name; 
        this.obj_name = obj_name; 
    }

    @Override
    public void setContext(expr_contextType ctx) {
    }
    
    
    // Stuff for visiting the node. 
    public <R> R accept(VisitorIF<R> visitor) throws Exception {
        // System.out.println("Accepting visitor"); 
        return visitor.visitRelInsert(this);
    }

    public void traverse(VisitorIF<?> visitor) throws Exception {
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
