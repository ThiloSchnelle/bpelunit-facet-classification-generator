package net.bpelunit.suitegenerator.suitebuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;

import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.datastructures.conditions.ConditionBundle;
import net.bpelunit.suitegenerator.datastructures.conditions.ICondition;
import net.bpelunit.suitegenerator.datastructures.testcases.TestCase;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;
import net.bpelunit.suitegenerator.reader.ICodeFragmentReader;
import net.bpelunit.suitegenerator.recommendation.IRecommender;
import net.bpelunit.suitegenerator.recommendation.Recommendation;
import net.bpelunit.suitegenerator.statistics.Selection;
import net.bpelunit.suitegenerator.util.XMLElementOutput;

public class SuiteBuilder {

	private Element currentRoot;
	private Element currentTestCasesElement;
	private Namespace nsBPELUnit;
	private VariableLibrary data;
	private File destFolder;

	public void buildSuite(Element baseFile, Classification classification, File destFolder, ICodeFragmentReader fragment, boolean ignoreUserTestCases) {

		Collection<TestCase> testCases = classification.getTestCases();

		currentRoot = baseFile.clone();
		currentTestCasesElement = currentRoot.getChild("testCases", currentRoot.getNamespace());
		nsBPELUnit = currentRoot.getNamespace();
		data = fragment.getVariables();
		this.destFolder = destFolder;
		if(!ignoreUserTestCases) {
			for (TestCase t : testCases) {
	//			System.out.println("Test Case " + t.getName() + ":");
				validateTestCase(t, classification.getForbidden());
				attachNewTestCase(t, currentTestCasesElement, nsBPELUnit, data);
			}
		}
	}

	private void validateTestCase(TestCase t, ICondition forbidden) {
		List<Selection> selections = new ArrayList<>(t.getSelections().size());
		for(ClassificationVariableSelection s : t.getSelections()) {
			selections.add(new Selection(s));
		}
		
		if(forbidden.evaluate(selections)) {
			ConditionBundle cb = (ConditionBundle)forbidden;
			
			System.err.println("Test Case " + t.getName() + " violates the specified forbidden constraints!!!");
			System.err.println("- Selection: " + selections);
			
			for(ICondition c : cb.getConditions()) {
				if(c.evaluate(selections)) {
					System.err.println("- Unsatisfied: " + c.toString());
				}
			}
		}
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

	public void addRecommendations(IRecommender recommender) {
		int num = 1;
		for (Recommendation r : recommender.getRecommendations()) {
			List<String> classificationNames = new ArrayList<>();
			for (ClassificationVariableSelection cvs : r.getRecommendedSelections()) {
				classificationNames.add(cvs.getCompleteName());
			}
			Collections.sort(classificationNames);
			String testCaseName = "TC" + (num++) + " " + String.join("|", classificationNames.toArray(new String[classificationNames.size()]));
//			String testCaseName = String.join("|", classificationNames.toArray(new String[classificationNames.size()]));
			
			TestCase t = new TestCase(testCaseName);
			for (ClassificationVariableSelection cvs : r.getRecommendedSelections()) {
				t.markAsNecessary(cvs);
			}
			attachNewTestCase(t, currentTestCasesElement, nsBPELUnit, data);
		}
	}

	public void saveSuite(String suiteFileName) {
		try {
			Element nameElement = currentRoot.getChild("name", nsBPELUnit);
			nameElement.setText(new File(suiteFileName).getName());
		} catch(Exception e) {
			// ignore, just cannot update the suite's name
		}
		writeDocument(currentRoot, new File(destFolder, suiteFileName));
	}

}
