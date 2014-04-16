package cn.quickj.dict;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import cn.quickj.dict.model.DictType;
import cn.quickj.dict.model.Dictionary;
import cn.quickj.plugin.AbstractPlugin;

public class DictPlugin extends AbstractPlugin {

	public Map<String, Class<?>> depend() {
		return null;
	}

	public String getId() {
		return "dict";
	}

	public ArrayList<Class<?>> getModels() {
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		result.add(Dictionary.class);
		result.add(DictType.class);
		return result;
	}

	public String getName() {
		return "数据字典插件";
	}

	public String getRootPackage() {
		return "cn.quickj.dict";
	}

	public void init(Configuration c) {
		
	}

}
