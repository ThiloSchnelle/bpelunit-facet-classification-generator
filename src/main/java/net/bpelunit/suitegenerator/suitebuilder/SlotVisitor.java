package net.bpelunit.suitegenerator.suitebuilder;

import net.bpelunit.suitegenerator.datastructures.variables.MessageVariableMapping;
import net.bpelunit.suitegenerator.datastructures.variables.VariableMapping;

public interface SlotVisitor {

	void useVarMapping(VariableMapping s);

	void useMessageMapping(MessageVariableMapping s);

	void deactivateSlot(String slotName);

	void deactivateTrack(String slotName);

}
