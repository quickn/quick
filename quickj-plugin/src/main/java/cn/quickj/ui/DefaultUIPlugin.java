package cn.quickj.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import cn.quickj.plugin.AbstractPlugin;
import cn.quickj.security.SecurityPlugin;
import cn.quickj.simpleui.SimpleUIPlugin;

public class DefaultUIPlugin  extends AbstractPlugin{

	public String getId() {
		return "ui";
	}

	public ArrayList<Class<?>> getModels() {
		ArrayList<Class<?>>result = new ArrayList<Class<?>>();
		return result;
	}

	public String getName() {
		return "缺省UI框架";
	}

	public String getRootPackage() {
		return "cn.quickj.ui";
	}

	public void init(Configuration c) {
		
	}

	public  Map<String,Class<?>> depend() {
		Map<String,Class<?>>result = new HashMap<String, Class<?>>();
		result.put("基于用户，部门，角色的权限控制插件", SecurityPlugin.class);
		result.put("简单用户界面", SimpleUIPlugin.class);
		return result;
	}

}
