package net.bpelunit.suitegenerator.reader;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationTree;

public class FreeMindReaderTest {

	@Test
	public void test() {
		FreeMindReader reader = new FreeMindReader(new File("src/test/resources/myprocesstest.mm"));
		reader.readAndEnrich(null, null);
		
		Classification c = reader.getClassification();
		ClassificationTree tree = c.getTree();
		
		assertEquals("Attr1", tree.getChildren().get(0).getName());
		assertEquals("V1", tree.getChildren().get(0).getChildren().get(0).getName());
		assertEquals("V2", tree.getChildren().get(0).getChildren().get(1).getName());
		assertEquals("Attr2", tree.getChildren().get(1).getName());
		assertEquals("V3", tree.getChildren().get(1).getChildren().get(0).getName());
		assertEquals("V4", tree.getChildren().get(1).getChildren().get(1).getName());
		
		assertEquals(1, c.getTestCases().size());
		assertEquals("TestCase1", c.getTestCases().get(0).getName());
		assertEquals("V3", c.getTestCases().get(0).getSelections().get(0).getName());
		assertEquals("V1", c.getTestCases().get(0).getSelections().get(1).getName());
		
		assertEquals("[Attr1:V1, (Attr1:V1 ^ Attr2:V3)]", c.getForbidden().toString());
	}

}
