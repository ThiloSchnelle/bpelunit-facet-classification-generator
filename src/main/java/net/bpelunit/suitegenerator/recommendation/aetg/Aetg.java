package net.bpelunit.suitegenerator.recommendation.aetg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableNameComparator;
import net.bpelunit.suitegenerator.recommendation.IConfigurableRecommender;
import net.bpelunit.suitegenerator.recommendation.Recommendation;
import net.bpelunit.suitegenerator.recommendation.Recommender;
import net.bpelunit.suitegenerator.statistics.Selection;

public class Aetg extends Recommender implements IConfigurableRecommender {

	public class UsageSortComparator implements Comparator<Entry<Selection, Integer>> {

		@Override
		public int compare(Entry<Selection, Integer> o1, Entry<Selection, Integer> o2) {
			return -o1.getValue().compareTo(o2.getValue());
		}

	}

	private int tCoverage = 2;
	private int repetitions = 50;

	private List<ClassificationVariable> roots = new ArrayList<>();
	private Map<ClassificationVariable, List<Selection>> leafsByRoot;
	private Random randomGenerator;
	private int maxEmptyRounds = 50;
	private long seed = System.currentTimeMillis();
	
	@Override
	public void setConfigurationParameter(String parameter) {
		System.out.println("Configuration passed: " + parameter);
		String[] paramPairs = parameter.split(",");
		
		for(String paramPair : paramPairs) {
			String[] paramPairTokens = paramPair.split("=");
			String param = paramPairTokens[0];
			String paramValue = "";
			if(paramPairTokens.length >= 2) {
				paramValue = paramPairTokens[1];
			}
			
			switch(param) {
				case "t": 
					tCoverage = Integer.valueOf(paramValue);
					break;
				case "M":
					repetitions = Integer.valueOf(paramValue);
					break;
				case "m":
					maxEmptyRounds = Integer.valueOf(paramValue);
					break;
				case "seed":
					seed = Long.valueOf(paramValue);
					break;
			}
		}
	}

	@Override
	protected void createRecommendations() {
		System.out.println("Using random seed: " + seed);
		randomGenerator = new Random(seed);
		super.createRecommendations();
		
		leafsByRoot = new HashMap<>(statistic.getRootVariables());
		
		roots.clear();
		roots.addAll(leafsByRoot.keySet());
		roots.sort(new ClassificationVariableNameComparator());
		
		List<Selection> allParameterValues = new ArrayList<>();
		for(List<Selection> selections : leafsByRoot.values()) {
			for(Selection s : selections) {
				allParameterValues.add(s);
			}
		}
		
		List<List<Selection>> uncoveredTupels = generateAllTupels(new ArrayList<>(roots));
		uncoveredTupels = removeAllInvalidTupels(uncoveredTupels);
		createRecommendations(uncoveredTupels);
		System.out.println("Generated " + recommendations.size() + " test cases");
	}
	
	private List<List<Selection>> removeAllInvalidTupels(List<List<Selection>> tupels) {
		List<List<Selection>> result = new ArrayList<>();
		
		for(List<Selection> l : tupels) {
			while(l.contains(null)) {
				l.remove(null);
			}
			
			if(l.size() == tCoverage) {
				result.add(l);
			}
		}
		
		return result;
	}

	private Selection getMostUnusedSelection(List<List<Selection>> uncovered) {
		Map<Selection, Integer> counters = new HashMap<>();
		for(List<Selection> l : uncovered) {
			for(Selection s : l) {
				Integer counter = counters.get(s);
				if(counter == null) {
					counter = 1;
				} else {
					counter += 1;
				}
				counters.put(s, counter);
			}
		}

		Set<ClassificationVariable> usedRoots = new HashSet<>();
		List<Entry<Selection, Integer>> selectionsByUse = new ArrayList<>(counters.entrySet());
		selectionsByUse.sort(new UsageSortComparator());
		int index = 0;
		List<Selection> result = new ArrayList<>();
		while (result.size() < tCoverage - 1) {
			Entry<Selection, Integer> currentEntry = selectionsByUse.get(index);
			Selection currentSelection = currentEntry.getKey();
			if(!usedRoots.contains(currentSelection.getRootElement())) {
				usedRoots.add(currentSelection.getRootElement());
				result.add(currentSelection);
			}
			index++;
		}
		
		return result.get(0);
	}
	
	private void createRecommendations(List<List<Selection>> uncoveredTupels) {
		int roundsWithoutNewTestCase = 0;
		
		List<List<Selection>> possibleNextTestCases = new ArrayList<>();
		do {
			possibleNextTestCases.clear();
			for(int i = 0; i < repetitions; i++) {
				List<Selection> nextTestCase = generateNextTestCase(uncoveredTupels);
				if(forbidden == null || !forbidden.evaluate(nextTestCase.toArray(new Selection[nextTestCase.size()]))) {
					possibleNextTestCases.add(nextTestCase);
				}
			}
			if(possibleNextTestCases.size() > 0) {
				List<Selection> r = getBestNewTestCase(possibleNextTestCases, uncoveredTupels);
				if(r != null) {
					if(removeNewlyCoveredTupels(r, uncoveredTupels)) {
						Recommendation tc = new Recommendation();
						tc.addSelections(r);
						recommendations.add(tc);
					} else {
						roundsWithoutNewTestCase++;
					}
				} else {
					roundsWithoutNewTestCase++;
				}
			} else {
				roundsWithoutNewTestCase++;
			}
		} while(uncoveredTupels.size() > 0 && roundsWithoutNewTestCase < maxEmptyRounds);
	}

