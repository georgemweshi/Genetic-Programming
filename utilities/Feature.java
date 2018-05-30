package utilities;

import weka.core.Attribute;

public class Feature {
	private Attribute attribute;
	private int attributeIndex;


	public Feature(Attribute attribute, int attributeIndex) {
		this.attribute = attribute;
		this.attributeIndex = attributeIndex;
	
	}

	public Attribute getAttribute() {
		return attribute;
	}

	
	public int getAttributeIndex(){
		return attributeIndex;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	public void setAttributeIndex(int attributeIndex) {
		this.attributeIndex = attributeIndex;
	}
}
