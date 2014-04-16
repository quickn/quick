package cn.quickj.hibernate;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;

import org.hibernate.criterion.Order;

public class Paginate {
	/**
	 * 总的查询数量
	 */
	int total;
	/**
	 * 开始的数量(不是页)
	 */
	int offset;
	/**
	 * 需要查询的数量
	 */
	int count;
	/**
	 * 当前页码。
	 */
	int page;
	/**
	 * 每页数量。
	 */
	int pagesize;
	/**
	 * 总页数
	 */
	int totalPage;

	List<Order> orders = new ArrayList<Order>();

	public Paginate(int pagesize) {
		this.pagesize = pagesize;
		page = 0;
		offset = 0;
		this.count = pagesize;
	}

	public Paginate(int start, int limit) {
		this.offset = start;
		this.count = limit;
		this.pagesize = limit;
	}

	@SuppressWarnings("unchecked")
	public Paginate(String id, ServletRequest request) {
		String mr = request.getParameter(id + "_mr_");
		String p = request.getParameter(id + "_p_");
		if (mr != null)
			pagesize = Integer.parseInt(mr);
		if (p != null)
			page = Integer.parseInt(p) - 1;
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			Pattern pattern = Pattern.compile(id + "_s_[\\d]+_([\\S]+)");
			Matcher m = pattern.matcher(paramName);
			if (m.find()) {
				String propName = m.group(1);
				if (!propName.equalsIgnoreCase("null")) {
					if ("asc".equalsIgnoreCase(request.getParameter(paramName)))
						orders.add(Order.asc(propName));
					else
						orders.add(Order.desc(propName));
				}
			}
		}
	}

	public int getCount() {
		return count;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setTotal(int total) {
		if (offset == 0 && count == 0) {
			offset = page * pagesize;
			count = total - offset;
			if (count > pagesize)
				count = pagesize;
		}
		this.total = total;
	}

	public int getTotal() {
		return total;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
		offset = page * pagesize;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
		offset = page * pagesize;
	}

	public void addSort(String sortField, String direction) {
		if (sortField != null && sortField.length() > 0
				&& "null".equals(sortField)) {
			if ("asc".equalsIgnoreCase(direction))
				orders.add(Order.asc(sortField));
			else
				orders.add(Order.desc(sortField));
		}
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalPage() {
		if (total % pagesize == 0) {
			totalPage = total / pagesize;
		} else {
			totalPage = total / pagesize + 1;
		}
		return totalPage;
	}
}
