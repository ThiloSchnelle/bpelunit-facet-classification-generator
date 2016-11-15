package net.bpelunit.suitegenerator.config;

/**
 * Short named getter for a singleton Configuration instance
 *
 */
public class Config {

	private static IConfiguration instance = new Configuration();

	public static IConfiguration get() {
		return instance;
	}

}