	private List<Selection> getBestNewTestCase(List<List<Selection>> possibleNextTestCases,
			List<List<Selection>> uncoveredTupels) {
		int max = 1;
		List<List<Selection>> bestTestCases = new ArrayList<>();
		
		for(List<Selection> possibleNextTestCase : possibleNextTestCases) {
			int newlyCoveredTupels = 0;
			for(List<Selection> uncoveredTupel : uncoveredTupels) {
				if(possibleNextTestCase.containsAll(uncoveredTupel)) {
					newlyCoveredTupels++;
				}
			}
			
			if(newlyCoveredTupels > max) {
				bestTestCases.clear();
				max = newlyCoveredTupels;
			}
			if(newlyCoveredTupels == max) {
				bestTestCases.add(possibleNextTestCase);
			}
		}
		
		if(bestTestCases.size() > 0) {
			return bestTestCases.get(randomGenerator.nextInt(bestTestCases.size()));
		} else {
			return null;
		}
	}

	private boolean removeNewlyCoveredTupels(List<Selection> newTest, List<List<Selection>> uncoveredTupels) {
		List<List<Selection>> selectionsToRemove = new ArrayList<>();
		
		for(List<Selection> u : uncoveredTupels) {
			if(newTest.containsAll(u)) {
				selectionsToRemove.add(u);
			}
		}
		
		uncoveredTupels.removeAll(selectionsToRemove);
		
		return selectionsToRemove.size() > 0;
	}

	private List<Selection> generateNextTestCase(List<List<Selection>> uncoveredTupels) {
		// from http://testingeducation.org/BBST/testdesign/Cohen_AETG_System.pdf
		// 1. Choose a parameter f and a value l for f such that that parameter value appears in the greatest number of uncovered pairs.
		// 2. Let f1 =	f. Then choose a random order for the remaining parameters. Then we	have an order for all k parameters 
		// f1, ... fk
		Selection l = getMostUnusedSelection(uncoveredTupels);
		List<ClassificationVariable> listF = new ArrayList<>();
		List<Selection> listV = new ArrayList<>();
		ClassificationVariable f = l.getRootElement();
		listF.add(f);
		listV.add(l);
		
		while(roots.size() > listF.size()) {
			int index = randomGenerator.nextInt(roots.size());
			ClassificationVariable v = roots.get(index);
			if(!listF.contains(v)) {
				listF.add(v);
			}
		}
		
		// 3. Assume that values have been selected for parameters f 1,...,fj. For 1 <=i<=j, let the selected value for f
		// i be called v i. Then choose a value	vj+1 for fj+1 as follows. For each possible value v for	fj,	find the number 
		// of new pairs in the set of pairs {fj+1 =	v and fi=vi for 1 <= i <= j}. Then let vj+1 be one of the values 
		// that appeared in the greatest number of new pairs 
		for(int i = 1; i < listF.size(); i++) {
			List<Selection> bestV = new ArrayList<>();
			int max = -1;
			
			ClassificationVariable fi = listF.get(i);
			List<Selection> valuesForFi = leafsByRoot.get(fi);
			for(Selection v : valuesForFi) {
				int newlyCoveredTupels = calculateNewlyCoveredTupels(listV, v, uncoveredTupels);
				
				if(newlyCoveredTupels > max) {
					max = newlyCoveredTupels;
					bestV.clear();
				}
				if(newlyCoveredTupels == max) {
					bestV.add(v);
				}
			}
			listV.add(bestV.get(randomGenerator.nextInt(bestV.size())));
		}
		
		return listV;
	}

	private int calculateNewlyCoveredTupels(List<Selection> listV, Selection v, List<List<Selection>> uncoveredTupels) {
		List<Selection> allSelections = new ArrayList<>(listV.size() + 1);
		allSelections.addAll(listV);
		allSelections.add(v);
		int count = 0;
		
		for(List<Selection> s : uncoveredTupels) {
			if(allSelections.containsAll(s)) {
				count ++;
			}
		}
		
		return count;
	}

	private List<List<Selection>> generateAllTupels(List<ClassificationVariable> remainingRoots) {
		List<List<Selection>> result = new ArrayList<>();
		if(remainingRoots.size() > 0) {
			ClassificationVariable currentRoot = remainingRoots.remove(0);
			List<List<Selection>> lowerSelections = generateAllTupels(remainingRoots);

			List<Selection> leafsByRootPlusNull = new ArrayList<>(leafsByRoot.get(currentRoot));
			leafsByRootPlusNull.add(null);
			for(List<Selection> lowerSelection : lowerSelections) {
				for(Selection s : leafsByRootPlusNull) {
					List<Selection> l = new ArrayList<>(lowerSelections.size() + 1);
					l.add(s);
					for(Selection t : lowerSelection) {
						if(t != null) {
							l.add(t);
						}
					}
					result.add(l);
				}
			}
		} else {
			result.add(Arrays.asList((Selection)null));
		}
		
		return result;
	}
}
