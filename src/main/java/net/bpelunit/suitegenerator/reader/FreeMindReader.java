package net.bpelunit.suitegenerator.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationTree;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.datastructures.classification.IClassificationElement;
import net.bpelunit.suitegenerator.datastructures.conditions.ConditionBundle;
import net.bpelunit.suitegenerator.datastructures.conditions.ConditionParser;
import net.bpelunit.suitegenerator.datastructures.conditions.ICondition;
import net.bpelunit.suitegenerator.datastructures.testcases.TestCase;
import net.bpelunit.suitegenerator.statistics.IStatistics;

public class FreeMindReader implements IClassificationReader {

	static final class Node {
		
		public Node(String id, String name, Node parent) {
			this.id = id;
			this.text = name;
			this.parent = parent;
		}
		
		Node parent;
		String id;
		String text;
		List<String> referencesId = new ArrayList<>();
		List<Node> children = new ArrayList<>();
	}
	
	class FreeMindHandler extends DefaultHandler {
		
		private Node currentNode;
		private Node documentNode;
		private Map<String, ClassificationVariableSelection> nodesById = new HashMap<>();
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			if("node".equals(localName)) {
				Node parent = currentNode;
				
				String id = attributes.getValue("ID");
				String name = attributes.getValue("TEXT");
				
				Node n = new Node(id, name, parent);
				if(parent != null) {
					parent.children.add(n);
				}
				currentNode = n;
				
				if(documentNode == null) {
					documentNode = n;
				}
			} else if("arrowlink".equals(localName)) {
				String destinationId = attributes.getValue("DESTINATION");
				currentNode.referencesId.add(destinationId);
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if("node".equals(localName) && currentNode != null) {
				currentNode = currentNode.parent;
			}
		}
		
		@Override
		public void endDocument() throws SAXException {
			Node testCasesNode = null;
			for(Node n : documentNode.children) {
				if("Forbidden Constraints".equals(n.text)) {
					resolveForbiddenConstraints(n);
				} if("Test Cases".equals(n.text)) {
					testCasesNode = n;
				} else {
					buildSubTree(n, true, tree);
				}
			}
			
			if(testCasesNode != null) {
				resolveTestCases(testCasesNode);
			}
		}

		private void resolveTestCases(Node n) {
			if(n.children.size() == 0) {
				TestCase test = new TestCase(n.text);
				for(String id : n.referencesId) {
					test.markAsNecessary(nodesById.get(id));
				}
				classification.addTestCase(test);
			} else {
				for(Node c : n.children) {
					resolveTestCases(c);
				}
			}
		}

		private void resolveForbiddenConstraints(Node n) {
			if(n.children.size() == 0) {
				ICondition cond = new ConditionParser().parse(n.text);
				conditions.addCondition(cond);
			} else {
				for(Node c : n.children) {
					resolveForbiddenConstraints(c);
				}
			}
		}

		private void buildSubTree(Node n, boolean isRoot, IClassificationElement parent) {
			if(n.children.size() > 0) {
				ClassificationVariable v = new ClassificationVariable(n.text, isRoot);
				if(parent != null) {
					parent.addChild(v);
				}
				
				for(Node c : n.children) {
					buildSubTree(c, false, v);
				}
			} else {
				ClassificationVariableSelection v = new ClassificationVariableSelection(n.text);
				parent.addChild(v);
				leafs.add(v);
				nodesById.put(n.id, v);
			}
		}
	}
	
	private File file;
	private ConditionBundle conditions = new ConditionBundle();
	private Classification classification = new Classification();
	private ClassificationTree tree = new ClassificationTree();
	private List<ClassificationVariableSelection> leafs = new ArrayList<>();

	public FreeMindReader(File file) {
		this.file = file;
		
	}
	
	@Override
	public Classification getClassification() {
		return classification;
	}

	@Override
	public void readAndEnrich(ICodeFragmentReader fragmentReader, IStatistics stat) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			spf.newSAXParser().parse(new FileInputStream(file), new FreeMindHandler());
			classification.setTree(tree);
			classification.setLeaves(leafs);
			classification.setForbidden(conditions);
			tree.combineWithFragments(fragmentReader);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

}
