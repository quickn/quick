package cn.quickj.logger;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import cn.quickj.plugin.AbstractPlugin;

public class DynamicLoggerPlugin extends AbstractPlugin{
	public Map<String, Class<?>> depend() {
		return null;
	}

	public String getId() {
		return "log";
	}

	public ArrayList<Class<?>> getModels() {
		return null;
	}

	public String getName() {
		return "日志动态配置插件";
	}

	public String getRootPackage() {
		return "cn.quickj.logger";
	}

	public void init(Configuration c) {
		
	}

}
