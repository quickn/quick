package cn.quickj.grid;

import org.jmesa.util.SupportUtils;
import org.jmesa.view.AbstractContextSupport;
import org.jmesa.view.component.Column;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.renderer.HtmlCellRenderer;
import org.jmesa.worksheet.editor.WorksheetEditor;
import org.mvel2.templates.TemplateRuntime;

public class DisplayCellRender extends AbstractContextSupport implements HtmlCellRenderer {
	private String format;
    private WorksheetEditor worksheetEditor;
    private String style;
    private String styleClass;
    private Column column;
    private CellEditor cellEditor;

	public DisplayCellRender(String format, HtmlColumn column){
		this.format = format;
		this.column = column;
	}

	public Object render(Object item, int rowcount) {
		String result = (String) TemplateRuntime.eval(format, item);
		HtmlBuilder html = new HtmlBuilder();
		html.td(2);
		html.width(getColumn().getWidth());
		html.style(getStyle());
		html.styleClass(getStyleClass());
        html.close();
		return html.toString()+result+"</td>";
	}

    public HtmlColumn getColumn() {
        return (HtmlColumn) column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    /**
     * @return The CellEditor.
     */
    public CellEditor getCellEditor() {
        return cellEditor;
    }

    public void setCellEditor(CellEditor cellEditor) {
        this.cellEditor = cellEditor;
        SupportUtils.setWebContext(cellEditor, getWebContext());
        SupportUtils.setCoreContext(cellEditor, getCoreContext());
        SupportUtils.setColumn(cellEditor, getColumn());
    }
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    public WorksheetEditor getWorksheetEditor() {
        return worksheetEditor;
    }

    public void setWorksheetEditor(WorksheetEditor worksheetEditor) {
        this.worksheetEditor = worksheetEditor;
        SupportUtils.setWebContext(worksheetEditor, getWebContext());
        SupportUtils.setCoreContext(worksheetEditor, getCoreContext());
        SupportUtils.setColumn(worksheetEditor, getColumn());
    }
 
}
