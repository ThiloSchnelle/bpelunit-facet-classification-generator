package net.bpelunit.suitegenerator.datastructures.variables;

import org.jdom2.Element;

/**
 * Encapsulates a SlotName with the Element where the belonging data can be attached.
 *
 */
public class VariableSlot {

	private Element parent;
	private String name;

	public VariableSlot(Element parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	public Element getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

}
