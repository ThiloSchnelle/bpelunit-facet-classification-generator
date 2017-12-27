package net.bpelunit.suitegenerator.output;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.jdom2.Element;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationTree;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.datastructures.classification.IClassificationElement;
import net.bpelunit.suitegenerator.datastructures.testcases.TestCase;
import net.bpelunit.suitegenerator.datastructures.variables.DataVariableInstance;
import net.bpelunit.suitegenerator.recommendation.Recommendation;
import net.bpelunit.suitegenerator.statistics.IStatistics;
import net.bpelunit.suitegenerator.statistics.Selection;
import net.bpelunit.suitegenerator.util.XMLElementOutput;

/**
 * An IOutputter writing everything with Syso.
 *
 */
public class ConsoleOut implements IOutput {

	@Override
	public void partnerTrackWithoutName(Element e) {
		System.out.println("There is no name given in partnerTrack " + XMLElementOutput.out(e));
	}

	@Override
	public void noPartnerTrackWithSlot(String slotName) {
		System.out.println("There is no partnerTrack with the selected slot " + slotName);
	}

	@Override
	public void noMessageExchangeWithName(String messageName) {
		System.out.println("There is no messageExchange with name: " + messageName);
	}

	@Override
	public void twoErrorElements(TestCase testCase, ClassificationVariableSelection faultElement,
			ClassificationVariableSelection inst) {
		System.out.println("In TestCase [" + testCase.getName() + "] there are at least two fault-elements selected: ["
				+ faultElement.getCompleteName() + "] and [" + inst.getCompleteName() + "]");
	}

	@Override
	public void moreThanOneSelectionInAnIndependentTree(TestCase testCase, IClassificationElement e) {
		System.out.println("For independent tree with root element [" + e.getName()
				+ "] there is more than one selection made in TestCase [" + testCase.getName() + "]");
	}

	@Override
	public void noBaseFile(String baseFileName) {
		System.out.println("There has to be a file called " + baseFileName
				+ " in the same folder as the ClassificationTable containing the base xml of the TestSuite.");
	}

	@Override
	public void noInstanceForVariableSlotInMessage(String key, String name) {
		System.out.println("There is no instance for variable " + key + " in message " + name);
	}

	@Override
	public void variableSelectionWithoutName(String selectionName) {
		System.out.println("There is a variable-selection without a variableName in " + selectionName);
	}

	@Override
	public void variableSelectionWithoutInstanceName(String selectionName) {
		System.out.println("There is a variable-selection without a variableInstanceName in " + selectionName);
	}

	@Override
	public void messageSelectionWithoutName(String selectionName) {
		System.out.println("There is a message-selection without a messageName in " + selectionName);
	}

	@Override
	public void messageSelectionWithoutSlot(String selectionName, String messageName) {
		System.out.println("The message-selection [" + messageName + "] misses a messageSlot in " + selectionName);
	}

	@Override
	public void noInstanceForSelectedVariable(String slotName, String instanceName) {
		System.out.println(
				"For the variable [" + slotName + "] there is no instance with name [" + instanceName + "] available");
	}

	@Override
	public void instanceNotUsed(DataVariableInstance d) {
		System.out.println("=====" + d.getVariableName() + ":" + d.getInstanceName() + ": IS NOT USED! ============");
	}

	@Override
	public void noPossibilityToUse(String instName, String varSlotName) {
		System.out.println(
				"There is no possibility to use instance [" + instName + "] in variable slot [" + varSlotName + "]");
	}

	@Override
	public void noMappingFor(ClassificationVariableSelection var) {
		System.out.println(
				"For Classification [" + var.getCompleteName() + "] there is no Mapping. Did you misspell the names?");
	}

	@Override
	public void messageExchangeWithoutName(File f) {
		System.out.println("There is a MessageExchange without name in file: " + f.getName());
	}

	@Override
	public void varSlotWithoutName(String name) {
		System.out.println("There is a VariableSlot without name in MessageExchange: " + name);
	}

	@Override
	public void variableWithoutName(File f) {
		System.out.println("There is a VariableDefinition without name in file: " + f.getName());
	}

	@Override
	public void variableInstanceWithoutName(String variableName, File f) {
		System.out.println(
				"There is a VariableInstance for Var [" + variableName + "] without name in file: " + f.getName());
	}

	@Override
	public void thereWasNoPossibilityToSatisfyAllPairs() {
		System.out.println("There was no possibility to satisfy all pairs");
	}

	@Override
	public void noPossibilityToAddParameter(List<Selection> sels, ClassificationVariable param) {
		System.out.println("There was no possibility to add a parameter of " + param.getName()
				+ " to the selections already chosen: " + sels.toString());
	}

	@Override
	public void printRecommendation(Collection<Recommendation> recommended) {
		System.out.println("Recommendations:");
		for (Recommendation r : recommended) {
			System.out.println(r);
		}
	}

	@Override
	public void printStatistics(IStatistics stat) {
		System.out.println(stat.getTestCoverage().toString());
		System.out.println(stat.getFaultCaseStats());
	}

	@Override
	public void printClassificationTree(ClassificationTree tree) {
		System.out.println(tree);
	}

}
