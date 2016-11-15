package net.bpelunit.suitegenerator.util;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import net.bpelunit.suitegenerator.config.Config;

/**
 * Utility class to provide an easy to use output method
 *
 */
public class XMLElementOutput {

	private static XMLOutputter xml = new XMLOutputter();

	static {
		xml.setFormat(Format.getPrettyFormat());
		xml.getFormat().setIndent(Config.get().getIntendation());
	}

	public static String out(Element e) {
		return xml.outputString(e);
	}

}
