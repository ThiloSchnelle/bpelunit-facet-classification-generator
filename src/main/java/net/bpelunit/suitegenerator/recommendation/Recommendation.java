package net.bpelunit.suitegenerator.recommendation;

import java.util.LinkedList;
import java.util.List;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.statistics.Selection;

public class Recommendation {

	private List<ClassificationVariableSelection> recommendedSelections = new LinkedList<>();

	public void addSelection(ClassificationVariableSelection selection) {
		recommendedSelections.add(selection);
	}

	public List<ClassificationVariableSelection> getRecommendedSelections() {
		return new LinkedList<>(recommendedSelections);
	}

	@Override
	public String toString() {
		String result = "";
		for (ClassificationVariableSelection cvs : recommendedSelections) {
			if (!result.isEmpty()) {
				result += ", ";
			}
			result += cvs.getCompleteName();
		}
		result += "]";
		result = "[" + result;
		return result;
	}

	public void addSelections(List<Selection> sel) {
		for (Selection s : sel) {
			recommendedSelections.add(s.getSelection());
		}
	}

}
