package net.bpelunit.suitegenerator.datastructures.variables;

import org.jdom2.Element;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.util.XMLElementOutput;

/**
 * Convenience baseclass
 *
 */
public abstract class BaseInstance extends BaseVariable implements IVariableInstance {

	protected String instanceName;
	protected int numberOfUsages = 0;

	public BaseInstance(String variableName, String instanceName, Element content) {
		super(variableName, content);
		this.instanceName = instanceName;
	}

	@Override
	public void attachToElement(Element e) {
		// Simple text was given in this instance
		if (content.getChildren().size() == 0) {
			String neu = content.getText();
			if (e.getName().equals(Config.get().getConditionValueName()) && e.getText().equals("''")) {
				neu = "'" + neu + "'";
				e.removeContent();
			}
			e.addContent(neu);
		} else {
			// Complex XML was given
			for (Element child : content.getChildren()) {
				e.addContent(child.clone());
			}
		}
		countUsage();
	}

	@Override
	public void countUsage() {
		numberOfUsages++;
	}

	@Override
	public int getNumberUsages() {
		return numberOfUsages;
	}

	@Override
	public String getInstanceName() {
		return instanceName;
	}

	@Override
	public String toString() {
		return "Instance [" + instanceName + "] for complex var [" + getVariableName() + "] containing {" + XMLElementOutput.out(content) + "}";
	}

}
