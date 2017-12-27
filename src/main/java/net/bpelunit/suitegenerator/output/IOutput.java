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

/**
 * Allows outputs to be delegated to a GUI in future versions.
 *
 */
public interface IOutput {

	public void partnerTrackWithoutName(Element e);

	public void noPartnerTrackWithSlot(String slotName);

	public void noMessageExchangeWithName(String messageName);

	public void twoErrorElements(TestCase testCase, ClassificationVariableSelection faultElement,
			ClassificationVariableSelection inst);

	public void moreThanOneSelectionInAnIndependentTree(TestCase testCase, IClassificationElement e);

	public void noBaseFile(String baseFileName);

	public void noInstanceForVariableSlotInMessage(String slotName, String messageName);

	public void variableSelectionWithoutName(String selectionName);

	public void variableSelectionWithoutInstanceName(String selectionName);

	public void messageSelectionWithoutName(String selectionName);

	public void messageSelectionWithoutSlot(String selectionName, String messageName);

	public void noInstanceForSelectedVariable(String slotName, String instanceName);

	public void instanceNotUsed(DataVariableInstance d);

	public void noPossibilityToUse(String instName, String varSlotName);

	public void noMappingFor(ClassificationVariableSelection var);

	public void messageExchangeWithoutName(File f);

	public void varSlotWithoutName(String name);

	public void variableWithoutName(File f);

	public void variableInstanceWithoutName(String variableName, File f);

	public void thereWasNoPossibilityToSatisfyAllPairs();

	public void noPossibilityToAddParameter(List<Selection> sels, ClassificationVariable param);

	public void printRecommendation(Collection<Recommendation> recommended);

	public void printStatistics(IStatistics stat);

	public void printClassificationTree(ClassificationTree tree);

}
