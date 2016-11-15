package net.bpelunit.suitegenerator.datastructures.classification;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.bpelunit.suitegenerator.datastructures.testcases.TestCase;
import net.bpelunit.suitegenerator.recommendation.permut.ICondition;

public class Classification {

	private List<TestCase> testCases = new LinkedList<>();
	private ClassificationTree tree;
	private ICondition forbidden;
	private Collection<ClassificationVariableSelection> leaves;

	public List<TestCase> getTestCases() {
		return testCases;
	}

	public void addTestCase(TestCase testCase) {
		this.testCases.add(testCase);
	}

	public ClassificationTree getTree() {
		return tree;
	}

	public void setTree(ClassificationTree tree) {
		this.tree = tree;
	}

	public ICondition getForbidden() {
		return forbidden;
	}

	public void setForbidden(ICondition forbidden) {
		this.forbidden = forbidden;
	}

	/**
	 * @return Everything that maps to a Mapping
	 */
	public Collection<ClassificationVariableSelection> getAllClassificationTreeLeaves() {
		return leaves;
	}

	public void setLeaves(Collection<ClassificationVariableSelection> leaves) {
		this.leaves = leaves;
	}

}
