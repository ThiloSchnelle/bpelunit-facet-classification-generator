package net.bpelunit.suitegenerator.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableNameComparator;
import net.bpelunit.suitegenerator.statistics.Selection;

public class FullTestRecommender extends Recommender {

	List<ClassificationVariable> roots = new ArrayList<>();
	Map<ClassificationVariable, List<Selection>> leafsByRoot;
	
	@Override
	protected void createRecommendations() {
		super.createRecommendations();
		
		leafsByRoot = new HashMap<>(statistic.getRootVariables());
		
		roots.clear();
		roots.addAll(leafsByRoot.keySet());
		roots.sort(new ClassificationVariableNameComparator());
		
		createRecommendation(new ArrayList<>());
	}
	
	private void createRecommendation(List<Selection> chosenValuesForRoots) {
		if(chosenValuesForRoots.size() == roots.size()) {
			if(!forbidden.evaluate(chosenValuesForRoots.toArray(new Selection[0]))) {
				Recommendation e = new Recommendation();
				e.addSelections(chosenValuesForRoots);
				recommendations.add(e);	
			}
			
			return;
		}
		
		int rootToHandle = chosenValuesForRoots.size();
		List<Selection> leafsForRoot = leafsByRoot.get(roots.get(rootToHandle));
		for(Selection s : leafsForRoot) {
			List<Selection> newSelection = new ArrayList<>(chosenValuesForRoots.size() + 1);
			newSelection.addAll(chosenValuesForRoots);
			newSelection.add(s);
			
			createRecommendation(newSelection);
		}
	}

}
