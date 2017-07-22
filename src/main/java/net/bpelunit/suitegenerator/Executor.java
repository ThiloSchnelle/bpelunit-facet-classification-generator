package net.bpelunit.suitegenerator;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.recommendation.IRecommender;
import net.bpelunit.suitegenerator.recommendation.RecommenderFactory;
import net.bpelunit.suitegenerator.recommendation.permut.Permutation;

public class Executor {

	private static boolean createRecommendations = false;
	private static boolean createNewTestCases = false;
	private static IRecommender recommender = new Permutation();
	private static RecommenderFactory recommenderFactory = new RecommenderFactory();
	private static File projectFolder = new File("files/suite/");
	private static String targetSuiteFileName;

	public static void main(String[] args) throws Exception {
		
		Options options = new Options();
		options.addOption("s", true, "Test Suite File relative to the project directory");
		options.addOption("r", false, "Create Recommendations");
		options.addOption("g", false, "Generate Test Cases based on recommendations (implies -r)");
		options.addOption("recommender", true, "Logical Name of a recommender to use. Defaults to permut.");
		options.addOption("recommenderclass", true, "A full qualified class name of the recommender to use. Overrides recommender.");
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);
		
		if(cmd.hasOption("r")) {
			createRecommendations = true;
		}
		if(cmd.hasOption("g")) {
			createRecommendations = true;
			createNewTestCases = true;
		}
		if(cmd.hasOption("s")) {
			targetSuiteFileName = cmd.getOptionValue("s"); 
		} else {
			targetSuiteFileName = Config.get().getOutputName();
		}
		if(cmd.hasOption("recommenderclass")) {
			recommender = recommenderFactory.getRecommenderByClassname(cmd.getOptionValue("recommenderclass"));
		} else if (cmd.hasOption("recommender")) {
			recommender = recommenderFactory.getRecommenderByName(cmd.getOptionValue("recommender"));
		}
		
		if(cmd.getArgList().size() > 0) {
			projectFolder = new File(cmd.getArgList().get(0).toString());
		}
		
		Generator g = new Generator(new File(projectFolder, Config.get().getClassificationTableName()));
		g.generate(projectFolder, createRecommendations ? recommender : null, createNewTestCases, targetSuiteFileName);
	}

}
