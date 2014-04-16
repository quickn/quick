package cn.quickj.imexport;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import cn.quickj.imexport.model.ExportSchema;
import cn.quickj.plugin.AbstractPlugin;

public class ImportExportPlugin extends AbstractPlugin{

	public Map<String, Class<?>> depend() {
		return null;
	}

	public String getId() {
		return "imexport";
	}

	public ArrayList<Class<?>> getModels() {
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		result.add(ExportSchema.class);
		return result;
		
	}

	public String getName() {
		return "导入导出插件";
	}

	public String getRootPackage() {
		return "cn.quickj.imexport";
	}

	public void init(Configuration c) {
		
	}
}
