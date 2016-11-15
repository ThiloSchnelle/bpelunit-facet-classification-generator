package net.bpelunit.suitegenerator.reader;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;

public class ReaderFactory {

	public static IClassificationReader findReader(File f) {
		return new XLSReader(f);
	}

	public static ICodeFragmentReader findFragmentReader(File f) {
		try {
			return new CodeFragmentReader(f);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
