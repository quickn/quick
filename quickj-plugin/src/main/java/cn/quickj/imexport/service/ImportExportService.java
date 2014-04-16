package cn.quickj.imexport.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.Transaction;

import cn.quickj.Setting;
import cn.quickj.dispatcher.FilterDispatcher;
import cn.quickj.hibernate.HibernateTemplate;
import cn.quickj.imexport.ExportTemplate;
import cn.quickj.imexport.Field;
import cn.quickj.imexport.ImportExportConfig;
import cn.quickj.imexport.ImportResult;
import cn.quickj.imexport.convert.BigDecimalConvertor;
import cn.quickj.imexport.convert.BigIntegerConvertor;
import cn.quickj.imexport.convert.BooleanConvertor;
import cn.quickj.imexport.convert.ConvertFilter;
import cn.quickj.imexport.convert.DateConvertor;
import cn.quickj.imexport.convert.DoubleConvertor;
import cn.quickj.imexport.convert.FloatConvertor;
import cn.quickj.imexport.convert.IDataConvertor;
import cn.quickj.imexport.convert.ImportContext;
import cn.quickj.imexport.convert.IntegerConvertor;
import cn.quickj.imexport.convert.LongConvertor;
import cn.quickj.imexport.convert.ScriptTextConvertor;
import cn.quickj.imexport.convert.ShortConvertor;
import cn.quickj.imexport.convert.StringConvertor;
import cn.quickj.imexport.model.ExportSchema;
import cn.quickj.imexport.utils.POIUtils;
import cn.quickj.imexport.utils.SheetRegion;
import cn.quickj.session.Session;
import cn.quickj.utils.Dom4jUtils;
import cn.quickj.utils.QuickUtils;
import cn.quickj.utils.StringUtil;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ImportExportService {
	private static Log log = LogFactory.getLog(ImportExportService.class);

	private Map<String, ImportExportConfig> importExportConfigMap;
	/**
	 * 存储Field的name为key的Map。
	 */
	private Map<String, Map<String, Field>> fieldsMap;
	/**
	 * 存储Field的text为key的map
	 */
	private Map<String, Map<String, Field>> fieldsTextMap;
	@Inject
	private HibernateTemplate ht;

	public ImportExportService() {
		fieldsMap = new HashMap<String, Map<String, Field>>();
		fieldsTextMap = new HashMap<String, Map<String, Field>>();
		// 初始化导入导出xml文件
		InputStream is;
		try {
			is = new FileInputStream(Setting.webRoot
					+ "/WEB-INF/imexport-config.xml");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(is);
			initFieldsMap(doc);
			importExportConfigMap = new HashMap<String, ImportExportConfig>();
			initImportExportConfig(Dom4jUtils.getElements(doc.getRootElement(),
					"/config/import"), "import");
			initImportExportConfig(Dom4jUtils.getElements(doc.getRootElement(),
					"/config/export"), "export");
		} catch (Exception e) {
			log.fatal("导入导出配置文件存在致命错误，导入导出将不能正常工作!",e);
		}
	}

	private void initImportExportConfig(List<Element> configs, String type)
			throws Exception {
		for (Element config : configs) {
			String description = config.attributeValue("description");
			if(description==null)
				description="";
			ImportExportConfig importExportConfig = new ImportExportConfig(
					config.attributeValue("id"), config.attributeValue("name"),
					type,description);
			List<Element> results = Dom4jUtils.getElements(config, "filter");
			for (Element element : results) {
				ConvertFilter filter = (ConvertFilter) Class.forName(
						element.getTextTrim()).newInstance();
				importExportConfig.addFilter(filter);
			}
			String refFields = Dom4jUtils.getElement(config, "fields")
					.attributeValue("ref");
			importExportConfig.setRefFields(refFields);
			if (Dom4jUtils.getElement(config, "bean") != null)
				importExportConfig.setBeanClass(Class.forName(Dom4jUtils
						.getElement(config, "bean").getTextTrim()));
			Map<String, Field> fields = fieldsMap.get(refFields);
			if (fields == null)
				throw new IllegalArgumentException("导入导出配置文件节点"
						+ importExportConfig.getName() + "的引用的" + refFields
						+ "字段找不到");

			if ("export".equals(type)) {
				Element e = Dom4jUtils.getElement(config, "template");
				String excelFile = e.attributeValue("file");
				if (excelFile != null) {
					int headerRow = 0, startCol = 0;
					if (e.attributeValue("header") != null)
						headerRow = Integer.valueOf(e.attributeValue("header"));
					if (e.attributeValue("startCol") != null)
						startCol = Integer
								.valueOf(e.attributeValue("startCol"));
					importExportConfig.setExportTemplate(new ExportTemplate(
							excelFile, headerRow, startCol));
				} else {
					String columns = e.attributeValue("columns");
					String url = e.attributeValue("url");
					if (columns != null)
						importExportConfig
								.setExportTemplate(new ExportTemplate(columns
										.split(","), url));
					else {
						log.warn("作为导出程序，配置文件中节点"
								+ importExportConfig.getName()
								+ "既没有Excel模板文件，也不指定导出的列，系统将使用引用的所有字段作为导出列!");

						importExportConfig
								.setExportTemplate(new ExportTemplate(
										(String[]) fields.values().toArray(),
										null));
					}
				}
			} else if ("import".equals(type)) {
				if (Dom4jUtils.getElement(config, "context") != null) {
					String contextClass = Dom4jUtils.getElement(config,
							"context").getText();
					importExportConfig.setContextClass(Class
							.forName(contextClass));
				}
			}
			importExportConfigMap.put(importExportConfig.getId(),
					importExportConfig);
		}
	}

	private void initFieldsMap(Document doc) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		List<Element> configs = Dom4jUtils.getElements(doc.getRootElement(),
				"/config/fields");
		for (Element config : configs) {
			Map<String, Field> fields = new HashMap<String, Field>();
			Map<String, Field> fieldsText = new HashMap<String, Field>();
			List<Element> results = Dom4jUtils.getElements(config, "field");
			for (Element e : results) {
				Field field = new Field();
				field.setName(e.attributeValue("name"));
				field.setText(e.attributeValue("text"));
				String type = e.attributeValue("type");
				String sort = e.attributeValue("sort");
				if (type == null)
					type = "string";
				Element importConvertElement = Dom4jUtils.getElement(e,
						"import-convert");
				Element exportConvertElement = Dom4jUtils.getElement(e,
						"export-convert");
				field.setImportConvertor(createConvert(importConvertElement,
						type));
				field.setExportConvertor(createConvert(exportConvertElement,
						type));
				field.setSort(sort);
				fields.put(field.getName(), field);
				fieldsText.put(field.getText(), field);
			}
			fieldsMap.put(config.attributeValue("id"), fields);
			fieldsTextMap.put(config.attributeValue("id"), fieldsText);
		}
	}

	private IDataConvertor createConvert(Element e, String type)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		IDataConvertor convertor = null;
		if (e == null) {
			if ("integer".equalsIgnoreCase(type))
				convertor = new IntegerConvertor();
			else if ("boolean".equalsIgnoreCase(type))
				convertor = new BooleanConvertor();
			else if ("double".equalsIgnoreCase(type))
				convertor = new DoubleConvertor();
			else if ("double".equalsIgnoreCase(type))
				convertor = new FloatConvertor();
			else if ("date".equalsIgnoreCase(type))
				convertor = new DateConvertor();
			else if ("long".equalsIgnoreCase(type))
				convertor = new LongConvertor();
			else if ("short".equalsIgnoreCase(type))
				convertor = new ShortConvertor();
			else if ("number".equalsIgnoreCase(type))
				convertor = new BigDecimalConvertor();
			else if ("bigint".equalsIgnoreCase(type))
				convertor = new BigIntegerConvertor();
		} else {
			String clazz = e.attributeValue("class");
			if ("script".equalsIgnoreCase(clazz))
				convertor = new ScriptTextConvertor();
			else
				convertor = (IDataConvertor) Class.forName(clazz).newInstance();
			convertor.config(e);
		}
		if (convertor == null)
			convertor = new StringConvertor();
		return convertor;
	}

	public ImportExportConfig getConfig(String id) {
		return importExportConfigMap.get(id);
	}

	public Map<String, Field> getFields(String refId) {
		return fieldsMap.get(refId);
	}

	public void exportExcel(HttpServletRequest request,
			HttpServletResponse response, List<?> results, String exportId,
			String outputFileName) {
		exportExcel(request, response, results, exportId, outputFileName, null);
	}

	public void exportExcel(HttpServletRequest request,
			HttpServletResponse response, List<?> results, String exportId,
			String outputFileName, String[] defColumns) {
		ImportExportConfig config = getConfig(exportId);
		if (config == null) {
			log.warn(exportId + "指定的导出配置没有找到，请检查配置文件或者参数!");
			return;
		}
		Map<String, Field> fields = fieldsTextMap.get(config.getRefFields());
		if (fields == null) {
			log.warn(exportId + "引用的字段定义" + config.getRefFields()
					+ "没有找到请检查配置文件或者参数!");
			return;
		}
		HSSFWorkbook wb;
		HSSFSheet sheet;
		ArrayList<String> columns = new ArrayList<String>();
		int headerRow = config.getExportTemplate().getHeaderRow();
		int startCol = config.getExportTemplate().getStartCol();

		if (defColumns == null
				&& config.getExportTemplate().getExcelFile() != null) {
			wb = POIUtils.readExcelFile(new File(config.getExportTemplate()
					.getExcelFile()));
			FormulaEvaluator evaluator = wb.getCreationHelper()
					.createFormulaEvaluator();
			sheet = wb.getSheetAt(0);
			SheetRegion sr = POIUtils.getSheetRegion(sheet);
			if (sr.isValid() && headerRow < sr.startRow)
				headerRow = sr.startRow;
			if (sr.isValid() && startCol < sr.startColumn)
				startCol = sr.startColumn;
			HSSFRow row = sheet.getRow(headerRow);
			for (int i = startCol; i <= sr.endColumn; i++) {
				columns.add(POIUtils.getCellValue(row.getCell(i), evaluator));
			}
		} else {
			wb = new HSSFWorkbook();
			sheet = wb.createSheet(config.getName());
			HSSFRow row = sheet.createRow(0);
			int i = 0;
			startCol = 0;
			headerRow = 1;
			if (defColumns == null)
				defColumns = config.getExportTemplate().getColumns();
			for (String s : defColumns) {
				columns.add(s);
				HSSFCell cell = row.createCell(i, HSSFCell.CELL_TYPE_STRING);
				i++;
				cell.setCellValue(s);
			}
		}
		// 存储每一列的宽度，用于调整最后的每列的宽度
		int[] columnWidth = new int[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			columnWidth[i] = StringUtil.lengthEx(columns.get(i));
		}
		int rowIndex = headerRow++;
		if (results != null) {
			for (Object o : results) {
				HSSFRow row = sheet.getRow(rowIndex);
				if (row == null)
					row = sheet.createRow(rowIndex);
				for (int i = 0; i < columns.size(); i++) {
					HSSFCell cell = row.getCell(startCol + i);
					if (cell == null)
						cell = row.createCell(startCol + i,
								HSSFCell.CELL_TYPE_STRING);
					Field field = fields.get(columns.get(i));
					String value = field.getExportConvertor().toString(
							QuickUtils.getFieldValue(o, field.getName()));
					cell.setCellValue(value);
					if (columnWidth[i] < StringUtil.lengthEx(value))
						columnWidth[i] = StringUtil.lengthEx(value);
				}
				rowIndex++;
			}
		}
		// 设置每列的宽度，保证每列上的文字都可见。
		for (int i = 0; i < columnWidth.length; i++) {
			sheet.setColumnWidth(i, (columnWidth[i] + 4) * 256);
		}
		try {
			if (request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0) {
				// Browser is IE.
				outputFileName = new String(outputFileName.getBytes("GBK"),
						"ISO-8859-1");
			} else {
				outputFileName = "=?UTF-8?B?"
						+ new String(Base64.encodeBase64(outputFileName
								.getBytes("UTF-8"))) + "?=";
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.reset();
		response.setContentType("application/msexcel;charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ outputFileName);
		OutputStream os;
		try {
			os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ImportResult importExcel(String importId,
			Map<Integer, Field> matchedFields, File excelFile,
			ImportContext context) {
		int success = 0, failure = 0;
		List<String> failures = new ArrayList<String>();
		ImportExportConfig config = importExportConfigMap.get(importId);
		HSSFWorkbook workbook = POIUtils.readExcelFile(excelFile);
		HSSFSheet sheet = workbook.getSheetAt(0);
		SheetRegion sheetRegion = POIUtils.getSheetRegion(sheet);
		FormulaEvaluator evaluator = workbook.getCreationHelper()
				.createFormulaEvaluator();
		StringBuffer sb;
		for (int r = sheetRegion.startRow + 1; r <= sheetRegion.endRow; r++) {
			Transaction t = ht.getSession().beginTransaction();
			sb = new StringBuffer();
			try {
				HSSFRow row = sheet.getRow(r);
				Object bean = config.getBeanClass().newInstance();
				for (Integer col : matchedFields.keySet()) {
					Field field = matchedFields.get(col);
					HSSFCell cell = row.getCell(col);
					String value = POIUtils.getCellValue(cell, evaluator);
					sb.append(value);
					sb.append(",");
					QuickUtils.setFieldValue(bean, field.getName(), field
							.getImportConvertor().convert(value));
				}
				boolean saved = false;
				for (ConvertFilter filter : config.getFilters()) {
					saved = !filter.save(ht, bean, context);
				}
				if (!saved && bean instanceof Serializable)
					ht.save((Serializable) bean);
				t.commit();
				success++;
				Session session = FilterDispatcher.getSession();
				session.set("successCount", success+"");
			} catch (Exception e) {
				e.printStackTrace();
				t.rollback();
				failure++;
				sb.append(e.getMessage());
				failures.add(sb.toString());
			}
		}
		return new ImportResult(success, failure, failures);
	}

	/**
	 * 保存导出模板
	 * @param exportSchema
	 * @return
	 */
	@cn.quickj.annotation.Transaction
	public ExportSchema saveExportSchema(ExportSchema exportSchema) {
		ht.save(exportSchema);
		return exportSchema;
	}

	/**
	 * 查询用户的导出模板
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ExportSchema> findExportSchemaByUserId(Integer userId,String exportId) {
		String hql = "from ExportSchema es where (es.user.id = ? or es.user = null) and es.exportId = ? order by es.id";
		return ht.query(hql).setInteger(0, userId).setString(1, exportId).list();
	}

	/**
	 * 根据ID查询导出模板
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ExportSchema findExportSchemaById(Integer id) {
		String hql = "from ExportSchema es where es.id = ?";
		List<ExportSchema> list = ht.query(hql).setInteger(0, id).list();
		if(!list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	/**
	 * 删除导出模板
	 * @param id
	 */
	@cn.quickj.annotation.Transaction
	public void deleteExportSchema(Integer id) {
		ht.delete(ExportSchema.class,id);
	}
	
	/**
	 * 修改导出模板
	 * @param exportSchema
	 * @return
	 */
	@cn.quickj.annotation.Transaction
	public ExportSchema updateExportSchema(ExportSchema exportSchema){
		ht.update(exportSchema);
		return exportSchema;
	}

}
