package net.bpelunit.suitegenerator.config;

import org.jdom2.Namespace;

import net.bpelunit.suitegenerator.output.IOutput;

/**
 * Obviously for configuring how the program works and for keeping track, where some tags are used.
 *
 */
public interface IConfiguration {

	String getIntendation();

	String getMessageExchangeTag();

	String getVariableTag();

	String getVariableNameTag();

	String getBaseFileName();

	String getInstanceNameTag();

	String getInstanceTag();

	String getInstanceContentTag();

	String getVariableDefinitionTag();

	String getOutputName();

	String getClassificationTableName();

	String getTableStart();

	String getTestCasesStart();

	String getFaultLine();

	String getSelectionDelimeter();

	String getSelectionTag();

	String getSelectionExtends();

	String getSelectionName();

	String getVariableInstanceTag();

	String getUseMessageExchange();

	String getUseVariable();

	Namespace getGeneratorSpace();

	IOutput out();

	String getMessageSlotName();

	String getPartnerTrackSequence();

	String getVariableSlotTag();

	String getMessageNameTag();

	String getConditionValueName();

	String getTrackTypeTag();

	String getDeactivationTag();

	String getPartnerTrackDeactivationTag();

	String getForbiddenStart();

}
