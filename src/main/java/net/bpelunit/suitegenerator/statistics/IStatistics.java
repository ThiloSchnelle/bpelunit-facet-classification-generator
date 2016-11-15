package net.bpelunit.suitegenerator.statistics;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.datastructures.testcases.TestCase;
import net.bpelunit.suitegenerator.datastructures.variables.DataVariableInstance;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;

/**
 * Change this so it can be used in Recommender as an aggregation for data
 *
 */
public interface IStatistics {

	/**
	 * For every instance used in a TestCase
	 * 
	 * @param inst
	 */
	public void classificationInstanceUsed(String testCase, ClassificationVariableSelection inst);

	public void update(Collection<ClassificationVariableSelection> allClassificationTreeLeaves, VariableLibrary variables);

	/**
	 * @param allInstances
	 *            All possible ClassificationInstances
	 * @return
	 */
	public TestCoverage getTestCoverage();

	public Collection<Selection> getUsedSelections();

	public Collection<Selection> getAllSelections();

	public Collection<Selection> getUnusedSelections();

	public int getNumTestCases();

	public FaultCaseStatistics getFaultCaseStats();

	public void addTest(TestCase test);

	public Collection<DataVariableInstance> getUnusedDataInstances();

	public Map<ClassificationVariable, List<Selection>> getRootVariables();

}
