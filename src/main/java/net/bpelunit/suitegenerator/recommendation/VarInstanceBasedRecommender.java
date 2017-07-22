package net.bpelunit.suitegenerator.recommendation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.variables.DataVariableInstance;
import net.bpelunit.suitegenerator.datastructures.variables.Mapping;
import net.bpelunit.suitegenerator.statistics.Selection;
import net.bpelunit.suitegenerator.util.Copy;

//This Recommender does not work with deactivated Slots/Tracks and Conditions
public class VarInstanceBasedRecommender extends Recommender {

	private Map<ClassificationVariable, List<Selection>> roots;
	private Set<DataVariableInstance> unusedData;

	public VarInstanceBasedRecommender() {
	}

	@Override
	protected void createRecommendations() {
		super.createRecommendations();
		unusedData = Copy.deepCopy(new HashSet<DataVariableInstance>(), statistic.getUnusedDataInstances());
		roots = copyHashMap(new HashMap<ClassificationVariable, List<Selection>>(), statistic.getRootVariables());
		while (unusedData.size() > 0) {
			createNewRecommendation();
		}
	}

	private void createNewRecommendation() {
		List<Selection> selections = new LinkedList<>();

		DataVariableInstance unused = unusedData.iterator().next();
		String instName = unused.getInstanceName();
		String varSlotName = unused.getVariableName();

		boolean alreadyMapped = false;
		boolean mayFindFault = true;
		boolean used = false;
		for (List<Selection> leaves : roots.values()) {
			SearchResult s = findBestLeave(leaves, instName, varSlotName, mayFindFault, alreadyMapped);
			selections.add(s.sel);
			if (s.sel.getSelection().isFault()) {
				mayFindFault = false;
			}
			if (s.hasMapping) {
				alreadyMapped = true;
			}
			if (s.numUsages > 0) {
				used = true;
			}
		}
		if (used) {
			fillRecommendation(selections);
		} else {
			Config.get().out().noPossibilityToUse(instName, varSlotName);
		}
		unusedData.remove(unused);
	}

	private void fillRecommendation(List<Selection> selections) {
		Recommendation r = new Recommendation();
		for (Selection s : selections) {
			s.countUp();
			r.addSelection(s.getSelection());
		}
		recommendations.add(r);
	}

	private SearchResult findBestLeave(List<Selection> leaves, String instName, String varSlotName,
			boolean mayFindFault, boolean alreadyMapped) {
		SearchResult bestNumberOfUsages = new SearchResult(0, leaves.get(0), false);
		SearchResult withMapping = new SearchResult(0, leaves.get(0), false);
		for (Selection s : leaves) {
			if (s.getSelection().isFault() && !mayFindFault) {
				continue;
			}
			Mapping m = s.getSelection().getMapping();
			boolean hasMapping = m.hasMappingForVar(varSlotName, instName);
			int numSlotsForVar = m.numberOfSlotsFor(varSlotName, variables);
			if (hasMapping && (!withMapping.hasMapping || withMapping.numUsages < numSlotsForVar)) {
				withMapping = new SearchResult(numSlotsForVar, s, hasMapping);
			}
			if (numSlotsForVar > 0 && bestNumberOfUsages.numUsages < numSlotsForVar) {
				bestNumberOfUsages = new SearchResult(numSlotsForVar, s, hasMapping);
			}
		}
		if (!alreadyMapped && withMapping.hasMapping) {
			return withMapping;
		}
		if (bestNumberOfUsages.numUsages > 0) {
			return bestNumberOfUsages;
		}
		Selection s = findLeastUsed(leaves, false);
		System.out.println(s);
		return new SearchResult(0, s, false);
	}

	static class SearchResult {
		final int numUsages;
		final boolean hasMapping;
		final Selection sel;

		public SearchResult(int numUsages, Selection sel, boolean hasMapping) {
			super();
			this.numUsages = numUsages;
			this.sel = sel;
			this.hasMapping = hasMapping;
		}
	}

}
