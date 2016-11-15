package net.bpelunit.suitegenerator.recommendation.permut;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.statistics.Selection;

public class Pairs {

	private Map<PairKey, Pair> pairs = new HashMap<>();
	private HashMap<ClassificationVariable, HashMap<Selection, ParameterValue>> parValuesByPar = new HashMap<>();
	private SortedSet<ParameterValue> parValues = new TreeSet<>();

	private ICondition conditions;

	public Pairs(ICondition conditions) {
		this.conditions = conditions;
	}

	public void add(ClassificationVariable first, ClassificationVariable second, List<Selection> sel1,
			List<Selection> sel2) {
		Pair p = new Pair(first, second, sel1, sel2, conditions);
		if (p.isSatisfied()) {
			return;
		}
		pairs.put(new PairKey(first, second), p);
		createParValues(first, sel1, p);
		createParValues(second, sel2, p);
	}

	private void createParValues(ClassificationVariable par, List<Selection> sel, Pair p) {
		Map<Selection, ParameterValue> pars = parValuesByPar.get(par);
		for (Selection s : sel) {
			ParameterValue pv = pars.get(s);
			if (pv == null) {
				pv = new ParameterValue(par, s, p.getNumUsages(s));
				pars.put(s, pv);
			} else {
				pv.numUnusedSubPairs += p.getNumUsages(s);
			}
		}
	}

	public void fillMap(Map<ClassificationVariable, List<Selection>> roots) {
		for (ClassificationVariable first : roots.keySet()) {
			// pairsForVar.put(first, new LinkedList<Pair>());
			parValuesByPar.put(first, new HashMap<Selection, ParameterValue>());
		}
	}

	public void setupDone() {
		for (HashMap<Selection, ParameterValue> pars : parValuesByPar.values()) {
			for (ParameterValue pv : pars.values()) {
				parValues.add(pv);
			}
		}
	}

	public boolean hasUnsatisfiedPairs() {
		// TODO Better
		for (Pair p : pairs.values()) {
			if (!p.isSatisfied()) {
				return true;
			}
		}
		return false;
	}

	public ParameterValue getBest() {
		/*ParameterValue last = parValues.last();
		List<ParameterValue> best = new LinkedList<>();
		int num = last.numUnusedSubPairs;
		best.add(last);
		parValues.remove(last);
		if (!parValues.isEmpty()) {
			ParameterValue tmp = parValues.last();
			while (tmp.numUnusedSubPairs == num) {
				best.add(tmp);
				parValues.remove(tmp);
				tmp = parValues.last();
			}
			Collections.shuffle(best);
		}
		last = best.get(0);
		parValues.addAll(best);
		return last;*/
		// Old version:
		return parValues.last();
	}

	public boolean markUsed(Selection s1, Selection s2) {
		PairKey pk = new PairKey(s1.getRootElement(), s2.getRootElement());
		Pair p = pairs.get(pk);
		if (p != null) {
			boolean firstUse = p.markUsed(s1, s2);
			if (firstUse) {
				ParameterValue pv1 = parValuesByPar.get(s1.getRootElement()).get(s1);
				parValues.remove(pv1);
				pv1.numUnusedSubPairs -= 1;
				if (pv1.numUnusedSubPairs > 0) {
					parValues.add(pv1);
				}
				ParameterValue pv2 = parValuesByPar.get(s2.getRootElement()).get(s2);
				parValues.remove(pv2);
				pv2.numUnusedSubPairs -= 1;
				if (pv2.numUnusedSubPairs > 0) {
					parValues.add(pv2);
				}

			}
		}
		return false;
	}

	public boolean isUnused(Selection sel, Selection s) {
		PairKey pk = new PairKey(sel.getRootElement(), s.getRootElement());
		Pair p = pairs.get(pk);
		return p != null && p.isUnused(sel, s);
	}

}

class ParameterValue implements Comparable<ParameterValue> {

	ClassificationVariable cv;
	Selection sel;
	int numUnusedSubPairs = 0;

	public ParameterValue(ClassificationVariable par, Selection s, int num) {
		this.cv = par;
		this.sel = s;
		this.numUnusedSubPairs = num;
	}

	@Override
	public int compareTo(ParameterValue o) {
		int compare = numUnusedSubPairs - o.numUnusedSubPairs;
		if (compare == 0) {
			return sel.compareTo(o.sel);
		}
		return compare;
	}

	@Override
	public String toString() {
		return sel.toString() + " numUnusedSubPairs: " + numUnusedSubPairs;
	}
}
