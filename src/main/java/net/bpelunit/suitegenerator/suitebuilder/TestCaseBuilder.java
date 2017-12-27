package net.bpelunit.suitegenerator.suitebuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.util.IteratorIterable;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.datastructures.testcases.TestCase;
import net.bpelunit.suitegenerator.datastructures.variables.IVariableInstance;
import net.bpelunit.suitegenerator.datastructures.variables.Mapping;
import net.bpelunit.suitegenerator.datastructures.variables.MessageExchangeInstance;
import net.bpelunit.suitegenerator.datastructures.variables.MessageExchangeVariable;
import net.bpelunit.suitegenerator.datastructures.variables.MessageVariableMapping;
import net.bpelunit.suitegenerator.datastructures.variables.PartnerTrack;
import net.bpelunit.suitegenerator.datastructures.variables.PartnerTrackInstance;
import net.bpelunit.suitegenerator.datastructures.variables.TrackType;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;
import net.bpelunit.suitegenerator.datastructures.variables.VariableMapping;

/**
 * Transforms a TestCase-Instance with given VariableInstances into the XML-Representation of a TestCase
 *
 */
public class TestCaseBuilder implements SlotVisitor {

	private TestCase testCase;
	private Namespace tes;
	private Element testCaseElement;

	private Map<String, PartnerTrackInstance> partner = new TreeMap<String, PartnerTrackInstance>();

	private VariableLibrary data;

	private Map<String, IVariableInstance> instanceForVarName = new HashMap<String, IVariableInstance>();
	private Set<String> deactivatedSlots = new HashSet<>();
	private Set<String> deactivatedTracks = new HashSet<>();

	private TestCaseBuilder(TestCase testCase, Namespace tes, Element testCaseElement, VariableLibrary data) {
		this.testCase = testCase;
		this.tes = tes;
		this.testCaseElement = testCaseElement;
		this.data = data;
		generateCase();
	}

	public static TestCaseBuilder buildCase(TestCase testCase, Namespace tes, Element testCaseElement, VariableLibrary data) {
		return new TestCaseBuilder(testCase, tes, testCaseElement, data);
	}

	private void generateCase() {
		for (ClassificationVariableSelection var : testCase.getSelections()) {
			Mapping chosenMapping = var.getMapping();
			if (chosenMapping == null) {
				Config.get().out().noMappingFor(var);
			}
			useMappings(chosenMapping);
		}
		insertVarInstances();
		attachPartnerTracks();
		
		removeUnusedGeneratorTags();
	}

	private void removeUnusedGeneratorTags() {
		ElementFilter filter = new ElementFilter(Config.get().getGeneratorSpace());
		IteratorIterable<Element> remainingGeneratorElements = testCaseElement.getDescendants(filter);
		List<Element> myCopy = new ArrayList<>(); // in order to avoid ConcurrentModificationException
		
		for(Element e : remainingGeneratorElements) {
			myCopy.add(e);
		}
		
		for(Element e : myCopy) {
			e.detach();
		}
	}

	/*
	 * Use necessary variableInstances from the most basic selection to the selection that is chosen in this TestCase. Due to the HashMap's replacement when inserting a duplicate key the extending
	 * selections overwrite the values inserted by the parent selections.
	 */
	private void useMappings(Mapping chosenSel) {
		// Mapping copies now everything from parentmappings into itself
		/*
		 * if (chosenSel.hasParent()) { for (Mapping parent : chosenSel.getParentMappings()) { useSelections(parent); } }
		 */
		if(chosenSel != null) {
			for (VariableMapping sel : chosenSel.getMappings()) {
				sel.accept(this); // <-- Visitorpattern: sel.accept calls the correct method of TestCaseBuilder for the concrete Type
//				System.out.println("| " + sel.getInstanceName() + " -> " + sel.getSlotName());
			}
		}
	}

	@Override
	public void useVarMapping(VariableMapping sel) {
		IVariableInstance dvi = data.getInstanceForName(sel.getSlotName(), sel.getInstanceName());
		if (dvi == null) {
			Config.get().out().noInstanceForSelectedVariable(sel.getSlotName(), sel.getInstanceName());
		}
		instanceForVarName.put(sel.getSlotName(), dvi);
	}

	@Override
	public void useMessageMapping(MessageVariableMapping sel) {
		PartnerTrackInstance partner = getFittingPartnerTrack(sel);
		if (partner != null) {
			createMessageExchangeInstance(sel, partner);
		}
	}

	@Override
	public void deactivateSlot(String slotName) {
		deactivatedSlots.add(slotName);
	}

	@Override
	public void deactivateTrack(String trackName) {
		deactivatedTracks.add(trackName);
	}

	private void createMessageExchangeInstance(MessageVariableMapping sel, PartnerTrackInstance partner) {
		MessageExchangeVariable mev = data.getMessage(sel.getInstanceName());
		if (mev == null) {
			Config.get().out().noMessageExchangeWithName(sel.getInstanceName());
			return;
		}
		MessageExchangeInstance mei = mev.getMessageExchangeInstance();
		partner.addMessage(sel.getSlotName(), mei);
	}

	/**
	 * Retrieves or creates a new PartnerTrack
	 * 
	 * @param messageElement
	 * @return
	 */
	private PartnerTrackInstance getFittingPartnerTrack(MessageVariableMapping sel) {
		PartnerTrackInstance partner = findPartner(sel);
		if (partner == null) {
			PartnerTrack p = data.getPartnerTrackWithSlot(sel.getSlotName());
			if (p != null) {
				if (deactivatedTracks.contains(p.getVariableName())) {
					return null;
				}
				partner = p.createPartnerTrackInstance();
				this.partner.put(partner.getName(), partner);
			} else {
				Config.get().out().noPartnerTrackWithSlot(sel.getSlotName());
			}
		}
		if (deactivatedTracks.contains(partner.getName())) {
			return null;
		}
		return partner;
	}

	private PartnerTrackInstance findPartner(MessageVariableMapping sel) {
		for (PartnerTrackInstance inst : partner.values()) {
			if (inst.hasSlot(sel.getSlotName())) {
				return inst;
			}
		}
		return null;
	}

	// Enrich the messages of all partnerTracks with the selected variableInstances
	private void insertVarInstances() {
		for (PartnerTrackInstance p : partner.values()) {
			p.deactivateSlots(deactivatedSlots);
			p.attachInstances(instanceForVarName);
		}
	}

	// Attach the completely processed PartnerTracks to the TestCaseXML. Keep ClientTrack first.
	private void attachPartnerTracks() {
		boolean client = false;
		for (PartnerTrackInstance p : partner.values()) {
			if (p.isClient()) {
				testCaseElement.addContent(p.getElement(tes));
				client = true;
				break;
			}
		}
		if (!client) {
			testCaseElement.addContent(new Element("clientTrack", tes));
		}
		for (PartnerTrackInstance p : partner.values()) {
			if (!p.isClient() && p.getTrackType() == TrackType.PARTNER && !deactivatedTracks.contains(p.getName())) {
				testCaseElement.addContent(p.getElement(tes));
			}
		}
		for (PartnerTrackInstance p : partner.values()) {
			if (!p.isClient() && p.getTrackType() == TrackType.HUMANTASK && !deactivatedTracks.contains(p.getName())) {
				testCaseElement.addContent(p.getElement(tes));
			}
		}
	}

}
