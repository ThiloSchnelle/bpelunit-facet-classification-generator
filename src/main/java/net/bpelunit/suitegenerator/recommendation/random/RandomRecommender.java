package net.bpelunit.suitegenerator.recommendation.random;

import net.bpelunit.suitegenerator.recommendation.IConfigurableRecommender;

public class RandomRecommender extends AbstractRandomRecommender implements IConfigurableRecommender {

	private int maximumRecommendations;

	@Override
	public void setConfigurationParameter(String size) {
		System.out.println("Configuration passed: " + size);
		this.maximumRecommendations = Integer.valueOf(size);
	}

	@Override
	protected int getMaximumRecommendations() {
		return maximumRecommendations;
	}

}
