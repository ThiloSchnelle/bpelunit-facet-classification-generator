package net.bpelunit.suitegenerator;

import java.io.File;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationTree;
import net.bpelunit.suitegenerator.datastructures.variables.DataVariableInstance;
import net.bpelunit.suitegenerator.reader.IClassificationReader;
import net.bpelunit.suitegenerator.reader.ICodeFragmentReader;
import net.bpelunit.suitegenerator.reader.ReaderFactory;
import net.bpelunit.suitegenerator.recommendation.Recommender;
import net.bpelunit.suitegenerator.recommendation.permut.Permutation;
import net.bpelunit.suitegenerator.statistics.IStatistics;
import net.bpelunit.suitegenerator.statistics.Statistics;
import net.bpelunit.suitegenerator.suitebuilder.SuiteBuilder;

public class Generator {

	private IClassificationReader classificationReader;
	private ICodeFragmentReader fragmentReader;

	private IStatistics stat = new Statistics();

	public Generator(File classificationTablePath) {
		fragmentReader = ReaderFactory.findFragmentReader(classificationTablePath);
		classificationReader = ReaderFactory.findReader(classificationTablePath);

		classificationReader.readAndEnrich(fragmentReader, stat);
		ClassificationTree tree = classificationReader.getClassification().getTree();

		Config.get().out().printClassificationTree(tree);
	}

	public void generate(File folder, boolean createRecommendations, boolean createNewTestCases) {
		SuiteBuilder sb = new SuiteBuilder();
		sb.buildSuite(fragmentReader.getSkeletalStructure(), classificationReader.getClassification(), folder,
				fragmentReader);
		stat.update(classificationReader.getClassification().getAllClassificationTreeLeaves(),
				fragmentReader.getVariables());
		Config.get().out().printStatistics(stat);

		for (DataVariableInstance d : fragmentReader.getVariables().getDataVarInstances()) {
			if (d.getNumberUsages() == 0) {
				Config.get().out().instanceNotUsed(d);
			} else {
				// System.out.println(d.getVariableName() + ":" + d.getInstanceName() + ": " + d.getNumberUsages());
			}
		}
		if (createRecommendations) {
			Recommender r = new Permutation(stat, fragmentReader.getVariables(),
					classificationReader.getClassification());
			Config.get().out().printRecommendation(r.getRecommendations());
			if (createNewTestCases) {
				sb.addRecommendations(r);
			}
		}
	}

	public IStatistics getStatistics() {
		return stat;
	}

	public IClassificationReader getClassificationReader() {
		return classificationReader;
	}

	public ICodeFragmentReader getFragmentReader() {
		return fragmentReader;
	}

}
