package org.cyphersim;

public class RCNode {
	private String identifier;
	private String label;
	private String relName;
	RCNode next;
	RCNode prev;

	public RCNode() {
		identifier = null;
		label = null;
		relName = null;
		next = null;
		prev = null;
	}
	
	public String toString(){
		return "{id : " + identifier + ", label: " + label + ", relName: " + relName + "}";
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getRelName() {
		return relName;
	}
	
	public RCNode getNext() {
		return next;
	}
	
	public RCNode getPrev() {
		return prev;
	}
	
	public void setIdentifier(String i) {
		identifier = i;
	}
	
	public void setLabel(String l) {
		label = l;
	}
	
	public void setRelName(String r) {
		relName = r;
	}
	
	public void setNext(RCNode n) {
		next = n;
	}
	
	public void setPrev(RCNode p) {
		prev = p;
	}
}
