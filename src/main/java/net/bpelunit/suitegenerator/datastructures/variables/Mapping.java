package net.bpelunit.suitegenerator.datastructures.variables;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;

import net.bpelunit.suitegenerator.config.Config;

/**
 * The selections of messages and variables that may belong to a leave in the ClassificationTree. Selections can also be defined without representing a leave if
 * they are there to be extended.
 *
 */
public class Mapping extends BaseVariable {

	private Collection<Mapping> parentMappings = new LinkedList<>();
	private Collection<String> parentNames = new LinkedList<>();

	private Collection<VariableMapping> allMappings = new HashSet<>();
	private Map<String, VariableMapping> variableMappings = new HashMap<>();
	private Map<String, MessageVariableMapping> messageMappings = new HashMap<>();
	private Set<MessageSlotDeactivation> deactivations = new HashSet<>();
	private Set<PartnerTrackDeactivation> trackDeactivations = new HashSet<>();

	public Mapping(String name, Element content) {
		super(name, content);
		readParentNames();
		readVariableMappings();
	}

	private void readVariableMappings() {
		for (Element variable : content.getChildren()) {
			if (isVariable(variable)) {
				readVar(variable);
			} else if (isMessageExchange(variable)) {
				readMsg(variable);
			} else if (isDeactivation(variable)) {
				readDeactivation(variable);
			} else if (isTrackDeactivation(variable)) {
				readTrackDeactivation(variable);
			}
		}
	}

	private boolean isDeactivation(Element variable) {
		return variable.getName().equals(Config.get().getDeactivationTag());
	}

	private boolean isTrackDeactivation(Element variable) {
		return variable.getName().equals(Config.get().getPartnerTrackDeactivationTag());
	}

	private boolean isMessageExchange(Element variable) {
		return variable.getName().equals(Config.get().getUseMessageExchange());
	}

	private boolean isVariable(Element variable) {
		return variable.getName().equals(Config.get().getUseVariable());
	}

	private void readVar(Element variable) {
		Attribute varName = variable.getAttribute("variableName");
		Attribute varInstance = variable.getAttribute("variableInstance");
		if (varName == null || varName.getValue().isEmpty()) {
			Config.get().out().variableSelectionWithoutName(name);
			return;
		}
		if (varInstance == null || varInstance.getValue().isEmpty()) {
			Config.get().out().variableSelectionWithoutInstanceName(name);
			return;
		}
		VariableMapping sel = new VariableMapping(varName.getValue(), varInstance.getValue());
		this.allMappings.add(sel);
		this.variableMappings.put(sel.getSlotName(), sel);
	}

	private void readDeactivation(Element variable) {
		Attribute nameAtt = variable.getAttribute("name");
		if (nameAtt != null && !nameAtt.getValue().isEmpty()) {
			// TODO nicht schön
			String slotName = nameAtt.getValue();
			MessageSlotDeactivation de = new MessageSlotDeactivation(slotName, "");
			this.allMappings.add(de);
			this.deactivations.add(de);
		}
	}

	private void readTrackDeactivation(Element variable) {
		Attribute nameAtt = variable.getAttribute("name");
		if (nameAtt != null && !nameAtt.getValue().isEmpty()) {
			String trackName = nameAtt.getValue();
			PartnerTrackDeactivation de = new PartnerTrackDeactivation(trackName);
			this.allMappings.add(de);
			this.trackDeactivations.add(de);
		}
	}

	private void readMsg(Element variable) {
		Attribute varName = variable.getAttribute("messageName");
		Attribute messageSlot = variable.getAttribute("messageSlot");
		if (varName == null || varName.getValue().isEmpty()) {
			Config.get().out().messageSelectionWithoutName(name);
			return;
		}
		if (messageSlot == null || messageSlot.getValue().isEmpty()) {
			Config.get().out().messageSelectionWithoutSlot(name, varName.getValue());
			return;
		}
		MessageVariableMapping sel = new MessageVariableMapping(messageSlot.getValue(), varName.getValue());
		this.allMappings.add(sel);
		this.messageMappings.put(sel.getInstanceName(), sel);
	}

	private void readParentNames() {
		for (Element ext : content.getDescendants(new ElementFilter(Config.get().getSelectionExtends(), Config.get().getGeneratorSpace()))) {
			if (ext.getText() != null && !ext.getText().trim().isEmpty()) {
				parentNames.add(ext.getText().trim());
			}
		}
	}

	public Collection<Mapping> getParentMappings() {
		return parentMappings;
	}

	// TODO: When Parent is added to child and after that the grandparent is read
	public void addParentSelection(Mapping parentMapping) {
		this.parentMappings.add(parentMapping);
//		allMappings.addAll(parentMapping.getMappings());
		
		for(String s : parentMapping.getMessageMappings().keySet()) {
			if(!messageMappings.containsKey(s)) {
				MessageVariableMapping mapping = parentMapping.getMessageMappings().get(s);
				messageMappings.put(s, mapping);
				this.allMappings.add(mapping);
			}
		}
		for(String s : parentMapping.getVariableMappings().keySet()) {
			if(!variableMappings.containsKey(s)) {
				VariableMapping mapping = parentMapping.getVariableMappings().get(s);
				variableMappings.put(s, mapping);
				this.allMappings.add(mapping);
			}
		}
		deactivations.addAll(parentMapping.getDeactivations());
		allMappings.addAll(parentMapping.getDeactivations());
		trackDeactivations.addAll(parentMapping.getTrackDeactivations());
		allMappings.addAll(parentMapping.getTrackDeactivations());
	}

	private Collection<PartnerTrackDeactivation> getTrackDeactivations() {
		return trackDeactivations;
	}

	private Collection<MessageSlotDeactivation> getDeactivations() {
		return deactivations;
	}

	public Collection<String> getParentNames() {
		return parentNames;
	}

	public boolean isParent(String name) {
		return parentNames.contains(name);
	}

	public boolean hasParent() {
		return parentNames != null;
	}

	public Collection<VariableMapping> getMappings() {
		return allMappings;
	}

	public Map<String, MessageVariableMapping> getMessageMappings() {
		return messageMappings;
	}

	public Map<String, VariableMapping> getVariableMappings() {
		return variableMappings;
	}

	public boolean hasMappingForVar(String slotName, String instanceName) {
		VariableMapping var = variableMappings.get(slotName);
		return var != null && (instanceName == null || var.getInstanceName().equals(instanceName));
	}

	public boolean hasMappingForVar(String slotName) {
		return hasMappingForVar(slotName, null);
	}

	public int numberOfSlotsFor(String slotName, VariableLibrary vars) {
		int sum = 0;
		for (MessageVariableMapping msg : messageMappings.values()) {
			MessageExchangeVariable mev = vars.getMessage(msg.getInstanceName());
			sum += mev.numberOfSlotsFor(slotName);
		}
		return sum;
	}
}
