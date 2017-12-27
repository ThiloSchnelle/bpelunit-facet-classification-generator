package net.bpelunit.suitegenerator.recommendation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class RecommenderFactory {

	private static final String SYSPROPERTYNAME_RECOMMENDERFACTORY_PROPERTIES = "recommenderfactory.properties";
	private Properties properties = null;
	
	public IRecommender getRecommenderByName(String name) {
		String recommenderClassname = null;
		try {
			if(properties == null) {
				readProperties();
			}
			recommenderClassname = properties.getProperty(name);
			return getRecommenderByClassname(recommenderClassname);
		
		} catch (Exception e) {
			throw new RuntimeException("Illegal RecommenderFactory configuration. Cannot resolve recommender " + name + " (resolved class name: " + recommenderClassname + ")", e);
		}
	}
	
	private void readProperties() throws FileNotFoundException, IOException {
		properties = new Properties();
		String propertiesFilename = System.getProperty(SYSPROPERTYNAME_RECOMMENDERFACTORY_PROPERTIES);
		if(propertiesFilename != null) {
			properties.load(new FileInputStream(propertiesFilename));
		} else {
			properties.load(getClass().getResourceAsStream("recommenderfactory.properties"));
		}
		
	}

	public IRecommender getRecommenderByClassname(String className) throws Exception {
		return (IRecommender) Class.forName(className).newInstance();
	}
	
	public List<String> getAvailableRecommenderNames() {
		try {
			if(properties == null) {
				readProperties();
			}
		
			List<String> recommenders = new ArrayList<String>(properties.size());
			for(Object o : properties.keySet()) {
				recommenders.add(o.toString());
			}
			Collections.sort(recommenders);
			
			return recommenders;
		} catch (Exception e) {
			throw new RuntimeException("Illegal RecommenderFactory configuration", e);
		}
	}
}
