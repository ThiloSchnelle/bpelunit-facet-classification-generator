package net.bpelunit.suitegenerator.datastructures.classification;

/**
 * All Nodes that are not leaves or the root of the classification are Variables.
 *
 */
public class ClassificationVariable extends BaseClassificationElement {

	protected boolean root = false;

	public ClassificationVariable(String name) {
		super(name);
	}

	public ClassificationVariable(String name, boolean root) {
		super(name);
		this.root = root;
	}

	@Override
	public boolean isRootOfClassification() {
		return root;
	}

	@Override
	public boolean isSatisfied() {
		return true;
	}

}
