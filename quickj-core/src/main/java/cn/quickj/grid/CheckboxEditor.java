package cn.quickj.grid;

import org.jmesa.view.component.Row;
import org.jmesa.view.editor.AbstractCellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.worksheet.UniqueProperty;

public class CheckboxEditor extends AbstractCellEditor {

	private static final String UNIQUE_PROPERTY = "up";
	public Object getValue(Object item, String property, int rowcount) {
		HtmlBuilder html = new HtmlBuilder();
		String id = getCoreContext().getLimit().getId();
        Row row = getColumn().getRow();
        UniqueProperty uniqueProperty = row.getUniqueProperty(item);
        String value;
        if(uniqueProperty==null){
        	value = String.valueOf(rowcount);
        }else{
        	value = uniqueProperty.getValue();
        }
		html.input().type("checkbox").id(id+"_chk_" + rowcount).name(
				id+"_chk_" + rowcount).value(value).end();
		return html.toString();
	}
    /**
     * @param item The Bean or Map.
     * @return The JavaScript for the unique properties.
     */
    protected String getUniquePropertyJavaScript(Object item) {
        Row row = getColumn().getRow();
        UniqueProperty uniqueProperty = row.getUniqueProperty(item);
        if (uniqueProperty == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("var " + UNIQUE_PROPERTY + " = {};");
        sb.append(UNIQUE_PROPERTY + "['" + uniqueProperty.getName() + "']='" + uniqueProperty.getValue() + "';");
        return sb.toString();
    }
}
