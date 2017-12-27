package net.bpelunit.suitegenerator.datastructures.variables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores the parsed Variables, MessageExchanges and Selections. Offers access to all of them.
 *
 */
public class VariableLibrary {

	private Map<String, Map<String, DataVariableInstance>> instances = new HashMap<>();
	private Map<String, MessageExchangeVariable> messages = new HashMap<>();
	private Map<String, Mapping> selections = new HashMap<>();
	private Map<String, PartnerTrack> tracks = new HashMap<>();

	private Map<String, Mapping> selectionsByParentName = new HashMap<>();

	public IVariableInstance getInstanceForName(String varName, String instanceName) {
		Map<String, DataVariableInstance> inst = instances.get(varName);
		if (inst != null) {
			return inst.get(instanceName);
		}
		return null;
	}

	public void put(DataVariableInstance var) {
		Map<String, DataVariableInstance> inst = instances.get(var.getVariableName());
		if (inst == null) {
			inst = new HashMap<>();
			instances.put(var.getVariableName(), inst);
		}
		inst.put(var.getInstanceName(), var);
	}

	public MessageExchangeVariable getMessage(String name) {
		return messages.get(name);
	}

	public void addMessageExchange(MessageExchangeVariable mev) {
		messages.put(mev.getVariableName(), mev);
	}

	public Mapping getSelection(String name) {
		return selections.get(name);
	}

	/**
	 * Adds s to this VariableLibrary AND makes connections between parent and child selections.
	 * 
	 * @param s
	 */
	public void addMapping(Mapping s) {
		selections.put(s.getVariableName(), s);
		if (s.hasParent()) {
			for (String parentName : s.getParentNames()) {
				selectionsByParentName.put(parentName, s);
			}
			for (String parentName : s.getParentNames()) {
				Mapping parent = selections.get(parentName);
				if (parent != null) {
					s.addParentSelection(parent);
				}
			}
		}
		Mapping child = selectionsByParentName.get(s.getVariableName());
		if (child != null) {
			child.addParentSelection(s);
		}
	}

	public void addPartnerTrack(PartnerTrack track) {
		this.tracks.put(track.getVariableName(), track);
	}

	public PartnerTrack getPartnerTrack(String name) {
		return this.tracks.get(name);
	}

	public Collection<DataVariableInstance> getDataVarInstances() {
		Collection<DataVariableInstance> res = new ArrayList<>(50);
		for (Map<String, DataVariableInstance> map : instances.values()) {
			for (DataVariableInstance d : map.values()) {
				res.add(d);
			}
		}
		return res;
	}

	public PartnerTrack getPartnerTrackWithSlot(String slotName) {
		for (PartnerTrack track : tracks.values()) {
			if (track.hasSlot(slotName)) {
				return track;
			}
		}
		return null;
	}
}
