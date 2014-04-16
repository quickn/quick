package cn.quickj;

import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.quickj.action.Action;

import com.google.inject.Module;

public interface Application {
	/**
	 * 处理HttpRequest的入口消息。
	 * 
	 * @return 如果需要继续处理，则返回true，否则返回false。
	 */
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response) throws ServletException;

	/**
	 * 初始化应用。
	 * 
	 * @throws Exception
	 */
	public void init(String rootPath) throws Exception;

	/**
	 * 加载Model的class数组，重载此函数以得到额外的Entity Class列表。
	 * 
	 * @return
	 */
	public ArrayList<Class<?>> getModels();

	/**
	 * 额外的Hibernate属性配置。
	 * 
	 * @param properties
	 */
	public void onHibernateConfig(Properties properties);

	/**
	 * 额外的Guice Module属性配置
	 * 
	 * @param modules
	 */
	public void onInitGuiceModules(ArrayList<Module> modules);

	/**
	 * 异常处理方法，当系统发生异常时，通知给应用的实现者，使其可以充分分析当前的上下文环境 打印活记录日志进行处理。
	 * 
	 * @param e
	 */
	public void handleException(Exception e,Action action) throws ServletException;
}