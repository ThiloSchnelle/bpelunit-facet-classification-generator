package net.bpelunit.suitegenerator.datastructures.classification;

import java.util.Comparator;

public class ClassificationVariableNameComparator implements Comparator<ClassificationVariable> {

	@Override
	public int compare(ClassificationVariable v1, ClassificationVariable v2) {
		return v1.getName().compareTo(v2.getName());
	}

}
