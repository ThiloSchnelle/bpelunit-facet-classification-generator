package net.bpelunit.suitegenerator.recommendation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class RecommenderFactory {

	private static final String SYSPROPERTYNAME_RECOMMENDERFACTORY_PROPERTIES = "recommenderfactory.properties";
	private Properties properties = null;
	
	public IRecommender getRecommenderByName(String name) {
		try {
			
			if(properties == null) {
				readProperties();
			}
			return getRecommenderByClassname(properties.getProperty(name));
		
		} catch (Exception e) {
			throw new RuntimeException("Illegal RecommenderFactory configuration", e);
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
	
}
