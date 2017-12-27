package net.bpelunit.suitegenerator.datastructures.variables;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;

import net.bpelunit.suitegenerator.config.Config;

/***
 * After an instance has been inserted into a any variable slot, it might still contain variableSlots itself These slots need to be filled. Therefore the
 * inserted XML is saved in such an instance and can be searched for new slots
 */
public class InsertedInstance extends BaseVariable implements IInsertedInstance {

	private Map<String, List<VariableSlot>> slots = new HashMap<>();

	public InsertedInstance(String name, Element content) {
		super("Inserted Instance of: " + name, content);
	}

	private void parseContent(Element content) {
		for (Element var : content.getDescendants(new ElementFilter(Config.get().getVariableSlotTag()))) {
			Attribute nameAtt = var.getAttribute("name");
			if (nameAtt == null || nameAtt.getValue() == null || nameAtt.getValue().isEmpty()) {
				Config.get().out().varSlotWithoutName(this.name);
				continue;
			}
			String name = nameAtt.getValue();
			Element parent = var.getParentElement();
			if (parent != null) {
				addSlot(new VariableSlot(var, name));
			}
		}
	}

	private void addSlot(VariableSlot variableSlot) {
		List<VariableSlot> slotList = slots.get(variableSlot.getName());
		if (slotList == null) {
			slotList = new LinkedList<>();
			slots.put(variableSlot.getName(), slotList);
		}
		slotList.add(variableSlot);
	}

	@Override
	public void insertVariables(Map<String, IVariableInstance> instanceForVarName) {
		parseContent(content);
		if (slots.size() != 0) {
			for (String slotName : instanceForVarName.keySet()) {
				List<VariableSlot> slotList = slots.get(slotName);
				if (slotList != null) {
					IVariableInstance inst = instanceForVarName.get(slotName);
					for (VariableSlot vs : slotList) {
						IInsertedInstance ii = inst.replaceWithVariable(vs);
						if(ii != null) {
							ii.insertVariables(instanceForVarName);
						}
					}
				}
			}
		}
	}

	public boolean hasSlot(String slotName) {
		return numberOfSlotsFor(slotName) != 0;
	}

	public int numberOfSlotsFor(String slotName) {
		List<VariableSlot> slotList = slots.get(slotName);
		return slotList != null ? slotList.size() : 0;
	}
}
