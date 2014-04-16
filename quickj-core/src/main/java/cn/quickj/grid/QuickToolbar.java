package cn.quickj.grid;

import org.jmesa.facade.TableFacade;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractToolbar;
import org.jmesa.view.html.toolbar.MaxRowsItem;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemType;

public class QuickToolbar extends AbstractToolbar{

	public QuickToolbar(TableFacade table) {
		setCoreContext(table.getCoreContext());
		setWebContext(table.getWebContext());
	    addToolbarItem(ToolbarItemType.FIRST_PAGE_ITEM);
	    addToolbarItem(ToolbarItemType.PREV_PAGE_ITEM);
	    addToolbarItem(ToolbarItemType.NEXT_PAGE_ITEM);
	    addToolbarItem(ToolbarItemType.LAST_PAGE_ITEM);
	    addToolbarItem(ToolbarItemType.SEPARATOR);
	    addToolbarItem(ToolbarItemType.PAGE_NUMBER_ITEMS);
	    MaxRowsItem maxRowsItem = (MaxRowsItem) addToolbarItem(ToolbarItemType.MAX_ROWS_ITEM);
	    if (getMaxRowsIncrements() != null) {
	        maxRowsItem.setIncrements(getMaxRowsIncrements());
	    }
	}

	@Override
	public String render() {
	    HtmlBuilder html = new HtmlBuilder();

		html.table(2).border("0").width("100%").cellpadding("0").cellspacing("1").close();
		html.tr(3).close();
		html.td(4).close();
		String id = getCoreContext().getLimit().getId();
        html
				.append("选择：<a class=\"check\" href=\"#\" onclick=\"javascript:selectAll('"+id+"');\">全选</a>－");
		html
				.append("<a class=\"check\" href=\"#\" onclick=\"javascript:selectInvert('"+id+"');\">反选</a>－");
		html
				.append("<a class=\"check\" href=\"#\" onclick=\"javascript:selectNone('"+id+"');\">全不选</a> ");
		html.tdEnd();
		html.td(4).width("10px").align("right").close();
		html.table(6).width("100%").border("0").cellpadding("0").cellspacing("0").close();
		html.tr(8).close();
		for (ToolbarItem item : getToolbarItems()) {
			html.td(9).close();
			html.append(item.getToolbarItemRenderer().render());
			html.tdEnd();
		}
		html.trEnd(8);
		html.tableEnd(6);
		html.tdEnd();
		html.trEnd(3);

		html.tableEnd(2);
		html.newline();
		html.tabs(2);

		return html.toString();
	}
}
