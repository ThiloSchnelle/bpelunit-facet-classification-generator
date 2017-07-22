package net.bpelunit.suitegenerator.datastructures.conditions;

import java.util.ArrayList;
import java.util.List;

public class NOT implements ICondition {

	private ICondition first;

	public NOT(ICondition first) {
		this.first = first;
	}

	public NOT() {

	}

	public ICondition visit(ConditionParser parser) {
		if (first != null) {
			return this;
		}
		first = parser.consumeNext();
		return this;
	}

	/**
	 * Returns the inverted result of the contained condition
	 */
	@Override
	public boolean evaluate(List<? extends IOperand> ops) {
		return !first.evaluate(ops);
	}

	/**
	 * Returns the inverted result of the contained condition
	 */
	@Override
	public boolean evaluate(List<? extends IOperand> ops, IOperand additional) {
		List<IOperand> neu = new ArrayList<>(ops);
		neu.add(additional);
		return evaluate(neu);
	}

	/**
	 * Returns the inverted result of the contained condition
	 */
	@Override
	public boolean evaluate(IOperand... ops) {
		List<IOperand> neu = new ArrayList<>();
		for (IOperand op : ops) {
			neu.add(op);
		}
		return evaluate(neu);
	}

	@Override
	public String toString() {
		if (first == null) {
			return "NOT";
		}
		return "(!" + first + ")";
	}

}
