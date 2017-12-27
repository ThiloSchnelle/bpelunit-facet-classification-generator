package net.bpelunit.suitegenerator.datastructures.variables;

import org.jdom2.Element;

import net.bpelunit.suitegenerator.util.XMLElementOutput;

/**
 * Encapsulates a SlotName with the Element where the belonging data can be inserted.
 *
 */
public class VariableSlot {

	private Element variableElement;
	private String name;

	public VariableSlot(Element variableElement, String name) {
		this.variableElement = variableElement;
		this.name = name;
	}

	public Element getParent() {
		return variableElement.getParentElement();
	}

	public Element getElement() {
		return variableElement;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Slot [" + name + "] containing {" + XMLElementOutput.out(variableElement) + "}";
	}

}
