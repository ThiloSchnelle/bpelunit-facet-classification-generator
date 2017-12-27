package net.bpelunit.suitegenerator.reader;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationTree;
import net.bpelunit.suitegenerator.datastructures.classification.IClassificationElement;

/**
 * Used to keep track what column has already had a Variable defined in it and which already had a leave of the ClassificationTree.
 *
 */
public class XLSColumn {

	private IClassificationElement element;
	private boolean isFilledSpecifically = false;

	public XLSColumn(ClassificationTree tree) {
		element = tree;
	}

	public void setClassificationElement(IClassificationElement element) {
		this.element = element;
	}

	public IClassificationElement getElement() {
		return element;
	}

	/**
	 * Whether in this column was a text, that caused the contained element to be created. If so do only overwrite this element with elements that were created
	 * because this column contained another text
	 * 
	 * @return
	 */
	public boolean isFilledSpecifically() {
		return isFilledSpecifically;
	}

	public void markFlagIsFilledSpecifically() {
		isFilledSpecifically = true;
	}

	@Override
	public String toString() {
		return "[" + element.getName() + " " + isFilledSpecifically() + "]";
	}

	public void flagFault() {
		element.setFault(true);
	}
	
	public boolean isFault() {
		return element.isFault();
	}

}
