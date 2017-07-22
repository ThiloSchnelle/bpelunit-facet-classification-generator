package net.bpelunit.suitegenerator;

import java.io.File;

import net.bpelunit.suitegenerator.config.Config;

public class Executor {

	private static boolean createRecommendations = true;
	private static boolean createNewTestCases = false;

	public static void main(String[] args) {
		File folder;
		if(args.length >= 1) {
			folder = new File(args[0]);
		} else {
			folder = new File("files/suite/");
		}
		
		String targetSuiteFileName = Config.get().getOutputName();
		if(args.length >= 2) {
			targetSuiteFileName = args[1];
		}
		
		Generator g = new Generator(new File(folder, Config.get().getClassificationTableName()));
		g.generate(folder, createRecommendations, createNewTestCases, targetSuiteFileName);
	}

}
