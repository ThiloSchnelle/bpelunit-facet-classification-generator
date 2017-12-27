package net.bpelunit.suitegenerator.recommendation.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;
import net.bpelunit.suitegenerator.recommendation.IRecommender;
import net.bpelunit.suitegenerator.recommendation.Recommendation;
import net.bpelunit.suitegenerator.recommendation.full.FullTestRecommender;
import net.bpelunit.suitegenerator.statistics.IStatistics;

public abstract class AbstractRandomRecommender implements IRecommender {

	private FullTestRecommender fullTestRecommender = new FullTestRecommender();
	
	@Override
	public Collection<Recommendation> getRecommendations() {
		List<Recommendation> allRecommendations = fullTestRecommender.getRecommendations();
		
		int recommendationCount = getMaximumRecommendations();
		
		System.out.println("Choosing " + recommendationCount + " from " + allRecommendations.size() + " possible combinations");
		if(allRecommendations.size() <= recommendationCount) {
			System.out.println("Only " + allRecommendations.size() + " possible recommendations!");
			System.out.println("Returning " + allRecommendations.size() + " random recommendations");
			return allRecommendations;
		}
		
		long seed = System.currentTimeMillis();
//		long seed = 1501709728002l;
		System.out.println("Using random seed: " + seed);
		Random random = new Random(seed);
		if(recommendationCount > 100) {
			System.out.println("!!! ATTENTION: When using the " + getClass().getSimpleName() + ", please make sure that BPELUnit has enough memory to execute the test suite successfully!");
		}
		
		List<Recommendation> randomRecommendations = new ArrayList<>(recommendationCount);
		while(randomRecommendations.size() < recommendationCount) {
			int i = random.nextInt(allRecommendations.size());
			Recommendation r = allRecommendations.remove(i);
			randomRecommendations.add(r);
		}
		
		System.out.println("Returning " + randomRecommendations.size() + " random recommendations");
		
		return randomRecommendations;
	}

	protected abstract int getMaximumRecommendations(); 

	@Override
	public void setClassificationData(IStatistics stat, VariableLibrary variables, Classification classification) {
		fullTestRecommender.setClassificationData(stat, variables, classification);
	}

}
