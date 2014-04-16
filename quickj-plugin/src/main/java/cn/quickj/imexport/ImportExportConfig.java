package cn.quickj.imexport;

import java.util.ArrayList;
import java.util.List;

import cn.quickj.imexport.convert.ConvertFilter;

public class ImportExportConfig {
	private String id;
	private String name;
	private String type;
	private String refFields;
	private List<ConvertFilter> filters;
	private Class<?> beanClass;
	private ExportTemplate exportTemplate;
	private Class<?> contextClass;
	private String description;
	public ImportExportConfig(String id, String name, String type,String description) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.description = description;
		filters = new ArrayList<ConvertFilter>();
		exportTemplate = new ExportTemplate();
	}
	public void setExportTemplate(ExportTemplate exportTemplate) {
		this.exportTemplate = exportTemplate;
	}
	public ExportTemplate getExportTemplate() {
		return exportTemplate;
	}
	public void addFilter(ConvertFilter filter) {
		filters.add(filter);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRefFields() {
		return refFields;
	}

	public void setRefFields(String refFields) {
		this.refFields = refFields;
	}

	public List<ConvertFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<ConvertFilter> filters) {
		this.filters = filters;
	}

	public void setBeanClass(Class<?> clazz) {
		this.beanClass = clazz;
	}
	public Class<?> getBeanClass() {
		return beanClass;
	}
	public void setContextClass(Class<?> contextClass) {
		this.contextClass = contextClass;
	}
	public Class<?> getContextClass() {
		return contextClass;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
