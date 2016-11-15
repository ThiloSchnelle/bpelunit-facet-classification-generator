package net.bpelunit.suitegenerator.datastructures.variables;

import net.bpelunit.suitegenerator.suitebuilder.SlotVisitor;

public class MessageSlotDeactivation extends VariableMapping {

	public MessageSlotDeactivation(String slotName, String instanceName) {
		super(slotName, instanceName);
	}

	@Override
	public void accept(SlotVisitor sv) {
		sv.deactivateSlot(slotName);
	}

}
