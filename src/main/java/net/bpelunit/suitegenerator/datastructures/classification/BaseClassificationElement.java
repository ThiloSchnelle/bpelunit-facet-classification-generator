package net.bpelunit.suitegenerator.datastructures.classification;

import java.util.LinkedList;
import java.util.List;

/**
 * Convenience baseclass.
 *
 */
public abstract class BaseClassificationElement implements IClassificationElement {

	protected IClassificationElement parent;
	protected List<IClassificationElement> children = new LinkedList<>();
	protected String name;
	protected boolean isFault = false;

	public BaseClassificationElement(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasParent() {
		return true;
	}

	@Override
	public IClassificationElement getParent() {
		return parent;
	}

	@Override
	public boolean hasChildren() {
		return children.size() > 0;
	}

	@Override
	public List<IClassificationElement> getChildren() {
		return children;
	}

	@Override
	public void addChild(IClassificationElement child) {
		child.setParent(this);
		children.add(child);
	}

	@Override
	public void setParent(IClassificationElement parent) {
		this.parent = parent;
	}

	@Override
	public boolean isRootOfClassification() {
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean isFault() {
		return isFault;
	}
	
	@Override
	public void setFault(boolean fault) {
		this.isFault = fault;
	}

}
