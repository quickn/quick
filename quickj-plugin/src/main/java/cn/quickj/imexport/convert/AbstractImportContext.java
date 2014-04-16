package cn.quickj.imexport.convert;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.quickj.session.Session;

public abstract class AbstractImportContext implements ImportContext {
	private Map<String,Object> context;
	public AbstractImportContext() {
		context = new HashMap<String, Object>();
	}
	public Object getContextAttribute(String key) {
		return context.get(key);
	}

	public HttpServletRequest getRequest() {
		return (HttpServletRequest) getContextAttribute(ImportContext.REQUEST_IMPORT_CONTEXT);
	}

	public HttpServletResponse getResponse() {
		return (HttpServletResponse) getContextAttribute(ImportContext.RESPONSE_IMPORT_CONTEXT);
	}

	public Session getSession() {
		return (Session) getContextAttribute(ImportContext.SESSION_IMPORT_CONTEXT);
	}

	public void setContextAttribute(String key, Object value) {
		context.put(key, value);
	}

}
