package cn.quickj.simpleui.grid;

import static org.jmesa.view.html.HtmlConstants.TOOLBAR_MAX_PAGE_NUMBERS;
import static org.jmesa.view.html.HtmlUtils.totalPages;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jmesa.limit.Limit;
import org.jmesa.limit.RowSelect;
import org.jmesa.view.component.Column;
import org.jmesa.view.html.AbstractHtmlView;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlUtils;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;

public class SimpleHtmlView extends AbstractHtmlView {

	public Object render() {
		HtmlBuilder html = new HtmlBuilder();
		String id = getCoreContext().getLimit().getId();
		html.table(0).styleClass("datalist fixwidth").id(id).close();
		html.thead(2).close();
		HtmlRow row = getTable().getRow();
		List<Column> columns = row.getColumns();

		for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
			HtmlColumn column = (HtmlColumn) iter.next();
			html.append(column.getHeaderRenderer().render());
		}
		html.theadEnd(2);

		int rowcount = HtmlUtils.startingRowcount(getCoreContext());

		Collection<?> items = getCoreContext().getPageItems();
		for (Object item : items) {
			rowcount++;
			html.append(row.getRowRenderer().render(item, rowcount));
			for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
				HtmlColumn column = (HtmlColumn) iter.next();
				html.append(column.getCellRenderer().render(item, rowcount));
			}
			html.trEnd(1);
		}
		int totalRows = getCoreContext().getLimit().getRowSelect()
				.getTotalRows();
		int maxRows = getCoreContext().getLimit().getRowSelect().getMaxRows();
		String str = "";
		if (totalRows == 0) {
			str = "共有记录<em>" + totalRows + "</em>个 每页<em>0</em>条 共<em>0</em>页";
		} else {
			if (totalRows % maxRows == 0) {
				str = "共有记录<em>"
						+ totalRows
						+ "</em>个 每页<input type=text maxLength=3 onchange=javascript:changeMaxRowsToLimit('"
						+ id + "',this) value=" + maxRows + ">条 共<em>"
						+ (totalRows / maxRows) + "</em>页";
			} else {
				str = "共有记录<em>"
						+ totalRows
						+ "</em>个 每页<input type=text maxLength=3 onchange=javascript:changeMaxRowsToLimit('"
						+ id + "',this) value=" + maxRows + ">条 共<em>"
						+ (totalRows / maxRows + 1) + "</em>页";
			}
		}
		html.tr(2).styleClass("nobg").close().td(3).styleClass("tdpage")
				.colspan(String.valueOf(columns.size())).close().div()
				.styleClass("pages").close().append(str);
		addPageNumber(id, html);
		html.divEnd().tdEnd().trEnd(2);
		html.tbodyEnd(1).tableEnd(0);
		appendJavascript(id, maxRows, html);
		return html.toString();
	}

	private void appendJavascript(String id, int maxRow, HtmlBuilder html) {
		html
				.append("<script type=\"text/javascript\" >addTableFacadeToManager('"
						+ id
						+ "');setMaxRowsToLimit('"
						+ id
						+ "','"
						+ maxRow
						+ "');setPageToLimit('"
						+ id
						+ "','1');setPageToLimit('"
						+ id
						+ "','1');jQuery(\"input[name='"
						+ id
						+ "_toggle"
						+ "']\").eq(0).toggle(function(){selectAll('"
						+ id
						+ "');},function(){selectNone('"
						+ id
						+ "');});	</script>");

	}

	private void addPageNumber(String id, HtmlBuilder html) {
		Limit limit = getCoreContext().getLimit();
		RowSelect rowSelect = limit.getRowSelect();

		int page = rowSelect.getPage();
		int totalPages = totalPages(getCoreContext());

		int maxPages = Integer.valueOf(getCoreContext().getPreference(
				TOOLBAR_MAX_PAGE_NUMBERS));

		int centerPage = maxPages / 2 + 1;
		int startEndPages = maxPages / 2;

		html.a().href(
				"javascript:setPageToLimit('" + id + "','" + 1
						+ "');onInvokeAction('" + id + "','page_number');")
				.close().append("首&nbsp;&nbsp;&nbsp;页").aEnd();
		if (page > 1)
			html.a().styleClass("prev").href(
					"javascript:setPageToLimit('" + id + "','" + (page - 1)
							+ "');onInvokeAction('" + id + "','page_number');")
					.close().append("&lsaquo;&lsaquo;").aEnd();
		if (totalPages > maxPages) {
			int start;
			int end;

			if (page <= centerPage) { // the start of the pages
				start = 1;
				end = maxPages;
			} else if (page >= totalPages - startEndPages) { // the last few
				// pages
				start = totalPages - (maxPages - 1);
				end = totalPages;
			} else { // center everything else
				start = page - startEndPages;
				end = page + startEndPages;
			}
			for (int i = start; i <= end; i++) {
				if (i == page) {
					html.append("<strong>").append(i).append("</strong>");
				} else
					html.a().href(
							"javascript:setPageToLimit('" + id + "','" + i
									+ "');onInvokeAction('" + id
									+ "','page_number');").close().append(i)
							.aEnd();
			}
		} else {
			for (int i = 1; i <= totalPages; i++) {
				if (i == page) {
					html.append("<strong>").append(i).append("</strong>");
				} else
					html.a().href(
							"javascript:setPageToLimit('" + id + "','" + i
									+ "');onInvokeAction('" + id
									+ "','page_number');").close().append(i)
							.aEnd();
			}
		}
		if (page < totalPages)
			html.a().styleClass("prev").href(
					"javascript:setPageToLimit('" + id + "','" + (page + 1)
							+ "');onInvokeAction('" + id + "','page_number');")
					.close().append("&rsaquo;&rsaquo;").aEnd();
		if (totalPages == 0) {
			html.a().href("javascript:").close().append("最后一页").aEnd();
		} else {
			html.a().href(
					"javascript:setPageToLimit('" + id + "','" + (totalPages)
							+ "');onInvokeAction('" + id + "','page_number');")
					.close().append("最后一页").aEnd();
		}
	}
}
