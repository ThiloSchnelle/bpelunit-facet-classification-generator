package net.bpelunit.suitegenerator.recommendation;

import java.util.Collection;

import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.datastructures.variables.VariableLibrary;
import net.bpelunit.suitegenerator.statistics.IStatistics;

public interface IRecommender {

	Collection<Recommendation> getRecommendations();

	void setClassificationData(IStatistics stat, VariableLibrary variables, Classification classification);

}
