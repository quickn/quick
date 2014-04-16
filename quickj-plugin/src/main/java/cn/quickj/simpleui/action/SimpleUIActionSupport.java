package cn.quickj.simpleui.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeImpl;

import cn.quickj.action.Action;
import cn.quickj.action.TableListActionSupport;
import cn.quickj.hibernate.Paginate;
import cn.quickj.simpleui.grid.SimpleHtmlView;
import cn.quickj.utils.QuickUtils;

/**
 * 简单UI Action所需要依赖的支持类。
 * 
 * @author Administrator
 * 
 */
public class SimpleUIActionSupport extends Action {

	private Map<String, TableFacade> tables = new HashMap<String, TableFacade>();
	public String _jmesa_table_id;
	public Integer pagesize;
	public boolean ajax;
	private Paginate paginate;

	public String renderTable(Collection<?> c, String tf, String cf) {
		Map<String, String> tableFormat = QuickUtils.parserTableFormat(tf);
		String id = tableFormat.get("id");
		TableFacade table = tables.get(id);
		if (table == null) {
			table = new TableFacadeImpl(id, getRequest());
			tables.put(id, table);
		}
		table = TableListActionSupport.prepareTable(table, c, tableFormat, cf,(paginate!=null)?paginate.getTotal():null);
		table.setView(new SimpleHtmlView());
		return table.render();
	}

	public Paginate getPaginate() {
		if (paginate == null){
			if(_jmesa_table_id != null) {
				paginate = new Paginate(_jmesa_table_id,getRequest());
			}else {
				if(pagesize==null)
					pagesize = 100;
				paginate = new Paginate(pagesize);
			}
		}
		return paginate;
	}

	/**
	 * 设置总数量
	 * 
	 * @param totalCount
	 */
	public void setTotalRows(int totalCount) {
		if (_jmesa_table_id != null) {
			TableFacade table = tables.get(_jmesa_table_id);
			if (table != null)
				table.setTotalRows(totalCount);
		}
	}

	public boolean isAjax() {
		return ajax;
	}
}
