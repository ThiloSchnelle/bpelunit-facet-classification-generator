package net.bpelunit.suitegenerator.recommendation.aetg;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationTree;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.datastructures.conditions.AND;
import net.bpelunit.suitegenerator.datastructures.conditions.OperandCondition;
import net.bpelunit.suitegenerator.recommendation.Recommendation;
import net.bpelunit.suitegenerator.statistics.IStatistics;
import net.bpelunit.suitegenerator.statistics.Selection;
import net.bpelunit.suitegenerator.statistics.Statistics;

public class AetgTest {

	@Test
	public void test_T2_NoConstraints_3_Parameters_With_3_Values_Each() {
		Aetg aetg = new Aetg();
		
		List<ClassificationVariableSelection> variableSelections = new ArrayList<>();
		Classification classification = new Classification();
		ClassificationTree tree = new ClassificationTree();
		for(String name : Arrays.asList("A", "B", "C")) {
			ClassificationVariable classificationVariable = new ClassificationVariable(name, true);
			tree.addChild(classificationVariable);
			for(String value : Arrays.asList("1", "2", "3")) {
				ClassificationVariableSelection s = new ClassificationVariableSelection(value);
				classificationVariable.addChild(s);
				variableSelections.add(s);
			}
		}
		classification.setTree(tree);
		IStatistics stat = new Statistics();
		stat.update(variableSelections, null);
		aetg.setClassificationData(stat , null, classification);
		
		aetg.setConfigurationParameter("seed=1513595980565,M=50");
		List<Recommendation> recommendations = aetg.getRecommendations();
		System.out.println(recommendations);
		assertEquals(9, recommendations.size());
	}
	
	@Test
	public void test_T3_NoConstraints_3_Parameters_With_3_Values_Each() {
		Aetg aetg = new Aetg();
		aetg.setConfigurationParameter("t=3");
		
		List<ClassificationVariableSelection> variableSelections = new ArrayList<>();
		Classification classification = new Classification();
		ClassificationTree tree = new ClassificationTree();
		for(String name : Arrays.asList("A", "B", "C")) {
			ClassificationVariable classificationVariable = new ClassificationVariable(name, true);
			tree.addChild(classificationVariable);
			for(String value : Arrays.asList("1", "2", "3")) {
				ClassificationVariableSelection s = new ClassificationVariableSelection(value);
				classificationVariable.addChild(s);
				variableSelections.add(s);
			}
		}
		classification.setTree(tree);
		IStatistics stat = new Statistics();
		stat.update(variableSelections, null);
		aetg.setClassificationData(stat , null, classification);
		
		List<Recommendation> recommendations = aetg.getRecommendations();
		System.out.println(recommendations);
		assertEquals(27, recommendations.size());
	}
	
	@Test
	public void test_T3_1Constraint_3_Parameters_With_3_Values_Each() {
		Aetg aetg = new Aetg();
		aetg.setConfigurationParameter("t=3,M=1000,m=100");
		
		List<ClassificationVariableSelection> variableSelections = new ArrayList<>();
		Classification classification = new Classification();
		ClassificationTree tree = new ClassificationTree();
		for(String name : Arrays.asList("A", "B", "C")) {
			ClassificationVariable classificationVariable = new ClassificationVariable(name, true);
			tree.addChild(classificationVariable);
			for(String value : Arrays.asList("1", "2", "3")) {
				ClassificationVariableSelection s = new ClassificationVariableSelection(value);
				classificationVariable.addChild(s);
				variableSelections.add(s);
			}
		}
		classification.setTree(tree);
		classification.setForbidden(new AND(new OperandCondition("A:1"), new OperandCondition("B:1")));
		IStatistics stat = new Statistics();
		stat.update(variableSelections, null);
		aetg.setClassificationData(stat , null, classification);
		
		List<Recommendation> recommendations = aetg.getRecommendations();
		System.out.println(recommendations);
		
		// TODO How to check random-sized outputs?
		// assertEquals(20, recommendations.size());
	}

}
