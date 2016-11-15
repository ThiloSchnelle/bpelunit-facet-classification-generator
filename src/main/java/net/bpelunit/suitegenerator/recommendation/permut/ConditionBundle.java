package net.bpelunit.suitegenerator.recommendation.permut;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConditionBundle implements ICondition {

	private List<ICondition> conditions = new LinkedList<>();

	/**
	 * Returns true if any of the contained conditions returns true
	 */
	@Override
	public boolean evaluate(List<? extends IOperand> ops) {
		for (ICondition cond : conditions) {
			if (cond.evaluate(ops)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if any of the contained conditions returns true
	 */
	@Override
	public boolean evaluate(List<? extends IOperand> ops, IOperand additional) {
		List<IOperand> neu = new ArrayList<>(ops);
		neu.add(additional);
		return evaluate(neu);
	}

	/**
	 * Returns true if any of the contained conditions returns true
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
	public ICondition visit(ConditionParser parser) {
		return null;
	}

	public void addCondition(ICondition add) {
		this.conditions.add(add);
	}

}
