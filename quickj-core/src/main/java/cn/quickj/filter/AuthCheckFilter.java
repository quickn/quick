package cn.quickj.filter;

import java.util.HashMap;

import org.mortbay.util.URIUtil;

import cn.quickj.action.Action;
import cn.quickj.filter.ActionFilter;

/**
 * 登录验证的Filter，实现记住密码的功能。
 * @author lbj
 *
 */
public class AuthCheckFilter implements ActionFilter {

	/**
	 * 如果不指定页面，则使用缺省的登录页面.
	 */
	private String page = "login.html";
	/**
	 * 需要跳过的uri
	 */
	private String[] ignores = { "/login" };
	/**
	 * 存储user信息的session key的名称
	 */
	private String key = "QUICKJ_USER_BEAN";

	public int after(Action action) {
		return 0;
	}

	public int before(Action action) {
		String uri = (String) action.getRequest().getAttribute("uri");
		if (uri == null) {
			uri = action.getRequest().getRequestURI();
			String contextPath = action.getRequest().getContextPath();
			uri = uri.substring(contextPath.length());
			uri = URIUtil.decodePath(uri);
		}
		for (String ignore : ignores) {
			if (uri.startsWith(ignore)) {
				return ActionFilter.NEED_PROCESS;
			}
		}
		if (action.getAttribute(key) != null)
			return ActionFilter.NEED_PROCESS;
		action.redirect(action.getCtx() + "/" + page);
		return ActionFilter.NO_PROCESS;
	}

	public void init() {

	}

	public void init(HashMap<String, String> params) {
		page = params.get("page");
		String ignore = params.get("ignore");
		key = params.get("key");
		if (page == null)
			page = "login.html";
		if (ignore != null) {
			ignores = ignore.split("\\u007C");
		}
		if (key == null)
			key = "QUICKJ_USER_BEAN";
	}

}
