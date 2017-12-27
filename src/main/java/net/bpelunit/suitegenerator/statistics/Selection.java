package net.bpelunit.suitegenerator.statistics;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.datastructures.classification.IClassificationElement;
import net.bpelunit.suitegenerator.datastructures.conditions.IOperand;
import net.bpelunit.suitegenerator.util.Copyable;

public class Selection implements Copyable<Selection>, IOperand {

	private ClassificationVariableSelection sel;
	private ClassificationVariable rootElement;
	private int numUsages = 0;

	public Selection(ClassificationVariableSelection sel) {
		this.sel = sel;
		IClassificationElement e = sel.getParent();
		while (!e.isRootOfClassification()) {
			e = e.getParent();
		}
		rootElement = (ClassificationVariable) e;
	}

	public void countUp() {
		numUsages++;
	}

	public ClassificationVariableSelection getSelection() {
		return sel;
	}

	public ClassificationVariable getRootElement() {
		return rootElement;
	}

	public int getNumUsages() {
		return numUsages;
	}

	@Override
	public int hashCode() {
		return sel.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Selection ? ((Selection) obj).sel.equals(this.sel) : false;
	}

	@Override
	public Selection copy() {
		Selection neu = new Selection(sel);
		neu.numUsages = this.numUsages;
		return neu;
	}

	@Override
	public String toString() {
		return sel.getCompleteName() + ": " + numUsages;
	}

	@Override
	public String getOpName() {
		return sel.getCompleteName();
	}

	public int compareTo(Selection sel) {
		return this.getSelection().getName().compareTo(sel.getSelection().getName());
	}

}
