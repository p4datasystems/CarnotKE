/*
 * Created on Feb 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package wdb.metadata;


/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class DVA extends Attribute {
	public String type;
	public Integer size;
	public Object initialValue;

	public DVA()
	{
		super();
	}
	
	/**
	 * @param _name
	 * @param _comment
	 */
	public DVA(String _name, String _comment, Boolean _required, String _type, Integer _size, Object _initialValue) {
		super(_name, _comment, _required);
		this.type = _type;
		this.size = _size;
		this.initialValue = _initialValue;
	}

}
