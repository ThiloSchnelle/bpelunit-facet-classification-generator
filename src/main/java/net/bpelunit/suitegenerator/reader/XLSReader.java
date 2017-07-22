package net.bpelunit.suitegenerator.reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import net.bpelunit.suitegenerator.config.Config;
import net.bpelunit.suitegenerator.datastructures.classification.Classification;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationTree;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariable;
import net.bpelunit.suitegenerator.datastructures.classification.ClassificationVariableSelection;
import net.bpelunit.suitegenerator.datastructures.classification.IClassificationElement;
import net.bpelunit.suitegenerator.datastructures.conditions.ConditionBundle;
import net.bpelunit.suitegenerator.datastructures.conditions.ConditionParser;
import net.bpelunit.suitegenerator.datastructures.conditions.ICondition;
import net.bpelunit.suitegenerator.datastructures.testcases.TestCase;
import net.bpelunit.suitegenerator.statistics.IStatistics;

public class XLSReader implements IClassificationReader {

	private ClassificationTree tree = new ClassificationTree();
	private Collection<TestCase> tests = new LinkedList<TestCase>();

	private List<XLSColumn> columns = new ArrayList<>(50);
	private Collection<ClassificationVariableSelection> leaves = new ArrayList<>();

	private File file;
	private ConditionBundle conditions = new ConditionBundle();

	private Classification classification = new Classification();

	public XLSReader(File file) {
		this.file = file;
	}

	@Override
	public void readAndEnrich(ICodeFragmentReader fragmentReader, IStatistics stat) {
		try {
			Workbook wb = WorkbookFactory.create(file);
			Sheet classification = wb.getSheetAt(0);
			int maxRow = classification.getLastRowNum();
			int tmp = readForbidden(classification, maxRow);
			tmp = readTree(classification, tmp, maxRow);
			tree.combineWithFragments(fragmentReader);
			this.classification.setTree(tree);
			tmp = findFaultLine(classification, maxRow, tmp);
			readTestCases(classification, maxRow, tmp, stat);
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			// TODO what else to do?
			e.printStackTrace();
		}
	}

	private int readForbidden(Sheet classification, int maxRow) {
		int tmp = 0;
		for (int i = 0; i <= maxRow; i++) {
			Cell c = classification.getRow(i).getCell(0);
			if (c != null && c.toString().equals(Config.get().getForbiddenStart())) {
				tmp = readConditions(classification, i + 1, maxRow);
				break;
			}
		}
		this.classification.setForbidden(conditions);
		return tmp;
	}

	private int readConditions(Sheet classification, int i, int maxRow) {
		for (; i < maxRow; i++) {
			Cell c = classification.getRow(i).getCell(1);
			if (c == null || c.toString().isEmpty()) {
				return i;
			}
			ICondition cond = new ConditionParser().parse(c.toString());
			conditions.addCondition(cond);
			System.out.println(cond);
		}
		return i;
	}

	private int findFaultLine(Sheet classification, int maxRow, int tmp) {
		for (int i = tmp; i <= maxRow; i++) {
			Cell c = classification.getRow(i).getCell(0);
			if (c != null && c.toString().equals(Config.get().getFaultLine())) {
				readFaultLine(classification.getRow(i));
				return i + 1;
			}
		}
		return maxRow;
	}

	private void readFaultLine(Row r) {
		for (int i = 0; i < columns.size(); i++) {
			Cell c = r.getCell(i + 1);
			if (c != null && !c.toString().isEmpty()) {
				columns.get(i).flagFault();
			}
		}
	}

	private void readTestCases(Sheet classification, int maxRow, int tmp, IStatistics stat) {
		for (int i = tmp; i <= maxRow; i++) {
			Cell c = classification.getRow(i).getCell(0);
			if (c != null && c.toString().equals(Config.get().getTestCasesStart())) {
				tmp = i + 1;
				break;
			}
		}
		for (; tmp <= maxRow; tmp++) {
			Row r = classification.getRow(tmp);
			if(!isEmptyRow(r)) {
				readCase(r, stat);
			}
		}
	}

	private int readTree(Sheet classification, int i, int maxRow) {
		int tmp = 0;
		for (; i <= maxRow; i++) {
			Row row = classification.getRow(i);
			Cell c = row != null ? row.getCell(0) : null;
			if (c != null && c.toString().equals(Config.get().getTableStart())) {
				tmp = readClassification(classification, i + 1, maxRow);
				break;
			}
		}
		this.classification.setLeaves(leaves);
		return tmp;
	}

	private void readCase(Row r, IStatistics stat) {
		Cell start = r.getCell(0);
		if (start != null) {
			String caseName = start.toString();
			// System.out.print("Case reading [" + caseName + "]: ");
			TestCase test = new TestCase(caseName);
			for (int i = 0; i < columns.size(); i++) {
				Cell c = r.getCell(i + 1);
				if (c != null && !c.toString().isEmpty()) {
					IClassificationElement ce = columns.get(i).getElement();
					if (ce instanceof ClassificationVariableSelection) {
						test.markAsNecessary((ClassificationVariableSelection) ce);
					}
				}
			}
			stat.addTest(test);
			tests.add(test);
			this.classification.addTestCase(test);
			// System.out.println(test);
		}
	}

	private int readClassification(Sheet classification, int startIndex, int maxRow) {
		for (int line = 0; startIndex <= maxRow; startIndex++, line++) {
			Row r = classification.getRow(startIndex);
			IClassificationElement element = getXLSColumn(0).getElement();
			if(isEmptyRow(r)) {
				return startIndex;
			}
			
			for (int n = 1; n < r.getLastCellNum(); n++) {
				XLSColumn col = getXLSColumn(n - 1);
				Cell c = r.getCell(n);
				boolean fresh = false;
				if (!isEmpty(c)) {
					fresh = true;
					String s = c.toString().trim();
					IClassificationElement neu = null;
					if (line == 0) {
						neu = new ClassificationVariable(s, true);
					} else {
						Row rNext = classification.getRow(startIndex + 1);
						Cell cNext = null;
						if (rNext != null) {
							cNext = rNext.getCell(n);
						}
						neu = readVariable(s, cNext, col);
					}
					col.getElement().addChild(neu);
					element = neu;
				}
				updateColumn(col, element, fresh);
			}
		}
		return startIndex;
	}

	private boolean isEmptyRow(Row r) {
		for(int i = 1; i < r.getLastCellNum(); i++) {
			if(!isEmpty(r.getCell(i))) {
				return false;
			}
		}
		return true;
	}

	private boolean isEmpty(Cell cell) {
		return ((cell == null || cell.toString().trim().isEmpty()));
	}

	private void updateColumn(XLSColumn coll, IClassificationElement element, boolean fresh) {
		if (fresh) {
			coll.markFlagIsFilledSpecifically();
		}
		if (fresh || !coll.isFilledSpecifically()) {
			coll.setClassificationElement(element);
		}
	}

	private IClassificationElement readVariable(String s, Cell cell, XLSColumn col) {
		if (isEmpty(cell)) {
			ClassificationVariableSelection inst = new ClassificationVariableSelection(s);
			leaves.add(inst);
			return inst;
		}
		return new ClassificationVariable(s);
	}

	private XLSColumn getXLSColumn(int n) {
		if (columns.size() > n) {
			return columns.get(n);
		}
		XLSColumn coll = new XLSColumn(tree);
		columns.add(coll);
		return coll;
	}

	@Override
	public Classification getClassification() {
		return classification;
	}
}
