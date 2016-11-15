package net.bpelunit.suitegenerator.recommendation.permut;

import java.util.List;

public interface ICondition {

	/**
	 * Returns false when the condition does not hold. This includes when there are not enough arguments given
	 * 
	 * @param ops
	 * @return
	 */
	boolean evaluate(List<? extends IOperand> ops);

	boolean evaluate(List<? extends IOperand> ops, IOperand additional);

	boolean evaluate(IOperand... ops);

	ICondition visit(ConditionParser parser);

}
