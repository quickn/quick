package cn.quickj.systeminfo;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import cn.quickj.plugin.AbstractPlugin;

public class SystemInfoPlugin  extends AbstractPlugin {

	public Map<String, Class<?>> depend() {
		return null;
	}

	public String getId() {
		return "sysinfo";
	}

	public ArrayList<Class<?>> getModels() {
		return null;
	}

	public String getName() {
		return "查看系统信息插件";
	}

	public String getRootPackage() {
		return "cn.quickj.systeminfo";
	}

	public void init(Configuration c) {
		
	}

}
