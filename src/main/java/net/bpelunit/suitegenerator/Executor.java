package net.bpelunit.suitegenerator;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.recommendation.IConfigurableRecommender;
import net.bpelunit.suitegenerator.recommendation.IRecommender;
import net.bpelunit.suitegenerator.recommendation.RecommenderFactory;
import net.bpelunit.suitegenerator.recommendation.permut.Permutation;

public class Executor {

	private static boolean createRecommendations = false;
	private static boolean createNewTestCases = false;
	private static boolean ignoreUserTestCases = false;
	private static IRecommender recommender = new Permutation();
	private static RecommenderFactory recommenderFactory = new RecommenderFactory();
	private static File projectFolder = new File("files/suite/");
	private static String targetSuiteFileName;

	public static void main(String[] args) throws Exception {
		
		Options options = new Options();
		options.addOption("c", true, "Filename of the Excel Test Classification [default: " + Config.get().getClassificationTableName() + "]");
		options.addOption("s", true, "Test Suite File relative to the project directory");
		options.addOption("r", false, "Create Recommendations");
		options.addOption("g", false, "Generate Test Cases based on recommendations (implies -r)");
		String recommenders = String.join(",", recommenderFactory.getAvailableRecommenderNames().toArray(new String[0]));
		options.addOption("recommender", true, "Logical Name of a recommender to use. [default: permut], available options: " + recommenders);
		options.addOption("n", true, "Test Cases to select with suitable recommender, e.g. random recommender=test suites to select, AETG etc. t-level, ...");
		options.addOption("recommenderclass", true, "A full qualified class name of the recommender to use. Overrides recommender");
		options.addOption("ignoreusertestcases", false, "Ignore all user-defined test cases. Only usable with -g");
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);
		
		if(cmd.hasOption("r")) {
			createRecommendations = true;
		}
		if(cmd.hasOption("g")) {
			createRecommendations = true;
			createNewTestCases = true;
			
			if(cmd.hasOption("ignoreusertestcases")) {
				ignoreUserTestCases = true;
			}
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
		
		if(cmd.hasOption("n")) {
			if(recommender instanceof IConfigurableRecommender) {
				((IConfigurableRecommender)recommender).setConfigurationParameter(cmd.getOptionValue("n"));
			} else {
				System.err.println("Recommender " + recommender.getClass() + " is not configurable (-n)!");
				System.exit(1);
			}
		}
		
		if(cmd.getArgList().size() > 0) {
			projectFolder = new File(cmd.getArgList().get(0).toString());
		}
		
		String classificationFileName = Config.get().getClassificationTableName();
		if(cmd.hasOption("c")) {
			classificationFileName = cmd.getOptionValue("c");
		}
		
		Generator g = new Generator(new File(projectFolder, classificationFileName));
		g.generate(projectFolder, createRecommendations ? recommender : null, createNewTestCases, targetSuiteFileName, ignoreUserTestCases);
	}

}
