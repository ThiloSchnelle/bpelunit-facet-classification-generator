package net.bpelunit.suitegenerator.recommendation.permut;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.statistics.Selection;

public class Pair {

	private ClassificationVariable first;
	private ClassificationVariable second;
	private Map<SubPairKey, SubPair> subPairs = new HashMap<>();
	private List<SubPairKey> keys;
	private List<SubPairKey> unused;
	private int lastReturnedPair = -1; // In case all combinations were used cycle through subPairs

	public Pair(ClassificationVariable first, ClassificationVariable second, List<Selection> firstChildren,
			List<Selection> secondChildren, ICondition conditions) {
		this.first = first;
		this.second = second;
		this.keys = new LinkedList<>();
		for (Selection s1 : firstChildren) {
			for (Selection s2 : secondChildren) {
				if (!conditions.evaluate(s1, s2)) {
					SubPairKey spk = new SubPairKey(s1, s2);
					subPairs.put(spk, new SubPair(s1, s2));
					keys.add(spk);
				}
			}
		}
		unused = new LinkedList<>(keys);
	}

	public boolean isSatisfied() {
		return unused.size() == 0;
	}

	public SubPair getNextPair() {
		if (!unused.isEmpty()) {
			return subPairs.get(unused.get(0));
		}
		if (++lastReturnedPair >= subPairs.size()) {
			lastReturnedPair = 0;
		}
		return subPairs.get(keys.get(lastReturnedPair));
	}

	public boolean contains(ClassificationVariable test) {
		return test.equals(first) || test.equals(second);
	}

	/**
	 * 
	 * @param s1
	 * @param s2
	 * @return Whether a pair was marked used that was not before used
	 */
	public boolean markUsed(Selection s1, Selection s2) {
		SubPairKey key = new SubPairKey(s1, s2);
		unused.remove(key);
		SubPair sp = subPairs.get(key);
		if (sp == null || sp.hasBeenUsed()) {
			return false;
		}
		sp.flagUsed();
		return true;
	}

	public boolean isUnused(Selection sel, Selection s) {
		SubPairKey subKey = new SubPairKey(sel, s);
		SubPair sp = subPairs.get(subKey);
		return sp != null && !sp.hasBeenUsed();
	}

	public int getNumUsages(Selection s) {
		int sum = 0;
		for (SubPairKey k : keys) {
			if (k.contains(s)) {
				sum++;
			}
		}
		return sum;
	}

	@Override
	public String toString() {
		return "Pair: " + first + ", " + second + " with unsed subpairs: " + unused;
	}

}

class PairKey {
	ClassificationVariable first;
	ClassificationVariable second;

	public PairKey(ClassificationVariable first, ClassificationVariable second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == PairKey.class) {
			PairKey test = (PairKey) obj;
			return (test.first.equals(first) && test.second.equals(second))
					|| (test.first.equals(second) && test.second.equals(first));
		}
		return false;
	}
}