package cn.quickj.action;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.quickj.Setting;
import cn.quickj.dispatcher.FilterDispatcher;
import cn.quickj.filter.ActionFilter;
import cn.quickj.filter.FormTokenFilter;
import cn.quickj.manager.CacheManager;
import cn.quickj.manager.TemplateManager;
import cn.quickj.plugin.Plugin;
import cn.quickj.session.Session;

import com.google.inject.Inject;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class Action implements TokenAction {
	/**
	 * 定义只在一次请求响应中有效的Flash类型,类似于Rails的Flash。
	 */

	//@Inject
	//public FlashMap<String, Object> flash;
	protected CacheManager cacheManager;
	protected ArrayList<ActionFilter> filters = new ArrayList<ActionFilter>();
	@Inject
	private HttpServletRequest request;
	@Inject
	private HttpServletResponse response;
	protected Template template;

	protected String message;
	protected String errorMsg;
	private Plugin plugin;
	private String forward;
	private String ctx;
	private String token;

	public String getCtx() {
		if (ctx == null) {
			ctx = request.getContextPath();
		}
		return ctx;
	}

	public Object getAttribute(String key) {
		Session session = getSession();
		if (session != null)
			return session.get(key);
		return null;
	}

	public CacheManager getCacheManager() {
		return CacheManager.getCacheManager();
	}

	public ArrayList<ActionFilter> getFilters() {
		return filters;
	}

	//public Map<String, Object> getFlash() {
	//	return flash;
	//}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Template getTemplate() {
		return template;
	}

	/**
	 * 重定向到一个URL.
	 * 
	 * @param url
	 */
	public void redirect(String url) {
		response.setHeader("Location", url);
	}

	public void forward(String url) {
		this.forward = url;
	}

	/**
	 * 返回标准的html页面。
	 * 
	 * @param templateFile
	 */
	public void render(String templateFile) {
		if (plugin != null) {
			if (Setting.theme != null)
				templateFile = "plugins/" + Setting.theme + "/"
						+ plugin.getId() + "/" + templateFile;
			else
				templateFile = "plugins/" + plugin.getId() + "/" + templateFile;
		}
		renderIt(templateFile);
	}

	private void renderIt(String templateFile) {
		try {
			response.setContentType("text/html; charset="
					+ Setting.DEFAULT_CHARSET);
			template = TemplateManager.getEngine().cfg
					.getTemplate(templateFile);
			template.process(this, response.getWriter());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回标准的wml页面。
	 * 
	 * @param templateFile
	 */
	public void renderWml(String templateFile) {
		if (plugin != null) {
			if (Setting.theme != null)
				templateFile = "plugins/" + Setting.theme + "/"
						+ plugin.getId() + "/" + templateFile;
			else
				templateFile = "plugins/" + plugin.getId() + "/" + templateFile;
		}
		renderWmlIt(templateFile);
	}

	private void renderWmlIt(String templateFile) {
		try {
			response.setContentType("text/vnd.wap.wml; charset="
					+ Setting.DEFAULT_CHARSET);
			template = TemplateManager.getEngine().cfg
					.getTemplate(templateFile);
			template.process(this, response.getWriter());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回一个文件格式的数据。
	 * 
	 * @param mimeType
	 * @param fileName
	 * @param is
	 */
	public void renderToFile(String mimeType, String fileName, InputStream is) {

	}

	public void setAttribute(String key, Object value) {
		Session session = getSession();

		if (session != null && key != null && value != null)
			session.set(key, value);
		if (session != null && key != null && value == null) {
			session.remove(key);
		}

	}

	@Inject
	public void setCacheManager(CacheManager manager) {
		this.cacheManager = manager;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public Session getSession() {
		return FilterDispatcher.getSession();
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg
	 *            the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getForward() {
		return forward;
	}

	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public void setCtx(String ctx) {
		this.ctx = ctx;
	}

	public String getToken() {
		return token;
	}

	public boolean checkToken() {
		// 不存在或者不相等，则视为成功。
		if (getAttribute(FormTokenFilter.FORM_TOKEN) == null
				|| !getAttribute(FormTokenFilter.FORM_TOKEN).equals(token))
			return true;
		return false;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
