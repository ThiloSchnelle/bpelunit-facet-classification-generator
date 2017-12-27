package net.bpelunit.suitegenerator.datastructures.variables;

import org.jdom2.Element;

/**
 * The blueprint for messages containing tags for variables.
 *
 */
public class MessageExchangeVariable extends BaseVariable {

	// To have access to the parsed VariableSlots in MessageExchangeInstance
	private MessageExchangeInstance privateInst;

	public MessageExchangeVariable(String name, Element content) {
		super(name, content);
		privateInst = getMessageExchangeInstance();
	}

	/**
	 * Clones the XML Structures so MessageExchangeInstances can modify the XML
	 * 
	 * @return
	 */
	public MessageExchangeInstance getMessageExchangeInstance() {
		return new MessageExchangeInstance(name, content.clone());
	}

	public boolean hasSlot(String slotName) {
		return privateInst.hasSlot(slotName);
	}

	public int numberOfSlotsFor(String slotName) {
		return privateInst.numberOfSlotsFor(slotName);
	}

}
