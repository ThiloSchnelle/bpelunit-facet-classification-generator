package net.bpelunit.suitegenerator.datastructures.variables;

import net.bpelunit.suitegenerator.suitebuilder.SlotVisitor;

/**
 * 
 * Offers to accept a visitor to allow "double dispatch" -> Distinguish which method to chose without checking instanceof VariableSelection or
 * MessageVariableSelection
 */
public class VariableMapping {

	protected String slotName;
	protected String instanceName;

	public VariableMapping(String slotName, String instanceName) {
		super();
		this.slotName = slotName;
		this.instanceName = instanceName;
	}

	public String getSlotName() {
		return slotName;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void accept(SlotVisitor sv) {
		sv.useVarMapping(this);
	}

}
