package net.bpelunit.suitegenerator.datastructures.classification;

import java.util.List;

/**
 * Elements from which the ClassificationTree is built. A ClassificationTree-Object should be the root and it's children roots of independent classes of the
 * classification.
 *
 */
public interface IClassificationElement {

	public String getName();

	/**
	 * Is there a codefragment given for this element OR does it need none?
	 * 
	 * @return
	 */
	public boolean isSatisfied();

	public boolean hasParent();

	public boolean hasChildren();

	public IClassificationElement getParent();

	public List<IClassificationElement> getChildren();

	public void addChild(IClassificationElement child);

	public void setParent(IClassificationElement parent);

	/**
	 * Root of Classification describes the top level element of an independent class of the classification. Their parent element should be a
	 * ClassificationTree-Object.
	 * 
	 * @return
	 */
	public boolean isRootOfClassification();
	
	/**
	 * Per Testcase there may only be one error selected, so error fields must be distinguished.
	 * @param fault
	 */
	public void setFault(boolean fault);
	
	/**
	 * Per Testcase there may only be one error selected, so error fields must be distinguished.
	 * @param fault
	 */
	public boolean isFault();

}
