package net.bpelunit.suitegenerator.config;

import org.jdom2.Namespace;

import net.bpelunit.suitegenerator.output.ConsoleOut;
import net.bpelunit.suitegenerator.output.IOutput;

class Configuration implements IConfiguration {

	private static IOutput out = new ConsoleOut();

	@Override
	public String getIntendation() {
		return "\t";
	}

	@Override
	public String getMessageExchangeTag() {
		return "messageExchange";
	}

	@Override
	public String getVariableTag() {
		return "variable";
	}

	@Override
	public String getVariableNameTag() {
		return "variableName";
	}

	@Override
	public String getBaseFileName() {
		return "baseFile.xml";
	}

	@Override
	public String getInstanceNameTag() {
		return "instanceName";
	}

	@Override
	public String getInstanceContentTag() {
		return "data";
	}

	@Override
	public String getInstanceTag() {
		return "instance";
	}

	@Override
	public String getVariableDefinitionTag() {
		return "variableDefinition";
	}

	@Override
	public String getOutputName() {
		return "suite.bpts";
	}

	@Override
	public String getClassificationTableName() {
		return "classification.xls";
	}

	@Override
	public String getTableStart() {
		return "Classification Table:";
	}

	@Override
	public String getTestCasesStart() {
		return "TestCases:";
	}

	@Override
	public String getSelectionDelimeter() {
		return ":";
	}

	@Override
	public String getSelectionTag() {
		return "mapping";
	}

	@Override
	public String getSelectionName() {
		return "name";
	}

	@Override
	public String getVariableInstanceTag() {
		return "variableInstance";
	}

	@Override
	public String getUseMessageExchange() {
		return "useMessageExchange";
	}

	@Override
	public String getUseVariable() {
		return "useVariable";
	}

	@Override
	public String getSelectionExtends() {
		return "extends";
	}

	@Override
	public Namespace getGeneratorSpace() {
		return Namespace.getNamespace("csg", "bpelunit.net/classificationTreeSuiteGenerator.xsd");
	}

	@Override
	public IOutput out() {
		return out;
	}

	@Override
	public String getMessageSlotName() {
		return "messageSlot";
	}

	@Override
	public String getPartnerTrackSequence() {
		return "partnerTrackSequence";
	}

	@Override
	public String getVariableSlotTag() {
		return "variableSlot";
	}

	@Override
	public String getFaultLine() {
		return "Fault:";
	}

	@Override
	public String getMessageNameTag() {
		return "messageName";
	}

	@Override
	public String getConditionValueName() {
		return "value";
	}

	@Override
	public String getTrackTypeTag() {
		return "type";
	}

	@Override
	public String getDeactivationTag() {
		return "deactivateSlot";
	}

	@Override
	public String getPartnerTrackDeactivationTag() {
		return "deactivateTrack";
	}

	@Override
	public String getForbiddenStart() {
		return "Forbidden:";
	}

}
