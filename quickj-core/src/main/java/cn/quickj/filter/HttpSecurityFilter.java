package cn.quickj.filter;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import cn.quickj.action.Action;

/**
 * 除了GET和POST请求方式外，全部踢回请求
 * 
 * @author lbj
 * 
 */
public class HttpSecurityFilter implements ActionFilter {

	public int after(Action action) {
		return 0;
	}

	public int before(Action action) {
		String method = action.getRequest().getMethod().toUpperCase();
		if ("GET".equals(method) || "POST".equals(method))
			return 0;
		else
			action.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return 1;
	}

	public void init(HashMap<String, String> hashMap) {
	}
}
