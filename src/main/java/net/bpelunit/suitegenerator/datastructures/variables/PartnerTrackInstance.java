package net.bpelunit.suitegenerator.datastructures.variables;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * Collects message-instances that are selected to be in the represented PartnerTrack
 *
 */
public class PartnerTrackInstance {

	private Map<String, MessageExchangeInstance> sequencedMessages = new LinkedHashMap<>();
	private TrackType trackType = TrackType.PARTNER;
	private String name;

	public PartnerTrackInstance(String name, TrackType type, List<String> sequenceOfSlots) {
		this.name = name;
		trackType = type;
		for (String s : sequenceOfSlots) {
			sequencedMessages.put(s, null);
		}
	}

	public Element getElement(Namespace tes) {
		Element partner = null;
		partner = new Element(trackType.getName(), tes);
		if (trackType != TrackType.CLIENT) {
			partner.setAttribute("name", name);
		}
		// Iterate over keySet as I'm not sure whether the values()-set has the same order.
		for (String key : sequencedMessages.keySet()) {
			MessageExchangeInstance mei = sequencedMessages.get(key);
			if (mei != null) { // There can be empty slots!
				mei.attachToElement(partner);
			}
		}
		return partner;
	}

	public TrackType getTrackType() {
		return trackType;
	}
	
	public void addMessage(String messageSlot, MessageExchangeInstance mei) {
		sequencedMessages.put(messageSlot, mei);
	}

	public MessageExchangeInstance getMessageAtSlot(String messageSlot) {
		return sequencedMessages.get(name);
	}

	public String getName() {
		return name;
	}

	public void attachInstances(Map<String, IVariableInstance> instanceForVarName) {
		for (MessageExchangeInstance mei : sequencedMessages.values()) {
			if (mei != null) { // There can be empty slots!
				mei.insertVariables(instanceForVarName);
			}
		}
	}

	public boolean isClient() {
		return trackType == TrackType.CLIENT;
	}

	public void deactivateSlots(Set<String> deactivatedSlots) {
		for (String de : deactivatedSlots) {
			sequencedMessages.remove(de);
		}
	}

	public boolean hasSlot(String slotName) {
		return sequencedMessages.containsKey(slotName);
	}

}
