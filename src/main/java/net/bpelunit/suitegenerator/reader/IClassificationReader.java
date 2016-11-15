package net.bpelunit.suitegenerator.reader;

import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.statistics.IStatistics;

public interface IClassificationReader {

	public Classification getClassification();

	/**
	 * Reads the classification and attaches Selection-Variables to the leaves of the ClassificationTree
	 * 
	 * @param fragmentReader
	 */
	public void readAndEnrich(ICodeFragmentReader fragmentReader, IStatistics stat);

}
