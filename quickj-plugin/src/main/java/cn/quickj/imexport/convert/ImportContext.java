package cn.quickj.imexport.convert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.quickj.session.Session;

/**
 * 转换所需要的上下文环境，用户可以实现这个类为特定的应用提供转换所需要的上下文环境
 * 该类使用DI的创建，所以可以使用@Inject指令。
 * @author lbj
 *
 */
public interface ImportContext {
	public final static String REQUEST_IMPORT_CONTEXT  = "REQUEST_IMPORT_CONTEXT";  
	public final static String RESPONSE_IMPORT_CONTEXT  = "RESPONSE_IMPORT_CONTEXT";
	public static final String SESSION_IMPORT_CONTEXT = "SESSION_IMPORT_CONTEXT";  
	
	public void init();
	public Object getContextAttribute(String key);
	public void setContextAttribute(String key,Object value);
	public Session getSession();
	public HttpServletRequest getRequest();
	public HttpServletResponse getResponse();
}
