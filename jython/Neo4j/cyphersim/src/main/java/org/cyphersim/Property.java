package org.cyphersim;

public class Property {
	public String nodeId;
	public String prop;
	
	public Property(String n, String p) {
		nodeId = n;
		prop = p;
	}
	
	public String toString() {
		return prop;
	}
}
