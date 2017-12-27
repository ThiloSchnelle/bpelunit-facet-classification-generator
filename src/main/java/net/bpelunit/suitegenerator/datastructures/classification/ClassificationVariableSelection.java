package net.bpelunit.suitegenerator.datastructures.classification;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.datastructures.variables.Mapping;

/**
 * Leaves of the Classification.
 *
 */
public class ClassificationVariableSelection extends BaseClassificationElement {

	protected Mapping mapping;
	protected String completeName;

	public ClassificationVariableSelection(String name) {
		super(name);
		completeName = name;
	}

	public String getVariableName() {
		return parent.getName();
	}

	public String getCompleteName() {
		return completeName;
	}

	public void setSelection(Mapping mapping) {
		this.mapping = mapping;
	}

	public Mapping getMapping() {
		return mapping;
	}

	@Override
	public boolean isSatisfied() {
		return mapping != null;
	}

	@Override
	public void setParent(IClassificationElement parent) {
		super.setParent(parent);
		completeName = name;
		IClassificationElement e = parent;
		while (e.hasParent()) {
			completeName = e.getName() + Config.get().getSelectionDelimeter() + completeName;
			e = e.getParent();
		}
	}

}
