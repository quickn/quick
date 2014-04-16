package cn.quickj.action;

import static org.jmesa.facade.TableFacadeFactory.createTableFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jmesa.facade.TableFacade;
import org.jmesa.limit.ExportType;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.editor.GroupCellEditor;
import org.jmesa.view.editor.NumberCellEditor;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlTable;

import cn.quickj.grid.CheckboxEditor;
import cn.quickj.grid.CheckboxHeaderEditor;
import cn.quickj.grid.DisplayCellRender;
import cn.quickj.grid.QuickToolbar;
import cn.quickj.utils.QuickUtils;

/**
 * 提供JMESA的Table支持。
 * 
 * @author lbj
 * 
 */
public class TableListActionSupport extends Action {
	private Map<String,TableFacade> tables = new HashMap<String, TableFacade>();

	public String renderTable(Collection<?> c, String tf, String cf) {
		Map<String, String> tableFormat = QuickUtils.parserTableFormat(tf);
		String id = tableFormat.get("id");
		TableFacade table = tables.get(id);
		if(table==null){
			table = createTableFacade(tableFormat.get("id"), getRequest());
		}
		table = prepareTable(table,c, tableFormat, cf);
		return table.render();
	}
	public static TableFacade prepareTable(TableFacade table, Collection<?> c, Map<String, String> tableFormat, String cf) {
		return prepareTable(table, c, tableFormat, cf,null);
	}
	public static TableFacade prepareTable(TableFacade table, Collection<?> c, Map<String, String> tableFormat, String cf, Integer total) {
		ArrayList<Map<String, String>> columns = QuickUtils
				.parserColumnFormat(cf);

		if (tableFormat.get("pagesize")!=null){
			table.setMaxRows(Integer.parseInt(tableFormat.get("pagesize")));
		}
		if(total!=null){
			table.setTotalRows(total);
		}
		table.setItems(c);
		setColumns(table,columns);
		HtmlTable t = (HtmlTable) table.getTable();
		if (tableFormat.get("width") != null) {
			t.getTableRenderer().setWidth(tableFormat.get("width"));
		}

		QuickToolbar tb = new QuickToolbar(table);

		if (tableFormat.get("export") != null) {
			String ets = tableFormat.get("export");
			String[] types = ets.split(",");
			for (String type : types) {
				if ("xls".equalsIgnoreCase(type))
					tb.addExportToolbarItem(ExportType.EXCEL);
				else if ("pdf".equalsIgnoreCase(type))
					tb.addExportToolbarItem(ExportType.PDF);
				else if ("pdfp".equalsIgnoreCase(type))
					tb.addExportToolbarItem(ExportType.PDFP);
				else if ("jxls".equalsIgnoreCase(type))
					tb.addExportToolbarItem(ExportType.JEXCEL);
				else if ("csv".equalsIgnoreCase(type))
					tb.addExportToolbarItem(ExportType.CSV);
			}
		}
		table.setToolbar(tb);
		return table;
	}

	public static void setColumns(TableFacade table, ArrayList<Map<String, String>> columns) {
		String[] columnProperties = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			Map<String, String> attributes = columns.get(i);
			columnProperties[i] = attributes.get("name");
		}
		table.setColumnProperties(columnProperties);

		Row r = table.getTable().getRow();
		// 是否已经设了unique标志。
		boolean isSetUnique = false;
		for (int i = 0; i < columnProperties.length; i++) {
			Map<String, String> attributes = columns.get(i);
			HtmlColumn column = (HtmlColumn) r.getColumn(i);
			String title = attributes.get("title");
			String width = attributes.get("width");
			String format = attributes.get("format");
			String key = attributes.get("key");
			String editable = attributes.get("edit");
			String sortable = attributes.get("sort");
			String filterable = attributes.get("filter");
			String groupable = attributes.get("group");
			String unique = attributes.get("unique");
			if (title != null)
				column.setTitle(title);
			if (width != null)
				column.setWidth(width);
			// 设置列的名称，适合只有一种语言的中文形式，如果是多语言，需要用properties文件。
			if (key != null)
				column.setTitleKey(key);
			if (editable != null)
				column.setEditable(Boolean.parseBoolean(editable));
			else
				column.setEditable(false);
			if (sortable != null)
				column.setSortable(Boolean.parseBoolean(sortable));
			else
				column.setSortable(false);
			if (filterable != null)
				column.setFilterable(Boolean.parseBoolean(filterable));
			else
				column.setFilterable(false);
			if (unique != null) {
				isSetUnique = true;
				r.setUniqueProperty(columnProperties[i]);
			}
			if (format != null) {
				if (format.startsWith("%N")) {
					column.getCellRenderer().setCellEditor(
							new NumberCellEditor(format.substring(2)));
				} else if (format.startsWith("%D"))
					column.getCellRenderer().setCellEditor(
							new DateCellEditor(format.substring(2)));
				else {
					CellEditor ce = column.getCellRenderer().getCellEditor();
					DisplayCellRender displayRender = new DisplayCellRender(
							format, column);
					displayRender.setCoreContext(table.getCoreContext());
					displayRender.setWebContext(table.getWebContext());
					displayRender.setCellEditor(ce);
					column.setCellRenderer(displayRender);
				}
			}
			if (groupable != null && Boolean.parseBoolean(groupable))
				column.getCellRenderer().setCellEditor(
						new GroupCellEditor(column.getCellRenderer()
								.getCellEditor()));

		}
		HtmlColumn chkbox =null;
		try{
			chkbox = (HtmlColumn) r.getColumn("selected");
		}catch(IllegalStateException e){
		}
		if (chkbox != null) {
			if (isSetUnique == false) {
				r.setUniqueProperty("id");
			}
			chkbox.getHeaderRenderer().setHeaderEditor(
					new CheckboxHeaderEditor());
			chkbox.getCellRenderer().setCellEditor(new CheckboxEditor());
		}

	}
}
