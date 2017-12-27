package net.bpelunit.suitegenerator.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationTree;
import net.bpelunit.suitegenerator.datastructures.classification.IClassificationElement;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;
import net.bpelunit.suitegenerator.reader.IClassificationReader;
import net.bpelunit.suitegenerator.reader.ICodeFragmentReader;
import net.bpelunit.suitegenerator.reader.ReaderFactory;

public class TreeSelectionGenerator {

	
	public void generate(File treeSelectionFile, ClassificationTree o) throws IOException {
		try(FileWriter out = new FileWriter(treeSelectionFile)) {
			out.write("<csg:mappings xmlns:csg=\"bpelunit.net/classificationTreeSuiteGenerator.xsd\">\n");

			List<IClassificationElement> allNodes = new ArrayList<>();
			List<IClassificationElement> nodesToProcess = new ArrayList<>(o.getChildren());
			
			IClassificationElement nodeToProcess = null;
			while(nodesToProcess.size() > 0) {
				nodeToProcess = nodesToProcess.remove(0);
				allNodes.add(nodeToProcess);
				List<IClassificationElement> children = nodeToProcess.getChildren();
				for(int i = children.size() - 1; i >= 0; i--) {
					IClassificationElement n = children.get(i);
					nodesToProcess.add(0, n);
				}
			}
			
			for(IClassificationElement n : allNodes) {
				out.write("  <csg:mapping name=\"");
				out.write(getClassificationPathName(n));
				out.write("\">\n");
				if(allNodes.contains(n.getParent())) {
					out.write("      <csg:extends>");
					out.write(getClassificationPathName(n.getParent()));
					out.write("</csg:extends>\n");
				}
				out.write("  </csg:mapping>\n\n");
			}
			
			out.write("</csg:mappings>\n");
		}
	}
	
	private String getClassificationPathName(IClassificationElement n) {
		StringBuilder sb = new StringBuilder();
		
		while(n.getParent() != null) {
			sb.insert(0, n.getName()).insert(0, ":");
			n = n.getParent();
		}
		sb.delete(0, 1);
		
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		IClassificationReader classificationReader = ReaderFactory.findReader(new File(args[0]));

		classificationReader.readAndEnrich(new ICodeFragmentReader() {
			
			@Override
			public VariableLibrary getVariables() {
				return new VariableLibrary();
			}
			
			@Override
			public Element getSkeletalStructure() {
				return null;
			}
		}, null);
		ClassificationTree tree = classificationReader.getClassification().getTree();
		
		new TreeSelectionGenerator().generate(new File(new File(args[0]).getParentFile(), "A_TreeSelection.xml"), tree);
	}
}
