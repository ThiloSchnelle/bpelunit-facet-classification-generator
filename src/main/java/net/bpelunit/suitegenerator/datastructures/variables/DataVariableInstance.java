package net.bpelunit.suitegenerator.datastructures.variables;

import org.jdom2.Element;

import net.bpelunit.suitegenerator.util.Copyable;

/**
 * Content for variables that are defined by variable-tags in messages
 *
 */
public class DataVariableInstance extends BaseInstance implements Copyable<DataVariableInstance> {

	public DataVariableInstance(String variableName, String instanceName, Element content) {
		super(variableName, instanceName, content);
	}

	@Override
	public DataVariableInstance copy() {
		DataVariableInstance res = new DataVariableInstance(name, instanceName, content.clone());
		res.numberOfUsages = this.numberOfUsages;
		return res;
	}

}
