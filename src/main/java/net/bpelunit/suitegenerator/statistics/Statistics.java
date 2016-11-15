package net.bpelunit.suitegenerator.statistics;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.datastructures.classification.IClassificationElement;
import net.bpelunit.suitegenerator.datastructures.testcases.TestCase;
import net.bpelunit.suitegenerator.datastructures.variables.DataVariableInstance;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;

public class Statistics implements IStatistics {

	private Set<Selection> selectionsUsed = new HashSet<>();
	private Collection<Selection> allSelections = new HashSet<>();
	private Set<Selection> notUsed;
	private Map<ClassificationVariable, List<Selection>> rootVariablesToSelections = new HashMap<>();

	private List<TestCase> testCases = new LinkedList<>();

	private FaultCaseStatistics error = new FaultCaseStatistics(this);
	private TestCoverage cover;

	private List<DataVariableInstance> unusedDataInstances = new LinkedList<>();

	@Override
	public void classificationInstanceUsed(String testCase, ClassificationVariableSelection inst) {
		Selection s = getSelectionFor(inst);
		s.countUp();
		selectionsUsed.add(s);
	}

	private TestCoverage calcTestCoverage() {
		notUsed = new HashSet<>();
		for (Selection cvi : allSelections) {
			if (!selectionsUsed.contains(cvi)) {
				notUsed.add(cvi);
			}
		}
		return new TestCoverage(selectionsUsed.size(), allSelections.size(), notUsed);
	}

	private void calcInstancesNotUsed(VariableLibrary variables) {
		for (DataVariableInstance d : variables.getDataVarInstances()) {
			if (d.getNumberUsages() == 0) {
				unusedDataInstances.add(d);
			}
		}
	}

	@Override
	public void update(Collection<ClassificationVariableSelection> allSelections, VariableLibrary variables) {
		mapToRootVariables(allSelections);
		cover = calcTestCoverage();
		calcInstancesNotUsed(variables);
	}

	private void mapToRootVariables(Collection<ClassificationVariableSelection> allSelections) {
		for (ClassificationVariableSelection csv : allSelections) {
			Selection s = getSelectionFor(csv);
			IClassificationElement e = csv.getParent();
			while (!e.isRootOfClassification()) {
				e = e.getParent();
			}
			ClassificationVariable root = (ClassificationVariable) e;
			List<Selection> list = rootVariablesToSelections.get(root);
			if (list == null) {
				list = new LinkedList<>();
				rootVariablesToSelections.put(root, list);
			}
			list.add(s);
		}
	}

	private Selection getSelectionFor(ClassificationVariableSelection csv) {
		Selection s = new Selection(csv);
		for (Selection old : allSelections) {
			if (old.equals(s)) {
				return old;
			}
		}
		allSelections.add(s);
		return s;
	}

	@Override
	public TestCoverage getTestCoverage() {
		return cover;
	}

	@Override
	public int getNumTestCases() {
		return testCases.size();
	}

	@Override
	public FaultCaseStatistics getFaultCaseStats() {
		return error;
	}

	@Override
	public void addTest(TestCase test) {
		testCases.add(test);
		if (test.isErrorCase()) {
			error.addErrorCase(test);
		}
		for (ClassificationVariableSelection cv : test.getSelections()) {
			classificationInstanceUsed(test.getName(), cv);
		}
	}

	@Override
	public Collection<DataVariableInstance> getUnusedDataInstances() {
		return new LinkedList<>(unusedDataInstances);
	}

	@Override
	public Collection<Selection> getAllSelections() {
		return allSelections;
	}

	@Override
	public Collection<Selection> getUnusedSelections() {
		return notUsed;
	}

	@Override
	public Collection<Selection> getUsedSelections() {
		return selectionsUsed;
	}

	@Override
	public Map<ClassificationVariable, List<Selection>> getRootVariables() {
		return rootVariablesToSelections;
	}
}
