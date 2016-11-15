package net.bpelunit.suitegenerator.suitebuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.jdom2.Element;
import org.jdom2.Namespace;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.datastructures.testcases.TestCase;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;
import net.bpelunit.suitegenerator.reader.ICodeFragmentReader;
import net.bpelunit.suitegenerator.recommendation.Recommendation;
import net.bpelunit.suitegenerator.recommendation.Recommender;
import net.bpelunit.suitegenerator.util.XMLElementOutput;

public class SuiteBuilder {

	Element root;
	Element testCaseElement;
	Namespace tes;
	VariableLibrary data;
	File destFolder;

	public void buildSuite(Element baseFile, Classification classification, File destFolder,
			ICodeFragmentReader fragment) {

		Collection<TestCase> testCases = classification.getTestCases();

		// Permutation p = new Permutation(tree);
		// System.out.println(p.getNumPermutations());

		root = baseFile.clone();
		testCaseElement = root.getChild("testCases", root.getNamespace());
		tes = root.getNamespace();
		data = fragment.getVariables();
		this.destFolder = destFolder;
		for (TestCase t : testCases) {
			attachNewTestCase(t, testCaseElement, tes, data);
		}
		writeDocument(root, new File(destFolder, Config.get().getOutputName()));
	}

	private void attachNewTestCase(TestCase t, Element testCaseElement, Namespace tes, VariableLibrary data) {
		Element tc = buildTestCaseElement(t, tes);
		testCaseElement.addContent(tc);
		TestCaseBuilder.buildCase(t, tes, tc, data);
	}

	private Element buildTestCaseElement(TestCase t, Namespace tes) {
		Element tc = new Element("testCase", tes);
		tc.setAttribute("name", t.getName());
		tc.setAttribute("basedOn", "");
		tc.setAttribute("abstract", "false");
		tc.setAttribute("vary", "false");
		return tc;
	}

	private void writeDocument(Element root, File f) {
		try (PrintWriter pw = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			pw.write(XMLElementOutput.out(root));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addRecommendations(Recommender recommender) {
		int num = 1;
		for (Recommendation r : recommender.getRecommendations()) {
			TestCase t = new TestCase("Recommended " + num++);
			for (ClassificationVariableSelection cvs : r.getRecommendedSelections()) {
				t.markAsNecessary(cvs);
			}
			attachNewTestCase(t, testCaseElement, tes, data);
		}
		writeDocument(root, new File(destFolder, Config.get().getOutputName()));
	}

}
