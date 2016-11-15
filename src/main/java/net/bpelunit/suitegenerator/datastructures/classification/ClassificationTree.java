package net.bpelunit.suitegenerator.datastructures.classification;

import java.util.LinkedList;
import java.util.List;

import net.bpelunit.suitegenerator.datastructures.variables.Mapping;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;
import net.bpelunit.suitegenerator.reader.ICodeFragmentReader;

public class ClassificationTree extends BaseClassificationElement {

	/**
	 * Those children that do not have Codefragments attached although those are necessary
	 */
	private List<String> unsatisfiedChildren = null;

	public ClassificationTree() {
		super("ClassificationTree");
	}

	@Override
	public boolean hasParent() {
		return false;
	}

	@Override
	public boolean isSatisfied() {
		checkSatisfaction();
		return unsatisfiedChildren.size() == 0;
	}

	public List<String> getUnsatisfiedChildren() {
		if (unsatisfiedChildren == null) {
			checkSatisfaction();
		}
		return unsatisfiedChildren;
	}

	private void checkSatisfaction() {
		unsatisfiedChildren = new LinkedList<>();
		traverseSatisfaction(unsatisfiedChildren, this);
	}

	private void traverseSatisfaction(List<String> results, IClassificationElement e) {
		for (IClassificationElement child : e.getChildren()) {
			if (!child.isSatisfied()) {
				results.add(child.getName());
			}
			if (child.hasChildren()) {
				traverseSatisfaction(results, child);
			}
		}
	}

	public void combineWithFragments(ICodeFragmentReader reader) {
		VariableLibrary selections = reader.getVariables();
		traverseEnrichment(selections, this);
	}

	private void traverseEnrichment(VariableLibrary selections, IClassificationElement e) {
		for (IClassificationElement child : e.getChildren()) {
			// TODO VisitorPattern
			if (child instanceof ClassificationVariableSelection) {
				fillVariableInstance((ClassificationVariableSelection) child, selections.getSelection(((ClassificationVariableSelection) child).getCompleteName()));
			}
			traverseEnrichment(selections, child);
		}
	}

	private void fillVariableInstance(ClassificationVariableSelection child, Mapping selection) {
		child.setSelection(selection);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ClassificationTree:");
		traverseToString(sb, 1, this);
		return sb.toString();
	}

	private void traverseToString(StringBuilder sb, int i, IClassificationElement e) {
		for (IClassificationElement child : e.getChildren()) {
			sb.append("\n");
			for (int x = 0; x < i; x++) {
				sb.append("  ");
			}
			sb.append(child.getName() + " " + child.isSatisfied() + (child.isFault() ? " fault" : ""));
			traverseToString(sb, i + 1, child);
		}
	}

}
