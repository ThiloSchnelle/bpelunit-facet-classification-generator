package net.bpelunit.suitegenerator.reader;

import java.io.File;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.datastructures.variables.DataVariableInstance;
import net.bpelunit.suitegenerator.datastructures.variables.Mapping;
import net.bpelunit.suitegenerator.datastructures.variables.MessageExchangeVariable;
import net.bpelunit.suitegenerator.datastructures.variables.PartnerTrack;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;

/**
 * Reads every file ending with .xml in the given folder as well as in its subfolders and tries to read Selections, Variables and VariableInstances. Also reads
 * the skeletal structure of the testsuite. That file's name is taken from an IConfiguration-instance.
 *
 */
public class CodeFragmentReader implements ICodeFragmentReader {

	private String baseFileName = Config.get().getBaseFileName();

	private VariableLibrary variables = new VariableLibrary();

	private Element baseRoot;

	CodeFragmentReader(File f) throws JDOMException, IOException {
		File folder = f;
		if (!f.isDirectory()) {
			folder = f.getParentFile();
		}
		readBaseFile(new File(folder, baseFileName));
		readOtherFiles(folder);
	}

	// Recursively read all files in the given folder and its children
	private void readOtherFiles(File folder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				readOtherFiles(f);
			}
			if (f.getName().endsWith(".xml")) {
				readFileForVariables(f);
			}
		}
	}

	private void readFileForVariables(File f) {
		SAXBuilder jdomBuilder = new SAXBuilder();
		Document jdomDocument;
		try {
			jdomDocument = jdomBuilder.build(f);
			Element root = jdomDocument.getRootElement();
			readMessageExchanges(root, Config.get().getMessageExchangeTag(), f);
			readDataVariables(root, Config.get().getVariableDefinitionTag(), f);
			readSelections(root, Config.get().getSelectionTag(), Config.get().getSelectionName());
			readPartnerTracks(root, Config.get().getPartnerTrackSequence());
		} catch (Exception e) {
			System.out.println("Tried to read File " + f + ". " + e);
			e.printStackTrace();
		}
	}

	private void readPartnerTracks(Element root, String partnerTrackTag) {
		// The following for-loop does not take the root-element itself into account
		if (root.getName().equals(partnerTrackTag)) {
			PartnerTrack pt = PartnerTrack.createPartnerTrack(root);
			if (pt != null) {
				variables.addPartnerTrack(pt);
			}
		}
		for (Element track : root.getDescendants(new ElementFilter(partnerTrackTag, Config.get().getGeneratorSpace()))) {
			PartnerTrack pt = PartnerTrack.createPartnerTrack(track);
			if (pt != null) {
				variables.addPartnerTrack(pt);
			}
		}
	}

	private void readSelections(Element root, String selectionTag, String nameTag) {
		// The following for-loop does not take the root-element itself into account
		if (root.getName().equals(selectionTag)) {
			readMapping(root, nameTag);
		}
		for (Element selection : root.getDescendants(new ElementFilter(selectionTag, Config.get().getGeneratorSpace()))) {
			readMapping(selection, nameTag);
		}
	}

	private void readMapping(Element mapping, String nameTag) {
		Attribute nameAtt = mapping.getAttribute("name");
		if (nameAtt != null) {
			Mapping s = new Mapping(nameAtt.getValue(), mapping);
			variables.addMapping(s);
		}
	}

	private void readMessageExchanges(Element root, String messageExchangeTag, File f) {
		// The following for-loop does not take the root-element itself into account
		if (root.getName().equals(messageExchangeTag)) {
			processMessageExchange(root, f);
		}
		for (Element mex : root.getDescendants(new ElementFilter(messageExchangeTag, Config.get().getGeneratorSpace()))) {
			processMessageExchange(mex, f);
		}
	}

	private void processMessageExchange(Element mex, File f) {
		Attribute nameAtt = mex.getAttribute("name");
		if (nameAtt != null && !nameAtt.getValue().isEmpty()) {
			Element contentElement = new Element("rootExchange");
			boolean contentFound = false;
			for (Element e : mex.getChildren()) {
				contentElement.addContent(e.clone());
				contentFound = true;
			}
			if (contentFound) {
				String name = nameAtt.getValue();
				MessageExchangeVariable mev = new MessageExchangeVariable(name, contentElement);
				variables.addMessageExchange(mev);
			}
		} else {
			Config.get().out().messageExchangeWithoutName(f);
		}
	}

	// Map VariableInstances to their Variable's name.
	private void readDataVariables(Element root, String complexVariableTag, File f) {
		for (Element complex : root.getDescendants(new ElementFilter(complexVariableTag, Config.get().getGeneratorSpace()))) {
			Attribute nameAtt = complex.getAttribute("name");
			if (nameAtt != null && !nameAtt.getValue().isEmpty()) {
				String variableName = nameAtt.getValue();
				for (Element e : complex.getChildren()) {
					processInstance(e, variableName, f);
				}
			} else {
				Config.get().out().variableWithoutName(f);
			}
		}
	}

	private void processInstance(Element e, String variableName, File f) {
		Attribute name = e.getAttribute("name");
		if (name != null && !name.getValue().isEmpty()) {
			DataVariableInstance cvi = new DataVariableInstance(variableName, name.getValue(), e);
			variables.put(cvi);
		} else {
			Config.get().out().variableInstanceWithoutName(variableName, f);
		}
	}

	private void readBaseFile(File f) throws JDOMException, IOException {
		SAXBuilder jdomBuilder = new SAXBuilder();
		Document jdomDocument;
		try {
			jdomDocument = jdomBuilder.build(f);
			baseRoot = jdomDocument.getRootElement();
			// TODO Check completeness
		} catch (Exception e) {
			Config.get().out().noBaseFile(baseFileName);
			throw e;
		}
	}

	@Override
	public Element getSkeletalStructure() {
		return baseRoot;
	}

	@Override
	public VariableLibrary getVariables() {
		return variables;
	}
}
