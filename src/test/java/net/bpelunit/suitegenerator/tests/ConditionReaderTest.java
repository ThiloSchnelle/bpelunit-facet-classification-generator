package net.bpelunit.suitegenerator.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.bpelunit.suitegenerator.datastructures.conditions.ConditionParser;

public class ConditionReaderTest {

	@Test
	public void test() {
		assertEquals("Betrag:ZuGroß", new ConditionParser().parse("(Betrag:ZuGroß)").toString());

		assertEquals("(Betrag:ZuGroß && (Bestandskunde && BLA))",
				new ConditionParser().parse("Betrag:ZuGroß AND (Bestandskunde AND BLA)").toString());

		assertEquals("(((Blubb ^ Bestandskunde:Ja) && (ZahlPDFs:Größer1 || Bla)) && (!Betrag:ZuGroß))",
				new ConditionParser()
						.parse("(Blubb XOR Bestandskunde:Ja) AND (ZahlPDFs:Größer1 OR Bla) AND NOT Betrag:ZuGroß")
						.toString());

		assertEquals("((Bestandskunde:Ja && (ZahlPDFs:Größer1 || Bla)) && (!Betrag:ZuGroß))", new ConditionParser()
				.parse("Bestandskunde:Ja AND (ZahlPDFs:Größer1 OR Bla) AND NOT Betrag:ZuGroß").toString());

		assertEquals("(Bestandskunde:Ja && (!(ZahlPDFs:Größer1 || Betrag:ZuGroß)))",
				new ConditionParser().parse("Bestandskunde:Ja AND NOT (ZahlPDFs:Größer1 OR Betrag:ZuGroß)").toString());

		assertEquals("((Bestandskunde:Ja && (!Betrag:ZuGroß)) && (ZahlPDFs:Größer1 || Bla))", new ConditionParser()
				.parse("Bestandskunde:Ja AND NOT Betrag:ZuGroß AND (ZahlPDFs:Größer1 OR Bla)").toString());
	}

}
