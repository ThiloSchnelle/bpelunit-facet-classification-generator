package net.bpelunit.suitegenerator.recommendation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;
import net.bpelunit.suitegenerator.statistics.IStatistics;
import net.bpelunit.suitegenerator.statistics.Selection;
import net.bpelunit.suitegenerator.util.Copy;

//TODO Conditions berücksichtigen
public class SimpleRecommender extends Recommender {

	private Set<Selection> usedSelections;
	private Set<Selection> unusedSelections;
	private Collection<Selection> allSelections;
	private Map<ClassificationVariable, List<Selection>> roots;

	public SimpleRecommender(IStatistics statistic, VariableLibrary variables, Classification classification) {
		super(statistic, variables, classification);
	}

	@Override
	protected void createRecommendations() {
		super.createRecommendations();
		usedSelections = Copy.deepCopy(new HashSet<Selection>(), statistic.getUsedSelections());
		unusedSelections = Copy.deepCopy(new HashSet<Selection>(), statistic.getUnusedSelections());
		allSelections = Copy.deepCopy(new HashSet<Selection>(), statistic.getAllSelections());
		roots = copyHashMap(new HashMap<ClassificationVariable, List<Selection>>(), statistic.getRootVariables());
		while (usedSelections.size() < allSelections.size()) {
			createNewRecommendation();
		}
	}

	private void createNewRecommendation() {
		Set<ClassificationVariable> usedRoot = new HashSet<>();
		List<Selection> prevUnused = new LinkedList<>();
		boolean usedFault = false;
		for (Selection unused : unusedSelections) {
			if (usedRoot.add(unused.getRootElement()) && (!usedFault || !unused.getSelection().isFault())) {
				if (unused.getSelection().isFault()) {
					usedFault = true;
				}
				use(unused);
				prevUnused.add(unused);
			}
		}
		unusedSelections.removeAll(prevUnused);
		System.out.println("New Selections: " + prevUnused.size());
		List<Selection> otherSelectionsToUse = new LinkedList<>();
		for (ClassificationVariable root : roots.keySet()) {
			if (!usedRoot.contains(root)) {
				Selection leastUsed = findLeastUsed(roots.get(root), !usedFault);
				if (leastUsed.getSelection().isFault()) {
					usedFault = true;
				}
				use(leastUsed);
				otherSelectionsToUse.add(leastUsed);
			}
		}
		fillRecommendation(prevUnused, otherSelectionsToUse);
	}

	private void fillRecommendation(List<Selection> prevUnused, List<Selection> otherSelectionsToUse) {
		Recommendation r = new Recommendation();
		for (Selection s : prevUnused) {
			r.addSelection(s.getSelection());
		}
		for (Selection s : otherSelectionsToUse) {
			r.addSelection(s.getSelection());
		}
		recommendations.add(r);
	}

	private void use(Selection unused) {
		unused.countUp();
		usedSelections.add(unused);
	}

}
