package net.bpelunit.suitegenerator.recommendation.random;

public class RandomEnvRecommender extends AbstractRandomRecommender {

	@Override
	protected int getMaximumRecommendations() {
		return Integer.parseInt(System.getProperty("classification.random"));
	}
	
}
