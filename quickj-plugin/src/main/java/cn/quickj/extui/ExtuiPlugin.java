package cn.quickj.extui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import cn.quickj.dict.DictPlugin;
import cn.quickj.imexport.ImportExportPlugin;
import cn.quickj.logger.DynamicLoggerPlugin;
import cn.quickj.plugin.AbstractPlugin;
import cn.quickj.security.SecurityPlugin;
import cn.quickj.simpleui.SimpleUIPlugin;
import cn.quickj.systeminfo.SystemInfoPlugin;

public class ExtuiPlugin extends AbstractPlugin {

	public Map<String, Class<?>> depend() {
		Map<String,Class<?>> results = new HashMap<String, Class<?>>();
		results.put("数据字典插件", DictPlugin.class);
		results.put("导入导出插件", ImportExportPlugin.class);
		results.put("日志动态配置插件", DynamicLoggerPlugin.class);
		results.put("基于用户，部门，角色的权限控制插件", SecurityPlugin.class);
		results.put("简单用户界面插件", SimpleUIPlugin.class);
		results.put("查看系统信息插件", SystemInfoPlugin.class);

		return results;
	}

	public String getId() {
		return "extui";
	}

	public ArrayList<Class<?>> getModels() {
		return null;
	}

	public String getName() {
		return "ext界面插件";
	}

	public String getRootPackage() {
		return "cn.quickj.extui";
	}

	public void init(Configuration c) {

	}

}
