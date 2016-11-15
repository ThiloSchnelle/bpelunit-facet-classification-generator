package net.bpelunit.suitegenerator.datastructures.variables;

import net.bpelunit.suitegenerator.suitebuilder.SlotVisitor;

public class MessageVariableMapping extends VariableMapping {

	public MessageVariableMapping(String slotName, String instanceName) {
		super(slotName, instanceName);
	}

	@Override
	public void accept(SlotVisitor sv) {
		sv.useMessageMapping(this);
	}

}
