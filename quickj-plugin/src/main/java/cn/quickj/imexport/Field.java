package cn.quickj.imexport;

import cn.quickj.imexport.convert.IDataConvertor;

public class Field {
	private String name;
	private IDataConvertor exportConvertor;
	private IDataConvertor importConvertor;
	private String text;
	private String sort;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public IDataConvertor getExportConvertor() {
		return exportConvertor;
	}
	public void setExportConvertor(IDataConvertor exportConvertor) {
		this.exportConvertor = exportConvertor;
	}
	public IDataConvertor getImportConvertor() {
		return importConvertor;
	}
	public void setImportConvertor(IDataConvertor importConvertor) {
		this.importConvertor = importConvertor;
	}
}
