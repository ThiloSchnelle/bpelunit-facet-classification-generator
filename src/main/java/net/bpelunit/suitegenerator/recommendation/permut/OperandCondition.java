package net.bpelunit.suitegenerator.recommendation.permut;

import java.util.ArrayList;
import java.util.List;

public class OperandCondition implements ICondition {

	private String tag;

	public OperandCondition(String tag) {
		this.tag = tag;
	}

	public ICondition visit(ConditionParser parser) {
		return this;
	}

	/**
	 * Returns true if any of the given Operands has the same opName
	 * 
	 * @param ops
	 * @return
	 */
	@Override
	public boolean evaluate(List<? extends IOperand> ops) {
		for (IOperand op : ops) {
			if (op.getOpName().equalsIgnoreCase(tag)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if any of the given Operands has the same opName
	 * 
	 * @param ops
	 * @return
	 */
	@Override
	public boolean evaluate(List<? extends IOperand> ops, IOperand additional) {
		List<IOperand> neu = new ArrayList<>(ops);
		neu.add(additional);
		return evaluate(neu);
	}

	/**
	 * Returns true if any of the given Operands has the same opName
	 * 
	 * @param ops
	 * @return
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
		return tag;
	}
}
