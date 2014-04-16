package cn.quickj.imexport;

public class ExportTemplate {
	private String excelFile;
	private int headerRow;
	private int startCol;
	private String[] columns;
	private String url;
	public ExportTemplate() {
	}
	public ExportTemplate(String[] columns,String url) {
		super();
		this.columns = columns;
		this.url = url;
	}
	public ExportTemplate(String excelFile, int headerRow, int startCol) {
		super();
		this.excelFile = excelFile;
		this.headerRow = headerRow;
		this.startCol = startCol;
	}
	public String getExcelFile() {
		return excelFile;
	}
	public void setExcelFile(String excelFile) {
		this.excelFile = excelFile;
	}
	public int getHeaderRow() {
		return headerRow;
	}
	public void setHeaderRow(int headerRow) {
		this.headerRow = headerRow;
	}
	public int getStartCol() {
		return startCol;
	}
	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}
	public String[] getColumns() {
		return columns;
	}
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
