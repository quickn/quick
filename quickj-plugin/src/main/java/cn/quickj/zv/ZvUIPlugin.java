package cn.quickj.zv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import cn.quickj.plugin.AbstractPlugin;
import cn.quickj.security.SecurityPlugin;

public class ZvUIPlugin extends AbstractPlugin {

	public Map<String, Class<?>> depend() {
		// TODO Auto-generated method stub
		Map<String, Class<?>> result = new HashMap<String, Class<?>>();
		result.put("基于用户，部门，角色的权限控制的插件", SecurityPlugin.class);
		// result.put("简单用户界面", SimpleUIPlugin.class);
		return result;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return "zv";
	}

	public ArrayList<Class<?>> getModels() {
		// TODO Auto-generated method stub
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		return result;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "ZV样式UI框架";
	}

	public String getRootPackage() {
		// TODO Auto-generated method stub
		return "cn.quickj.zv";
	}

	public void init(Configuration c) {
		// TODO Auto-generated method stub

	}

}
