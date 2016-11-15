package net.bpelunit.suitegenerator.recommendation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;
import net.bpelunit.suitegenerator.recommendation.permut.ICondition;
import net.bpelunit.suitegenerator.statistics.IStatistics;
import net.bpelunit.suitegenerator.statistics.Selection;
import net.bpelunit.suitegenerator.util.Copy;

public class Recommender {

	protected IStatistics statistic;
	protected VariableLibrary variables;
	protected Classification classification;
	protected ICondition forbidden;

	protected List<Recommendation> recommendations = null;

	public Recommender(IStatistics statistic, VariableLibrary variables, Classification classification) {
		this.statistic = statistic;
		this.variables = variables;
		this.classification = classification;
		this.forbidden = classification.getForbidden();
	}

	protected void createRecommendations() {
		recommendations = new LinkedList<>();
	}

	public List<Recommendation> getRecommendations() {
		if (recommendations == null) {
			createRecommendations();
		}
		return recommendations;
	}

	protected Map<ClassificationVariable, List<Selection>> copyHashMap(
			HashMap<ClassificationVariable, List<Selection>> dest, Map<ClassificationVariable, List<Selection>> src) {
		for (ClassificationVariable cv : src.keySet()) {
			List<Selection> l = src.get(cv);
			List<Selection> neu = Copy.deepCopy(new LinkedList<Selection>(), l);
			dest.put(cv, neu);
		}
		return dest;
	}

	protected Selection findLeastUsed(List<Selection> list, boolean mayFindFault) {
		Selection least = list.get(0);
		for (Selection neu : list) {
			if (least.getNumUsages() > neu.getNumUsages() && !neu.getSelection().isFault()) {
				least = neu;
			}
		}
		return least;
	}

}
