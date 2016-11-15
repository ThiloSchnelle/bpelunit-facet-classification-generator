package net.bpelunit.suitegenerator.datastructures.variables;

import org.jdom2.Element;

import net.bpelunit.suitegenerator.util.XMLElementOutput;

/**
 * Convenience baseclass
 *
 */
public abstract class BaseVariable implements IVariable {

	protected final String name;
	protected final Element content;

	public BaseVariable(String name, Element content) {
		this.name = name;
		this.content = content;
	}

	@Override
	public String getVariableName() {
		return name;
	}

	@Override
	public String toString() {
		return "Variable [" + name + "]: {" + XMLElementOutput.out(content) + "}";
	}

}
