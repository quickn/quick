package cn.quickj.systeminfo.action;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.TimeZone;

import cn.quickj.action.Action;

public class ViewAction extends Action {
	private HashMap<String, Properties> infos = new HashMap<String, Properties>();

	/**
	 * 查看系统信息
	 */
	public void index() {
		Properties temp = System.getProperties();
		Properties sysproperties = new Properties();
		sysproperties.put("Date:", DateFormat.getDateInstance(DateFormat.LONG,
				getRequest().getLocale()).format(new Date()));
		sysproperties
				.put("Time:", DateFormat.getDateInstance(DateFormat.MEDIUM,
						getRequest().getLocale()).format(new Date()));
		sysproperties.put("Timezone:", TimeZone.getDefault().getDisplayName(
				getRequest().getLocale()));
		sysproperties.put("Java Version:", temp.getProperty("java.version"));
		sysproperties.put("JVM Version:", temp.getProperty("java.vm.version"));
		sysproperties.put("JVM Vendor:", temp.getProperty("java.vm.vendor"));
		sysproperties.put("JVM Runtime:", temp.getProperty("java.vm.name"));
		sysproperties.put("Username:", temp.getProperty("user.name"));
		sysproperties.put("Operating System:", temp.getProperty("os.name")
				+ " " + temp.getProperty("os.version"));
		sysproperties.put("Architecture:", temp.getProperty("os.arch"));
		infos.put("System Information", sysproperties);
		Properties memproperties = new Properties();
		memproperties.put("Total Memory:", String.valueOf(Runtime.getRuntime()
				.maxMemory()
				/ (1024 * 1024))
				+ "MB");
		memproperties.put("Used Memory:", String.valueOf((Runtime.getRuntime()
				.maxMemory() - Runtime.getRuntime().freeMemory())
				/ (1024 * 1024))
				+ "MB");
		memproperties.put("Free Memory:", String.valueOf(Runtime.getRuntime()
				.freeMemory()
				/ (1024 * 1024))
				+ "MB");
		infos.put("JVM Statistics", memproperties);
		render("sysinfo.html");
	}

	public HashMap<String, Properties> getInfos() {
		return infos;
	}
}
