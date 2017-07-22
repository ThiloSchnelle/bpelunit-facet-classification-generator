package net.bpelunit.suitegenerator.datastructures.conditions;

import java.util.ArrayList;
import java.util.List;

public class XOR implements ICondition {

	private ICondition first, second;

	public XOR(ICondition first, ICondition second) {
		this.first = first;
		this.second = second;
	}

	public XOR() {

	}

	public ICondition visit(ConditionParser parser) {
		if (first != null && second != null) {
			return this;
		}
		first = parser.consumeBefore();
		second = parser.consumeNext();
		return this;
	}

	/**
	 * Returns true if exactly one of the contained conditions holds for the given Operands
	 */
	@Override
	public boolean evaluate(List<? extends IOperand> ops) {
		return first.evaluate(ops) ^ second.evaluate(ops);
	}

	/**
	 * Returns true if exactly one of the contained conditions holds for the given Operands
	 */
	@Override
	public boolean evaluate(List<? extends IOperand> ops, IOperand additional) {
		List<IOperand> neu = new ArrayList<>(ops);
		neu.add(additional);
		return evaluate(neu);
	}

	/**
	 * Returns true if exactly one of the contained conditions holds for the given Operands
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
		if (first == null || second == null) {
			return "XOR";
		}
		return "(" + first + " ^ " + second + ")";
	}

}
