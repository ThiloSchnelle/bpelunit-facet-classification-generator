package net.bpelunit.suitegenerator.recommendation.permut;

import net.bpelunit.suitegenerator.statistics.Selection;

public class SubPair {
	private Selection first, second;
	private boolean used = false;

	public SubPair(Selection first, Selection second) {
		this.first = first;
		this.second = second;
	}

	public Selection getFirst() {
		return first;
	}

	public Selection getSecond() {
		return second;
	}

	public boolean hasBeenUsed() {
		return used;
	}

	public void flagUsed() {
		used = true;
	}

	public boolean contains(Selection s) {
		return s.equals(first) || s.equals(second);
	}
}

class SubPairKey {

	Selection s1, s2;

	public SubPairKey(Selection s1, Selection s2) {
		this.s1 = s1;
		this.s2 = s2;
	}

	@Override
	public int hashCode() {
		return s1.hashCode() + s2.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == SubPairKey.class) {
			SubPairKey test = (SubPairKey) obj;
			return (test.s1.equals(s1) && test.s2.equals(s2)) || (test.s1.equals(s2) && test.s2.equals(s1));
		}
		return false;
	}

	public boolean contains(Selection s) {
		return s.equals(s1) || s.equals(s2);
	}

	@Override
	public String toString() {
		return "{" + s1 + ", " + s2 + "}";
	}

}
