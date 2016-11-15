package net.bpelunit.suitegenerator.statistics;

import java.util.LinkedList;
import java.util.List;

import net.bpelunit.suitegenerator.datastructures.testcases.TestCase;

public class FaultCaseStatistics {

	private List<TestCase> faultCases = new LinkedList<>();
	private List<TestCase> moreThanOneFaultCases = new LinkedList<>();

	private IStatistics stat;

	public FaultCaseStatistics(IStatistics stat) {
		this.stat = stat;
	}

	public void addErrorCase(TestCase test) {
		faultCases.add(test);
		if (test.hasMoreThanOneFaultElement()) {
			moreThanOneFaultCases.add(test);
		}
	}

	public String listErrorCases() {
		return faultCases.toString();
	}

	public String listSeveralFaultCases() {
		return moreThanOneFaultCases.toString();
	}

	@Override
	public String toString() {
		String res = faultCases.size() + "/" + stat.getNumTestCases() + " TestCases contain(s) a fault element. " + moreThanOneFaultCases.size() + " case(s) conatin(s) more than one FaultElement.";
		return res;
	}

}
