package net.bpelunit.suitegenerator.recommendation.permut;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;
import net.bpelunit.suitegenerator.recommendation.Recommendation;
import net.bpelunit.suitegenerator.recommendation.Recommender;
import net.bpelunit.suitegenerator.statistics.IStatistics;
import net.bpelunit.suitegenerator.statistics.Selection;

public class Permutation extends Recommender {

	private final int NUM_TRIALS = 50;

	private Pairs pairs;

	private List<Test> tests = new LinkedList<>();
	Map<ClassificationVariable, List<Selection>> roots;

	public Permutation() {
	}

	@Override
	public void setClassificationData(IStatistics stat, VariableLibrary variables, Classification classification) {
		super.setClassificationData(stat, variables, classification);
		pairs = new Pairs(forbidden);
		roots = copyHashMap(new HashMap<ClassificationVariable, List<Selection>>(), statistic.getRootVariables());

		pairs.fillMap(roots);
		createPairs(roots);
		pairs.setupDone();
		createTests();
	}
	
	@Override
	protected void createRecommendations() {
		super.createRecommendations();
		for (Test t : tests) {
			Recommendation r = new Recommendation();
			r.addSelections(t.sel);
			recommendations.add(r);
		}
	}

	private void createTests() {
		int i = 100;
		while (pairs.hasUnsatisfiedPairs() && i-- > 0) {
			Test t = createBestNewTest();
			if (t != null) {
				tests.add(t);
			}
		}
		if (i == 0 && pairs.hasUnsatisfiedPairs()) {
			Config.get().out().thereWasNoPossibilityToSatisfyAllPairs();
		}
	}

	private Test createBestNewTest() {
		Test best = Test.WORST;
		for (int i = 0; i < NUM_TRIALS; i++) {
			Test neu = createSingleTest();
			if (neu != null) {
				if (best.newPairs < neu.newPairs) {
					best = neu;
				}
			}
		}
		if (best == Test.WORST) {
			return null;
		}
		markUsed(best);
		return best;
	}

	private void markUsed(Test test) {
		for (Selection s : test.sel) {
			boolean start = false;
			for (Selection s2 : test.sel) {
				if (start) {
					pairs.markUsed(s, s2);
				} else if (s == s2) {
					start = true;
				}
			}
		}
	}

	private Test createSingleTest() {
		// Step 1 - Following Greedy Algorithm by Cohen et al
		ParameterValue pv = pairs.getBest();
		Test t = new Test();
		List<ClassificationVariable> params = new LinkedList<>();

		// Step2
		for (ClassificationVariable r : roots.keySet()) {
			if (r != pv.cv) {
				params.add(r);
			}
		}
		Collections.shuffle(params);
		params.add(0, pv.cv);

		// Step 3
		List<Selection> sels = new LinkedList<Selection>();
		sels.add(pv.sel);
		boolean first = true;
		boolean faultElementContained = pv.sel.getSelection().isFault();
		for (ClassificationVariable param : params) {
			if (first) { // Already in the selection list
				first = false;
				continue;
			}
			Selection best = null;
			int numNewPairs = 0;
			for (Selection s : roots.get(param)) {
				if (!isAllowed(s, faultElementContained) || forbidden.evaluate(sels, s)) {
					continue;
				}
				if (best == null) {
					best = s;
					numNewPairs = findNumberOfNewPairs(sels, s);
					continue;
				}

				int pairs = findNumberOfNewPairs(sels, s);
				if (pairs > numNewPairs) {
					best = s;
					numNewPairs = pairs;
				}
			}
			if (best == null) {
				Config.get().out().noPossibilityToAddParameter(sels, param);
				return null;
			}
			faultElementContained |= best.getSelection().isFault();
			sels.add(best);
			t.newPairs += numNewPairs;
		}
		if (t.newPairs == 0) {
			return null;
		}
		t.sel = sels;
		return t;
	}

	private boolean isAllowed(Selection s, boolean faultElementContained) {
		return !(faultElementContained && s.getSelection().isFault());
	}

	private int findNumberOfNewPairs(List<Selection> sels, Selection s) {
		int sum = 0;
		for (Selection sel : sels) {
			if (pairs.isUnused(sel, s)) {
				sum++;
			}
		}
		return sum;
	}

	private void createPairs(Map<ClassificationVariable, List<Selection>> roots) {
		for (ClassificationVariable first : roots.keySet()) {
			List<Selection> sel1 = roots.get(first);
			boolean start = false;
			for (ClassificationVariable second : roots.keySet()) {
				if (start) {
					List<Selection> sel2 = roots.get(second);
					pairs.add(first, second, sel1, sel2);
				}
				// Create only once per combination
				if (first == second) {
					start = true;
				}
			}
		}
	}

}

class Test {

	public static final Test WORST = new Test(-1000);

	public List<Selection> sel = new LinkedList<Selection>();
	public int newPairs = 0;

	public Test() {

	}

	public void add(Selection s) {
		sel.add(s);
	}

	public Test(int newPairs) {
		this.newPairs = newPairs;
	}

	@Override
	public String toString() {
		return sel.toString() + " " + newPairs;
	}
}
