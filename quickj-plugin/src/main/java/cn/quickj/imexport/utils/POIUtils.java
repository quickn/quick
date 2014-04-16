package cn.quickj.imexport.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class POIUtils {
	private static HSSFDataFormatter formatter = new HSSFDataFormatter();

	public static HSSFWorkbook readExcelFile(File excelFile) {
		HSSFWorkbook workbook;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(excelFile));
			return workbook;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取一个Sheet的有效内容的范围，从第一个有内容的行开始，到第最后一个有内容行结束作为行的范围
	 * 从第一个有内容的列开始，到最后一个有内容的列结束作为列的访问。
	 * 
	 * @param sheet
	 * @return
	 */
	public static SheetRegion getSheetRegion(HSSFSheet sheet) {
		SheetRegion sheetRegion = new SheetRegion();
		FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper()
				.createFormulaEvaluator();
		int rows = sheet.getPhysicalNumberOfRows();
		for (int i = 0; i < rows; i++) {
			HSSFRow row = sheet.getRow(i);
			boolean rowEmpty = true;
			if (row != null) {
				int cells = row.getPhysicalNumberOfCells();
				for (int j = 0; j < cells; j++) {
					HSSFCell cell = row.getCell(j);
					if (cell != null) {
						if (getCellValue(cell, evaluator).length() > 0) {
							rowEmpty = false;
							if (sheetRegion.startColumn > j) {
								sheetRegion.startColumn = j;
								sheetRegion.endColumn = j + 1;
							}
							if (sheetRegion.endColumn < j)
								sheetRegion.endColumn = j;
						}
					}
				}
			}
			if (!rowEmpty) {
				if (sheetRegion.startRow > i) {
					sheetRegion.startRow = i;
					sheetRegion.endRow = i + 1;
				}
				if (sheetRegion.endRow < i)
					sheetRegion.endRow = i;
			}
		}
		return sheetRegion;
	}

	public static String getCellValue(HSSFCell cell, FormulaEvaluator evaluator) {
		return formatter.formatCellValue(cell, evaluator);
	}
}
