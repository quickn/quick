package cn.quickj.simpleui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import cn.quickj.plugin.AbstractPlugin;
import cn.quickj.security.SecurityPlugin;
import cn.quickj.simpleui.model.SimpleMenu;

public class SimpleUIPlugin  extends AbstractPlugin{

	public Map<String, Class<?>> depend() {
		Map<String,Class<?>>result = new HashMap<String, Class<?>>();
		result.put("基于用户，部门，角色的权限控制插件", SecurityPlugin.class);
		return result;
	}

	public String getId() {
		return "simpleui";
	}

	public ArrayList<Class<?>> getModels() {
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		result.add(SimpleMenu.class);
		return result ;
	}

	public String getName() {
		return "简单用户界面插件";
	}

	public String getRootPackage() {
		return "cn.quickj.simpleui";
	}

	public void init(Configuration c) {
	}

}
