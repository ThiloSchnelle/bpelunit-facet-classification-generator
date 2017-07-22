package net.bpelunit.suitegenerator.datastructures.variables;

import org.jdom2.Element;

/**
 * Everything that is read from XML and gives values for variables
 *
 */
public interface IVariableInstance extends IVariable {

	/**
	 * Attaches the content of this VariableInstance onto the given parent element.
	 * 
	 * @param e
	 */
	public void attachToElement(Element e);

	public String getInstanceName();

	public void countUsage();

	public int getNumberUsages();

	public void replaceWithVariable(VariableSlot vs);

}
