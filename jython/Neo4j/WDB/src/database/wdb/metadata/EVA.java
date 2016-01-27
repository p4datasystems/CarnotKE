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
public final class EVA extends Attribute {
	public static final Integer SINGLEVALUED = 0;
	public static final Integer MULTIVALUED = 1;
	
	public String baseClassName;
	public String inverseEVA;
	public Integer cardinality;
	public Boolean distinct;
	public Integer max;
	
	public EVA()
	{
		super();
	}
	
	/**
	 * @param _name
	 * @param _comment
	 * @param _required
	 */
	public EVA(String _name, String _comment, Boolean _required, String _baseClassName, String _inverseEVA, int _cardinality) {
		super(_name, _comment, _required);
		baseClassName = _baseClassName;
		inverseEVA = _inverseEVA;
		cardinality = _cardinality;
	}

}
