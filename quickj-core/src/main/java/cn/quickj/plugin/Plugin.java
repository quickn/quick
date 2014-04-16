package cn.quickj.plugin;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

public interface Plugin {

	/**
	 * 返回插件的名称。
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 包含的Model。
	 * 
	 * @return
	 */
	public ArrayList<Class<?>> getModels();

	/**
	 * 插件的id，用于区分action的命名空间，只有/id/下的action会 调用该插件的action。
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * 插件的所在package的根路径。
	 * 
	 * @return
	 */
	public String getRootPackage();
	/**
	 * 初始化插件，插件可以通过这个方式接收配置信息（只包括&lt;plugin&gt;&lt;/plugin&gt;)。
	 * @param c
	 */
	public void init(Configuration c);
	/**
	 * 插件所需要依赖的其他插件的类名称和类。插件管理器可以依此来判断依赖的插件是否已经导入。				
	 * @return 如果不需要依赖，则可以返回null。
	 */
	public Map<String,Class<?>> depend();
	/**
	 * 指定的uri是否和插件匹配。
	 * @param uri
	 * @return
	 */
	public boolean uriMatch(String uri);

}