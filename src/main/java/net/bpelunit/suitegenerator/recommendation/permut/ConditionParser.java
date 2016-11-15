package net.bpelunit.suitegenerator.recommendation.permut;

import java.util.LinkedList;
import java.util.List;

public class ConditionParser {

	private int position = 0;
	private String[] parts;
	private List<ICondition> conds = new LinkedList<>();

	public ICondition parse(String cond) {
		cond = "(" + cond + ")";
		cond = cond.replace("(", "( ");
		cond = cond.replace(")", " )");
		parts = cond.split(" ");
		parseConds();
		return handleCondition(conds.get(0));
		// parseBracket(0, cond.length() - 1);
	}

	private void parseConds() {
		for (String part : parts) {
			if (part.equals("(")) {
				conds.add(new OpenBracket());
			} else if (part.equals(")")) {
				conds.add(new CloseBracket());
			} else if (part.equals("AND")) {
				conds.add(new AND());
			} else if (part.equals("OR")) {
				conds.add(new OR());
			} else if (part.equals("XOR")) {
				conds.add(new XOR());
			} else if (part.equals("NOT")) {
				conds.add(new NOT());
			} else {
				conds.add(new OperandCondition(part));
			}
		}
	}

	public ICondition handleCondition(ICondition cond) {
		return cond.visit(this);
	}

	public ICondition consumeBefore() {
		return handleCondition(conds.remove(--position));
	}

	public ICondition consumeNext() {
		ICondition cond = conds.get(++position);
		ICondition result = handleCondition(cond);
		conds.remove(position);
		position--;
		return result;
	}

	public ICondition parseBrackets() {
		int backTo = position;
		conds.remove(position);
		for (; conds.size() > position; position++) {
			ICondition c = conds.get(position).visit(this);
			if (c == null) {
				ICondition ret = conds.get(position);
				position = backTo;
				return ret;
			}
		}
		// Just for the compiler
		return conds.get(0);
	}

	public void moveForward() {
		position++;
	}

	public void moveBackward() {
		position--;
	}

	public ICondition endParseBracket() {
		conds.remove(position--);
		return null;
	}

	// TODO Leerzeichen nach Klammern könnten leere Strings beim Splitten auslösen?
	public static void main(String[] args) {

		System.out.println(new ConditionParser().parse("A AND B AND NOT C AND D"));

		System.out.println(new ConditionParser().parse("(Betrag:ZuGroß)"));
		System.out.println("==================");
		System.out.println(new ConditionParser().parse("Betrag:ZuGroß AND (Bestandskunde AND BLA)"));

		System.out.println("==================");
		System.out.println("(Blubb XOR Bestandskunde:Ja) AND (ZahlPDFs:Größer1 OR Bla) AND NOT Betrag:ZuGroß");
		System.out.println(new ConditionParser()
				.parse("(Blubb XOR Bestandskunde:Ja) AND (ZahlPDFs:Größer1 OR Bla) AND NOT Betrag:ZuGroß"));

		System.out.println("==================");
		System.out.println("Bestandskunde:Ja AND (ZahlPDFs:Größer1 OR Bla) AND NOT Betrag:ZuGroß");
		System.out.println(
				new ConditionParser().parse("Bestandskunde:Ja AND (ZahlPDFs:Größer1 OR Bla) AND NOT Betrag:ZuGroß"));

		System.out.println("==================");
		System.out.println("Bestandskunde:Ja AND NOT (ZahlPDFs:Größer1 OR Betrag:ZuGroß)");
		System.out.println(new ConditionParser().parse("Bestandskunde:Ja AND NOT (ZahlPDFs:Größer1 OR Betrag:ZuGroß)"));

		System.out.println("==================");
		System.out.println("Bestandskunde:Ja AND NOT Betrag:ZuGroß AND (ZahlPDFs:Größer1 OR Bla)");
		System.out.println(
				new ConditionParser().parse("Bestandskunde:Ja AND NOT Betrag:ZuGroß AND (ZahlPDFs:Größer1 OR Bla)"));
	}

}

class OpenBracket implements ICondition {

	@Override
	public boolean evaluate(List<? extends IOperand> ops) {
		return false;
	}

	@Override
	public boolean evaluate(List<? extends IOperand> ops, IOperand additional) {
		return false;
	}

	@Override
	public boolean evaluate(IOperand... ops) {
		return false;
	}

	@Override
	public ICondition visit(ConditionParser parser) {
		return parser.parseBrackets();
	}

	@Override
	public String toString() {
		return "{ ";
	}

}

class CloseBracket implements ICondition {

	@Override
	public boolean evaluate(List<? extends IOperand> ops) {
		return false;
	}

	@Override
	public boolean evaluate(List<? extends IOperand> ops, IOperand additional) {
		return false;
	}

	@Override
	public boolean evaluate(IOperand... ops) {
		return false;
	}

	@Override
	public ICondition visit(ConditionParser parser) {
		return parser.endParseBracket();
	}

	@Override
	public String toString() {
		return " }";
	}

}