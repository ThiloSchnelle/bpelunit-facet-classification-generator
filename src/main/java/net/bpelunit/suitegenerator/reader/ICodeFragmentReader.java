package net.bpelunit.suitegenerator.reader;

import org.jdom2.Element;

import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;

public interface ICodeFragmentReader {

	public Element getSkeletalStructure();

	VariableLibrary getVariables();

}
