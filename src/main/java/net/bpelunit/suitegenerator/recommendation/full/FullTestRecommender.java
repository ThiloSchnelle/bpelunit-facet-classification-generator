package net.bpelunit.suitegenerator.recommendation.full;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableNameComparator;
import net.bpelunit.suitegenerator.recommendation.Recommendation;
import net.bpelunit.suitegenerator.recommendation.Recommender;
import net.bpelunit.suitegenerator.statistics.Selection;

public class FullTestRecommender extends Recommender {

	private List<ClassificationVariable> roots = new ArrayList<>();
	private Map<ClassificationVariable, List<Selection>> leafsByRoot;
	
	@Override
	protected void createRecommendations() {
		super.createRecommendations();
		
		leafsByRoot = new HashMap<>(statistic.getRootVariables());
		
		roots.clear();
		roots.addAll(leafsByRoot.keySet());
		roots.sort(new ClassificationVariableNameComparator());
		
		createRecommendation(new ArrayList<>());
		System.out.println("Generated " + recommendations.size() + " test cases");
	}
	
	private void createRecommendation(List<Selection> chosenValuesForRoots) {
		int rootToHandle = chosenValuesForRoots.size();
		
		if(rootToHandle == roots.size()) {
			if(!forbidden.evaluate(chosenValuesForRoots.toArray(new Selection[rootToHandle]))) {
				Recommendation e = new Recommendation();
				e.addSelections(chosenValuesForRoots);
				recommendations.add(e);
			} 
			
			return;
		}
		
		List<Selection> leafsForRoot = leafsByRoot.get(roots.get(rootToHandle));
		for(Selection s : leafsForRoot) {
			List<Selection> newSelection = new ArrayList<>(chosenValuesForRoots.size() + 1);
			newSelection.addAll(chosenValuesForRoots);
			newSelection.add(s);
			
			createRecommendation(newSelection);
		}
	}

}
