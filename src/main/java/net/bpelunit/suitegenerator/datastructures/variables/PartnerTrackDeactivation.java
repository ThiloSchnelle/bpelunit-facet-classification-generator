package net.bpelunit.suitegenerator.datastructures.variables;

import net.bpelunit.suitegenerator.suitebuilder.SlotVisitor;

public class PartnerTrackDeactivation extends VariableMapping {

	public PartnerTrackDeactivation(String slotName) {
		super(slotName, "");
	}

	@Override
	public void accept(SlotVisitor sv) {
		sv.deactivateTrack(slotName);
	}

}
