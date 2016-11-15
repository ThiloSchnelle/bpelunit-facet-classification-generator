package net.bpelunit.suitegenerator.datastructures.testcases;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.datastructures.classification.IClassificationElement;

/**
 * Knows which leaves of the ClassificationTree are checked.
 *
 */
public class TestCase {

	private List<ClassificationVariableSelection> activatedVariables = new LinkedList<>();
	private Set<ClassificationVariable> activatedRootVariables = new HashSet<>();
	private String name;
	private ClassificationVariableSelection faultElement = null;
	private boolean moreThanOneFaultElement;

	public TestCase(String name) {
		this.name = name;
	}

	public void markAsNecessary(ClassificationVariableSelection inst) {
		activatedVariables.add(inst);
		IClassificationElement e = inst.getParent();
		while (!e.isRootOfClassification()) {
			e = e.getParent();
		}
		if (!activatedRootVariables.add((ClassificationVariable) e)) {
			Config.get().out().moreThanOneSelectionInAnIndependentTree(this, e);
		}
		if(inst.isFault()) {
			if(isErrorCase()) {
				moreThanOneFaultElement = true;
				Config.get().out().twoErrorElements(this, faultElement, inst);
			}
			faultElement = inst;
		}
	}

	public String getName() {
		return name;
	}

	public List<ClassificationVariableSelection> getSelections() {
		return activatedVariables;
	}
	
	public Collection<ClassificationVariable> getActivatedRootElements() {
		return activatedRootVariables;
	}
	
	public ClassificationVariableSelection getFaultElement() {
		return faultElement;
	}
	
	public boolean isErrorCase() {
		return faultElement != null;
	}
	
	public boolean hasMoreThanOneFaultElement() {
		return moreThanOneFaultElement;
	}

	@Override
	public String toString() {
		return "TestCase [" + name + "] with Selections " + activatedVariables;
	}
}
