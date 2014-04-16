package cn.quickj.imexport.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.quickj.action.Action;
import cn.quickj.imexport.Field;
import cn.quickj.imexport.ImportExportConfig;
import cn.quickj.imexport.model.ExportSchema;
import cn.quickj.imexport.service.ImportExportService;

import com.google.inject.Inject;

public class ExportAction extends Action {
	@Inject
	private ImportExportService importExportService;
	@Inject
	private ExportSchema exportSchema;//模板表
	private List<Field> fields;//系统字段集合
	private String exportId;//Xml中的导出ID
	private String url;//做导出动作的URL
	private String[] exportColumns;//默认导出的字段
	private String data;//导出时的参数，一个参数直接跟后面，如：data="?"。若有多个，用特定符合分隔开，如：data="userId=?;exportId=?"
	private List<ExportSchema> exportSchemas;//模板集合
	private Integer userId;//登录用户Id

	/**
	 * 初始化导出数据 DIV选择模式
	 */
	public void init() {
		ImportExportConfig exportExportConfig = importExportService
				.getConfig(exportId);
		Map<String, Field> refFields = importExportService
				.getFields(exportExportConfig.getRefFields());
		fields = new ArrayList<Field>();
		for (Field field : refFields.values()) {
			fields.add(field);
		}
		fields = sortFields(fields);
		url = exportExportConfig.getExportTemplate().getUrl();
		exportColumns = exportExportConfig.getExportTemplate().getColumns();
		exportSchemas = importExportService.findExportSchemaByUserId(userId,
				exportId);
		render("export_excel.html");
	}

	/**
	 * 根据sort对Field进行排序
	 * @param fields
	 * @return
	 */
	private List<Field> sortFields(List<Field> fields) {
		for (int i = 0; i < fields.size(); i++) {
			for (int j = i; j < fields.size(); j++) {
				String sort1 = fields.get(i).getSort();
				String sort2 = fields.get(j).getSort();
				if(sort1 != null && !sort1.equals("") && sort2 != null && !sort2.equals("")){
					if(Integer.valueOf(sort1) > Integer.valueOf(sort2)){
						Field f = fields.get(i);
						fields.set(i, fields.get(j));
						fields.set(j, f);
					}
				}
			}
		}
		return fields;
	}

	/**
	 * 初始化导出数据 选择框模式
	 */
	public void initCheckbox() {
		ImportExportConfig exportExportConfig = importExportService
				.getConfig(exportId);
		Map<String, Field> refFields = importExportService
				.getFields(exportExportConfig.getRefFields());
		fields = new ArrayList<Field>();
		for (Field field : refFields.values()) {
			fields.add(field);
		}
		fields = sortFields(fields);
		url = exportExportConfig.getExportTemplate().getUrl();
		exportColumns = exportExportConfig.getExportTemplate().getColumns();
		render("export_checkbox_excel.html");
	}

	/**
	 * 转到创建导出模板页面
	 */
	public void createExportSchema() {
		render("create_exportSchema.html");
	}

	/**
	 * 保存导出模板
	 */
	public void saveExportSchema() {
		if (exportSchema.getIsShare() != null && exportSchema.getIsShare() == 1) {
			exportSchema.setUser(null);
		}
		exportSchema = importExportService.saveExportSchema(exportSchema);
		render("create_exportSchema.html");
	}

	/**
	 * 修改导出模板
	 * @param esId
	 * @return
	 */
	public String updateExportSchema(String esId) {
		ExportSchema es = importExportService.findExportSchemaById(Integer
				.valueOf(esId));
		es.setDefColumns(exportSchema.getDefColumns());
		importExportService.updateExportSchema(es);
		return "保存成功";
	}

	/**
	 * 删除导出模板
	 * @param esId
	 */
	public void deleteExportSchema(String esId) {
		importExportService.deleteExportSchema(Integer.valueOf(esId));
	}

	/**
	 * 选择导出模板
	 */
	public void selectExportSchema() {
		ImportExportConfig exportExportConfig = importExportService
				.getConfig(exportId);
		Map<String, Field> refFields = importExportService
				.getFields(exportExportConfig.getRefFields());
		fields = new ArrayList<Field>();
		for (Field field : refFields.values()) {
			fields.add(field);
		}
		fields = sortFields(fields);
		url = exportExportConfig.getExportTemplate().getUrl();
		ExportSchema es = importExportService.findExportSchemaById(exportSchema
				.getId());
		if (es != null) {
			exportColumns = es.getDefColumns().split(",");
		}
		exportSchemas = importExportService.findExportSchemaByUserId(userId,
				exportId);
		render("export_excel.html");
	}

	public List<Field> getFields() {
		return fields;
	}

	public String getExportId() {
		return exportId;
	}

	public String getUrl() {
		return url;
	}

	public String[] getExportColumns() {
		return exportColumns;
	}

	public String getData() {
		return data;
	}

	public ExportSchema getExportSchema() {
		return exportSchema;
	}

	public List<ExportSchema> getExportSchemas() {
		return exportSchemas;
	}

	public Integer getUserId() {
		return userId;
	}
}
