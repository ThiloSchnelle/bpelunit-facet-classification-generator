package net.bpelunit.suitegenerator.statistics;

import java.text.NumberFormat;
import java.util.Set;

public class TestCoverage {

	private int instancesUsed;
	private int instancesAvailable;
	private Set<Selection> notUsed;

	public TestCoverage(int instancesUsed, int instancesAvailable, Set<Selection> notUsed) {
		this.instancesUsed = instancesUsed;
		this.instancesAvailable = instancesAvailable;
		this.notUsed = notUsed;
	}

	public float getPercentage() {
		return ((float) instancesUsed) / instancesAvailable * 100;
	}

	public String getFormattedPercentage() {
		return NumberFormat.getIntegerInstance().format(getPercentage());
	}

	public int getInstancesUsed() {
		return instancesUsed;
	}

	public int getInstancesAvailable() {
		return instancesAvailable;
	}

	public String getUnusedNames() {
		String res = "";
		for (Selection inst : notUsed) {
			if (!res.isEmpty()) {
				res += ", ";
			}
			res += inst.getSelection().getCompleteName();
		}
		return res;
	}

	@Override
	public String toString() {
		return "TestCoverage: " + getInstancesUsed() + "/" + getInstancesAvailable() + " = " + getFormattedPercentage() + "%."
				+ (notUsed.isEmpty() ? "" : " The following leaves are not used: [" + getUnusedNames() + "]");
	}

}
