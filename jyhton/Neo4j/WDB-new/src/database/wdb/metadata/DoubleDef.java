/*
 * Created on Apr 20, 2016
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package wdb.metadata;

/**
 * @author Noel Negusse
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoubleDef extends Query {
	public ClassDef cc;
	public InsertQuery ii;
	
	public DoubleDef() {
		super();
	}
	
	public DoubleDef(ClassDef cc, InsertQuery ii)
	{
		super();
		this.cc = cc;
		this.ii = ii;
	}

	/**
	 * @param cc
	 * @param ii
	 */

	public ClassDef getC() 
	{ 
		return cc; 
	}
	
	public InsertQuery getI() 
	{ 
		return ii; 
	}

}
