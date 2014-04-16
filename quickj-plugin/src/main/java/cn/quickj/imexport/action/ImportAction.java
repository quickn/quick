package cn.quickj.imexport.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import cn.quickj.AbstractApplication;
import cn.quickj.action.Action;
import cn.quickj.action.LabelValue;
import cn.quickj.imexport.Field;
import cn.quickj.imexport.ImportExportConfig;
import cn.quickj.imexport.ImportResult;
import cn.quickj.imexport.convert.ImportContext;
import cn.quickj.imexport.service.ImportExportService;
import cn.quickj.imexport.utils.POIUtils;
import cn.quickj.imexport.utils.SheetRegion;

import com.google.inject.Inject;

public class ImportAction extends Action {
	@Inject
	private ImportExportService importExportService;
	private String importId;
	private List<Field> fields;
	private List<LabelValue> columns;
	private List<LabelValue> matchedList;
	private List<String> fieldNames;
	private File excelFile;
	private String excelFileName;
	private ImportResult importResult;
	// 对应关系列表。
	private String matched;
	private String description;

	public void init() throws ServletException {
		if (excelFile == null) {
			ImportExportConfig importExportConfig = importExportService
					.getConfig(importId);
			description = importExportConfig.getDescription();
			Map<String, Field> ff = importExportService
					.getFields(importExportConfig.getRefFields());
			fieldNames = new ArrayList<String>();
			for (Field f : ff.values()) {
				fieldNames.add(f.getText());
			}
			render("upload_excel.html");
			return;
		}
		if (importId == null) {
			throw new ServletException("必须指定导入的id号!");
		}
		ImportExportConfig importExportConfig = importExportService
				.getConfig(importId);
		HSSFWorkbook workbook = POIUtils.readExcelFile(excelFile);
		HSSFSheet sheet = workbook.getSheetAt(0);
		setAttribute("totalCount", sheet.getLastRowNum());
		SheetRegion sheetRegion = POIUtils.getSheetRegion(sheet);
		List<LabelValue> tempColumns = new ArrayList<LabelValue>();
		if (sheetRegion.isValid()) {
			HSSFRow row = sheet.getRow(sheetRegion.startRow);
			for (int i = sheetRegion.startColumn; i <= sheetRegion.endColumn; i++) {
				LabelValue lv = new LabelValue(row.getCell(i)
						.getStringCellValue(), String.valueOf(i));
				if (lv.getLabel() != null && lv.getLabel().length() > 0)
					tempColumns.add(lv);
			}
		}
		Map<String, Field> refFields = importExportService
				.getFields(importExportConfig.getRefFields());
		fields = new ArrayList<Field>();
		columns = new ArrayList<LabelValue>();
		matchedList = new ArrayList<LabelValue>();
		Map<String, Field> matchedFields = new HashMap<String, Field>();
		// 匹配2个List，将对应的字段放入matched列表中，并从2个列表中移除。
		for (LabelValue lv : tempColumns) {
			Field field = getFieldByText(refFields, lv.getLabel());
			if (field != null) {
				matchedFields.put(field.getName(), field);
				matchedList
						.add(new LabelValue(field.getText() + "<==>"
								+ lv.getLabel(), field.getName() + ":"
								+ lv.getValue()));
			} else {
				columns.add(lv);
			}
		}
		// 将没有匹配的Field放入fields列表中，用于人工选择。
		for (Field field : refFields.values()) {
			if (matchedFields.get(field.getName()) == null)
				fields.add(field);
		}
		excelFileName = excelFile.getAbsolutePath();
		render("init.html");
	}

	/**
	 * 获取导入成功的条数
	 * 
	 * @return
	 */
	public String returnSuccessCount() {
		if (getAttribute("successCount") == null) {
			return "0";
		} else {
			return getAttribute("successCount").toString();
		}
	}

	private Field getFieldByText(Map<String, Field> refFields, String label) {
		Collection<Field> values = refFields.values();
		for (Field f : values) {
			if (label != null && label.equalsIgnoreCase(f.getText()))
				return f;
		}
		return null;
	}

	/**
	 * 缺省的Excel导入方法
	 */
	public void index() {
		excelFile = new File(excelFileName);
		Map<Integer, Field> matchedFields = new HashMap<Integer, Field>();
		ImportExportConfig importExportConfig = importExportService
				.getConfig(importId);
		Map<String, Field> refFields = importExportService
				.getFields(importExportConfig.getRefFields());
		ImportContext context = (ImportContext) AbstractApplication.injector
				.getInstance(importExportConfig.getContextClass());
		context.init();
		if (matched != null) {
			String[] a1 = matched.split(",");
			for (String id : a1) {
				String[] a2 = id.split(":");
				Field field = refFields.get(a2[0]);
				matchedFields.put(Integer.valueOf(a2[1]), field);
			}
			context.setContextAttribute(ImportContext.REQUEST_IMPORT_CONTEXT,
					getRequest());
			context.setContextAttribute(ImportContext.RESPONSE_IMPORT_CONTEXT,
					getResponse());
			context.setContextAttribute(ImportContext.SESSION_IMPORT_CONTEXT,
					getSession());
			importResult = importExportService.importExcel(importId,
					matchedFields, excelFile, context);
			render("import_result.html");
		}
	}

	public List<Field> getFields() {
		return fields;
	}

	public List<LabelValue> getColumns() {
		return columns;
	}

	public List<LabelValue> getMatchedList() {
		return matchedList;
	}

	public String getImportId() {
		return importId;
	}

	public String getExcelFileName() {
		return excelFileName;
	}

	public ImportResult getImportResult() {
		return importResult;
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public String getDescription() {
		return description;
	}
}
